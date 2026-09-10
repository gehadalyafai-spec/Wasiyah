package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.WasiyyahDao
import com.example.data.model.AuditLogEntry
import com.example.data.model.DebtItem
import com.example.data.model.GuardianKeyShare
import com.example.data.model.PulseSettings
import com.example.data.model.SpecialRightItem
import com.example.data.model.TrustDepositItem
import com.example.data.model.WasiyyahDocument
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.security.MessageDigest

class WasiyyahRepository(private val dao: WasiyyahDao) {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (dao.getDocumentDirect() == null) {
                    AppDatabase.seedInitialData(dao)
                    Log.i("WasiyyahRepository", "Initial seed data loaded successfully.")
                }
            } catch (e: Exception) {
                Log.e("WasiyyahRepository", "Failed to seed default data safely", e)
            }
        }
    }

    val document: Flow<WasiyyahDocument?> = dao.getDocument()
    val debts: Flow<List<DebtItem>> = dao.getAllDebts()
    val trusts: Flow<List<TrustDepositItem>> = dao.getAllTrusts()
    val specialRights: Flow<List<SpecialRightItem>> = dao.getAllSpecialRights()
    val guardians: Flow<List<GuardianKeyShare>> = dao.getAllGuardians()
    val auditLogs: Flow<List<AuditLogEntry>> = dao.getAllAuditLogs()
    val pulseSettings: Flow<PulseSettings?> = dao.getPulseSettings()

    suspend fun saveDocument(doc: WasiyyahDocument) {
        val newHash = calculateHash(doc.testatorName + doc.testatorNationalId + doc.totalEstimatedWealth + doc.thirdBequestAmount + System.currentTimeMillis())
        val updated = doc.copy(
            cryptographicHash = "SHA-256: $newHash",
            lastUpdated = System.currentTimeMillis()
        )
        dao.insertOrUpdateDocument(updated)
        dao.insertAuditLog(
            AuditLogEntry(
                actionType = "EDIT",
                description = "تم تحديث محتوى وصية الإصدار #${updated.version} بنجاح",
                securityLevel = "INFO",
                actor = "الموصي"
            )
        )
    }

    suspend fun signAndSealDocument(signatureName: String, w1Name: String, w1Id: String, w1Phone: String, w2Name: String, w2Id: String, w2Phone: String) {
        val current = dao.getDocumentDirect() ?: WasiyyahDocument()
        val newVersion = current.version + 1
        val newHash = calculateHash(signatureName + w1Name + w2Name + System.currentTimeMillis())
        val sealed = current.copy(
            electronicSignature = signatureName,
            signatureDate = System.currentTimeMillis(),
            witness1Name = w1Name,
            witness1NationalId = w1Id,
            witness1Phone = w1Phone,
            witness2Name = w2Name,
            witness2NationalId = w2Id,
            witness2Phone = w2Phone,
            status = "SIGNED_SEALED",
            version = newVersion,
            cryptographicHash = "SHA-256: $newHash",
            lastUpdated = System.currentTimeMillis()
        )
        dao.insertOrUpdateDocument(sealed)
        dao.insertAuditLog(
            AuditLogEntry(
                actionType = "SEAL",
                description = "تم توثيق وتوقيع الوصية وتوليد التشفير الرقمي للإصدار #$newVersion بحضور الشاهدين",
                securityLevel = "SECURE",
                actor = "الموصي وشاهدا العدل"
            )
        )
    }

    suspend fun recordPulseCheckIn() {
        val current = dao.getPulseSettingsDirect() ?: PulseSettings()
        val nextDue = System.currentTimeMillis() + (current.intervalDays.toLong() * 24 * 60 * 60 * 1000)
        val updated = current.copy(
            lastCheckInDate = System.currentTimeMillis(),
            nextDueTimestamp = nextDue
        )
        dao.insertOrUpdatePulseSettings(updated)
        dao.insertAuditLog(
            AuditLogEntry(
                actionType = "PULSE_CHECKIN",
                description = "تم تأكيد نبض الحياة (Check-in) - تم تأكيد سلامة الموصي وتمديد مؤقت الأمان",
                securityLevel = "INFO",
                actor = "الموصي (تحقق حيوي/PIN)"
            )
        )
    }

    suspend fun toggleGuardianConsensus(id: Int, isConfirmed: Boolean) {
        dao.updateGuardianConsensus(id, isConfirmed)
        dao.insertAuditLog(
            AuditLogEntry(
                actionType = "KEY_SIMULATION",
                description = if (isConfirmed) "قام الوصي بإدخال حصة المفتاح الخاص به لتأكيد الوفاة" else "تم إلغاء تأكيد حصة الوصي",
                securityLevel = if (isConfirmed) "WARNING" else "INFO",
                actor = "محاكي مفاتيح Shamir"
            )
        )
    }

    suspend fun addDebt(debt: DebtItem) {
        dao.insertDebt(debt)
        val typeDesc = if (debt.type == "ON_ME") "دين عليه" else "دين له عند الغير"
        dao.insertAuditLog(
            AuditLogEntry(
                actionType = "EDIT",
                description = "إضافة بند $typeDesc بمبلغ ${debt.amount} ${debt.currency} لصالح/من ${debt.counterpartyName}",
                securityLevel = "INFO"
            )
        )
    }

    suspend fun deleteDebt(id: Int) {
        dao.deleteDebt(id)
        dao.insertAuditLog(
            AuditLogEntry(
                actionType = "EDIT",
                description = "حذف بند دين من السجل",
                securityLevel = "INFO"
            )
        )
    }

    suspend fun addTrust(trust: TrustDepositItem) {
        dao.insertTrust(trust)
        dao.insertAuditLog(
            AuditLogEntry(
                actionType = "EDIT",
                description = "إضافة أمانة/وديعة مسجلة باسم: ${trust.ownerName}",
                securityLevel = "INFO"
            )
        )
    }

    suspend fun deleteTrust(id: Int) {
        dao.deleteTrust(id)
    }

    suspend fun addSpecialRight(right: SpecialRightItem) {
        dao.insertSpecialRight(right)
        dao.insertAuditLog(
            AuditLogEntry(
                actionType = "EDIT",
                description = "إضافة حق شرعي خاص: ${right.title}",
                securityLevel = "INFO"
            )
        )
    }

    suspend fun deleteSpecialRight(id: Int) {
        dao.deleteSpecialRight(id)
    }

    suspend fun addGuardian(guardian: GuardianKeyShare) {
        dao.insertGuardian(guardian)
        dao.insertAuditLog(
            AuditLogEntry(
                actionType = "EDIT",
                description = "تعيين وصي وحارس مفتاح جديد: ${guardian.guardianName} (${guardian.roleScope})",
                securityLevel = "SECURE"
            )
        )
    }

    suspend fun deleteGuardian(id: Int) {
        dao.deleteGuardian(id)
    }

    suspend fun updatePulseSettings(settings: PulseSettings) {
        dao.insertOrUpdatePulseSettings(settings)
    }

    suspend fun logAction(actionType: String, description: String, securityLevel: String = "INFO") {
        dao.insertAuditLog(
            AuditLogEntry(
                actionType = actionType,
                description = description,
                securityLevel = securityLevel
            )
        )
    }

    private fun calculateHash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
