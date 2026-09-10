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
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DebtItem
import com.example.data.model.GuardianKeyShare
import com.example.data.model.SpecialRightItem
import com.example.data.model.TrustDepositItem
import com.example.data.model.WasiyyahDocument
import com.example.ui.components.SecurityChip
import com.example.ui.theme.CyanCipher
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.SuccessGreen
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PartitionedDeliveryScreen(
    document: WasiyyahDocument?,
    debts: List<DebtItem>,
    trusts: List<TrustDepositItem>,
    specialRights: List<SpecialRightItem>,
    guardians: List<GuardianKeyShare>,
    modifier: Modifier = Modifier
) {
    var selectedPerspectiveIndex by remember { mutableIntStateOf(0) }
    val numberFormatter = NumberFormat.getNumberInstance(Locale("ar", "SA"))

    val perspectives = listOf(
        "المنفذ العام (الوكيل المعتمد)" to Icons.Default.Security,
        "وصي القُصّر" to Icons.Default.ChildCare,
        "الورثة الشرعيون" to Icons.Default.People,
        "ناظر الوقف والثلث" to Icons.Default.VolunteerActivism,
        "صاحب أمانة محددة" to Icons.Default.Handshake
    )

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
                            text = "نظام التسليم المقسّم حسب الصلاحية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        SecurityChip(text = "مجالات الصلاحية Scoped", icon = Icons.Default.Lock)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "محاكاة واقعية لما يراه كل مستفيد أو وصي بعد فتح الوثيقة المشفرة. كل طرف يُكشف له فقط الجزء الخاص به بأمر الموصي، لحفظ الأسرار وحماية خصوصيات الأسرة والتركة.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD2E8E0),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Perspective Switcher Tabs
        item {
            Text(
                text = "اختر منظور المستفيد لمعاينة مغلف التسليم الخاص به:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            ScrollableTabRow(
                selectedTabIndex = selectedPerspectiveIndex,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldPrimary
            ) {
                perspectives.forEachIndexed { index, (label, icon) ->
                    Tab(
                        selected = selectedPerspectiveIndex == index,
                        onClick = { selectedPerspectiveIndex = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text(text = label, fontWeight = if (selectedPerspectiveIndex == index) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        modifier = Modifier.testTag("perspective_tab_$index")
                    )
                }
            }
        }

        // Perspective Envelope View
        item {
            when (selectedPerspectiveIndex) {
                0 -> GeneralExecutorEnvelope(document, debts, trusts, numberFormatter)
                1 -> MinorsGuardianEnvelope(document, specialRights)
                2 -> HeirsEnvelope(document, debts, numberFormatter)
                3 -> WaqfExecutorEnvelope(document, numberFormatter)
                4 -> TrustOwnerEnvelope(trusts.firstOrNull())
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun GeneralExecutorEnvelope(
    doc: WasiyyahDocument?,
    debts: List<DebtItem>,
    trusts: List<TrustDepositItem>,
    numberFormatter: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val testatorDisplay = doc?.testatorName?.takeIf { it.isNotBlank() } ?: "(فلان ابن فلان)"
            BadgeRow(title = "مغلف: الوصي والمنفذ العام لوصية $testatorDisplay", badge = "صلاحية شاملة", color = EmeraldPrimary)
            Divider()
            Text(text = "• الافتتاحية والشهادة الشرعية: معلنة ومصادقة", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "• الديون التي على الموصي: يرى كافة الديون (${debts.filter { it.type == "ON_ME" }.size} بنود بإجمالي ${numberFormatter.format(debts.filter { it.type == "ON_ME" }.sumOf { it.amount })} ر.س) مع أرقام السندات والكمبيالات لسدادها من رأس التركة قبل أي توزيع.",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "• الأمانات والودائع: يرى جميع الأمانات (${trusts.size} أمانات) مع رموز الخزائن لردها لأصحابها.",
                style = MaterialTheme.typography.bodySmall
            )
            val w1 = doc?.witness1Name?.takeIf { it.isNotBlank() } ?: "الشاهد الأول"
            val w2 = doc?.witness2Name?.takeIf { it.isNotBlank() } ?: "الشاهد الثاني"
            Text(text = "• الشاهدان العدلان: ($w1 و $w2)", style = MaterialTheme.typography.bodySmall)
            Surface(shape = RoundedCornerShape(8.dp), color = EmeraldContainer.copy(alpha = 0.5f)) {
                Text(
                    text = "✓ الصلاحية: كاملة للتنفيذ القضائي وتصفية التركة تحت إشراف المحكمة المختصة.",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldPrimary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun MinorsGuardianEnvelope(
    doc: WasiyyahDocument?,
    specialRights: List<SpecialRightItem>
) {
    val guardianship = specialRights.firstOrNull { it.category == "GUARDIANSHIP" }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val guardianPerson = guardianship?.designatedPerson?.takeIf { it.isNotBlank() } ?: "(فلان ابن فلان)"
            BadgeRow(title = "مغلف: وصي القُصّر ($guardianPerson)", badge = "صلاحية حضانة ورعاية", color = GoldSecondary)
            Divider()
            Text(
                text = "• بند الوصاية الشرعية:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = EmeraldPrimary
            )
            Text(
                text = guardianship?.details?.takeIf { it.isNotBlank() } ?: "الوصاية على الأبناء القُصّر ورعاية شؤونهم وتوجيههم.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• التوجيهات التربوية الخاصة بالأبناء مسموح بالاطلاع عليها.",
                style = MaterialTheme.typography.bodySmall
            )
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFEE2E2)) {
                Text(
                    text = "🔒 محجوب تلقائياً: لا يظهر لوصي القُصّر المعاملات المالية البنكية أو الديون التجارية الموجهة للمنفذ العام.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF991B1B),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun HeirsEnvelope(
    doc: WasiyyahDocument?,
    debts: List<DebtItem>,
    numberFormatter: NumberFormat
) {
    val debtsForMe = debts.filter { it.type == "FOR_ME" }
    val debtsOnMe = debts.filter { it.type == "ON_ME" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            BadgeRow(title = "مغلف: الورثة الشرعيون (الأسرة)", badge = "صلاحية التركة والحقوق", color = SuccessGreen)
            Divider()
            Text(
                text = "• وصية الموصي لأهله وبنيه:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = doc?.spiritualDirectives ?: "التقوى وإصلاح ذات البين والاعتصام بحبل الله والتجهيز والدفن على السنة.",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• الديون التي للموصي عند الغير (حق الورثة للاستيفاء): ${numberFormatter.format(debtsForMe.sumOf { it.amount })} ر.س مع تفاصيل المدينين لإضافتها للتركة.",
                style = MaterialTheme.typography.bodySmall,
                color = SuccessGreen,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "• الديون التي على الموصي (واجبة السداد قبل القسمة): ${numberFormatter.format(debtsOnMe.sumOf { it.amount })} ر.س.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFB91C1C)
            )
            Surface(shape = RoundedCornerShape(8.dp), color = GoldContainer.copy(alpha = 0.5f)) {
                Text(
                    text = "🔒 محجوب: الأمانات الخاصة بأشخاص آخرين والودائع المحفوظة لا تظهر للورثة لعدم اختصاصهم بها.",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldSecondary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun WaqfExecutorEnvelope(
    doc: WasiyyahDocument?,
    numberFormatter: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            BadgeRow(title = "مغلف: ناظر الوقف والجهة الخيرية", badge = "الوصية بالثلث", color = CyanCipher)
            Divider()
            Text(text = "• مقدار الوصية المخصصة: ${numberFormatter.format(doc?.thirdBequestAmount ?: 0.0)} ر.س", fontWeight = FontWeight.Bold)
            val beneficiaryText = doc?.thirdBequestBeneficiary?.takeIf { it.isNotBlank() } ?: "الجهة المستفيدة المحددة في الوثيقة"
            val purposeText = doc?.thirdBequestPurpose?.takeIf { it.isNotBlank() } ?: "المصرف الخيري المحدد في الوثيقة"
            Text(text = "• الجهة المستفيدة: $beneficiaryText")
            Text(text = "• المصرف الخيري: $purposeText")
            Surface(shape = RoundedCornerShape(8.dp), color = CyanCipher.copy(alpha = 0.1f)) {
                Text(
                    text = "🔒 خصوصية: لا يملك ناظر الوقف صلاحية الاطلاع على أسماء الورثة أو تفاصيل الديون أو الأمانات.",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanCipher,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun TrustOwnerEnvelope(trust: TrustDepositItem?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val ownerDisplayName = trust?.ownerName?.takeIf { it.isNotBlank() } ?: "(فلان ابن فلان)"
            BadgeRow(title = "مغلف: صاحب الأمانة ($ownerDisplayName)", badge = "رد الأمانة فقط", color = GoldSecondary)
            Divider()
            Text(text = "• الأمانة المودعة: ${trust?.description?.takeIf { it.isNotBlank() } ?: "الأمانة المسجلة"}", fontWeight = FontWeight.Bold)
            Text(text = "• مكان استلامها: ${trust?.locationDetails?.takeIf { it.isNotBlank() } ?: "وفق البيانات المشفرة بحوزة الوصي"}")
            Text(text = "• تعليمات التسليم: ${trust?.returnInstructions?.takeIf { it.isNotBlank() } ?: "تسليم للأصيل أو وكيله الشرعي بموجب الإثبات"}")
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF3F4F6)) {
                Text(
                    text = "🔒 عزل تام: صاحب الأمانة لا يرى حرفاً واحداً من الوصية أو التركة سوى إشعار رد أمانته فقط.",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun BadgeRow(title: String, badge: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Surface(shape = RoundedCornerShape(6.dp), color = color.copy(alpha = 0.15f)) {
            Text(
                text = badge,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
