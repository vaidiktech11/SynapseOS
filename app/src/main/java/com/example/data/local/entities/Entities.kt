package com.example.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val username: String = "iQOO Scholar",
    @ColumnInfo(name = "hashed_pin")
    val hashedPin: String = "",
    @ColumnInfo(name = "biometric_enabled")
    val biometricEnabled: Boolean = true,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

enum class SessionType {
    DIAGNOSTIC,
    SCRIBE,
    AR_VISION
}

@Entity(
    tableName = "study_sessions"
)
data class StudySessionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "session_type")
    val sessionType: SessionType = SessionType.DIAGNOSTIC,
    @ColumnInfo(name = "started_at")
    val startedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Int = 0,
    val title: String = "Untitled Session",
    val summary: String = "",
    @ColumnInfo(name = "subject_tag")
    val subjectTag: String = "Mathematics"
)

@Entity(tableName = "concept_nodes")
data class ConceptNodeEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val subject: String,
    val topic: String,
    val subtopic: String,
    @ColumnInfo(name = "prerequisite_node_id")
    val prerequisiteNodeId: String? = null,
    @ColumnInfo(name = "mastery_score")
    val masteryScore: Float = 0.5f, // 0.0 to 1.0
    val status: String = "IN_PROGRESS" // MASTERED, IN_PROGRESS, AT_RISK
)

enum class GapType {
    KNOWLEDGE_GAP,
    HIDDEN_STRUGGLE,
    COGNITIVE_OVERLOAD
}

@Entity(tableName = "learning_gaps")
data class LearningGapEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "session_id")
    val sessionId: String,
    @ColumnInfo(name = "concept_node_id")
    val conceptNodeId: String,
    @ColumnInfo(name = "gap_type")
    val gapType: GapType = GapType.KNOWLEDGE_GAP,
    @ColumnInfo(name = "error_step_index")
    val errorStepIndex: Int = 0,
    @ColumnInfo(name = "confidence_score")
    val confidenceScore: Float = 0.92f,
    val timestamp: Long = System.currentTimeMillis(),
    val description: String = "",
    @ColumnInfo(name = "solution_fix")
    val solutionFix: String = ""
)

@Entity(tableName = "telemetry_logs")
data class TelemetryLogEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "session_id")
    val sessionId: String,
    @ColumnInfo(name = "time_to_solve_ms")
    val timeToSolveMs: Long = 0L,
    @ColumnInfo(name = "stroke_count")
    val strokeCount: Int = 0,
    @ColumnInfo(name = "erasure_frequency")
    val erasureFrequency: Float = 0f,
    @ColumnInfo(name = "eye_strain_score")
    val eyeStrainScore: Float = 0.15f,
    @ColumnInfo(name = "npu_latency_ms")
    val npuLatencyMs: Long = 18L,
    @ColumnInfo(name = "cognitive_load_index")
    val cognitiveLoadIndex: Float = 0.42f
)
