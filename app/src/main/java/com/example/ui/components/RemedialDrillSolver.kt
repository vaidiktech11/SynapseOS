package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ConceptNodeEntity
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ErrorCrimson
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SynapseBackground
import com.example.ui.theme.SynapseBorder
import com.example.ui.theme.SynapseSurface
import com.example.ui.theme.SynapseSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class DrillQuestion(
    val id: Int,
    val title: String,
    val latexProblem: String,
    val options: List<String>,
    val correctIndex: Int,
    val hint: String,
    val explanation: String
)

@Composable
fun RemedialDrillSolver(
    targetNode: ConceptNodeEntity?,
    onCompleteDrill: (scoreImprovement: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val drillQuestions = remember {
        listOf(
            DrillQuestion(
                id = 1,
                title = "Problem 1: Linear Factor Integration",
                latexProblem = "∫ (3x + 1) e^(3x) dx",
                options = listOf(
                    "(1/3)(3x + 1)e^(3x) - (1/3)e^(3x) + C = x e^(3x) + C",
                    "(3x + 1)e^(3x) - 3e^(3x) + C",
                    "(1/9)(3x + 1)e^(3x) - (1/9)e^(3x) + C",
                    "3x^2 e^(3x) + e^(3x) + C"
                ),
                correctIndex = 0,
                hint = "Let u = (3x + 1) => du = 3 dx; dv = e^(3x) dx => v = (1/3)e^(3x).",
                explanation = "Applying ∫ u dv = uv - ∫ v du gives (3x+1)*(1/3)e^(3x) - ∫ (1/3)e^(3x)*3 dx = (x + 1/3)e^(3x) - (1/3)e^(3x) + C = x e^(3x) + C."
            ),
            DrillQuestion(
                id = 2,
                title = "Problem 2: Repeated Second-Order Integration",
                latexProblem = "∫ x² cos(x) dx",
                options = listOf(
                    "x² sin(x) + 2x cos(x) - 2 sin(x) + C",
                    "x² sin(x) - 2x cos(x) + 2 sin(x) + C",
                    "2x sin(x) - x² cos(x) + C",
                    "x³ sin(x) / 3 + C"
                ),
                correctIndex = 0,
                hint = "Tabular DI method: D: [x², 2x, 2, 0] and I: [cos x, sin x, -cos x, -sin x]. Apply alternating signs (+, -, +).",
                explanation = "Row 1: + x² sin(x). Row 2: - (2x)(-cos x) = + 2x cos(x). Row 3: + 2(-sin x) = - 2 sin(x) + C."
            ),
            DrillQuestion(
                id = 3,
                title = "Problem 3: Cyclic Looping Integral",
                latexProblem = "∫ e^x sin(x) dx",
                options = listOf(
                    "(1/2) e^x (sin(x) - cos(x)) + C",
                    "e^x (sin(x) + cos(x)) + C",
                    "(1/2) e^x (cos(x) - sin(x)) + C",
                    "e^x sin(x) - e^x cos(x) + C"
                ),
                correctIndex = 0,
                hint = "Integrate by parts twice to obtain I = e^x sin x - e^x cos x - I, then add I to both sides.",
                explanation = "2I = e^x (sin x - cos x) => I = (1/2) e^x (sin(x) - cos(x)) + C."
            )
        )
    }

    var currentQIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var hasSubmitted by remember { mutableStateOf(false) }
    var correctAnswersCount by remember { mutableIntStateOf(0) }
    var showHint by remember { mutableStateOf(false) }
    var isDrillFinished by remember { mutableStateOf(false) }

    val currentQ = drillQuestions[currentQIndex]

    CyberCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("remedial_drill_solver_card"),
        borderColor = MatrixGreen.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MatrixGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Practice Drill",
                            tint = MatrixGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Interactive Remedial Drill",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Target: ${targetNode?.subtopic ?: "Integration by Parts"}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                PulseBadge(
                    text = if (isDrillFinished) "Completed" else "Q${currentQIndex + 1}/${drillQuestions.size}",
                    color = if (isDrillFinished) MatrixGreen else ElectricCyan
                )
            }

            // Progress bar
            LinearProgressIndicator(
                progress = {
                    if (isDrillFinished) 1f else (currentQIndex) / drillQuestions.size.toFloat()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MatrixGreen,
                trackColor = SynapseSurfaceElevated
            )

            if (isDrillFinished) {
                // Summary Screen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SynapseSurfaceElevated)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MatrixGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Mastery Achieved",
                            tint = MatrixGreen,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "Drill Completed Successfully!",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = "Score: $correctAnswersCount / ${drillQuestions.size} Correct (+25% Mastery)",
                        color = MatrixGreen,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "Your prerequisite foundation vector for ${targetNode?.subtopic ?: "Integration by Parts"} has been recalibrated on Hexagon NPU.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Button(
                        onClick = {
                            currentQIndex = 0
                            selectedOption = null
                            hasSubmitted = false
                            correctAnswersCount = 0
                            showHint = false
                            isDrillFinished = false
                            onCompleteDrill(0.25f)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MatrixGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("drill_restart_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = "Restart",
                            tint = SynapseBackground,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Practice Again or Sync to PC",
                            color = SynapseBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                // Question Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SynapseBackground)
                        .border(1.dp, SynapseBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentQ.title,
                            color = ElectricCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        IconButton(
                            onClick = { showHint = !showHint },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Show Hint",
                                tint = if (showHint) CyberAmber else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // LaTeX Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(SynapseSurfaceElevated)
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentQ.latexProblem,
                            color = TextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp
                        )
                    }

                    AnimatedVisibility(visible = showHint) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberAmber.copy(alpha = 0.1f))
                                .border(1.dp, CyberAmber.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "💡 Hint: ${currentQ.hint}",
                                color = CyberAmber,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Options List
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentQ.options.forEachIndexed { index, option ->
                        val isSelected = selectedOption == index
                        val isCorrect = index == currentQ.correctIndex

                        val itemBorderColor = when {
                            hasSubmitted && isCorrect -> MatrixGreen
                            hasSubmitted && isSelected && !isCorrect -> ErrorCrimson
                            isSelected -> ElectricCyan
                            else -> SynapseBorder
                        }

                        val itemBgColor = when {
                            hasSubmitted && isCorrect -> MatrixGreen.copy(alpha = 0.12f)
                            hasSubmitted && isSelected && !isCorrect -> ErrorCrimson.copy(alpha = 0.12f)
                            isSelected -> ElectricCyan.copy(alpha = 0.08f)
                            else -> SynapseSurfaceElevated
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(itemBgColor)
                                .border(1.dp, itemBorderColor, RoundedCornerShape(8.dp))
                                .clickable(enabled = !hasSubmitted) {
                                    selectedOption = index
                                }
                                .padding(10.dp)
                                .testTag("drill_option_$index"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(itemBorderColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ('A' + index).toString(),
                                    color = if (isSelected || (hasSubmitted && isCorrect)) itemBorderColor else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = option,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(1f)
                            )

                            if (hasSubmitted) {
                                if (isCorrect) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Correct",
                                        tint = MatrixGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Incorrect",
                                        tint = ErrorCrimson,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Feedback Explanation on Submit
                AnimatedVisibility(visible = hasSubmitted) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(SynapseBackground)
                            .border(1.dp, SynapseBorder, RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (selectedOption == currentQ.correctIndex) "✓ Correct Deduction" else "✕ Needs Review",
                                color = if (selectedOption == currentQ.correctIndex) MatrixGreen else ErrorCrimson,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = currentQ.explanation,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!hasSubmitted) {
                        Button(
                            onClick = {
                                if (selectedOption != null) {
                                    hasSubmitted = true
                                    if (selectedOption == currentQ.correctIndex) {
                                        correctAnswersCount++
                                    }
                                }
                            },
                            enabled = selectedOption != null,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("drill_check_answer_btn")
                        ) {
                            Text(
                                text = "Verify on Hexagon NPU",
                                color = SynapseBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                if (currentQIndex < drillQuestions.lastIndex) {
                                    currentQIndex++
                                    selectedOption = null
                                    hasSubmitted = false
                                    showHint = false
                                } else {
                                    isDrillFinished = true
                                    onCompleteDrill((correctAnswersCount / drillQuestions.size.toFloat()) * 0.3f)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MatrixGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("drill_next_q_btn")
                        ) {
                            Text(
                                text = if (currentQIndex < drillQuestions.lastIndex) "Next Question →" else "Complete Practice",
                                color = SynapseBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
