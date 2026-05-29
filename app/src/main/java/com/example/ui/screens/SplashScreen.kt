package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    // Background gradient (Light polished slate/violet theme)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF3F5F7),
            Color(0xFFEADDFF).copy(alpha = 0.2f),
            Color(0xFFF3F5F7)
        )
    )

    // Animation states
    var startAnimations by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val opacity by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(1200, easing = LinearOutSlowInEasing),
        label = "textOpacity"
    )

    var loadingProgress by remember { mutableStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = loadingProgress,
        animationSpec = tween(2000, easing = FastOutSlowInEasing),
        label = "loadingProgress"
    )

    LaunchedEffect(Unit) {
        startAnimations = true
        // Increment progress over time
        delay(300)
        loadingProgress = 1f
        delay(1900) // Give more time to enjoy splash
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated Custom Logo (Calculator + Weight Balance representation)
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Outer glowing violet ring for visual polish
                    drawCircle(
                        color = Color(0xFF6750A4).copy(alpha = 0.15f),
                        radius = (w / 2) - 10,
                        style = Stroke(width = 12.dp.toPx())
                    )

                    // Draw a modern digital calculator shape in the center
                    val boxWidth = w * 0.45f
                    val boxHeight = h * 0.55f
                    val left = (w - boxWidth) / 2
                    val top = (h - boxHeight) / 2

                    // Main body of the calculator
                    drawRoundRect(
                        color = Color(0xFF6750A4),
                        topLeft = androidx.compose.ui.geometry.Offset(left, top),
                        size = androidx.compose.ui.geometry.Size(boxWidth, boxHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                        style = Stroke(width = 4.dp.toPx())
                    )

                    // Calculator Screen region
                    drawRoundRect(
                        color = Color(0xFFEADDFF),
                        topLeft = androidx.compose.ui.geometry.Offset(left + 8.dp.toPx(), top + 8.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(boxWidth - 16.dp.toPx(), boxHeight * 0.28f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )

                    // Draw some small keyboard grid of the calculator
                    val buttonRows = 3
                    val buttonCols = 3
                    val gridTop = top + (boxHeight * 0.45f)
                    val gridWidth = boxWidth - 16.dp.toPx()
                    val gridHeight = boxHeight * 0.45f

                    val cellW = gridWidth / buttonCols
                    val cellH = gridHeight / buttonRows

                    for (r in 0 until buttonRows) {
                        for (c in 0 until buttonCols) {
                            drawCircle(
                                color = Color(0xFF6750A4).copy(alpha = 0.8f),
                                radius = 2.dp.toPx(),
                                center = androidx.compose.ui.geometry.Offset(
                                    left + 8.dp.toPx() + (c * cellW) + (cellW / 2),
                                    gridTop + (r * cellH) + (cellH / 2)
                                )
                            )
                        }
                    }

                    // Draw a weight hanging scale hook around calculator to symbolize Weights
                    drawArc(
                        color = Color(0xFFB58AFF),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset((w / 2) - 20.dp.toPx(), top - 18.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(40.dp.toPx(), 20.dp.toPx()),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name Animation
            Text(
                text = "Master Calculator",
                color = Color(0xFF1C1B1F),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.scale(scale)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Smart Price & Weight Assistant",
                color = Color(0xFF64748B),
                fontSize = 14.sp,
                modifier = Modifier.scale(scale)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Smooth linear loading slider
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .width(180.dp)
                    .height(6.dp),
                color = Color(0xFF6750A4),
                trackColor = Color(0xFFCBD5E1),
                strokeCap = StrokeCap.Round,
            )
        }
    }
}
