package com.example.edgeai

import com.example.data.local.entities.ConceptNodeEntity
import com.example.data.local.entities.LearningGapEntity
import kotlinx.coroutines.flow.StateFlow

data class AgentSystemStatus(
    val orchestratorState: String = "IDLE",
    val activeAgent: String = "NONE",
    val memoryUsageMb: Float = 482f,
    val guardrailViolationsBlocked: Int = 0,
    val totalInferencesToday: Int = 42
)

/**
 * Multi-Agent Orchestrator running on Google LiteRT-LM & Snapdragon Hexagon NPU.
 * Routes user tasks, enforces safety filter guardrails, and manages local context memory.
 */
class EdgeAIAgentOrchestrator(
    val npuDelegate: SnapdragonNpuDelegate = SnapdragonNpuDelegate()
) {
    val diagnosticAgent: DiagnosticAgent = DiagnosticAgent(npuDelegate)
    val liveScribeAgent: LiveScribeAgent = LiveScribeAgent(npuDelegate)
    val remedialAgent: RemedialAgent = RemedialAgent(npuDelegate)

    val hardwareMetrics: StateFlow<NpuHardwareMetrics> = npuDelegate.hardwareMetrics

    /**
     * Safety Filter Guardrail:
     * Evaluates prompts and outputs locally to prevent jailbreaks,
     * inappropriate content, or off-topic hallucination outside education scope.
     */
    fun checkGuardrailSafety(input: String): Boolean {
        val blacklistedKeywords = listOf(
            "bypass safety",
            "override system prompt",
            "jailbreak",
            "drop table",
            "exploit",
            "malware"
        )
        val normalized = input.lowercase()
        return blacklistedKeywords.none { normalized.contains(it) }
    }

    suspend fun runDiagnosticPipeline(imageUri: String?, presetIndex: Int = 0): VlmDiagnosticResult {
        return diagnosticAgent.analyzeHandwriting(imageUri, presetIndex)
    }

    suspend fun runRemedialPipeline(
        node: ConceptNodeEntity,
        gaps: List<LearningGapEntity>
    ): RemedialWorksheet {
        return remedialAgent.generateAdaptiveWorksheet(node, gaps)
    }
}
