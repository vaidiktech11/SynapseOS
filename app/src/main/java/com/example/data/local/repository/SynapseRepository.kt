package com.example.data.local.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.ConceptNodeEntity
import com.example.data.local.entities.GapType
import com.example.data.local.entities.LearningGapEntity
import com.example.data.local.entities.SessionType
import com.example.data.local.entities.StudySessionEntity
import com.example.data.local.entities.TelemetryLogEntity
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class SynapseRepository(private val database: AppDatabase) {

    val activeUser: Flow<UserEntity?> = database.userDao().getActiveUser()
    val allSessions: Flow<List<StudySessionEntity>> = database.studySessionDao().getAllSessions()
    val allConceptNodes: Flow<List<ConceptNodeEntity>> = database.conceptNodeDao().getAllConceptNodes()
    val allLearningGaps: Flow<List<LearningGapEntity>> = database.learningGapDao().getAllLearningGaps()
    val allTelemetryLogs: Flow<List<TelemetryLogEntity>> = database.telemetryLogDao().getAllLogs()

    suspend fun saveUser(user: UserEntity) = database.userDao().insertUser(user)
    
    suspend fun createStudySession(
        title: String,
        sessionType: SessionType,
        summary: String,
        subject: String,
        durationSeconds: Int = 0
    ): StudySessionEntity {
        val session = StudySessionEntity(
            id = UUID.randomUUID().toString(),
            userId = "user_default_iqoo",
            sessionType = sessionType,
            startedAt = System.currentTimeMillis(),
            durationSeconds = durationSeconds,
            title = title,
            summary = summary,
            subjectTag = subject
        )
        database.studySessionDao().insertSession(session)
        return session
    }

    suspend fun recordLearningGap(
        sessionId: String,
        conceptNodeId: String,
        gapType: GapType,
        errorStepIndex: Int,
        confidenceScore: Float,
        description: String,
        solutionFix: String
    ): LearningGapEntity {
        val gap = LearningGapEntity(
            sessionId = sessionId,
            conceptNodeId = conceptNodeId,
            gapType = gapType,
            errorStepIndex = errorStepIndex,
            confidenceScore = confidenceScore,
            description = description,
            solutionFix = solutionFix
        )
        database.learningGapDao().insertGap(gap)

        // Adjust node mastery score based on error detection
        database.conceptNodeDao().getNodeById(conceptNodeId)?.let { node ->
            val penalty = if (gapType == GapType.KNOWLEDGE_GAP) 0.12f else 0.06f
            val updatedMastery = (node.masteryScore - penalty).coerceIn(0.1f, 1.0f)
            val updatedStatus = if (updatedMastery < 0.6f) "AT_RISK" else if (updatedMastery > 0.85f) "MASTERED" else "IN_PROGRESS"
            database.conceptNodeDao().updateNode(
                node.copy(masteryScore = updatedMastery, status = updatedStatus)
            )
        }
        return gap
    }

    suspend fun updateNodeMastery(nodeId: String, newScore: Float) {
        database.conceptNodeDao().getNodeById(nodeId)?.let { node ->
            val status = if (newScore < 0.6f) "AT_RISK" else if (newScore > 0.85f) "MASTERED" else "IN_PROGRESS"
            database.conceptNodeDao().updateNode(node.copy(masteryScore = newScore.coerceIn(0f, 1f), status = status))
        }
    }

    suspend fun recordTelemetry(
        sessionId: String,
        timeToSolveMs: Long,
        strokeCount: Int,
        erasureFrequency: Float,
        eyeStrainScore: Float,
        npuLatencyMs: Long,
        cognitiveLoadIndex: Float
    ) {
        val log = TelemetryLogEntity(
            sessionId = sessionId,
            timeToSolveMs = timeToSolveMs,
            strokeCount = strokeCount,
            erasureFrequency = erasureFrequency,
            eyeStrainScore = eyeStrainScore,
            npuLatencyMs = npuLatencyMs,
            cognitiveLoadIndex = cognitiveLoadIndex
        )
        database.telemetryLogDao().insertLog(log)
    }

    fun getGapsForSession(sessionId: String): Flow<List<LearningGapEntity>> =
        database.learningGapDao().getGapsForSession(sessionId)

    fun getNodesBySubject(subject: String): Flow<List<ConceptNodeEntity>> =
        database.conceptNodeDao().getNodesBySubject(subject)
}
