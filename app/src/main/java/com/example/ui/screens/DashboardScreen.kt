package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ConceptNodeEntity
import com.example.data.local.entities.LearningGapEntity
import com.example.data.local.entities.StudySessionEntity
import com.example.edgeai.NpuHardwareMetrics
import com.example.ui.components.CognitiveEnergyGauge
import com.example.ui.components.CyberCard
import com.example.ui.components.FlowSessionTimer
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
import com.example.ui.viewmodel.SynapseScreen

@Composable
fun DashboardScreen(
    npuMetrics: NpuHardwareMetrics,
    conceptNodes: List<ConceptNodeEntity>,
    studySessions: List<StudySessionEntity>,
    learningGaps: List<LearningGapEntity>,
    onNavigate: (SynapseScreen) -> Unit,
    onSelectConceptNode: (ConceptNodeEntity) -> Unit,
    onOpenOfficeKit: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SynapseBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Snapdragon Hexagon NPU Hardware Banner
        item {
            NpuHardwareBanner(npuMetrics = npuMetrics, onOpenOfficeKit = onOpenOfficeKit)
        }

        // Real-Time Cognitive Energy Gauge
        item {
            CognitiveEnergyGauge(
                energyPercent = 0.88f,
                cognitiveLoad = 0.36f,
                flowScore = 0.94f
            )
        }

        // Deep Work Flow Timer
        item {
            FlowSessionTimer()
        }

        // Quick Action Cards Grid
        item {
            Text(
                text = "EDGE-AI MULTI-AGENT ENGINES",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = ElectricCyan,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionTile(
                        title = "Synapse Snap",
                        subtitle = "Handwriting VLM Diagnosis",
                        badge = "Qwen2.5-VL",
                        icon = Icons.Default.CameraAlt,
                        accentColor = ElectricCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(SynapseScreen.DIAGNOSTIC_VLM) }
                    )
                    ActionTile(
                        title = "Focus Flow",
                        subtitle = "Live Lecture Audio Scribe",
                        badge = "Whisper.cpp",
                        icon = Icons.Default.GraphicEq,
                        accentColor = NeonViolet,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(SynapseScreen.LIVE_SCRIBE) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionTile(
                        title = "Remedial Studio",
                        subtitle = "Reasoning Worksheet Engine",
                        badge = "Phi-4 Mini",
                        icon = Icons.Default.Psychology,
                        accentColor = CyberAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(SynapseScreen.KNOWLEDGE_GRAPH) }
                    )
                    ActionTile(
                        title = "AR Insight",
                        subtitle = "Knowledge Graph Explorer",
                        badge = "LiteRT Edge",
                        icon = Icons.Default.AutoGraph,
                        accentColor = MatrixGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(SynapseScreen.KNOWLEDGE_GRAPH) }
                    )
                }
            }
        }

        // Dynamic Knowledge Graph Heatmap Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "KNOWLEDGE GRAPH MASTERY HIERARCHY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonViolet,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "Real-time concept prerequisite mastery heatmap",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                }
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ElectricCyan,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { onNavigate(SynapseScreen.KNOWLEDGE_GRAPH) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(conceptNodes) { node ->
                    KnowledgeNodeCard(
                        node = node,
                        onClick = {
                            onSelectConceptNode(node)
                            onNavigate(SynapseScreen.KNOWLEDGE_GRAPH)
                        }
                    )
                }
            }
        }

        // Diagnostic Telemetry & Recent Sessions Feed
        item {
            Text(
                text = "RECENT DIAGNOSTIC TELEMETRY",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyberAmber,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (studySessions.isEmpty()) {
                CyberCard {
                    Text(
                        text = "No study sessions recorded yet. Launch Synapse Snap to diagnose handwriting.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (session in studySessions.take(3)) {
                        SessionFeedCard(session = session)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun NpuHardwareBanner(
    npuMetrics: NpuHardwareMetrics,
    onOpenOfficeKit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        SynapseSurfaceVariant,
                        SynapseSurface
                    )
                )
            )
            .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(16.dp)
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
                            .background(MatrixGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Snapdragon® 8 Elite | Hexagon™ NPU",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "45 TOPS Peak • ${npuMetrics.inferenceLatencyMs}ms Inference • Zero Data Egress",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SynapseSurfaceElevated)
                    .border(1.dp, NeonViolet.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .clickable { onOpenOfficeKit() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CastConnected,
                        contentDescription = "iQOO Bridge",
                        tint = NeonViolet,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Office Kit",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionTile(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() }
            .testTag("action_tile_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SynapseSurface),
        border = BorderStroke(1.dp, SynapseBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SynapseSurfaceElevated)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun KnowledgeNodeCard(
    node: ConceptNodeEntity,
    onClick: () -> Unit
) {
    val statusColor = when (node.status) {
        "MASTERED" -> MatrixGreen
        "AT_RISK" -> ErrorCrimson
        else -> CyberAmber
    }

    Card(
        modifier = Modifier
            .width(190.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SynapseSurface),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = node.subject.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ElectricCyan,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
            }

            Text(
                text = node.subtopic,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                maxLines = 1
            )

            Text(
                text = "Topic: ${node.topic}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 10.sp
                ),
                maxLines = 1
            )

            // Mastery bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mastery",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp)
                )
                Text(
                    text = "${(node.masteryScore * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = statusColor,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun SessionFeedCard(session: StudySessionEntity) {
    CyberCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ElectricCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = session.sessionType.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ElectricCyan,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = session.subjectTag,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                if (session.summary.isNotEmpty()) {
                    Text(
                        text = session.summary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open session",
                tint = TextSecondary
            )
        }
    }
}
