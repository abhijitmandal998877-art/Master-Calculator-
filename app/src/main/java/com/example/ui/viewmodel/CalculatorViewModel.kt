package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.CalculationEntity
import com.example.data.repository.CalculationRepository
import com.example.data.network.Web3FormsClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CalculationRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CalculationRepository(database.calculationDao())
        
        // Auto-delete history older than 3 days on startup
        pruneOldHistory()
    }

    val historyState: StateFlow<List<CalculationEntity>> = repository.allCalculations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI state for inputs
    val pricePerKg = MutableStateFlow("")
    val weightGrams = MutableStateFlow("")
    val amountRupees = MutableStateFlow("")

    // Results state
    val priceResult = MutableStateFlow<String?>(null)
    val weightResult = MutableStateFlow<String?>(null)

    // Validation errors
    val priceError = MutableStateFlow<String?>(null)
    val weightError = MutableStateFlow<String?>(null)
    val amountError = MutableStateFlow<String?>(null)

    // Contact Form State
    val contactName = MutableStateFlow("")
    val contactEmail = MutableStateFlow("")
    val contactMessage = MutableStateFlow("")
    val contactLoading = MutableStateFlow(false)
    val contactSuccessMessage = MutableStateFlow<String?>(null)
    val contactErrorMessage = MutableStateFlow<String?>(null)

    // Formatting helpers
    private val decimalFormat = DecimalFormat("#.##")

    // Event Flow for triggering Haptic Feedback, Toast or other actions in Compose
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    sealed interface UiEvent {
        object TriggerHaptics : UiEvent
        data class ShowToast(val message: String) : UiEvent
    }

    private fun pruneOldHistory() {
        viewModelScope.launch {
            val threeDaysAgo = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000)
            repository.deleteOld(threeDaysAgo)
        }
    }

    fun calculatePrice() {
        priceError.value = null
        priceResult.value = null

        val priceVal = pricePerKg.value.toDoubleOrNull()
        val weightVal = weightGrams.value.toDoubleOrNull()

        if (priceVal == null || priceVal <= 0) {
            priceError.value = "Enter positive Price per KG"
            return
        }
        if (weightVal == null || weightVal <= 0) {
            priceError.value = "Enter positive Weight in Grams"
            return
        }

        val totalPrice = (priceVal * weightVal) / 1000.0
        val formattedPrice = decimalFormat.format(totalPrice)
        val resultString = "$weightVal grams = ₹$formattedPrice"
        priceResult.value = resultString

        viewModelScope.launch {
            _uiEvent.emit(UiEvent.TriggerHaptics)
            _uiEvent.emit(UiEvent.ShowToast("Price Calculated & Saved"))

            // Save to database
            repository.insert(
                CalculationEntity(
                    type = "WEIGHT_TO_PRICE",
                    pricePerKg = priceVal,
                    inputWeightGrams = weightVal,
                    outputPrice = totalPrice,
                    inputAmount = 0.0,
                    outputWeightGrams = 0.0
                )
            )
        }
    }

    fun calculateWeight() {
        amountError.value = null
        weightResult.value = null

        val priceVal = pricePerKg.value.toDoubleOrNull()
        val amountVal = amountRupees.value.toDoubleOrNull()

        if (priceVal == null || priceVal <= 0) {
            amountError.value = "Enter positive Price per KG first"
            return
        }
        if (amountVal == null || amountVal <= 0) {
            amountError.value = "Enter positive Amount in ₹"
            return
        }

        val totalWeight = (amountVal * 1000.0) / priceVal
        val formattedWeight = decimalFormat.format(totalWeight)
        val resultString = "₹$amountVal = $formattedWeight grams"
        weightResult.value = resultString

        viewModelScope.launch {
            _uiEvent.emit(UiEvent.TriggerHaptics)
            _uiEvent.emit(UiEvent.ShowToast("Weight Calculated & Saved"))

            // Save to database
            repository.insert(
                CalculationEntity(
                    type = "MONEY_TO_WEIGHT",
                    pricePerKg = priceVal,
                    inputWeightGrams = 0.0,
                    outputPrice = 0.0,
                    inputAmount = amountVal,
                    outputWeightGrams = totalWeight
                )
            )
        }
    }

    fun clearAllFields() {
        pricePerKg.value = ""
        weightGrams.value = ""
        amountRupees.value = ""
        priceResult.value = null
        weightResult.value = null
        priceError.value = null
        weightError.value = null
        amountError.value = null

        viewModelScope.launch {
            _uiEvent.emit(UiEvent.TriggerHaptics)
        }
    }

    fun deleteHistoryItem(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
            _uiEvent.emit(UiEvent.TriggerHaptics)
            _uiEvent.emit(UiEvent.ShowToast("Deleted from history"))
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
            _uiEvent.emit(UiEvent.TriggerHaptics)
            _uiEvent.emit(UiEvent.ShowToast("History cleared"))
        }
    }

    fun submitContactForm() {
        val nameVal = contactName.value.trim()
        val emailVal = contactEmail.value.trim()
        val msgVal = contactMessage.value.trim()

        contactErrorMessage.value = null
        contactSuccessMessage.value = null

        if (nameVal.isEmpty()) {
            contactErrorMessage.value = "Name cannot be empty"
            return
        }
        if (emailVal.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()) {
            contactErrorMessage.value = "Enter a valid email address"
            return
        }
        if (msgVal.isEmpty()) {
            contactErrorMessage.value = "Message cannot be empty"
            return
        }

        contactLoading.value = true
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.TriggerHaptics)
            val result = Web3FormsClient.submitContactForm(nameVal, emailVal, msgVal)
            contactLoading.value = false
            result.onSuccess { msg ->
                contactSuccessMessage.value = msg
                contactName.value = ""
                contactEmail.value = ""
                contactMessage.value = ""
                _uiEvent.emit(UiEvent.ShowToast("Message sent successfully!"))
            }.onFailure { err ->
                contactErrorMessage.value = err.localizedMessage ?: "Failed to send message. Please try again."
            }
        }
    }

    fun copyToClipboard(text: String, label: String = "Result") {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.TriggerHaptics)
            _uiEvent.emit(UiEvent.ShowToast("Copied to clipboard!"))
        }
    }

    fun shareResult(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(intent, "Share Result via")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(chooser)
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.TriggerHaptics)
        }
    }
}
