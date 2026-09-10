package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SafetyCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.data.model.DebtItem
import com.example.data.model.GuardianKeyShare
import com.example.data.model.PulseSettings
import com.example.data.model.TrustDepositItem
import com.example.data.model.WasiyyahDocument
import com.example.ui.components.SecurityChip
import com.example.ui.components.ShariaAlertBanner
import com.example.ui.components.StatCard
import com.example.ui.theme.CyanCipher
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDarkSecondary
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AppScreen
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    document: WasiyyahDocument?,
    debts: List<DebtItem>,
    trusts: List<TrustDepositItem>,
    guardians: List<GuardianKeyShare>,
    pulseSettings: PulseSettings?,
    onNavigate: (AppScreen) -> Unit,
    onCheckIn: () -> Unit,
    onLockApp: () -> Unit,
    onUpdatePin: (String, String) -> Pair<Boolean, String> = { _, _ -> Pair(true, "") },
    modifier: Modifier = Modifier
) {
    var showChangePinDialog by remember { mutableStateOf(false) }
    var showWhyEncryptionDialog by remember { mutableStateOf(false) }

    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var pinDialogError by remember { mutableStateOf<String?>(null) }
    var pinDialogSuccess by remember { mutableStateOf<String?>(null) }

    val totalDebtsOnMe = debts.filter { it.type == "ON_ME" }.sumOf { it.amount }
    val totalDebtsForMe = debts.filter { it.type == "FOR_ME" }.sumOf { it.amount }
    val totalTrustsCount = trusts.size
    val guardiansCount = guardians.size
    val numberFormatter = NumberFormat.getNumberInstance(Locale("ar", "SA"))

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Hadith Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hadith_banner_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EmeraldDark
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = GoldLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "حديث شريف في فضل الوصية",
                                color = GoldLight,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        SecurityChip(
                            text = "تشفير E2EE",
                            icon = Icons.Default.Lock
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "«مَا حَقُّ امْرِئٍ مُسْلِمٍ لَهُ شَيْءٌ يُوصِي فِيهِ يَبِيتُ لَيْلَتَيْنِ إِلَّا وَوَصِيَّتُهُ مَكْتُوبَةٌ عِنْدَهُ»",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "متفق عليه (البخاري ومسلم) عن عبد الله بن عمر رضي الله عنهما",
                        color = Color(0xFFB5CCC3),
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }

        // Pulse Heartbeat & Dead Man's Switch Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EmeraldPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .testTag("pulse_status_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SuccessGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "نبض الحياة (Dead Man's Switch)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "الحالة: نشط ومطمئن • فحص دوري كل ${pulseSettings?.intervalDays ?: 30} يوماً",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                EmeraldContainer.copy(alpha = 0.5f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "موعد التجديد القادم:",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnEmeraldContainer
                            )
                            Text(
                                text = "خلال 28 يوماً (مفعل تلقائياً)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }
                        Button(
                            onClick = onCheckIn,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("check_in_now_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "تأكيد نبض الحياة")
                        }
                    }
                }
            }
        }

        // Vault Security & PIN Management Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EmeraldPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .testTag("vault_security_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "أمان الخزنة ورمز الدخول السري (PIN)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "الخزنة مشفرة محلياً • قفل PIN مفعل",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldPrimary
                                )
                            }
                        }
                        SecurityChip(
                            text = "Zero-Knowledge",
                            icon = Icons.Default.Lock
                        )
                    }

                    Text(
                        text = "لا يمكن لأي شخص فتح التطبيق أو الاطلاع على وصيتك وديونك وأماناتك إلا بإدخال رمز المرور الخاص بك. الرمز الافتراضي: 1234 (يُنصح بتغييره الآن).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                oldPin = ""
                                newPin = ""
                                confirmPin = ""
                                pinDialogError = null
                                pinDialogSuccess = null
                                showChangePinDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("change_pin_btn")
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "تغيير رمز المرور", style = MaterialTheme.typography.labelMedium)
                        }

                        OutlinedButton(
                            onClick = onLockApp,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("lock_now_btn")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "قفل الخزنة الآن", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    TextButton(
                        onClick = { showWhyEncryptionDialog = true },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "لماذا نستخدم التشفير المحلي Zero-Knowledge؟", color = GoldSecondary, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Will Document Overview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("will_overview_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "وثيقة وصية: ${document?.testatorName ?: "الموصي"}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "الإصدار #${document?.version ?: 1} • موثقة وموقعة رقمياً",
                                style = MaterialTheme.typography.labelMedium,
                                color = EmeraldPrimary
                            )
                        }
                        Button(
                            onClick = { onNavigate(AppScreen.WILL_WIZARD) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldSecondary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("open_wizard_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "معالج الوصية", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "الهاش التشفيري المقفل:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = document?.cryptographicHash ?: "SHA-256: e3b0c442...852b855",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = CyanCipher,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    // 1/3 Alert inline
                    val wealth = document?.totalEstimatedWealth ?: 1.0
                    val thirdAmount = document?.thirdBequestAmount ?: 0.0
                    val thirdPercentage = if (wealth > 0) (thirdAmount / wealth) * 100 else 0.0
                    val isOverThird = thirdPercentage > 33.334

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isOverThird) WarningAmber.copy(alpha = 0.15f)
                                else GoldContainer.copy(alpha = 0.6f)
                            )
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "الوصية بالثلث لغير الورثة: ${numberFormatter.format(thirdAmount)} ر.س (${"%.1f".format(thirdPercentage)}%)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isOverThird) WarningAmber else GoldSecondary
                        )
                        Text(
                            text = if (isOverThird) "يتطلب إجازة الورثة" else "ضمن الحد الشرعي",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isOverThird) DangerRed else SuccessGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 4 Key Stats Grid
        item {
            Text(
                text = "سجل الحقوق والذمم المالية والشرعية",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "ديون عليّ",
                    value = "${numberFormatter.format(totalDebtsOnMe)} ر.س",
                    subtitle = "تُقضى قبل قسمة التركة",
                    icon = Icons.Default.MonetizationOn,
                    accentColor = DangerRed,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AppScreen.WILL_WIZARD) }
                )
                StatCard(
                    title = "ديون لي عند الغير",
                    value = "${numberFormatter.format(totalDebtsForMe)} ر.س",
                    subtitle = "استيفاء لحق الورثة",
                    icon = Icons.Default.AccountBalance,
                    accentColor = SuccessGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AppScreen.WILL_WIZARD) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "أمانات وودائع",
                    value = "$totalTrustsCount أمانات",
                    subtitle = "محددة مواقع التسليم",
                    icon = Icons.Default.Handshake,
                    accentColor = GoldSecondary,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AppScreen.WILL_WIZARD) }
                )
                StatCard(
                    title = "الأوصياء وحراس المفاتيح",
                    value = "$guardiansCount أوصياء",
                    subtitle = "توزيع Shamir (2 من 3)",
                    icon = Icons.Default.Key,
                    accentColor = CyanCipher,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AppScreen.DEAD_MAN_PULSE) }
                )
            }
        }

        // Navigation Action Tiles for remaining modules
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "الأقسام والخدمات المتخصصة",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Section Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ServiceCard(
                    title = "آلية نبض الحياة وتوزيع المفاتيح (Shamir)",
                    description = "إدارة الأوصياء الثلاثة، ومحاكاة فك التشفير المشروط بشرط إجماع اثنين",
                    icon = Icons.Default.SafetyCheck,
                    badge = "نظام الأمان",
                    color = EmeraldPrimary,
                    onClick = { onNavigate(AppScreen.DEAD_MAN_PULSE) }
                )
                ServiceCard(
                    title = "التسليم المشروط المقسّم (Partitioned Delivery)",
                    description = "معاينة ما يراه كل وريث أو وصي وفق الصلاحيات المعطاة له دون كشف أسرار الآخرين",
                    icon = Icons.Default.Visibility,
                    badge = "خصوصية متقدمة",
                    color = CyanCipher,
                    onClick = { onNavigate(AppScreen.PARTITIONED_DELIVERY) }
                )
                ServiceCard(
                    title = "الاستشارة الشرعية وقائمة الأحكام الفقهية",
                    description = "ضوابط الوصية الشرعية، وحكم الوصية للوارث، والربط مع مستشار شرعي معتمد",
                    icon = Icons.Default.MenuBook,
                    badge = "فقه الوصية",
                    color = GoldSecondary,
                    onClick = { onNavigate(AppScreen.SHARIA_HUB) }
                )
                ServiceCard(
                    title = "المخطط المعماري وتدفق النظام (User Flow & Architecture)",
                    description = "عرض خريطة تدفق المستخدم الكاملة، ومخطط Zero-Knowledge، والشاشات الرئيسية",
                    icon = Icons.Default.Assignment,
                    badge = "الهندسة التقنية",
                    color = Color(0xFF4338CA),
                    onClick = { onNavigate(AppScreen.ARCHITECTURE_FLOW) }
                )
                ServiceCard(
                    title = "سجل التدقيق والإصدارات التاريخية (Audit Log)",
                    description = "سجل زمني مشفر لكل عملية فتح، تعديل، تحقق، أو فحص للوثيقة",
                    icon = Icons.Default.History,
                    badge = "النزاهة والمصداقية",
                    color = Color(0xFF4B5563),
                    onClick = { onNavigate(AppScreen.AUDIT_VERSIONS) }
                )
                ServiceCard(
                    title = "الإعدادات والمظهر وثيمات التطبيق (Settings)",
                    description = "تخصيص ألوان وثيمات التطبيق (نهاري/ليلي)، أمان الخزنة، ونبذة عن التطبيق وإصداره",
                    icon = Icons.Default.Settings,
                    badge = "المظهر والنظام",
                    color = EmeraldPrimary,
                    onClick = { onNavigate(AppScreen.SETTINGS) }
                )
            }
        }

        // Legal & Sharia Disclaimer
        item {
            ShariaAlertBanner(
                title = "إفصاح وإخلاء مسؤولية شرعي وقانوني",
                message = "تطبيق «وصية» هو منصة تقنية تنظيمية وتوثيقية مشفرة لمساعدة المسلم في ترتيب حقوقه ووصيته، ولا يُعتبر بديلاً عن الفتوى الشرعية الصادرة من الهيئات الرسمية المعتمدة أو توثيق كتابة العدل الرسمية في بلدك.",
                isWarning = false
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Change PIN Dialog
    if (showChangePinDialog) {
        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "تغيير رمز المرور السري (PIN)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "يتكون رمز المرور من 4 أرقام ويُستخدم لفك تشفير خزنتك محلياً عند كل فتح للتطبيق.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = oldPin,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) oldPin = it },
                        label = { Text("رمز المرور الحالي (الافتراضي: 1234)") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("old_pin_field"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) newPin = it },
                        label = { Text("رمز المرور الجديد (4 أرقام)") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_pin_field"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) confirmPin = it },
                        label = { Text("تأكيد رمز المرور الجديد") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("confirm_pin_field"),
                        singleLine = true
                    )

                    pinDialogError?.let { err ->
                        Text(
                            text = err,
                            color = DangerRed,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    pinDialogSuccess?.let { succ ->
                        Text(
                            text = succ,
                            color = SuccessGreen,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPin.length != 4) {
                            pinDialogError = "يجب أن يتكون الرمز الجديد من 4 أرقام"
                            return@Button
                        }
                        if (newPin != confirmPin) {
                            pinDialogError = "الرمزان غير متطابقين، يرجى التأكد"
                            return@Button
                        }
                        val (success, msg) = onUpdatePin(oldPin, newPin)
                        if (success) {
                            pinDialogSuccess = "تم تحديث رمز المرور السري بنجاح!"
                            pinDialogError = null
                            showChangePinDialog = false
                        } else {
                            pinDialogError = msg
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier.testTag("confirm_change_pin_btn")
                ) {
                    Text("حفظ الرمز")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePinDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Why Encryption Dialog
    if (showWhyEncryptionDialog) {
        AlertDialog(
            onDismissRequest = { showWhyEncryptionDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "ما الغرض من تشفير الوصية وقفلها؟",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "الوصية الشرعية تحتوي على أسرار شديدة الخصوصية لا يجوز إفشاؤها في حياة الإنسان:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "• الديون المستورة: مبالغ اقترضتها أو أقرضتها ولا يعلم بها أحد غيرك وتبرأ بها ذمتك أمام الله.\n• الأمانات والودائع: ودائع الناس لديك لحمايتها من الضياع وردها لأصحابها.\n• أسماء الأوصياء وحصص الثلث: تجنباً للخلافات الأسرية أو التدخلات أثناء حياتك.\n• تقنية Zero-Knowledge: بياناتك تُحفظ مشفرة داخل جهازك بمفتاح مشتق من رمز المرور فقط، ولا يستطيع أي سيرفر أو متطفل قراءتها.",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showWhyEncryptionDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("تم الفهم")
                }
            }
        )
    }
}

@Composable
fun ServiceCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badge: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = color.copy(alpha = 0.12f)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}
