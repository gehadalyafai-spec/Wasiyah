package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLogEntry
import com.example.data.model.PulseSettings
import com.example.data.model.WasiyyahDocument
import com.example.ui.components.SecurityChip
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AuditAndVersionsScreen(
    document: WasiyyahDocument?,
    auditLogs: List<AuditLogEntry>,
    pulseSettings: PulseSettings?,
    onUpdateRenewalInterval: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRenewalMonths by remember { mutableIntStateOf(pulseSettings?.periodicReviewMonths ?: 6) }
    val dateFormat = SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale("ar", "SA"))

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سجل التدقيق والإصدارات (Audit Log)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        SecurityChip(text = "سجل غير قابل للتلاعب", icon = Icons.Default.Shield)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "تسجيل زمني ومؤرخ رقمياً لكل عملية فتح، تعديل، تحقق، أو محاكاة للمفاتيح لضمان سلامة الوصية ومطابقتها للمتطلبات القضائية.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD2E8E0),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Periodic Renewal Reminder Card (كل 6 أو 12 شهراً)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldSecondary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = GoldSecondary)
                        }
                        Column {
                            Text(
                                text = "نظام التحديث الدوري التلقائي للوصية",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "تنبيه إلزامي لمراجعة الديون والأمانات وتغيرات الأسرة",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = selectedRenewalMonths == 6,
                            onClick = {
                                selectedRenewalMonths = 6
                                onUpdateRenewalInterval(6)
                            },
                            label = { Text("تنبيه كل 6 أشهر (موصى به شرعاً)") },
                            leadingIcon = {
                                if (selectedRenewalMonths == 6) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldContainer)
                        )
                        FilterChip(
                            selected = selectedRenewalMonths == 12,
                            onClick = {
                                selectedRenewalMonths = 12
                                onUpdateRenewalInterval(12)
                            },
                            label = { Text("تنبيه كل سنة (12 شهراً)") },
                            leadingIcon = {
                                if (selectedRenewalMonths == 12) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldContainer)
                        )
                    }
                }
            }
        }

        // Version History
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "سجل الإصدارات الموثقة للوثيقة",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Divider()

                    // Active Version
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "الإصدار #${document?.version ?: 2} (الحالي والنافذ)",
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                                Surface(shape = RoundedCornerShape(4.dp), color = SuccessGreen.copy(alpha = 0.15f)) {
                                    Text(
                                        text = "موقعة ومعتمدة",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SuccessGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "التوقيع الرقمي: ${document?.electronicSignature ?: "معتمد"}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "البصمة التشفيرية: ${document?.cryptographicHash ?: "SHA-256"}",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = CyanCipher,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Divider()

                    // Previous Version 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "الإصدار #1 (مسودة تأسيسية سابقة)",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "تاريخ الأرشفة: قبل 15 يوماً • استُبدلت بالإصدار الأخير",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Live Audit Log Trail
        item {
            Text(
                text = "سجل العمليات والأنشطة الأمنية (${auditLogs.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(auditLogs) { log ->
            Card(
                modifier = Modifier.fillMaxWidth().testTag("audit_log_item_${log.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val (icon, color) = when (log.actionType) {
                        "SEAL" -> Icons.Default.Security to EmeraldPrimary
                        "PULSE_CHECKIN" -> Icons.Default.CheckCircle to SuccessGreen
                        "KEY_SIMULATION" -> Icons.Default.Lock to CyanCipher
                        "VIEW" -> Icons.Default.Visibility to GoldSecondary
                        else -> Icons.Default.Edit to Color(0xFF6B7280)
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = log.description,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dateFormat.format(Date(log.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "• الفاعل: ${log.actor}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
