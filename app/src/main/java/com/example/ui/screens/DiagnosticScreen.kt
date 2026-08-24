package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edgeai.StepDiagnosis
import com.example.edgeai.VlmDiagnosticResult
import com.example.ui.components.BoundingBoxOverlay
import com.example.ui.components.CyberCard
import com.example.ui.components.HandwritingCanvas
import com.example.ui.components.PulseBadge
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ErrorCrimson
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SynapseBackground
import com.example.ui.theme.SynapseBorder
import com.example.ui.theme.SynapseSurface
import com.example.ui.theme.SynapseSurfaceElevated
import com.example.ui.theme.SynapseSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DiagnosticScreen(
    diagnosticResult: VlmDiagnosticResult?,
    isAnalyzing: Boolean,
    selectedPreset: Int,
    onSelectPreset: (Int) -> Unit,
    onReAnalyze: () -> Unit,
    onNavigateToRemedial: () -> Unit,
    onCopyLatex: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = listOf("Calculus Integral", "Faraday Induction", "Eigenvalues")
    var inputMode by remember { mutableStateOf(0) } // 0: Viewfinder & Presets, 1: Stylus Drawing Canvas

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SynapseBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Mode Switcher: Camera Viewfinder vs Stylus Canvas
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SynapseSurface)
                    .border(1.dp, SynapseBorder, RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (inputMode == 0) ElectricCyan else Color.Transparent)
                        .clickable { inputMode = 0 }
                        .padding(vertical = 8.dp)
                        .testTag("mode_viewfinder_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📷 VLM Viewfinder",
                        color = if (inputMode == 0) SynapseBackground else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (inputMode == 1) ElectricCyan else Color.Transparent)
                        .clickable { inputMode = 1 }
                        .padding(vertical = 8.dp)
                        .testTag("mode_stylus_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✍️ Stylus Canvas",
                        color = if (inputMode == 1) SynapseBackground else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (inputMode == 1) {
            // Interactive Stylus Drawing Pad
            item {
                HandwritingCanvas(
                    onAnalyzeDrawing = { _ ->
                        onReAnalyze()
                    }
                )
            }
        } else {
            // Preset selector tabs
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedPreset,
                    containerColor = SynapseSurface,
                    contentColor = ElectricCyan,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedPreset]),
                            color = ElectricCyan,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, SynapseBorder, RoundedCornerShape(10.dp))
                ) {
                    presets.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedPreset == index,
                            onClick = { onSelectPreset(index) },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (selectedPreset == index) FontWeight.Bold else FontWeight.Normal,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }

        // Live Diagnostic Viewfinder / Simulated Camera HUD
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF0F172A),
                                Color(0xFF070B14)
                            )
                        )
                    )
                    .border(1.5.dp, ElectricCyan.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            ) {
                // Background handwriting simulation layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    diagnosticResult?.steps?.forEach { step ->
                        Text(
                            text = step.rawEquation,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (step.isCorrect) TextPrimary else ErrorCrimson,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Render real-time VLM bounding boxes
                if (diagnosticResult != null) {
                    BoundingBoxOverlay(
                        steps = diagnosticResult.steps,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Top Viewfinder HUD info banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PulseBadge(
                        text = "VLM SNAP VIEW (QWEN2.5-VL)",
                        color = ElectricCyan
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onReAnalyze,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SynapseSurfaceElevated)
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = ElectricCyan,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Re-analyze",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Bottom HUD stats pill
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SynapseBackground.copy(alpha = 0.85f))
                        .border(1.dp, SynapseBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "NPU Inference: ${diagnosticResult?.npuInferenceTimeMs ?: 14}ms • Zero Cloud",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MatrixGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // Diagnostic Overview Summary Card
        if (diagnosticResult != null) {
            item {
                CyberCard(
                    borderColor = if (diagnosticResult.overallStatus == "ERRORS_DETECTED") ErrorCrimson.copy(alpha = 0.6f) else MatrixGreen.copy(alpha = 0.6f)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = diagnosticResult.problemTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            PulseBadge(
                                text = if (diagnosticResult.overallStatus == "ERRORS_DETECTED") "BREAKDOWN POINT FLAGGED" else "ALL STEPS VERIFIED",
                                color = if (diagnosticResult.overallStatus == "ERRORS_DETECTED") ErrorCrimson else MatrixGreen
                            )
                        }

                        Text(
                            text = diagnosticResult.remedialRecommendation,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = onNavigateToRemedial,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonViolet,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("open_remedial_worksheet_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Remedial Studio",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Launch Remedial Practice Drill",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }
                }
            }

            // Step-by-Step Diagnostic Breakdown
            item {
                Text(
                    text = "STEP-WISE EQUATION TELEMETRY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ElectricCyan,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            items(diagnosticResult.steps) { step ->
                StepDiagnosticCard(
                    step = step,
                    onCopyLatex = onCopyLatex
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun StepDiagnosticCard(
    step: StepDiagnosis,
    onCopyLatex: (String) -> Unit
) {
    val borderColor = if (step.isCorrect) SynapseBorder else ErrorCrimson.copy(alpha = 0.7f)
    val statusIcon = if (step.isCorrect) Icons.Default.CheckCircle else Icons.Default.Error
    val iconColor = if (step.isCorrect) MatrixGreen else ErrorCrimson

    CyberCard(borderColor = borderColor) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = if (step.isCorrect) "Correct" else "Error",
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Step ${step.stepNumber}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Conf: ${(step.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { onCopyLatex(step.rawEquation) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy LaTeX",
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Raw Equation in Monospace
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SynapseSurfaceElevated)
                    .padding(10.dp)
            ) {
                Text(
                    text = step.rawEquation,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (step.isCorrect) TextPrimary else ErrorCrimson,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Explanation
            Text(
                text = step.explanation,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (step.isCorrect) TextSecondary else TextPrimary,
                    fontSize = 12.sp
                )
            )

            // Correction if error
            if (step.correction != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MatrixGreen.copy(alpha = 0.1f))
                        .border(1.dp, MatrixGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "PEDAGOGICAL CORRECTION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MatrixGreen,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = step.correction,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
