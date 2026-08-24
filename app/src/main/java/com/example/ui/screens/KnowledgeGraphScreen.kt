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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.local.entities.LearningGapEntity
import com.example.edgeai.PracticeProblem
import com.example.edgeai.RemedialWorksheet
import com.example.ui.components.CyberCard
import com.example.ui.components.InteractiveConceptGraph
import com.example.ui.components.PulseBadge
import com.example.ui.components.RemedialDrillSolver
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
fun KnowledgeGraphScreen(
    conceptNodes: List<ConceptNodeEntity>,
    selectedNode: ConceptNodeEntity?,
    learningGaps: List<LearningGapEntity>,
    generatedWorksheet: RemedialWorksheet?,
    isGeneratingWorksheet: Boolean,
    onSelectNode: (ConceptNodeEntity) -> Unit,
    onGenerateWorksheet: (ConceptNodeEntity) -> Unit,
    onExportWorksheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    var subjectFilter by remember { mutableStateOf("ALL") }
    var studioViewMode by remember { mutableStateOf(0) } // 0: 2D Concept Map, 1: Hierarchy Tree & Worksheets, 2: Interactive Drill
    val subjects = listOf("ALL", "Mathematics", "Physics", "Computer Science")

    val filteredNodes = conceptNodes.filter {
        subjectFilter == "ALL" || it.subject.equals(subjectFilter, ignoreCase = true)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SynapseBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Section Title & Filter Pills
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "KNOWLEDGE GRAPH & REMEDIAL STUDIO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberAmber,
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Text(
                            text = "Phi-4 reasoning & gap telemetry synthesis",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                        )
                    }
                    PulseBadge(text = "${conceptNodes.size} NODES MAPPED", color = ElectricCyan)
                }

                // Studio Mode Selector Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SynapseSurface)
                        .border(1.dp, SynapseBorder, RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf("🕸️ 2D Neural Map", "📋 Hierarchy & PDF", "✍️ Practice Drill")
                    tabs.forEachIndexed { index, label ->
                        val isSelected = studioViewMode == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonViolet else Color.Transparent)
                                .clickable { studioViewMode = index }
                                .padding(vertical = 8.dp)
                                .testTag("studio_tab_$index"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Subject filter row
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subjects) { subj ->
                        val isSelected = subjectFilter == subj
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CyberAmber else SynapseSurfaceVariant)
                                .border(1.dp, if (isSelected) CyberAmber else SynapseBorder, RoundedCornerShape(8.dp))
                                .clickable { subjectFilter = subj }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = subj,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) SynapseBackground else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }
                }
            }
        }

        if (studioViewMode == 0) {
            // Interactive 2D Concept Map Canvas
            item {
                InteractiveConceptGraph(
                    nodes = filteredNodes,
                    selectedNode = selectedNode,
                    onSelectNode = onSelectNode,
                    onLaunchRemedial = { node ->
                        onGenerateWorksheet(node)
                        studioViewMode = 2 // Switch to Drill
                    }
                )
            }
        }

        if (studioViewMode == 2) {
            // Interactive Remedial Practice Drill Solver
            item {
                RemedialDrillSolver(
                    targetNode = selectedNode ?: filteredNodes.firstOrNull(),
                    onCompleteDrill = { _ ->
                        // Completed drill
                    }
                )
            }
        }

        // Visual Mastery Hierarchy Grid
        item {
            Text(
                text = "PREREQUISITE MASTERY HIERARCHY",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = ElectricCyan,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (node in filteredNodes) {
                    val isSelected = selectedNode?.id == node.id
                    val statusColor = when (node.status) {
                        "MASTERED" -> MatrixGreen
                        "AT_RISK" -> ErrorCrimson
                        else -> CyberAmber
                    }

                    CyberCard(
                        borderColor = if (isSelected) ElectricCyan else statusColor.copy(alpha = 0.4f),
                        backgroundColor = if (isSelected) SynapseSurfaceElevated else SynapseSurface,
                        onClick = { onSelectNode(node) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(statusColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${node.subject} • ${node.topic}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextSecondary,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = node.subtopic,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                if (node.prerequisiteNodeId != null) {
                                    Text(
                                        text = "↳ Prerequisite: ${node.prerequisiteNodeId}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${(node.masteryScore * 100).toInt()}%",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                                Text(
                                    text = node.status,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = statusColor,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Selected Concept Node Remedial Worksheet Studio
        if (selectedNode != null) {
            item {
                Text(
                    text = "REMEDIAL WORKSHEET STUDIO (${selectedNode.subtopic})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = NeonViolet,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                CyberCard(borderColor = NeonViolet.copy(alpha = 0.6f)) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Adaptive Pedagogical Synthesis",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )

                            Button(
                                onClick = { onGenerateWorksheet(selectedNode) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonViolet,
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                if (isGeneratingWorksheet) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = TextPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "Generate",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Regenerate",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        if (generatedWorksheet != null) {
                            Text(
                                text = "Diagnosed Struggle Telemetry:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyberAmber,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            for (struggle in generatedWorksheet.diagnosedStruggles) {
                                Text(
                                    text = "• $struggle",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Custom Practice Problems:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ElectricCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            for (problem in generatedWorksheet.practiceProblems) {
                                PracticeProblemAccordion(problem = problem)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Broadcast Worksheet PDF to PC
                            Button(
                                onClick = onExportWorksheet,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MatrixGreen,
                                    contentColor = SynapseBackground
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("export_worksheet_pdf_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "Export PDF",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Broadcast Remedial PDF to Paired PC Desktop",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun PracticeProblemAccordion(problem: PracticeProblem) {
    var isExpanded by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }

    CyberCard(backgroundColor = SynapseSurfaceElevated) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ElectricCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = problem.targetGapType.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ElectricCyan,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle",
                        tint = TextSecondary
                    )
                }
            }

            Text(
                text = problem.problemStatement,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Hint Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showHint = !showHint },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Hint",
                            tint = CyberAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showHint) "Hide Hint" else "Show Pedagogical Hint",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberAmber,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    if (showHint) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberAmber.copy(alpha = 0.1f))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = problem.hint,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Step by step solution
                    Text(
                        text = "Step-by-Step Solution:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MatrixGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(MatrixGreen.copy(alpha = 0.08f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = problem.solutionStepByStep,
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
