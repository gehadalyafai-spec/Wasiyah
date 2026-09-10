package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SecurityChip
import com.example.ui.theme.CyanCipher
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun BiometricLockScreen(
    onAttemptPin: (String) -> Boolean,
    onBiometricUnlock: () -> Unit,
    currentPinHint: String = "1234",
    modifier: Modifier = Modifier
) {
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var failedAttempts by remember { mutableIntStateOf(0) }
    var showExplanationDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val shakeOffset = remember { Animatable(0f) }
    val maxPinLength = 4

    fun triggerShake() {
        coroutineScope.launch {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    -20f at 50
                    20f at 100
                    -15f at 150
                    15f at 200
                    -10f at 250
                    10f at 300
                    0f at 400
                }
            )
        }
    }

    fun verifyPin(pin: String) {
        val valid = onAttemptPin(pin)
        if (valid) {
            isSuccess = true
            isError = false
            errorMessage = null
        } else {
            isError = true
            isSuccess = false
            failedAttempts++
            errorMessage = "رمز المرور غير صحيح! الرمز الافتراضي: $currentPinHint"
            triggerShake()
            coroutineScope.launch {
                delay(600)
                enteredPin = ""
                isError = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EmeraldDark)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Brand & Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(EmeraldPrimary)
                    .border(2.dp, GoldLight, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "و",
                    color = GoldLight,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "وَصِيَّة",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Text(
                text = "الخزنة الرقمية المشفرة للوصية والأمانات",
                color = GoldLight,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            SecurityChip(
                text = "مشفرة محلياً • Zero-Knowledge AES-256",
                icon = Icons.Default.Lock
            )
        }

        // PIN Section with Shake Animation
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
        ) {
            Text(
                text = if (isSuccess) "تم فك تشفير الخزنة بنجاح..." else "أدخل رمز المرور السري (PIN) للدخول",
                color = if (isSuccess) SuccessGreen else Color(0xFFD5E8E1),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            // PIN indicator dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until maxPinLength) {
                    val isFilled = i < enteredPin.length
                    val dotColor = when {
                        isSuccess -> SuccessGreen
                        isError -> DangerRed
                        isFilled -> GoldSecondary
                        else -> Color.White.copy(alpha = 0.25f)
                    }
                    val borderColor = when {
                        isSuccess -> SuccessGreen
                        isError -> DangerRed
                        isFilled -> GoldLight
                        else -> Color.Transparent
                    }

                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                            .border(1.5.dp, borderColor, CircleShape)
                    )
                }
            }

            // Error or Status text
            AnimatedVisibility(visible = errorMessage != null) {
                errorMessage?.let {
                    Text(
                        text = it,
                        color = DangerRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        // Numeric Keypad
        Column(
            modifier = Modifier.fillMaxWidth(0.85f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val keyRows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("BIO", "0", "DEL")
            )

            keyRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEach { key ->
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.09f))
                                .clickable {
                                    if (isSuccess) return@clickable
                                    when (key) {
                                        "DEL" -> {
                                            if (enteredPin.isNotEmpty()) {
                                                enteredPin = enteredPin.dropLast(1)
                                                isError = false
                                                errorMessage = null
                                            }
                                        }
                                        "BIO" -> {
                                            // Biometric action
                                            onBiometricUnlock()
                                        }
                                        else -> {
                                            if (enteredPin.length < maxPinLength) {
                                                val newPin = enteredPin + key
                                                enteredPin = newPin
                                                isError = false
                                                errorMessage = null
                                                if (newPin.length == maxPinLength) {
                                                    verifyPin(newPin)
                                                }
                                            }
                                        }
                                    }
                                }
                                .testTag("pin_key_$key"),
                            contentAlignment = Alignment.Center
                        ) {
                            when (key) {
                                "BIO" -> Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = "بصمة حيوية",
                                    tint = GoldLight,
                                    modifier = Modifier.size(28.dp)
                                )
                                "DEL" -> Icon(
                                    imageVector = Icons.Default.Backspace,
                                    contentDescription = "حذف",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                else -> Text(
                                    text = key,
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Info & Hint Area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            // Hint for first-time use
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth(0.92f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = GoldLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "رمز الدخول المبدئي: $currentPinHint (تستطيع تغييره بعد الدخول)",
                        color = Color(0xFFE2EDE8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Explanation trigger
            TextButton(
                onClick = { showExplanationDialog = true },
                modifier = Modifier.testTag("why_encryption_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = null,
                    tint = GoldLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ما الغرض من التشفير وقفل الخزنة؟",
                    color = GoldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Comprehensive Dialog explaining why encryption and locks exist in Wasiyyah
    if (showExplanationDialog) {
        AlertDialog(
            onDismissRequest = { showExplanationDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = GoldSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "لماذا تُشفَّر وصيتك بقفل صارم؟",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "تطبيق «وصية» ليس مجرد مفكرة عادية، بل خزانة إسلامية قانونية لحفظ الذمم والأسرار المالية والشرعية:",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )

                    // Point 1
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "١. حفظ أسرار الذمم والحقوق (Zero-Knowledge)",
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "تحتوي الوصية على ديونك السرية وأمانات الآخرين لديك، وحقوق القُصّر. التشفير المحلي يمنع أي شخص أو تطبيق على هاتفك من الاطلاع عليها.",
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Point 2
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "٢. منع التلاعب والتبديل بعد التوقيع",
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ترتبط بنود وصيتك ببصمة تجزئة مشفرة (SHA-256) لمنع أي تحريف في المبالغ الموصى بها أو أسماء المستحقين.",
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Point 3
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "٣. التسليم المشروط للأوصياء الشرعيين (Shamir SSS)",
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "في حال الوفاة (أو انقطاع نبض الحياة)، لا يمكن لأي وصي مفرد فتح الوصية إلا بعد اكتمال نصاب الأوصياء المعتمدين معاً، صيانةً للأمانة.",
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Text(
                        text = "• رمز المرور الافتراضي لتسجيل الدخول هو: 1234\n• يمكنك تغييره فوراً إلى أي رمز سري خاص بك من داخل لوحة التحكم.",
                        style = MaterialTheme.typography.bodySmall,
                        color = GoldSecondary,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExplanationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("فهمت، العودة لإدخال الرمز")
                }
            }
        )
    }
}
