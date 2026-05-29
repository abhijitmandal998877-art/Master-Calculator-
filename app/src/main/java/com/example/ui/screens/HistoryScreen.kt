package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.CalculationEntity
import com.example.ui.viewmodel.CalculatorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: CalculatorViewModel) {
    val haptic = LocalHapticFeedback.current
    val history by viewModel.historyState.collectAsState()
    
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    // Screen BG brush
    val darkBlueGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F2027),
            Color(0xFF162A32),
            Color(0xFF1E3540)
        )
    )

    val isDark by viewModel.isDarkMode.collectAsState()
    val bgColor = if (isDark) Color(0xFF14151C) else Color(0xFFF3F5F7)
    val cardBgColor = if (isDark) Color(0xFF20222F) else Color.White
    val borderStrokeColor = if (isDark) Color(0xFF2D313F) else Color(0xFFE2E8F0)
    val textColorPrimary = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1C1B1F)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "History Log",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary
                    )
                    Text(
                        text = "Auto-cleaned older than 3 days",
                        fontSize = 12.sp,
                        color = textColorSecondary
                    )
                }

                if (history.isNotEmpty()) {
                    IconButton(
                        onClick = { showClearConfirmDialog = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (isDark) Color(0xFF3B1E1E) else Color(0xFFFEE2E2),
                            contentColor = if (isDark) Color(0xFFFCA5A5) else Color(0xFFDC2626)
                        ),
                        modifier = Modifier.testTag("clear_history_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear All History"
                        )
                    }
                }
            }

            HorizontalDivider(
                color = borderStrokeColor,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (history.isEmpty()) {
                // Empty state view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        tint = textColorSecondary,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = "No Saved Calculations",
                        color = textColorPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recent shop weights and price matches will appear here automatically.",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            } else {
                // Calculation records list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 110.dp)
                ) {
                    items(history, key = { it.id }) { item ->
                        HistoryCard(
                            item = item,
                            formattedTime = dateFormat.format(Date(item.timestamp)),
                            onDelete = { viewModel.deleteHistoryItem(item.id) },
                            isDark = isDark
                        )
                    }
                }
            }
        }

        // Clear All Dialog Confirmation
        if (showClearConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showClearConfirmDialog = false },
                title = { Text("Clear All Logs?") },
                text = { Text("Are you sure you want to permanently delete all historical calculations?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearAllHistory()
                            showClearConfirmDialog = false
                        }
                    ) {
                        Text("Delete All", color = Color(0xFFDC2626))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun HistoryCard(
    item: CalculationEntity,
    formattedTime: String,
    onDelete: () -> Unit,
    isDark: Boolean = false
) {
    val isWeightToPrice = item.type == "WEIGHT_TO_PRICE"
    
    val cardBg = if (isDark) Color(0xFF20222F) else Color.White
    val borderCol = if (isDark) Color(0xFF2E3244) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1C1B1F)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    // Gradient accent color tags
    val typeContainerColor = if (isWeightToPrice) {
        if (isDark) Color(0xFF4F378B).copy(alpha = 0.3f) else Color(0xFFEADDFF)
    } else {
        if (isDark) Color(0xFF0369A1).copy(alpha = 0.3f) else Color(0xFFE0F2FE)
    }
    val typeContentColor = if (isWeightToPrice) {
        if (isDark) Color(0xFFD0BCFF) else Color(0xFF6750A4)
    } else {
        if (isDark) Color(0xFF38BDF8) else Color(0xFF0369A1)
    }
    val typeLabel = if (isWeightToPrice) "Weight ➔ Price" else "Money ➔ Weight"

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, borderCol),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${item.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with type label and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge
                Surface(
                    color = typeContainerColor,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = typeLabel,
                        color = typeContentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Delete Icon Button
                IconButton(
                    onClick = onDelete,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = textSecondary
                    ),
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_item_btn_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Delete record",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Formula details block
            if (isWeightToPrice) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${item.inputWeightGrams} Grams",
                            color = textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Rate: ₹${item.pricePerKg} / KG",
                            color = textSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${String.format(Locale.US, "%.2f", item.outputPrice)}",
                            color = textPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Calculated Cost",
                            color = textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "₹${item.inputAmount}",
                            color = textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Rate: ₹${item.pricePerKg} / KG",
                            color = textSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${String.format(Locale.US, "%.2f", item.outputWeightGrams)} g",
                            color = textPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Calculated Weight",
                            color = textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Time stamp
            Text(
                text = "Recorded at: $formattedTime",
                color = textSecondary,
                fontSize = 10.sp
            )
        }
    }
}
