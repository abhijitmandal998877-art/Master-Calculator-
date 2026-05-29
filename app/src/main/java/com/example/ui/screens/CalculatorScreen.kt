package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel) {
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Grab values from VM
    val pricePerKg by viewModel.pricePerKg.collectAsState()
    val weightGrams by viewModel.weightGrams.collectAsState()
    val amountRupees by viewModel.amountRupees.collectAsState()

    val priceResult by viewModel.priceResult.collectAsState()
    val weightResult by viewModel.weightResult.collectAsState()

    val priceError by viewModel.priceError.collectAsState()
    val weightError by viewModel.weightError.collectAsState()
    val amountError by viewModel.amountError.collectAsState()

    // Screen BG brush
    val darkBlueGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F2027),
            Color(0xFF162A32),
            Color(0xFF1E3540)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF6750A4), Color(0xFFB58AFF))
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            text = "Master Calculator",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1B1F)
                        )
                        Text(
                            text = "Weight & Price Assistant",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
                
                // Clear all action
                IconButton(
                    onClick = { viewModel.clearAllFields() },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFFEADDFF),
                        contentColor = Color(0xFF21005D)
                    ),
                    modifier = Modifier.testTag("reset_all_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear All Inputs"
                    )
                }
            }

            // Input Section: Base Price
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Step 1: Set Base Price",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6750A4),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = pricePerKg,
                        onValueChange = { viewModel.pricePerKg.value = it },
                        label = { Text("Price Per KG (₹)") },
                        placeholder = { Text("e.g. 240") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFF6750A4),
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedTextColor = Color(0xFF1C1B1F),
                            unfocusedTextColor = Color(0xFF1C1B1F),
                            focusedLabelColor = Color(0xFF6750A4),
                            unfocusedLabelColor = Color(0xFF64748B),
                            cursorColor = Color(0xFF6750A4)
                        ),
                        isError = priceError != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("price_per_kg_input")
                    )
                    if (priceError != null) {
                        Text(
                            text = priceError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }
            }

            // SECTION 1: Calculate Price by entering Weight
            HorizontalDivider(
                color = Color(0xFFE2E8F0),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Scale,
                            contentDescription = null,
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Calculate Total Price",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1C1B1F)
                        )
                    }

                    OutlinedTextField(
                        value = weightGrams,
                        onValueChange = { viewModel.weightGrams.value = it },
                        label = { Text("Weight in Grams") },
                        placeholder = { Text("e.g. 2500 (for 2.5 KG)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFF6750A4),
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedTextColor = Color(0xFF1C1B1F),
                            unfocusedTextColor = Color(0xFF1C1B1F),
                            focusedLabelColor = Color(0xFF6750A4),
                            unfocusedLabelColor = Color(0xFF64748B),
                            cursorColor = Color(0xFF6750A4)
                        ),
                        isError = weightError != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.calculatePrice()
                            }
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("weight_grams_input")
                    )
                    
                    if (weightError != null) {
                        Text(
                            text = weightError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.calculatePrice()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6750A4),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("calculate_price_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculate Price", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    // Price Result with slide/fade entry animation
                    AnimatedVisibility(
                        visible = priceResult != null,
                        enter = slideInVertically { -20 } + fadeIn(),
                        exit = fadeOut()
                    ) {
                        priceResult?.let { result ->
                            ResultCard(
                                title = "Total Price",
                                resultValue = result,
                                onCopy = { viewModel.copyToClipboard(result, "Calculated Price") },
                                onShare = { viewModel.shareResult("Weight-Price: $result (Calculated via Master Calculator)") }
                            )
                        }
                    }
                }
            }

            // SECTION 2: Calculate Weight by entering Money Amount
            HorizontalDivider(
                color = Color(0xFFE2E8F0),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = null,
                            tint = Color(0xFF00A36C),
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Calculate Weight from Money",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1C1B1F)
                        )
                    }

                    OutlinedTextField(
                        value = amountRupees,
                        onValueChange = { viewModel.amountRupees.value = it },
                        label = { Text("Amount in Rupees (₹)") },
                        placeholder = { Text("e.g. 100") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFF6750A4),
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedTextColor = Color(0xFF1C1B1F),
                            unfocusedTextColor = Color(0xFF1C1B1F),
                            focusedLabelColor = Color(0xFF6750A4),
                            unfocusedLabelColor = Color(0xFF64748B),
                            cursorColor = Color(0xFF6750A4)
                        ),
                        isError = amountError != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.calculateWeight()
                            }
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("amount_rupees_input")
                    )

                    if (amountError != null) {
                        Text(
                            text = amountError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.calculateWeight()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6750A4),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("calculate_weight_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculate Weight", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    // Weight Result with slide/fade animation
                    AnimatedVisibility(
                        visible = weightResult != null,
                        enter = slideInVertically { -20 } + fadeIn(),
                        exit = fadeOut()
                    ) {
                        weightResult?.let { result ->
                            ResultCard(
                                title = "You Get",
                                resultValue = result,
                                onCopy = { viewModel.copyToClipboard(result, "Calculated Weight") },
                                onShare = { viewModel.shareResult("Money-Weight: $result (Calculated via Master Calculator)") }
                            )
                        }
                    }
                }
            }
            
            // Safe padding bottom so items aren't cropped by NavBar/Safe Insets
            Spacer(modifier = Modifier.height(84.dp))
        }
    }
}

@Composable
fun ResultCard(
    title: String,
    resultValue: String,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Elegant white card with crisp details and border
        ),
        border = BorderStroke(1.2.dp, Color(0xFF6750A4).copy(alpha = 0.15f)),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .testTag("result_card")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = resultValue,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1C1B1F)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Copy
                IconButton(
                    onClick = onCopy,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0x126750A4),
                        contentColor = Color(0xFF6750A4)
                    ),
                    modifier = Modifier.size(36.dp).testTag("copy_result_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy Result",
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))

                // Share
                IconButton(
                    onClick = onShare,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0x126750A4),
                        contentColor = Color(0xFF6750A4)
                    ),
                    modifier = Modifier.size(36.dp).testTag("share_result_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share Result",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
