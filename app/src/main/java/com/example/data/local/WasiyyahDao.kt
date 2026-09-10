package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AuditLogEntry
import com.example.data.model.DebtItem
import com.example.data.model.GuardianKeyShare
import com.example.data.model.PulseSettings
import com.example.data.model.SpecialRightItem
import com.example.data.model.TrustDepositItem
import com.example.data.model.WasiyyahDocument
import kotlinx.coroutines.flow.Flow

@Dao
interface WasiyyahDao {
    // Wasiyyah Document
    @Query("SELECT * FROM wasiyyah_document WHERE id = 1 LIMIT 1")
    fun getDocument(): Flow<WasiyyahDocument?>

    @Query("SELECT * FROM wasiyyah_document WHERE id = 1 LIMIT 1")
    suspend fun getDocumentDirect(): WasiyyahDocument?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDocument(doc: WasiyyahDocument)

    // Debts
    @Query("SELECT * FROM debts ORDER BY id DESC")
    fun getAllDebts(): Flow<List<DebtItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtItem)

    @Update
    suspend fun updateDebt(debt: DebtItem)

    @Query("DELETE FROM debts WHERE id = :id")
    suspend fun deleteDebt(id: Int)

    // Trusts
    @Query("SELECT * FROM trust_deposits ORDER BY id DESC")
    fun getAllTrusts(): Flow<List<TrustDepositItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrust(trust: TrustDepositItem)

    @Update
    suspend fun updateTrust(trust: TrustDepositItem)

    @Query("DELETE FROM trust_deposits WHERE id = :id")
    suspend fun deleteTrust(id: Int)

    // Special Rights
    @Query("SELECT * FROM special_rights ORDER BY id DESC")
    fun getAllSpecialRights(): Flow<List<SpecialRightItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecialRight(item: SpecialRightItem)

    @Update
    suspend fun updateSpecialRight(item: SpecialRightItem)

    @Query("DELETE FROM special_rights WHERE id = :id")
    suspend fun deleteSpecialRight(id: Int)

    // Guardians & Key Shares
    @Query("SELECT * FROM guardian_key_shares ORDER BY shareIndex ASC")
    fun getAllGuardians(): Flow<List<GuardianKeyShare>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuardian(guardian: GuardianKeyShare)

    @Update
    suspend fun updateGuardian(guardian: GuardianKeyShare)

    @Query("DELETE FROM guardian_key_shares WHERE id = :id")
    suspend fun deleteGuardian(id: Int)

    @Query("UPDATE guardian_key_shares SET hasConfirmedConsensus = :confirmed WHERE id = :id")
    suspend fun updateGuardianConsensus(id: Int, confirmed: Boolean)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntry)

    // Pulse Settings
    @Query("SELECT * FROM pulse_settings WHERE id = 1 LIMIT 1")
    fun getPulseSettings(): Flow<PulseSettings?>

    @Query("SELECT * FROM pulse_settings WHERE id = 1 LIMIT 1")
    suspend fun getPulseSettingsDirect(): PulseSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePulseSettings(settings: PulseSettings)
}
