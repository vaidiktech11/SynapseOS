package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
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
import kotlin.math.sqrt

data class GraphNodePosition(
    val entity: ConceptNodeEntity,
    val relX: Float, // 0.0 to 1.0
    val relY: Float  // 0.0 to 1.0
)

@Composable
fun InteractiveConceptGraph(
    nodes: List<ConceptNodeEntity>,
    selectedNode: ConceptNodeEntity?,
    onSelectNode: (ConceptNodeEntity) -> Unit,
    onLaunchRemedial: (ConceptNodeEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeNode by remember(selectedNode, nodes) {
        mutableStateOf(selectedNode ?: nodes.firstOrNull())
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_graph")
    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_phase"
    )

    // Compute coordinate layout for nodes
    val layoutPositions = remember(nodes) {
        val count = nodes.size
        nodes.mapIndexed { index, node ->
            val (rx, ry) = when (index) {
                0 -> 0.20f to 0.25f // Integration by Parts (Prerequisite core)
                1 -> 0.50f to 0.18f // Tabular DI Decomposition
                2 -> 0.80f to 0.28f // Cyclic Looping Integrals
                3 -> 0.30f to 0.65f // Faraday Magnetic Induction
                4 -> 0.70f to 0.75f // Matrix Eigenvalues & Diagonalization
                else -> {
                    val angle = (index.toFloat() / count) * 2 * Math.PI
                    (0.5f + 0.35f * kotlin.math.cos(angle).toFloat()) to (0.5f + 0.35f * kotlin.math.sin(angle).toFloat())
                }
            }
            GraphNodePosition(node, rx, ry)
        }
    }

    CyberCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("interactive_concept_graph_card"),
        borderColor = NeonViolet.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            .background(NeonViolet.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = "Neural Graph",
                            tint = NeonViolet,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Dynamic Neural Concept Map",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Tap nodes to inspect prerequisites & mastery vector",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                PulseBadge(
                    text = "${nodes.size} Nodes Tracked",
                    color = ElectricCyan
                )
            }

            // 2D Interactive Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SynapseBackground)
                    .border(1.dp, SynapseBorder, RoundedCornerShape(10.dp))
                    .testTag("concept_graph_canvas_box")
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(layoutPositions) {
                            detectTapGestures { tapOffset ->
                                val w = size.width
                                val h = size.height
                                for (pos in layoutPositions) {
                                    val nx = pos.relX * w
                                    val ny = pos.relY * h
                                    val dist = sqrt((tapOffset.x - nx) * (tapOffset.x - nx) + (tapOffset.y - ny) * (tapOffset.y - ny))
                                    if (dist <= 36.dp.toPx()) {
                                        activeNode = pos.entity
                                        onSelectNode(pos.entity)
                                        break
                                    }
                                }
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // Draw prerequisite dependency edges
                    val edgePairs = listOf(
                        0 to 1,
                        0 to 2,
                        1 to 2,
                        0 to 3,
                        0 to 4
                    )

                    edgePairs.forEach { (srcIdx, dstIdx) ->
                        if (srcIdx < layoutPositions.size && dstIdx < layoutPositions.size) {
                            val src = layoutPositions[srcIdx]
                            val dst = layoutPositions[dstIdx]
                            val start = Offset(src.relX * w, src.relY * h)
                            val end = Offset(dst.relX * w, dst.relY * h)

                            val midX = (start.x + end.x) / 2f
                            val midY = (start.y + end.y) / 2f - 20f

                            val edgePath = Path().apply {
                                moveTo(start.x, start.y)
                                quadraticBezierTo(midX, midY, end.x, end.y)
                            }

                            drawPath(
                                path = edgePath,
                                color = NeonViolet.copy(alpha = 0.25f),
                                style = Stroke(
                                    width = 1.5f,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                                )
                            )

                            // Synaptic energy pulse
                            val pulseOffset = (pulsePhase + (srcIdx * 0.2f)) % 1f
                            val pulseX = (1 - pulseOffset) * (1 - pulseOffset) * start.x + 2 * (1 - pulseOffset) * pulseOffset * midX + pulseOffset * pulseOffset * end.x
                            val pulseY = (1 - pulseOffset) * (1 - pulseOffset) * start.y + 2 * (1 - pulseOffset) * pulseOffset * midY + pulseOffset * pulseOffset * end.y

                            drawCircle(
                                color = ElectricCyan.copy(alpha = 0.8f),
                                radius = 2.5f,
                                center = Offset(pulseX, pulseY)
                            )
                        }
                    }

                    // Render Nodes
                    layoutPositions.forEach { pos ->
                        val center = Offset(pos.relX * w, pos.relY * h)
                        val isSelected = activeNode?.id == pos.entity.id

                        val statusColor = when {
                            pos.entity.masteryScore >= 0.8f -> MatrixGreen
                            pos.entity.masteryScore >= 0.5f -> CyberAmber
                            else -> ErrorCrimson
                        }

                        // Halo glow
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    (if (isSelected) ElectricCyan else statusColor).copy(alpha = if (isSelected) 0.35f else 0.15f),
                                    Color.Transparent
                                ),
                                center = center,
                                radius = if (isSelected) 36.dp.toPx() else 24.dp.toPx()
                            ),
                            radius = if (isSelected) 36.dp.toPx() else 24.dp.toPx(),
                            center = center
                        )

                        // Outer border ring
                        drawCircle(
                            color = if (isSelected) ElectricCyan else statusColor,
                            radius = 16.dp.toPx(),
                            center = center,
                            style = Stroke(width = if (isSelected) 2.5f else 1.5f)
                        )

                        // Center solid core
                        drawCircle(
                            color = SynapseSurfaceElevated,
                            radius = 14.dp.toPx(),
                            center = center
                        )

                        // Mastery inner fill indicator
                        drawCircle(
                            color = statusColor.copy(alpha = 0.7f),
                            radius = 14.dp.toPx() * pos.entity.masteryScore.coerceIn(0.1f, 1.0f),
                            center = center
                        )
                    }
                }

                // Legend Overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SynapseSurface.copy(alpha = 0.85f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem("Mastered (≥80%)", MatrixGreen)
                    LegendItem("Learning", CyberAmber)
                    LegendItem("At-Risk (<50%)", ErrorCrimson)
                }
            }

            // Node Inspector Bar
            activeNode?.let { node ->
                val statusColor = when {
                    node.masteryScore >= 0.8f -> MatrixGreen
                    node.masteryScore >= 0.5f -> CyberAmber
                    else -> ErrorCrimson
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SynapseSurfaceElevated)
                        .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = node.subtopic,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${node.topic} • Prereq: ${node.prerequisiteNodeId ?: "Root Foundation"}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Text(
                            text = "${(node.masteryScore * 100).toInt()}% Mastery",
                            color = statusColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = { onLaunchRemedial(node) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .testTag("launch_node_drill_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Remedial Drill",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Launch Edge Remedial Drill for ${node.subtopic}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
