package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ConceptNodeEntity
import com.example.data.local.entities.LearningGapEntity
import com.example.data.local.entities.StudySessionEntity
import com.example.data.local.entities.TelemetryLogEntity
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getActiveUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getActiveUserDirect(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY started_at DESC")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): StudySessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity)

    @Delete
    suspend fun deleteSession(session: StudySessionEntity)
}

@Dao
interface ConceptNodeDao {
    @Query("SELECT * FROM concept_nodes ORDER BY subject ASC, topic ASC")
    fun getAllConceptNodes(): Flow<List<ConceptNodeEntity>>

    @Query("SELECT * FROM concept_nodes WHERE subject = :subject")
    fun getNodesBySubject(subject: String): Flow<List<ConceptNodeEntity>>

    @Query("SELECT * FROM concept_nodes WHERE id = :id LIMIT 1")
    suspend fun getNodeById(id: String): ConceptNodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNode(node: ConceptNodeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNodes(nodes: List<ConceptNodeEntity>)

    @Update
    suspend fun updateNode(node: ConceptNodeEntity)
}

@Dao
interface LearningGapDao {
    @Query("SELECT * FROM learning_gaps ORDER BY timestamp DESC")
    fun getAllLearningGaps(): Flow<List<LearningGapEntity>>

    @Query("SELECT * FROM learning_gaps WHERE session_id = :sessionId")
    fun getGapsForSession(sessionId: String): Flow<List<LearningGapEntity>>

    @Query("SELECT * FROM learning_gaps WHERE concept_node_id = :nodeId")
    fun getGapsForNode(nodeId: String): Flow<List<LearningGapEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGap(gap: LearningGapEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGaps(gaps: List<LearningGapEntity>)
}

@Dao
interface TelemetryLogDao {
    @Query("SELECT * FROM telemetry_logs ORDER BY id DESC")
    fun getAllLogs(): Flow<List<TelemetryLogEntity>>

    @Query("SELECT * FROM telemetry_logs WHERE session_id = :sessionId LIMIT 1")
    suspend fun getTelemetryForSession(sessionId: String): TelemetryLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: TelemetryLogEntity)
}
