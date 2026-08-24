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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bridge.CloudSyncClusterStatus
import com.example.edgeai.NpuExecutionDelegate
import com.example.edgeai.NpuHardwareMetrics
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
fun SettingsScreen(
    npuMetrics: NpuHardwareMetrics,
    cloudClusterStatus: CloudSyncClusterStatus,
    onSetDelegate: (NpuExecutionDelegate) -> Unit,
    onTriggerCloudBackup: () -> Unit,
    onLockApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SynapseBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Header
        item {
            Column {
                Text(
                    text = "SYSTEM CONTROLS & PRIVACY ENCLAVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MatrixGreen,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Text(
                    text = "Hardware delegates, encryption, and zero-cloud audit",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                )
            }
        }

        // Zero-Cloud Privacy Guarantee Card
        item {
            CyberCard(borderColor = MatrixGreen.copy(alpha = 0.6f)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Zero-Cloud Verified",
                                tint = MatrixGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Zero-Cloud Privacy Enclave",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                        PulseBadge(text = "100% OFFLINE", color = MatrixGreen)
                    }

                    Text(
                        text = "• Local Storage: SQLite DB encrypted with SQLCipher (AES-256-GCM)\n" +
                               "• Edge AI Execution: Hexagon NPU on Snapdragon 8 Elite (No server calls)\n" +
                               "• Office Kit Bridge: Local Wi-Fi Direct P2P to PC (Air-gapped safe)\n" +
                               "• Inactivity Timeout: 15-minute cryptographic session auto-lock",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }

        // Snapdragon Hexagon NPU Delegate Selector
        item {
            Text(
                text = "HEXAGON NPU ACCELERATOR DELEGATE",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = ElectricCyan,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val delegates = listOf(
                    Triple(NpuExecutionDelegate.HEXAGON_HTP_INT4, "Hexagon HTP (W4A16 Quantized - 45 TOPS)", "Fastest (12-16ms) • Lowest Power"),
                    Triple(NpuExecutionDelegate.HEXAGON_HTP_INT8, "Hexagon HTP (INT8 Precision - 32 TOPS)", "Balanced (18-22ms) • High Accuracy"),
                    Triple(NpuExecutionDelegate.QUALCOMM_ADRENO_GPU, "Qualcomm Adreno GPU (OpenCL Delegate)", "Standard (34ms) • 19 TOPS"),
                    Triple(NpuExecutionDelegate.KRYO_CPU_FALLBACK, "Kryo CPU Fallback (Multi-Threaded FP32)", "Compatibility (145ms)")
                )

                for ((del, title, desc) in delegates) {
                    val isSelected = npuMetrics.activeDelegate == del
                    CyberCard(
                        borderColor = if (isSelected) ElectricCyan else SynapseBorder,
                        backgroundColor = if (isSelected) SynapseSurfaceElevated else SynapseSurface,
                        onClick = { onSetDelegate(del) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isSelected) ElectricCyan else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(ElectricCyan)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Encrypted Cloud Mesh Snapshot
        item {
            Text(
                text = "HARDWARE-SEALED BLOB REPLICATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyberAmber,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

            CyberCard(borderColor = CyberAmber.copy(alpha = 0.5f)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Encrypted Blob Sync Service",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        PulseBadge(text = "${cloudClusterStatus.totalBlobsReplicated} SNAPSHOTS", color = CyberAmber)
                    }

                    Text(
                        text = cloudClusterStatus.statusMessage,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )

                    Button(
                        onClick = onTriggerCloudBackup,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SynapseSurfaceVariant,
                            contentColor = CyberAmber
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CyberAmber.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("trigger_encrypted_backup_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Backup",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Seal Snapshot & Replicate (AES-256-GCM)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }
        }

        // Lock App Now Button
        item {
            Button(
                onClick = onLockApp,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorCrimson.copy(alpha = 0.15f),
                    contentColor = ErrorCrimson
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, ErrorCrimson.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("manual_lock_app_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Lock SynapseOS Secure Session",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
