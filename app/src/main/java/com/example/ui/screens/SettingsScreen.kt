package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: CalculatorViewModel) {
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Grab ViewModel Form States
    val contactName by viewModel.contactName.collectAsState()
    val contactEmail by viewModel.contactEmail.collectAsState()
    val contactMessage by viewModel.contactMessage.collectAsState()
    val contactLoading by viewModel.contactLoading.collectAsState()
    val successText by viewModel.contactSuccessMessage.collectAsState()
    val errorText by viewModel.contactErrorMessage.collectAsState()

    // Screen BG gradient (Deep cosmic slate)
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
            Column(modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)) {
                Text(
                    text = "Developer & Contact",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1B1F)
                )
                Text(
                    text = "App details, developer bio, and feedback",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            HorizontalDivider(
                color = Color(0xFFE2E8F0),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // DEVELOPER DETAILS CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .testTag("developer_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Profile details layout (Visual Avatar + Name Info)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        // Custom avatar box with circular gradient layout
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF6750A4), Color(0xFFB58AFF))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AM",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Column {
                            Text(
                                text = "Abhijit Mandal",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1C1B1F)
                            )
                            Text(
                                text = "Android Creator & Developer",
                                fontSize = 13.sp,
                                color = Color(0xFF6750A4),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Contact rows with interactive action pills
                    DeveloperInfoRow(
                        icon = Icons.Default.Email,
                        label = "Email Address",
                        value = "imm.abhijit@gmail.com",
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:imm.abhijit@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "Master Calculator App Inquiry")
                            }
                            context.startActivity(Intent.createChooser(intent, "Send Email"))
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    DeveloperInfoRow(
                        icon = Icons.Default.AlternateEmail,
                        label = "Instagram Handle",
                        value = "@imm.abhijit",
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://instagram.com/imm.abhijit")
                            )
                            context.startActivity(intent)
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    DeveloperInfoRow(
                        icon = Icons.Default.LocationOn,
                        label = "HQ Location",
                        value = "Rampurhat, Birbhum, WB",
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("geo:0,0?q=Rampurhat, Birbhum, West Bengal, India")
                            )
                            context.startActivity(intent)
                        }
                    )
                }
            }

            // CONTACT FORM SECTION
            Text(
                text = "Contact Support",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1B1F),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("contact_form_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Have ideas or run into issues? Submit this form directly powered by Web3Forms API.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Error Alert
                    AnimatedVisibility(
                        visible = errorText != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                            border = BorderStroke(1.dp, Color(0xFFEF4444)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    tint = Color(0xFFDC2626),
                                    contentDescription = null
                                )
                                Text(
                                    text = errorText ?: "",
                                    color = Color(0xFFDC2626),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Success Alert
                    AnimatedVisibility(
                        visible = successText != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5)),
                            border = BorderStroke(1.dp, Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    tint = Color(0xFF059669),
                                    contentDescription = null
                                )
                                Text(
                                    text = successText ?: "",
                                    color = Color(0xFF047857),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Input Form
                    OutlinedTextField(
                        value = contactName,
                        onValueChange = { viewModel.contactName.value = it },
                        label = { Text("Your Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF64748B)) },
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
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("contact_name_input")
                    )

                    OutlinedTextField(
                        value = contactEmail,
                        onValueChange = { viewModel.contactEmail.value = it },
                        label = { Text("Your Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF64748B)) },
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
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("contact_email_input")
                    )

                    OutlinedTextField(
                        value = contactMessage,
                        onValueChange = { viewModel.contactMessage.value = it },
                        label = { Text("Message details") },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF64748B)) },
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
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                            .testTag("contact_message_input")
                    )

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.submitContactForm()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6750A4),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !contactLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_contact_btn")
                    ) {
                        if (contactLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send Message", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }

            // Safe spacing for scrolling above bottom bar
            Spacer(modifier = Modifier.height(110.dp))
        }
    }
}

@Composable
fun DeveloperInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = BorderStroke(1.dp, Color(0xFFEDF2F7)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF6750A4),
                modifier = Modifier.size(20.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1C1B1F)
                )
            }
        }
    }
}
