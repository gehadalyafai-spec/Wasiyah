package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.DebtItem
import com.example.data.model.GuardianKeyShare
import com.example.data.model.PulseSettings
import com.example.data.model.SpecialRightItem
import com.example.data.model.TrustDepositItem
import com.example.data.model.WasiyyahDocument
import com.example.data.repository.WasiyyahRepository
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.ColorPalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen(val titleAr: String) {
    DASHBOARD("الرئيسية"),
    WILL_WIZARD("معالج الوصية"),
    DEAD_MAN_PULSE("نبض الحياة والأوصياء"),
    PARTITIONED_DELIVERY("التسليم المقسّم"),
    AUDIT_VERSIONS("سجل التدقيق والإصدارات"),
    SHARIA_HUB("الأحكام والاستشارة الشرعية"),
    ARCHITECTURE_FLOW("المخطط المعماري وتدفق النظام"),
    SETTINGS("الإعدادات والمظهر")
}

class WasiyyahViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WasiyyahRepository

    init {
        val database = AppDatabase.getInstance(application)
        repository = WasiyyahRepository(database.wasiyyahDao())
    }

    val document: StateFlow<WasiyyahDocument?> = repository.document
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val debts: StateFlow<List<DebtItem>> = repository.debts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trusts: StateFlow<List<TrustDepositItem>> = repository.trusts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val specialRights: StateFlow<List<SpecialRightItem>> = repository.specialRights
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val guardians: StateFlow<List<GuardianKeyShare>> = repository.guardians
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs = repository.auditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pulseSettings: StateFlow<PulseSettings?> = repository.pulseSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI Theme & Styling State
    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _selectedPalette = MutableStateFlow(ColorPalette.EMERALD_GOLD)
    val selectedPalette: StateFlow<ColorPalette> = _selectedPalette.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        _statusNotification.value = "تم تغيير مظهر التطبيق إلى: ${mode.titleAr}"
    }

    fun setSelectedPalette(palette: ColorPalette) {
        _selectedPalette.value = palette
        _statusNotification.value = "تم تفعيل نمط ألوان: ${palette.titleAr}"
    }

    // UI Navigation State
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _wizardStep = MutableStateFlow(0)
    val wizardStep: StateFlow<Int> = _wizardStep.asStateFlow()

    // Security & PIN Lock State (Starts securely locked by default)
    private val _isAppLocked = MutableStateFlow(true)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private val _statusNotification = MutableStateFlow<String?>(null)
    val statusNotification: StateFlow<String?> = _statusNotification.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setWizardStep(step: Int) {
        if (step in 0..6) {
            _wizardStep.value = step
        }
    }

    fun nextWizardStep() {
        if (_wizardStep.value < 6) {
            _wizardStep.value += 1
        }
    }

    fun prevWizardStep() {
        if (_wizardStep.value > 0) {
            _wizardStep.value -= 1
        }
    }

    fun unlockAppWithPin(enteredPin: String): Boolean {
        val correctPin = pulseSettings.value?.appPinCode ?: "1234"
        return if (enteredPin == correctPin) {
            _isAppLocked.value = false
            _statusNotification.value = "تم فك قفل الخزنة المشفرة بنجاح"
            viewModelScope.launch {
                repository.logAction(
                    actionType = "UNLOCK",
                    description = "تم تسجيل الدخول وفك تشفير الخزنة برمز المرور السري",
                    securityLevel = "SECURE"
                )
            }
            true
        } else {
            viewModelScope.launch {
                repository.logAction(
                    actionType = "SECURITY_ALERT",
                    description = "محاولة غير مصرح بها لفك قفل الخزنة برمز غير صحيح",
                    securityLevel = "WARNING"
                )
            }
            false
        }
    }

    fun updateAppPin(oldPin: String, newPin: String): Pair<Boolean, String> {
        val currentPin = pulseSettings.value?.appPinCode ?: "1234"
        if (oldPin != currentPin) {
            return Pair(false, "رمز المرور الحالي غير صحيح")
        }
        if (newPin.length != 4 || !newPin.all { it.isDigit() }) {
            return Pair(false, "يجب أن يتكون الرمز الجديد من 4 أرقام عددية")
        }
        viewModelScope.launch {
            val current = pulseSettings.value ?: PulseSettings()
            repository.updatePulseSettings(current.copy(appPinCode = newPin))
            repository.logAction(
                actionType = "PIN_CHANGE",
                description = "تم تحديث رمز المرور السري للخزنة بنجاح",
                securityLevel = "SECURE"
            )
            _statusNotification.value = "تم تغيير رمز المرور السري بنجاح"
        }
        return Pair(true, "تم تحديث الرمز بنجاح")
    }

    fun lockApp() {
        _isAppLocked.value = true
    }

    fun recordPulseCheckIn() {
        viewModelScope.launch {
            repository.recordPulseCheckIn()
            _statusNotification.value = "تم تسجيل نبض الحياة بنجاح! تم تجديد المؤقت لـ 30 يوماً إضافية"
        }
    }

    fun toggleGuardianConsensus(guardianId: Int, isConfirmed: Boolean) {
        viewModelScope.launch {
            repository.toggleGuardianConsensus(guardianId, isConfirmed)
        }
    }

    fun saveDocument(doc: WasiyyahDocument) {
        viewModelScope.launch {
            repository.saveDocument(doc)
            _statusNotification.value = "تم حفظ بنود الوصية بنجاح"
        }
    }

    fun signAndSealWill(
        signatureName: String,
        w1Name: String, w1Id: String, w1Phone: String,
        w2Name: String, w2Id: String, w2Phone: String
    ) {
        viewModelScope.launch {
            repository.signAndSealDocument(signatureName, w1Name, w1Id, w1Phone, w2Name, w2Id, w2Phone)
            _statusNotification.value = "تم توثيق وتشفير الوصية رسمياً وإصدار بصمة الهاش للمفتاح المشفر"
        }
    }

    fun addDebt(debt: DebtItem) {
        viewModelScope.launch {
            repository.addDebt(debt)
            _statusNotification.value = "تم تسجيل الدين بنجاح"
        }
    }

    fun deleteDebt(id: Int) {
        viewModelScope.launch {
            repository.deleteDebt(id)
        }
    }

    fun addTrust(trust: TrustDepositItem) {
        viewModelScope.launch {
            repository.addTrust(trust)
            _statusNotification.value = "تم تسجيل الأمانة بنجاح"
        }
    }

    fun deleteTrust(id: Int) {
        viewModelScope.launch {
            repository.deleteTrust(id)
        }
    }

    fun addSpecialRight(right: SpecialRightItem) {
        viewModelScope.launch {
            repository.addSpecialRight(right)
            _statusNotification.value = "تم حفظ الحق الخاص بنجاح"
        }
    }

    fun deleteSpecialRight(id: Int) {
        viewModelScope.launch {
            repository.deleteSpecialRight(id)
        }
    }

    fun addGuardian(guardian: GuardianKeyShare) {
        viewModelScope.launch {
            repository.addGuardian(guardian)
            _statusNotification.value = "تم تسجيل الوصي وتوليد حصة المفتاح المشفر بنجاح"
        }
    }

    fun deleteGuardian(id: Int) {
        viewModelScope.launch {
            repository.deleteGuardian(id)
        }
    }

    fun updateRenewalInterval(months: Int) {
        viewModelScope.launch {
            val current = pulseSettings.value ?: PulseSettings()
            repository.updatePulseSettings(current.copy(periodicReviewMonths = months))
            _statusNotification.value = "تم تحديث فترة التذكير الدوري للمراجعة إلى كل $months أشهر"
        }
    }

    fun clearNotification() {
        _statusNotification.value = null
    }
}
