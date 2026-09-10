package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wasiyyah_document")
data class WasiyyahDocument(
    @PrimaryKey val id: Int = 1,
    val testatorName: String = "",
    val testatorNationalId: String = "",
    val testimonyOpening: String = "بسم الله الرحمن الرحيم\nهذا ما أوصى به العبد الفقير إلى ربه ومولاه، وهو يشهد أن لا إله إلا الله وحده لا شريك له، وأن محمداً عبده ورسوله، وأن عيسى عبد الله ورسوله وكلمته ألقاها إلى مريم وروح منه، وأن الجنة حق والنار حق، وأن الساعة آتية لا ريب فيها، وأن الله يبعث من في القبور.\n\nوأوصي أهلي وبنيّ وعشيرتي بتقوى الله العظيم وطاعته، وإصلاح ذات البين، وأن يعتصموا بحبل الله جميعاً ولا يتفرقوا.",
    val spiritualDirectives: String = "أوصي بأن يُغسلني ويُكفنني ويصلي عليّ من أهل السنة والصلاح، وألا تتبع جنازتي بنياحة ولا لطم ولا صراخ ولا بدعة، وأن يُسارع بقضاء ديوني وردّ الأمانات قبل كل شيء، وأن يستغفروا لي ويدعوا لي بالثبات.",
    val totalEstimatedWealth: Double = 0.0,
    val thirdBequestAmount: Double = 0.0,
    val thirdBequestBeneficiary: String = "",
    val thirdBequestPurpose: String = "",
    val heirsConsentRequired: Boolean = false,
    val heirsConsentObtained: Boolean = false,
    val electronicSignature: String = "",
    val signatureDate: Long = 0L,
    val witness1Name: String = "",
    val witness1NationalId: String = "",
    val witness1Phone: String = "",
    val witness2Name: String = "",
    val witness2NationalId: String = "",
    val witness2Phone: String = "",
    val status: String = "DRAFT", // DRAFT, SIGNED_SEALED, ACTIVE_PULSE
    val version: Int = 1,
    val cryptographicHash: String = "",
    val isEncrypted: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "debts")
data class DebtItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "ON_ME" (دين عليّ) or "FOR_ME" (دين لي عند الغير)
    val counterpartyName: String,
    val amount: Double,
    val currency: String = "SAR",
    val dueDate: String,
    val evidenceNotes: String,
    val contactInfo: String = "",
    val isSettled: Boolean = false
)

@Entity(tableName = "trust_deposits")
data class TrustDepositItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ownerName: String,
    val description: String,
    val locationDetails: String,
    val returnInstructions: String,
    val ownerPhone: String = "",
    val isReturned: Boolean = false
)

@Entity(tableName = "special_rights")
data class SpecialRightItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // "GUARDIANSHIP", "VOW", "SPONSORSHIP", "MORAL_TESTIMONY"
    val title: String,
    val details: String,
    val designatedPerson: String,
    val priorityLevel: String = "HIGH"
)

@Entity(tableName = "guardian_key_shares")
data class GuardianKeyShare(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val guardianName: String,
    val relationship: String,
    val phone: String,
    val email: String,
    val shareIndex: Int,
    val shareFragmentHash: String,
    val hasConfirmedConsensus: Boolean = false,
    val roleScope: String = "FULL_EXECUTOR" // "FULL_EXECUTOR", "DEBTS_ONLY", "MINORS_CUSTODY", "THIRD_BEQUEST"
)

@Entity(tableName = "audit_logs")
data class AuditLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String, // "VIEW", "EDIT", "SEAL", "PULSE_CHECKIN", "KEY_SIMULATION", "EXPORT"
    val description: String,
    val securityLevel: String = "INFO", // "INFO", "WARNING", "SECURE"
    val actor: String = "مالك الوصية (الموصي)"
)

@Entity(tableName = "pulse_settings")
data class PulseSettings(
    @PrimaryKey val id: Int = 1,
    val intervalDays: Int = 30,
    val lastCheckInDate: Long = System.currentTimeMillis(),
    val nextDueTimestamp: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
    val gracePeriodDays: Int = 14,
    val autoEscalationEnabled: Boolean = true,
    val appPinCode: String = "1234",
    val biometricsEnabled: Boolean = true,
    val shamirThreshold: Int = 2,
    val shamirTotalShares: Int = 3,
    val periodicReviewMonths: Int = 6
)
