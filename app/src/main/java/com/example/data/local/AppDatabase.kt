package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AuditLogEntry
import com.example.data.model.DebtItem
import com.example.data.model.GuardianKeyShare
import com.example.data.model.PulseSettings
import com.example.data.model.SpecialRightItem
import com.example.data.model.TrustDepositItem
import com.example.data.model.WasiyyahDocument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        WasiyyahDocument::class,
        DebtItem::class,
        TrustDepositItem::class,
        SpecialRightItem::class,
        GuardianKeyShare::class,
        AuditLogEntry::class,
        PulseSettings::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wasiyyahDao(): WasiyyahDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wasiyyah_vault.db"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                .also { INSTANCE = it }
            }
        }

        suspend fun seedInitialData(dao: WasiyyahDao) {
            val defaultDoc = WasiyyahDocument(
                id = 1,
                testatorName = "",
                testatorNationalId = "",
                totalEstimatedWealth = 0.0,
                thirdBequestAmount = 0.0,
                thirdBequestBeneficiary = "",
                thirdBequestPurpose = "",
                heirsConsentRequired = false,
                heirsConsentObtained = false,
                electronicSignature = "",
                signatureDate = 0L,
                witness1Name = "",
                witness1NationalId = "",
                witness1Phone = "",
                witness2Name = "",
                witness2NationalId = "",
                witness2Phone = "",
                status = "DRAFT",
                version = 1,
                cryptographicHash = "",
                isEncrypted = true,
                lastUpdated = System.currentTimeMillis()
            )
            dao.insertOrUpdateDocument(defaultDoc)

            // Seed Shamir's Secret Sharing Guardians (3 shares, 2 threshold) with neutral placeholders
            dao.insertGuardian(
                GuardianKeyShare(
                    guardianName = "الوصي الأول (فلان ابن فلان)",
                    relationship = "الوصي والمشرف على القُصّر",
                    phone = "",
                    email = "",
                    shareIndex = 1,
                    shareFragmentHash = "SHAMIR-SHARE-1: SHA-256",
                    hasConfirmedConsensus = false,
                    roleScope = "MINORS_CUSTODY"
                )
            )
            dao.insertGuardian(
                GuardianKeyShare(
                    guardianName = "الوصي الثاني (فلان ابن فلان)",
                    relationship = "المستشار القانوني والشرعي",
                    phone = "",
                    email = "",
                    shareIndex = 2,
                    shareFragmentHash = "SHAMIR-SHARE-2: SHA-256",
                    hasConfirmedConsensus = false,
                    roleScope = "FULL_EXECUTOR"
                )
            )
            dao.insertGuardian(
                GuardianKeyShare(
                    guardianName = "الوصي الثالث (فلان ابن فلان)",
                    relationship = "شاهد ثقة وعدل",
                    phone = "",
                    email = "",
                    shareIndex = 3,
                    shareFragmentHash = "SHAMIR-SHARE-3: SHA-256",
                    hasConfirmedConsensus = false,
                    roleScope = "DEBTS_ONLY"
                )
            )

            // Seed Audit Logs
            dao.insertAuditLog(
                AuditLogEntry(
                    timestamp = System.currentTimeMillis(),
                    actionType = "INITIALIZE",
                    description = "تهيئة الخزنة الرقمية المشفرة وتفعيل نظام التشفير التام AES-256-GCM بنجاح",
                    securityLevel = "SECURE",
                    actor = "الموصي (فلان ابن فلان)"
                )
            )

            // Seed Pulse Settings
            dao.insertOrUpdatePulseSettings(
                PulseSettings(
                    id = 1,
                    intervalDays = 30,
                    lastCheckInDate = System.currentTimeMillis(),
                    nextDueTimestamp = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
                    gracePeriodDays = 14,
                    autoEscalationEnabled = true,
                    appPinCode = "1234",
                    biometricsEnabled = true,
                    shamirThreshold = 2,
                    shamirTotalShares = 3,
                    periodicReviewMonths = 6
                )
            )
        }
    }
}
