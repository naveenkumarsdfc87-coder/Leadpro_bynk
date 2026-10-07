package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LeadActivityEntity
import com.example.data.model.LeadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LeadDao {
    @Query("SELECT * FROM leads WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE id = :id LIMIT 1")
    fun getLeadById(id: Long): Flow<LeadEntity?>

    @Query("SELECT * FROM leads WHERE id = :id LIMIT 1")
    suspend fun getLeadByIdSync(id: Long): LeadEntity?

    @Query("SELECT * FROM leads WHERE status = :status AND isArchived = 0 ORDER BY createdAt DESC")
    fun getLeadsByStatus(status: String): Flow<List<LeadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeads(leads: List<LeadEntity>): List<Long>

    @Update
    suspend fun updateLead(lead: LeadEntity)

    @Delete
    suspend fun deleteLead(lead: LeadEntity)

    @Query("DELETE FROM leads WHERE id = :id")
    suspend fun deleteLeadById(id: Long)

    @Query("DELETE FROM leads")
    suspend fun deleteAllLeads()

    // Activity Logs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: LeadActivityEntity): Long

    @Query("SELECT * FROM lead_activities WHERE leadId = :leadId ORDER BY timestamp DESC")
    fun getActivitiesForLead(leadId: Long): Flow<List<LeadActivityEntity>>

    @Query("DELETE FROM lead_activities WHERE leadId = :leadId")
    suspend fun deleteActivitiesForLead(leadId: Long)

    @Query("DELETE FROM lead_activities")
    suspend fun deleteAllActivities()
}
