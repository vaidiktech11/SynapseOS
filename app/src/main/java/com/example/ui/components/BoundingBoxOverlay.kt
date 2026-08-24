package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edgeai.BoundingBox
import com.example.edgeai.StepDiagnosis
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ErrorCrimson
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.SynapseBackground

@Composable
fun BoundingBoxOverlay(
    steps: List<StepDiagnosis>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val infiniteTransition = rememberInfiniteTransition(label = "hud_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        for (step in steps) {
            val box = step.boundingBox
            val left = box.xMin * width
            val top = box.yMin * height
            val right = box.xMax * width
            val bottom = box.yMax * height
            val boxWidth = right - left
            val boxHeight = bottom - top

            val boxColor = if (box.isError) ErrorCrimson else MatrixGreen
            val strokeColor = if (box.isError) boxColor.copy(alpha = pulseAlpha) else ElectricCyan.copy(alpha = 0.8f)

            // Draw bounding box rectangle
            drawRoundRect(
                color = boxColor.copy(alpha = if (box.isError) 0.18f else 0.08f),
                topLeft = Offset(left, top),
                size = Size(boxWidth, boxHeight),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Draw bounding box border
            drawRoundRect(
                color = strokeColor,
                topLeft = Offset(left, top),
                size = Size(boxWidth, boxHeight),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                style = Stroke(
                    width = if (box.isError) 2.5.dp.toPx() else 1.5.dp.toPx(),
                    pathEffect = if (box.isError) null else PathEffect.dashPathEffect(floatArrayOf(16f, 8f), 0f)
                )
            )

            // Draw Tag Label Pill
            val labelText = "${box.label} (${(step.confidence * 100).toInt()}%)"
            val textLayoutResult = textMeasurer.measure(
                text = AnnotatedString(labelText),
                style = TextStyle(
                    color = SynapseBackground,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )

            val pillPadding = 4.dp.toPx()
            val pillWidth = textLayoutResult.size.width + pillPadding * 2
            val pillHeight = textLayoutResult.size.height + pillPadding

            drawRoundRect(
                color = strokeColor,
                topLeft = Offset(left + 6.dp.toPx(), top - pillHeight / 2),
                size = Size(pillWidth, pillHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(left + 6.dp.toPx() + pillPadding, top - pillHeight / 2 + pillPadding / 2)
            )
        }
    }
}
