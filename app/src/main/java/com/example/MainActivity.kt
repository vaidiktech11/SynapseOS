package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SynapseBottomNav
import com.example.ui.components.SynapseTopBar
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DiagnosticScreen
import com.example.ui.screens.KnowledgeGraphScreen
import com.example.ui.screens.LockScreen
import com.example.ui.screens.NpuTelemetryDialog
import com.example.ui.screens.OfficeKitDialog
import com.example.ui.screens.ScribeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SynapseBackground
import com.example.ui.theme.SynapseSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.SynapseScreen
import com.example.ui.viewmodel.SynapseViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SynapseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SynapseApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SynapseApp(viewModel: SynapseViewModel) {
    val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val npuMetrics by viewModel.npuMetrics.collectAsStateWithLifecycle()
    val studySessions by viewModel.studySessions.collectAsStateWithLifecycle()
    val conceptNodes by viewModel.conceptNodes.collectAsStateWithLifecycle()
    val learningGaps by viewModel.learningGaps.collectAsStateWithLifecycle()

    val showOfficeKitDialog by viewModel.showOfficeKitDialog.collectAsStateWithLifecycle()
    val showNpuDialog by viewModel.showNpuDialog.collectAsStateWithLifecycle()
    val pairedDevice by viewModel.pairedDevice.collectAsStateWithLifecycle()
    val syncedFiles by viewModel.syncedFiles.collectAsStateWithLifecycle()
    val isClipboardSync by viewModel.isClipboardSync.collectAsStateWithLifecycle()
    val cloudClusterStatus by viewModel.cloudClusterStatus.collectAsStateWithLifecycle()

    val diagnosticResult by viewModel.activeDiagnosticResult.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzingHandwriting.collectAsStateWithLifecycle()
    val selectedPreset by viewModel.selectedPresetIndex.collectAsStateWithLifecycle()

    val isRecordingAudio by viewModel.isRecordingAudio.collectAsStateWithLifecycle()
    val liveTranscript by viewModel.liveTranscript.collectAsStateWithLifecycle()
    val lectureSummary by viewModel.lectureSummary.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()

    val selectedNode by viewModel.selectedConceptNode.collectAsStateWithLifecycle()
    val isGeneratingWorksheet by viewModel.isGeneratingWorksheet.collectAsStateWithLifecycle()
    val generatedWorksheet by viewModel.generatedWorksheet.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    if (authUiState.isLocked) {
        LockScreen(
            authUiState = authUiState,
            onDigitEntered = { viewModel.onPinDigitEntered(it) },
            onBackspace = { viewModel.onPinBackspace() },
            onBiometricUnlock = { viewModel.onBiometricUnlockSuccess() }
        )
    } else {
        Scaffold(
            topBar = {
                SynapseTopBar(
                    title = "SYNAPSE OS",
                    subtitle = "Snapdragon 8 Elite • Zero-Cloud",
                    npuMetrics = npuMetrics,
                    onOpenOfficeKit = { viewModel.setOfficeKitDialogVisible(true) },
                    onOpenNpuTelemetry = { viewModel.setNpuDialogVisible(true) },
                    onLockApp = { viewModel.lockApp() }
                )
            },
            bottomBar = {
                SynapseBottomNav(
                    currentScreen = currentScreen,
                    onSelectScreen = { viewModel.navigateTo(it) }
                )
            },
            containerColor = SynapseBackground,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    SynapseScreen.DASHBOARD -> DashboardScreen(
                        npuMetrics = npuMetrics,
                        conceptNodes = conceptNodes,
                        studySessions = studySessions,
                        learningGaps = learningGaps,
                        onNavigate = { viewModel.navigateTo(it) },
                        onSelectConceptNode = { viewModel.selectConceptNode(it) },
                        onOpenOfficeKit = { viewModel.setOfficeKitDialogVisible(true) }
                    )

                    SynapseScreen.DIAGNOSTIC_VLM -> DiagnosticScreen(
                        diagnosticResult = diagnosticResult,
                        isAnalyzing = isAnalyzing,
                        selectedPreset = selectedPreset,
                        onSelectPreset = { viewModel.selectDiagnosticPreset(it) },
                        onReAnalyze = { viewModel.analyzeHandwritingSample() },
                        onNavigateToRemedial = {
                            conceptNodes.firstOrNull { it.id == diagnosticResult?.conceptNodeId }?.let {
                                viewModel.selectConceptNode(it)
                            }
                            viewModel.navigateTo(SynapseScreen.KNOWLEDGE_GRAPH)
                        },
                        onCopyLatex = { viewModel.copyToClipboard(it) }
                    )

                    SynapseScreen.LIVE_SCRIBE -> ScribeScreen(
                        isRecording = isRecordingAudio,
                        transcript = liveTranscript,
                        lectureSummary = lectureSummary,
                        selectedLanguage = selectedLanguage,
                        onToggleRecording = { viewModel.toggleAudioRecording() },
                        onSelectLanguage = { viewModel.setSelectedLanguage(it) },
                        onBroadcastToOfficeKit = {
                            lectureSummary?.let { summary ->
                                viewModel.copyToClipboard(summary.markdownNotes)
                                viewModel.showToast("Lecture notes broadcast to paired PC Desktop!")
                            }
                        }
                    )

                    SynapseScreen.KNOWLEDGE_GRAPH -> KnowledgeGraphScreen(
                        conceptNodes = conceptNodes,
                        selectedNode = selectedNode ?: conceptNodes.firstOrNull(),
                        learningGaps = learningGaps,
                        generatedWorksheet = generatedWorksheet,
                        isGeneratingWorksheet = isGeneratingWorksheet,
                        onSelectNode = { viewModel.selectConceptNode(it) },
                        onGenerateWorksheet = { viewModel.generateRemedialWorksheet(it) },
                        onExportWorksheet = { viewModel.exportWorksheetToOfficeKit() }
                    )

                    SynapseScreen.SETTINGS -> SettingsScreen(
                        npuMetrics = npuMetrics,
                        cloudClusterStatus = cloudClusterStatus,
                        onSetDelegate = { viewModel.setNpuDelegate(it) },
                        onTriggerCloudBackup = { viewModel.triggerCloudBackup() },
                        onLockApp = { viewModel.lockApp() }
                    )
                }

                // Toast banner
                AnimatedVisibility(
                    visible = toastMessage != null,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                ) {
                    toastMessage?.let { msg ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SynapseSurfaceElevated)
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ElectricCyan,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            if (showOfficeKitDialog) {
                OfficeKitDialog(
                    pairedDevice = pairedDevice,
                    syncedFiles = syncedFiles,
                    isClipboardSyncEnabled = isClipboardSync,
                    onToggleClipboardSync = { viewModel.toggleClipboardSync(it) },
                    onDismiss = { viewModel.setOfficeKitDialogVisible(false) }
                )
            }

            if (showNpuDialog) {
                NpuTelemetryDialog(
                    npuMetrics = npuMetrics,
                    onDismiss = { viewModel.setNpuDialogVisible(false) }
                )
            }
        }
    }
}
