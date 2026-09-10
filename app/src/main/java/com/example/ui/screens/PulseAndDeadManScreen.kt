package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GuardianKeyShare
import com.example.data.model.PulseSettings
import com.example.ui.components.SecurityChip
import com.example.ui.components.ShariaAlertBanner
import com.example.ui.theme.CyanCipher
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun PulseAndDeadManScreen(
    pulseSettings: PulseSettings?,
    guardians: List<GuardianKeyShare>,
    onCheckIn: () -> Unit,
    onToggleConsensus: (Int, Boolean) -> Unit,
    onAddGuardian: (GuardianKeyShare) -> Unit,
    onDeleteGuardian: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddGuardianDialog by remember { mutableStateOf(false) }
    var showCertDialog by remember { mutableStateOf(false) }

    val threshold = pulseSettings?.shamirThreshold ?: 2
    val confirmedCount = guardians.count { it.hasConfirmedConsensus }
    val isThresholdMet = confirmedCount >= threshold

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDark)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "نظام التشفير والتسليم المشروط (Dead Man's Switch)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        SecurityChip(text = "Zero-Knowledge", icon = Icons.Default.Shield)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "آلية أمنية ذكية تضمن عدم فتح الوصية إلا بعد التحقق اليقيني من الوفاة عبر مزيج متكامل: مؤقت نبض الحياة + توافق الأوصياء المشفر (Shamir SSS) + شهادة الوفاة الرسمية.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD0E6DE),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Section 1: Heartbeat Pulse (نبض الحياة)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SuccessGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = SuccessGreen)
                            }
                            Column {
                                Text(text = "مؤشر نبض الحياة الدوري (Heartbeat)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = "الدورية الحالية: كل ${pulseSettings?.intervalDays ?: 30} يوماً", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    LinearProgressIndicator(
                        progress = { 0.85f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = SuccessGreen,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "آخر تسجيل نبض: قبل يومين", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "متبقي 28 يوماً قبل بدء التنبيهات", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onCheckIn,
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("pulse_screen_checkin_btn")
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "أنا بخير / تأكيد نبض الحياة")
                        }
                    }
                }
            }
        }

        // Section 2: Shamir's Secret Sharing Key Splitting (مفاتيح تشفير موزعة)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isThresholdMet) DangerRed else CyanCipher, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                text = "توزيع المفاتيح (Shamir's Secret Sharing)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "النصاب المطلوب: $threshold من ${guardians.size} أوصياء لفك التشفير",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = if (isThresholdMet) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isThresholdMet) DangerRed else CyanCipher,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    // Threshold status banner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isThresholdMet) DangerRed.copy(alpha = 0.12f) else EmeraldContainer.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isThresholdMet) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isThresholdMet) DangerRed else EmeraldPrimary
                            )
                            Text(
                                text = if (isThresholdMet)
                                    "⚠️ اكتمل النصاب ($confirmedCount/$threshold): إجماع الأوصياء يسمح بفك تشفير الحصص المصرح بها!"
                                else
                                    "🔒 الأمان محكم ($confirmedCount/$threshold): لا يمكن لأي وصي بمفرده قراءة الوصية. التشفير غير قابل للاختراق بدون إجماع $threshold أوصياء.",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isThresholdMet) DangerRed else EmeraldPrimary
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Guardians List with consensus switches
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "قائمة الأوصياء وحراس الحصص التشفيرية (${guardians.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { showAddGuardianDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldSecondary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_guardian_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "إضافة وصي")
                }
            }
        }

        items(guardians) { guardian ->
            Card(
                modifier = Modifier.fillMaxWidth().testTag("guardian_item_${guardian.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = guardian.guardianName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CyanCipher.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "حصة #${guardian.shareIndex}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyanCipher,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${guardian.relationship} • الصلاحية: ${getRoleScopeLabel(guardian.roleScope)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${guardian.phone} • ${guardian.email}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onDeleteGuardian(guardian.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = DangerRed.copy(alpha = 0.7f))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Key consensus switch simulation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "محاكاة تأكيد حصة الوصي:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (guardian.hasConfirmedConsensus) "الحصة مدخلة ومطابقة ✓" else "في انتظار إدخال المفتاح",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (guardian.hasConfirmedConsensus) SuccessGreen else WarningAmber
                            )
                        }
                        Switch(
                            checked = guardian.hasConfirmedConsensus,
                            onCheckedChange = { onToggleConsensus(guardian.id, it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary, checkedTrackColor = EmeraldContainer),
                            modifier = Modifier.testTag("consensus_switch_${guardian.id}")
                        )
                    }
                }
            }
        }

        // Death Certificate Link Option
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "الربط مع شهادة الوفاة الرسمية (تكامل حكومي)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "إمكانية إرفاق أو التحقق الآلي من شهادة الوفاة عبر البوابة الوطنية للأحوال المدنية كشرط أمان إضافي قبل التسليم.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { showCertDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "فحص الشهادة")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Add Guardian Dialog
    if (showAddGuardianDialog) {
        AddGuardianDialog(
            nextShareIndex = guardians.size + 1,
            onDismiss = { showAddGuardianDialog = false },
            onConfirm = { g ->
                onAddGuardian(g)
                showAddGuardianDialog = false
            }
        )
    }

    // Death Certificate Dialog
    if (showCertDialog) {
        AlertDialog(
            onDismissRequest = { showCertDialog = false },
            title = { Text("التحقق من شهادة الوفاة الرسمية", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "يسمح هذا الملحق للجهات المعنية برفع رقم الشهادة الرسمية أو مسح رمز QR الصادر من وزارة الصحة/الأحوال المدنية لمطابقته مع السجل المدني للموصي قبل السماح بتجميع مفاتيح Shamir.",
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(onClick = { showCertDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)) {
                    Text("تم الفهم")
                }
            }
        )
    }
}

fun getRoleScopeLabel(scope: String): String {
    return when (scope) {
        "FULL_EXECUTOR" -> "وصي شامل على كامل التركة"
        "MINORS_CUSTODY" -> "مختص بحضانة القُصّر ورعايتهم"
        "DEBTS_ONLY" -> "مختص بسداد الديون واستيفائها"
        "THIRD_BEQUEST" -> "مختص بالوصية الخيرية والوقف"
        else -> "وصي معتمد"
    }
}

@Composable
fun AddGuardianDialog(
    nextShareIndex: Int,
    onDismiss: () -> Unit,
    onConfirm: (GuardianKeyShare) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var roleScope by remember { mutableStateOf("FULL_EXECUTOR") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تعيين وصي وحارس مفتاح تشفيري", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الوصي الكامل") },
                    placeholder = { Text("مثال: فلان ابن فلان") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("صلة القرابة أو الصفة (أخ، محامٍ، صديق عدل)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم الجوال لتلقي التنبيهات المشفرة") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("البريد الإلكتروني") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Text(text = "صلاحية الاطلاع المخصصة له:", style = MaterialTheme.typography.labelSmall)
                listOf(
                    "FULL_EXECUTOR" to "وصي شامل",
                    "MINORS_CUSTODY" to "حضانة القُصّر",
                    "DEBTS_ONLY" to "الديون والذمم",
                    "THIRD_BEQUEST" to "الوقف والثلث"
                ).forEach { (scope, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { roleScope = scope },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = roleScope == scope,
                            onClick = { roleScope = scope }
                        )
                        Text(text = label, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            GuardianKeyShare(
                                guardianName = name,
                                relationship = relationship,
                                phone = phone,
                                email = email,
                                shareIndex = nextShareIndex,
                                shareFragmentHash = "SHAMIR-SHARE-$nextShareIndex: ${System.currentTimeMillis().toString(16)}",
                                hasConfirmedConsensus = false,
                                roleScope = roleScope
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("توليد الحصة وتعيين الوصي")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
