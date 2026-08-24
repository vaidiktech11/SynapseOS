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
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edgeai.LectureSummary
import com.example.edgeai.LiveTranscriptChunk
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.CyberCard
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
fun ScribeScreen(
    isRecording: Boolean,
    transcript: List<LiveTranscriptChunk>,
    lectureSummary: LectureSummary?,
    selectedLanguage: String,
    onToggleRecording: () -> Unit,
    onSelectLanguage: (String) -> Unit,
    onBroadcastToOfficeKit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val languages = listOf("en-US", "hi-IN", "es-ES", "zh-CN", "de-DE", "ja-JP")
    var searchQuery by remember { mutableStateOf("") }
    var playbackPace by remember { mutableStateOf("1.0x") }
    val paces = listOf("0.75x", "1.0x", "1.25x", "1.5x")

    val filteredTranscript = remember(transcript, searchQuery) {
        if (searchQuery.isBlank()) transcript
        else transcript.filter {
            it.text.contains(searchQuery, ignoreCase = true) ||
            it.speaker.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SynapseBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Audio Stream Header & Waveform HUD
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "FOCUS FLOW: LIVE AUDIO SCRIBE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = NeonViolet,
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Text(
                            text = "Whisper.cpp Tiny streaming on Hexagon NPU",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                        )
                    }

                    PulseBadge(
                        text = if (isRecording) "RECORDING (LOCAL NPU)" else "STANDBY",
                        color = if (isRecording) MatrixGreen else CyberAmber
                    )
                }

                // Waveform Canvas Visualizer
                AudioWaveformVisualizer(isRecording = isRecording)
            }
        }

        // Action Controls & Language Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onToggleRecording,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) ErrorCrimson else ElectricCyan,
                        contentColor = SynapseBackground
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("toggle_scribe_recording_button")
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isRecording) "Stop" else "Start Scribe",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRecording) "Synthesize Notes" else "Start Lecture Scribe",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        }

        // Language Pills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = "Language",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(languages) { lang ->
                        val isSelected = selectedLanguage == lang
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) NeonViolet else SynapseSurfaceVariant)
                                .border(1.dp, if (isSelected) NeonViolet else SynapseBorder, RoundedCornerShape(6.dp))
                                .clickable { onSelectLanguage(lang) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = lang,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        }

        // Real-Time Streaming Transcript Card
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STREAMING TRANSCRIPTION BUFFER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ElectricCyan,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    paces.forEach { pace ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (playbackPace == pace) ElectricCyan else SynapseSurfaceElevated)
                                .clickable { playbackPace = pace }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = pace,
                                color = if (playbackPace == pace) SynapseBackground else TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            if (transcript.isEmpty()) {
                CyberCard {
                    Text(
                        text = "Press 'Start Lecture Scribe' to capture classroom acoustics. Transcription runs 100% locally with zero cloud streaming.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (chunk in filteredTranscript) {
                        TranscriptBubble(chunk = chunk)
                    }
                }
            }
        }

        // Synthesized Lecture Keynotes Card (Phi-4 Reasoning output)
        if (lectureSummary != null) {
            item {
                Text(
                    text = "SYNTHESIZED LECTURE KEYNOTES & FORMULAS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyberAmber,
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
                                text = lectureSummary.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            PulseBadge(text = "OFFICE KIT READY", color = MatrixGreen)
                        }

                        // Extracted Formulas
                        Text(
                            text = "Extracted Core Formulas:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ElectricCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        for (formula in lectureSummary.extractedFormulas) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SynapseSurfaceElevated)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = formula,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Key Takeaways
                        Text(
                            text = "Key Takeaways:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberAmber,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        for (takeaway in lectureSummary.keyTakeaways) {
                            Text(
                                text = "• $takeaway",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = onBroadcastToOfficeKit,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SynapseSurfaceVariant,
                                contentColor = ElectricCyan
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("sync_lecture_notes_office_kit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CastConnected,
                                contentDescription = "Sync to PC",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Broadcast Notes to Paired PC Desktop",
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

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun TranscriptBubble(chunk: LiveTranscriptChunk) {
    CyberCard(
        backgroundColor = if (chunk.isFormula) SynapseSurfaceElevated else SynapseSurface
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chunk.speaker,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (chunk.speaker.contains("Prof")) NeonViolet else ElectricCyan,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "+${chunk.timestampSeconds.toInt()}s",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                )
            }
            Text(
                text = chunk.text,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimary,
                    fontSize = 12.sp
                )
            )
        }
    }
}
