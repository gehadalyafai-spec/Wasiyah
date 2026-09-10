package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.components.WasiyyahTopBar
import com.example.ui.screens.ArchitectureFlowScreen
import com.example.ui.screens.AuditAndVersionsScreen
import com.example.ui.screens.BiometricLockScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PartitionedDeliveryScreen
import com.example.ui.screens.PulseAndDeadManScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShariaHubScreen
import com.example.ui.screens.WillWizardScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.WasiyyahTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.WasiyyahViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: WasiyyahViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val selectedPalette by viewModel.selectedPalette.collectAsState()

            WasiyyahTheme(themeMode = themeMode, palette = selectedPalette) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    WasiyyahApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun WasiyyahApp(viewModel: WasiyyahViewModel) {
    val document by viewModel.document.collectAsState()
    val debts by viewModel.debts.collectAsState()
    val trusts by viewModel.trusts.collectAsState()
    val specialRights by viewModel.specialRights.collectAsState()
    val guardians by viewModel.guardians.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val pulseSettings by viewModel.pulseSettings.collectAsState()

    val currentScreen by viewModel.currentScreen.collectAsState()
    val wizardStep by viewModel.wizardStep.collectAsState()
    val isAppLocked by viewModel.isAppLocked.collectAsState()
    val statusNotification by viewModel.statusNotification.collectAsState()

    val themeMode by viewModel.themeMode.collectAsState()
    val selectedPalette by viewModel.selectedPalette.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusNotification) {
        statusNotification?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearNotification()
        }
    }

    if (isAppLocked) {
        val currentPin = pulseSettings?.appPinCode ?: "1234"
        BiometricLockScreen(
            onAttemptPin = { entered ->
                viewModel.unlockAppWithPin(entered)
            },
            onBiometricUnlock = {
                viewModel.unlockAppWithPin(currentPin)
            }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                WasiyyahTopBar(
                    title = currentScreen.titleAr,
                    onBackClick = if (currentScreen != AppScreen.DASHBOARD) {
                        { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    } else null,
                    onLockClick = { viewModel.lockApp() },
                    onPulseClick = { viewModel.recordPulseCheckIn() },
                    onSettingsClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                    isPulseActive = true
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = EmeraldPrimary,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    val navItems = listOf(
                        Triple(AppScreen.DASHBOARD, "الرئيسية", Icons.Default.Home),
                        Triple(AppScreen.WILL_WIZARD, "معالج الوصية", Icons.Default.Edit),
                        Triple(AppScreen.DEAD_MAN_PULSE, "نبض الأمان", Icons.Default.Favorite),
                        Triple(AppScreen.PARTITIONED_DELIVERY, "التسليم المقسّم", Icons.Default.Visibility),
                        Triple(AppScreen.SHARIA_HUB, "الأحكام والفتوى", Icons.Default.MenuBook)
                    )

                    navItems.forEach { (screen, label, icon) ->
                        val selected = currentScreen == screen
                        NavigationBarItem(
                            selected = selected,
                            onClick = { viewModel.navigateTo(screen) },
                            icon = { Icon(imageVector = icon, contentDescription = label) },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldPrimary,
                                selectedTextColor = EmeraldPrimary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    AppScreen.DASHBOARD -> DashboardScreen(
                        document = document,
                        debts = debts,
                        trusts = trusts,
                        guardians = guardians,
                        pulseSettings = pulseSettings,
                        onNavigate = { viewModel.navigateTo(it) },
                        onCheckIn = { viewModel.recordPulseCheckIn() },
                        onLockApp = { viewModel.lockApp() },
                        onUpdatePin = { oldPin, newPin -> viewModel.updateAppPin(oldPin, newPin) }
                    )

                    AppScreen.WILL_WIZARD -> WillWizardScreen(
                        document = document,
                        debts = debts,
                        trusts = trusts,
                        specialRights = specialRights,
                        currentStep = wizardStep,
                        onStepSelect = { viewModel.setWizardStep(it) },
                        onNextStep = { viewModel.nextWizardStep() },
                        onPrevStep = { viewModel.prevWizardStep() },
                        onSaveDocument = { viewModel.saveDocument(it) },
                        onSignAndSeal = { sig, w1n, w1id, w1ph, w2n, w2id, w2ph ->
                            viewModel.signAndSealWill(sig, w1n, w1id, w1ph, w2n, w2id, w2ph)
                        },
                        onAddDebt = { viewModel.addDebt(it) },
                        onDeleteDebt = { viewModel.deleteDebt(it) },
                        onAddTrust = { viewModel.addTrust(it) },
                        onDeleteTrust = { viewModel.deleteTrust(it) },
                        onAddSpecialRight = { viewModel.addSpecialRight(it) },
                        onDeleteSpecialRight = { viewModel.deleteSpecialRight(it) }
                    )

                    AppScreen.DEAD_MAN_PULSE -> PulseAndDeadManScreen(
                        pulseSettings = pulseSettings,
                        guardians = guardians,
                        onCheckIn = { viewModel.recordPulseCheckIn() },
                        onToggleConsensus = { gid, conf -> viewModel.toggleGuardianConsensus(gid, conf) },
                        onAddGuardian = { viewModel.addGuardian(it) },
                        onDeleteGuardian = { viewModel.deleteGuardian(it) }
                    )

                    AppScreen.PARTITIONED_DELIVERY -> PartitionedDeliveryScreen(
                        document = document,
                        debts = debts,
                        trusts = trusts,
                        specialRights = specialRights,
                        guardians = guardians
                    )

                    AppScreen.AUDIT_VERSIONS -> AuditAndVersionsScreen(
                        document = document,
                        auditLogs = auditLogs,
                        pulseSettings = pulseSettings,
                        onUpdateRenewalInterval = { viewModel.updateRenewalInterval(it) }
                    )

                    AppScreen.SHARIA_HUB -> ShariaHubScreen()

                    AppScreen.ARCHITECTURE_FLOW -> ArchitectureFlowScreen()

                    AppScreen.SETTINGS -> SettingsScreen(
                        currentThemeMode = themeMode,
                        currentPalette = selectedPalette,
                        pulseSettings = pulseSettings,
                        onSelectThemeMode = { viewModel.setThemeMode(it) },
                        onSelectPalette = { viewModel.setSelectedPalette(it) },
                        onUpdatePin = { oldPin, newPin -> viewModel.updateAppPin(oldPin, newPin) },
                        onLockApp = { viewModel.lockApp() }
                    )
                }
            }
        }
    }
}
