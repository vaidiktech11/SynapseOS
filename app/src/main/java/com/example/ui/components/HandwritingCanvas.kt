package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

data class DrawnStroke(
    val path: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val isEraser: Boolean = false
)

@Composable
fun HandwritingCanvas(
    onAnalyzeDrawing: (strokeCount: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strokes = remember { mutableStateListOf<DrawnStroke>() }
    val undoneStrokes = remember { mutableStateListOf<DrawnStroke>() }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

    val colors = listOf(
        ElectricCyan,
        NeonViolet,
        MatrixGreen,
        CyberAmber,
        Color.White
    )
    var selectedColor by remember { mutableStateOf(ElectricCyan) }
    var strokeWidth by remember { mutableFloatStateOf(4f) }
    var isEraser by remember { mutableStateOf(false) }
    var showGrid by remember { mutableStateOf(true) }

    CyberCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("handwriting_canvas_card"),
        borderColor = ElectricCyan.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Bar
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
                            .background(ElectricCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Brush,
                            contentDescription = "Handwriting Stylus",
                            tint = ElectricCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Interactive Stylus / Touch Canvas",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Draw equations, integrals, or circuit diagrams",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                PulseBadge(
                    text = if (strokes.isEmpty()) "Canvas Empty" else "${strokes.size} Strokes",
                    color = if (strokes.isEmpty()) TextMuted else MatrixGreen
                )
            }

            // Canvas Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SynapseSurfaceElevated)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color Palettes
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (selectedColor == color && !isEraser) 2.dp else 0.dp,
                                    color = if (selectedColor == color && !isEraser) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .testTag("color_picker_${color.value}")
                                .clickable {
                                    selectedColor = color
                                    isEraser = false
                                }
                        )
                    }
                }

                // Action controls: Eraser, Grid, Undo, Redo, Clear
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isEraser = !isEraser },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("canvas_eraser_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Eraser Mode",
                            tint = if (isEraser) CyberAmber else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { showGrid = !showGrid },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("canvas_grid_toggle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Toggle Grid",
                            tint = if (showGrid) ElectricCyan else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (strokes.isNotEmpty()) {
                                val last = strokes.removeAt(strokes.lastIndex)
                                undoneStrokes.add(last)
                            }
                        },
                        enabled = strokes.isNotEmpty(),
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("canvas_undo_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Undo Stroke",
                            tint = if (strokes.isNotEmpty()) TextSecondary else TextMuted.copy(alpha = 0.3f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (undoneStrokes.isNotEmpty()) {
                                val redo = undoneStrokes.removeAt(undoneStrokes.lastIndex)
                                strokes.add(redo)
                            }
                        },
                        enabled = undoneStrokes.isNotEmpty(),
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("canvas_redo_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Redo,
                            contentDescription = "Redo Stroke",
                            tint = if (undoneStrokes.isNotEmpty()) TextSecondary else TextMuted.copy(alpha = 0.3f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            strokes.clear()
                            undoneStrokes.clear()
                        },
                        enabled = strokes.isNotEmpty(),
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("canvas_clear_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Canvas",
                            tint = if (strokes.isNotEmpty()) ErrorCrimson else TextMuted.copy(alpha = 0.3f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Stroke Width Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Width: ${strokeWidth.toInt()}px",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.width(68.dp)
                )
                Slider(
                    value = strokeWidth,
                    onValueChange = { strokeWidth = it },
                    valueRange = 2f..14f,
                    steps = 5,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricCyan,
                        activeTrackColor = ElectricCyan,
                        inactiveTrackColor = SynapseBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("canvas_stroke_slider")
                )
            }

            // Drawing Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SynapseBackground)
                    .border(1.dp, SynapseBorder, RoundedCornerShape(10.dp))
                    .pointerInput(selectedColor, strokeWidth, isEraser) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                undoneStrokes.clear()
                                currentPoints = listOf(offset)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentPoints = currentPoints + change.position
                            },
                            onDragEnd = {
                                if (currentPoints.isNotEmpty()) {
                                    strokes.add(
                                        DrawnStroke(
                                            path = currentPoints,
                                            color = if (isEraser) SynapseBackground else selectedColor,
                                            strokeWidth = if (isEraser) strokeWidth * 3f else strokeWidth,
                                            isEraser = isEraser
                                        )
                                    )
                                    currentPoints = emptyList()
                                }
                            },
                            onDragCancel = {
                                currentPoints = emptyList()
                            }
                        )
                    }
                    .testTag("interactive_drawing_pad")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw optional subtle grid lines
                    if (showGrid) {
                        val step = 24.dp.toPx()
                        val gridColor = Color(0xFF142030).copy(alpha = 0.5f)
                        var x = 0f
                        while (x < size.width) {
                            drawLine(
                                color = gridColor,
                                start = Offset(x, 0f),
                                end = Offset(x, size.height),
                                strokeWidth = 0.6f
                            )
                            x += step
                        }
                        var y = 0f
                        while (y < size.height) {
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 0.6f
                            )
                            y += step
                        }
                    }

                    // Render committed strokes
                    strokes.forEach { stroke ->
                        if (stroke.path.size > 1) {
                            val path = Path().apply {
                                moveTo(stroke.path.first().x, stroke.path.first().y)
                                for (i in 1 until stroke.path.size) {
                                    lineTo(stroke.path[i].x, stroke.path[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = stroke.color,
                                style = Stroke(
                                    width = stroke.strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        } else if (stroke.path.size == 1) {
                            drawCircle(
                                color = stroke.color,
                                radius = stroke.strokeWidth / 2,
                                center = stroke.path.first()
                            )
                        }
                    }

                    // Render in-progress stroke
                    if (currentPoints.size > 1) {
                        val path = Path().apply {
                            moveTo(currentPoints.first().x, currentPoints.first().y)
                            for (i in 1 until currentPoints.size) {
                                lineTo(currentPoints[i].x, currentPoints[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = if (isEraser) SynapseBackground else selectedColor,
                            style = Stroke(
                                width = if (isEraser) strokeWidth * 3f else strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                if (strokes.isEmpty() && currentPoints.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✍️ Write equation here using finger or stylus...",
                            color = TextMuted.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Analyze Canvas Button
            Button(
                onClick = { onAnalyzeDrawing(strokes.size) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("analyze_canvas_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoFixHigh,
                    contentDescription = "Analyze Handwriting",
                    tint = SynapseBackground,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (strokes.isEmpty()) "Analyze Standard Template" else "Run VLM on Stylus Strokes (${strokes.size})",
                    color = SynapseBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
