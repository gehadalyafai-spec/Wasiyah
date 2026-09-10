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
                testatorName = "عبدالرحمن بن خالد السعدون",
                testatorNationalId = "1048291048",
                totalEstimatedWealth = 450000.0,
                thirdBequestAmount = 90000.0,
                thirdBequestBeneficiary = "وقف تحفيظ القرآن الكريم ورعاية الأرامل والأيتام",
                thirdBequestPurpose = "بناء سقاية ماء ووقف تعليمي مستدام صدقة جارية عني وعن والديّ",
                heirsConsentRequired = false,
                heirsConsentObtained = false,
                electronicSignature = "عبدالرحمن بن خالد بن سليمان السعدون - توقيع رقمي موثق",
                signatureDate = System.currentTimeMillis() - (15L * 24 * 60 * 60 * 1000),
                witness1Name = "سليمان بن ناصر الخاطر",
                witness1NationalId = "1029481920",
                witness1Phone = "+966504433221",
                witness2Name = "صالح بن فهد التميمي",
                witness2NationalId = "1038592817",
                witness2Phone = "+966556677889",
                status = "ACTIVE_PULSE",
                version = 2,
                cryptographicHash = "SHA-256: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                lastUpdated = System.currentTimeMillis()
            )
            dao.insertOrUpdateDocument(defaultDoc)

            // Seed Debts
            dao.insertDebt(
                DebtItem(
                    type = "ON_ME",
                    counterpartyName = "مؤسسة الوفاء لمواد البناء (م. عمر)",
                    amount = 18500.0,
                    currency = "SAR",
                    dueDate = "1448/04/15 هـ",
                    evidenceNotes = "فاتورة ضريبية وسند لأمر رقم 4091 مسجل بمنصة نافذ",
                    contactInfo = "+966541122334"
                )
            )
            dao.insertDebt(
                DebtItem(
                    type = "ON_ME",
                    counterpartyName = "سعد بن إبراهيم الراجحي",
                    amount = 7000.0,
                    currency = "SAR",
                    dueDate = "عند الميسرة",
                    evidenceNotes = "قرض حسن شخصي مثبت برسائل الواتساب والتحويل البنكي",
                    contactInfo = "+966552233445"
                )
            )
            dao.insertDebt(
                DebtItem(
                    type = "FOR_ME",
                    counterpartyName = "خالد بن منصور العبدلي",
                    amount = 32000.0,
                    currency = "SAR",
                    dueDate = "1448/01/01 هـ",
                    evidenceNotes = "عقد شراكة تجارية منتهي وإقرار خطي موثق وموقع من الشاهدين",
                    contactInfo = "+966509988776"
                )
            )

            // Seed Trusts
            dao.insertTrust(
                TrustDepositItem(
                    ownerName = "أبو عبدالله طارق الشمري",
                    description = "صك ملكية مزرعة الأصالة بمنطقة سدير وصندوق حديدي صغير مختوم",
                    locationDetails = "الخزنة المنزلية الرئيسية - الرف العلوي الرمز السري لدى الوصي الأول",
                    returnInstructions = "يُسلم له شخصياً بموجب البطاقة الوطنية، أو لورثته الشرعيين في حال وفاته",
                    ownerPhone = "+966567788990"
                )
            )
            dao.insertTrust(
                TrustDepositItem(
                    ownerName = "أخي فهد السعدون",
                    description = "ساعة رولكس تراثية موروثة عن الوالد رحمه الله أمانة لحين تخرج ابنه",
                    locationDetails = "درج المكتب الخاص المغلق بالمفتاح الفضي",
                    returnInstructions = "تُسلم لابنه عمر عند بلوغه سن الرشد أو تسليمها لفهد مباشرة",
                    ownerPhone = "+966503344556"
                )
            )

            // Seed Special Rights
            dao.insertSpecialRight(
                SpecialRightItem(
                    category = "GUARDIANSHIP",
                    title = "الوصاية على أبنائي القُصّر (يوسف ومريم)",
                    details = "أوصي بأن يكون أخي الشقيق فهد وصياً ومربياً لأولادي القُصّر، يعتني بدينهم ودنياهم وحفظ أموالهم بما يرضي الله حتى يبلغوا أشدهم ورشدهم.",
                    designatedPerson = "فهد بن خالد السعدون (العم الشقيق)",
                    priorityLevel = "HIGH"
                )
            )
            dao.insertSpecialRight(
                SpecialRightItem(
                    category = "SPONSORSHIP",
                    title = "استمرار كفالة اليتيم (أنس) في جمعية إنسان",
                    details = "أوصي باستقطاع مبلغ الكفالة السنوي (3,600 ريال) من غلة الوقف أو ريع التركة لمدة 5 سنوات قادمة وفاءً بالعهد.",
                    designatedPerson = "ناظر الوقف / الوصي المختار",
                    priorityLevel = "MEDIUM"
                )
            )
            dao.insertSpecialRight(
                SpecialRightItem(
                    category = "VOW",
                    title = "نذر صيام 3 أيام وإطعام عشرة مساكين",
                    details = "عليّ نذر طاعة لله تعالى كفارة يمين ونذر صيام، أرجو من ورثتي قضاء الصيام عني وإخراج الكفارة من مالي قبل قسمة التركة.",
                    designatedPerson = "الورثة الشرعيون",
                    priorityLevel = "HIGH"
                )
            )

            // Seed Shamir's Secret Sharing Guardians (3 shares, 2 threshold)
            dao.insertGuardian(
                GuardianKeyShare(
                    guardianName = "فهد بن خالد السعدون",
                    relationship = "شقيق الموصي (الوصي الأول والمشرف على القُصّر)",
                    phone = "+966503344556",
                    email = "fahad.saadoun@email.com",
                    shareIndex = 1,
                    shareFragmentHash = "SHAMIR-SHARE-1: 9f82c...b31e",
                    hasConfirmedConsensus = false,
                    roleScope = "MINORS_CUSTODY"
                )
            )
            dao.insertGuardian(
                GuardianKeyShare(
                    guardianName = "المحامي د. عادل بن عبدالعزيز المقرن",
                    relationship = "المستشار القانوني والشرعي المعتمد",
                    phone = "+966540099887",
                    email = "adel.almuqrin.law@firm.com",
                    shareIndex = 2,
                    shareFragmentHash = "SHAMIR-SHARE-2: 4a71d...e88c",
                    hasConfirmedConsensus = false,
                    roleScope = "FULL_EXECUTOR"
                )
            )
            dao.insertGuardian(
                GuardianKeyShare(
                    guardianName = "سليمان بن ناصر الخاطر",
                    relationship = "صديق ونسيب ثقة وشاهد عدل",
                    phone = "+966504433221",
                    email = "sulaiman.khater@email.com",
                    shareIndex = 3,
                    shareFragmentHash = "SHAMIR-SHARE-3: 2c19f...d40a",
                    hasConfirmedConsensus = false,
                    roleScope = "DEBTS_ONLY"
                )
            )

            // Seed Audit Logs
            dao.insertAuditLog(
                AuditLogEntry(
                    timestamp = System.currentTimeMillis() - (15L * 24 * 60 * 60 * 1000),
                    actionType = "SEAL",
                    description = "تم توقيع الوصية وتوليد بصمة الهاش المشفرة وتوزيع حصص Shamir للأوصياء الثلاثة",
                    securityLevel = "SECURE",
                    actor = "الموصي (عبدالرحمن السعدون)"
                )
            )
            dao.insertAuditLog(
                AuditLogEntry(
                    timestamp = System.currentTimeMillis() - (2L * 24 * 60 * 60 * 1000),
                    actionType = "PULSE_CHECKIN",
                    description = "تم تجديد إشارة نبض الحياة (Check-in) بنجاح - حالة الموصي نشطة وبخير",
                    securityLevel = "INFO",
                    actor = "الموصي (المصادقة الحيوية)"
                )
            )
            dao.insertAuditLog(
                AuditLogEntry(
                    timestamp = System.currentTimeMillis() - (1L * 24 * 60 * 60 * 1000),
                    actionType = "VIEW",
                    description = "فتح الوثيقة وعرض ملخص الديون والأمانات بواسطة رمز PIN المعتمد",
                    securityLevel = "INFO",
                    actor = "الموصي"
                )
            )

            // Seed Pulse Settings
            dao.insertOrUpdatePulseSettings(
                PulseSettings(
                    id = 1,
                    intervalDays = 30,
                    lastCheckInDate = System.currentTimeMillis() - (2L * 24 * 60 * 60 * 1000),
                    nextDueTimestamp = System.currentTimeMillis() + (28L * 24 * 60 * 60 * 1000),
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
