package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ConceptNodeDao
import com.example.data.local.dao.LearningGapDao
import com.example.data.local.dao.StudySessionDao
import com.example.data.local.dao.TelemetryLogDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entities.ConceptNodeEntity
import com.example.data.local.entities.GapType
import com.example.data.local.entities.LearningGapEntity
import com.example.data.local.entities.SessionType
import com.example.data.local.entities.StudySessionEntity
import com.example.data.local.entities.TelemetryLogEntity
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromSessionType(value: SessionType): String = value.name

    @TypeConverter
    fun toSessionType(value: String): SessionType = try {
        SessionType.valueOf(value)
    } catch (e: Exception) {
        SessionType.DIAGNOSTIC
    }

    @TypeConverter
    fun fromGapType(value: GapType): String = value.name

    @TypeConverter
    fun toGapType(value: String): GapType = try {
        GapType.valueOf(value)
    } catch (e: Exception) {
        GapType.KNOWLEDGE_GAP
    }
}

@Database(
    entities = [
        UserEntity::class,
        StudySessionEntity::class,
        ConceptNodeEntity::class,
        LearningGapEntity::class,
        TelemetryLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun conceptNodeDao(): ConceptNodeDao
    abstract fun learningGapDao(): LearningGapDao
    abstract fun telemetryLogDao(): TelemetryLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "synapse_os_secure.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate initial knowledge graph nodes
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    seedInitialData(database)
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(database: AppDatabase) {
            val user = UserEntity(
                id = "user_default_iqoo",
                username = "Vaidik (iQOO Elite)",
                hashedPin = "",
                biometricEnabled = true
            )
            database.userDao().insertUser(user)

            // Math Concept Nodes
            val nodeAlgebra = ConceptNodeEntity(
                id = "node_calc_1",
                subject = "Mathematics",
                topic = "Calculus",
                subtopic = "Chain Rule & Derivatives",
                prerequisiteNodeId = null,
                masteryScore = 0.88f,
                status = "MASTERED"
            )
            val nodeIntegration = ConceptNodeEntity(
                id = "node_calc_2",
                subject = "Mathematics",
                topic = "Calculus",
                subtopic = "Integration by Parts",
                prerequisiteNodeId = "node_calc_1",
                masteryScore = 0.54f,
                status = "AT_RISK"
            )
            val nodeDifferential = ConceptNodeEntity(
                id = "node_calc_3",
                subject = "Mathematics",
                topic = "Differential Equations",
                subtopic = "First-Order Linear ODEs",
                prerequisiteNodeId = "node_calc_2",
                masteryScore = 0.72f,
                status = "IN_PROGRESS"
            )
            val nodeLinAlgebra = ConceptNodeEntity(
                id = "node_linalg_1",
                subject = "Mathematics",
                topic = "Linear Algebra",
                subtopic = "Eigenvalues & Eigenvectors",
                prerequisiteNodeId = null,
                masteryScore = 0.95f,
                status = "MASTERED"
            )

            // Physics Concept Nodes
            val nodeMaxwell = ConceptNodeEntity(
                id = "node_phys_1",
                subject = "Physics",
                topic = "Electromagnetism",
                subtopic = "Gauss's Law & Flux",
                prerequisiteNodeId = null,
                masteryScore = 0.65f,
                status = "IN_PROGRESS"
            )
            val nodeFaraday = ConceptNodeEntity(
                id = "node_phys_2",
                subject = "Physics",
                topic = "Electromagnetism",
                subtopic = "Faraday's Induction & Lenz's Law",
                prerequisiteNodeId = "node_phys_1",
                masteryScore = 0.42f,
                status = "AT_RISK"
            )
            val nodeQuantum = ConceptNodeEntity(
                id = "node_phys_3",
                subject = "Physics",
                topic = "Quantum Mechanics",
                subtopic = "Schrödinger Wave Equation",
                prerequisiteNodeId = "node_calc_3",
                masteryScore = 0.78f,
                status = "IN_PROGRESS"
            )

            // CS Concept Nodes
            val nodeNpuEdge = ConceptNodeEntity(
                id = "node_cs_1",
                subject = "Computer Science",
                topic = "Edge AI & Hardware",
                subtopic = "Hexagon NPU Tensor Execution",
                prerequisiteNodeId = null,
                masteryScore = 0.98f,
                status = "MASTERED"
            )

            database.conceptNodeDao().insertNodes(
                listOf(
                    nodeAlgebra,
                    nodeIntegration,
                    nodeDifferential,
                    nodeLinAlgebra,
                    nodeMaxwell,
                    nodeFaraday,
                    nodeQuantum,
                    nodeNpuEdge
                )
            )

            // Initial Sample Diagnostic Session
            val session1 = StudySessionEntity(
                id = "session_demo_math",
                userId = "user_default_iqoo",
                sessionType = SessionType.DIAGNOSTIC,
                startedAt = System.currentTimeMillis() - 3600000,
                durationSeconds = 840,
                title = "Calculus II: Integration by Parts Drill",
                summary = "Step 3 algebraic sign breakdown detected in ∫ x * e^(2x) dx",
                subjectTag = "Mathematics"
            )
            database.studySessionDao().insertSession(session1)

            val gap1 = LearningGapEntity(
                id = "gap_demo_1",
                sessionId = "session_demo_math",
                conceptNodeId = "node_calc_2",
                gapType = GapType.HIDDEN_STRUGGLE,
                errorStepIndex = 3,
                confidenceScore = 0.94f,
                description = "Algebraic sign flip error when integrating v = 1/2 e^(2x). Forgotten coefficient 1/4 in final term.",
                solutionFix = "Recall: ∫ u dv = u*v - ∫ v du. Ensure du = dx and dv = e^(2x)dx => v = (1/2)e^(2x)."
            )
            database.learningGapDao().insertGap(gap1)

            val telemetry1 = TelemetryLogEntity(
                id = "telemetry_demo_1",
                sessionId = "session_demo_math",
                timeToSolveMs = 42000L,
                strokeCount = 184,
                erasureFrequency = 0.22f,
                eyeStrainScore = 0.12f,
                npuLatencyMs = 14L,
                cognitiveLoadIndex = 0.38f
            )
            database.telemetryLogDao().insertLog(telemetry1)
        }
    }
}
