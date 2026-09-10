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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SecurityChip
import com.example.ui.theme.CyanCipher
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.SuccessGreen

data class FlowStage(
    val stageNumber: String,
    val title: String,
    val description: String,
    val techDetail: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class ScreenWireframeSpec(
    val id: String,
    val name: String,
    val category: String,
    val purpose: String,
    val keyElements: List<String>
)

@Composable
fun ArchitectureFlowScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val flowStages = remember {
        listOf(
            FlowStage(
                stageNumber = "1",
                title = "التسجيل والمصادقة الصفرية (Zero-Knowledge Onboarding)",
                description = "إنشاء محفظة التشفير المحلية للمستخدم. اشتقاق المفتاح الرئيسي محلياً على الجهاز عبر PBKDF2/Argon2 دون أن يغادر المفتاح هاتف المستخدم نهائياً.",
                techDetail = "Client-Side Key Derivation + Biometric Keystore (No plaintext leaves device).",
                icon = Icons.Default.Lock
            ),
            FlowStage(
                stageNumber = "2",
                title = "معالج صياغة الوصية التفاعلي (Will Creation Wizard)",
                description = "مرور المستخدم بـ 7 خطوات شرعية تفاعلية (الشهادتين، الديون عليه وله، الأمانات، الوصاية على القُصّر، حاسبة الثلث، والتوقيع الرقمي مع الشاهدين).",
                techDetail = "Reactive Room Local State + Sharia Real-time Constraints Validation (Third limit, non-heir rule).",
                icon = Icons.Default.Assignment
            ),
            FlowStage(
                stageNumber = "3",
                title = "التشفير والختم وتجزئة المفاتيح (Shamir's Secret Sharing)",
                description = "يتم تشفير الوصية بخوارزمية AES-256-GCM، ثم تجزئة مفتاح فك التشفير إلى (N=3) حصص تشفيرية عبر خوارزمية Shamir's SSS بنصاب (M=2) يتوزع على الأوصياء المعتمدين.",
                techDetail = "AES-256-GCM + SSS Polynomial (2-of-3 threshold) + SHA-256 Cryptographic Stamp.",
                icon = Icons.Default.Key
            ),
            FlowStage(
                stageNumber = "4",
                title = "نبض الحياة الدوري (Dead Man's Switch Heartbeat)",
                description = "فحص دوري كل 30 يوماً للتأكد من سلامة الموصي. إشعار 'أنا بخير'. وفي حال عدم الاستجابة لمدة 14 يوماً إضافية تبدأ بروتوكولات التصعيد الآمنة.",
                techDetail = "Local AlarmManager + Cryptographic Heartbeat timestamp + Multi-tier Escalation Protocol.",
                icon = Icons.Default.Security
            ),
            FlowStage(
                stageNumber = "5",
                title = "بروتوكول التحقق من الوفاة وتجميع الحصص",
                description = "عند ثبوت الوفاة عبر انقطاع النبض + تأكيد إجماع 2 من 3 أوصياء + مطابقة شهادة الوفاة الرسمية، يتم تجميع حصص Shamir المشفرة لإعادة بناء المفتاح.",
                techDetail = "Consensus Verification Engine + Death Certificate Hash Validation + Key Reconstruction.",
                icon = Icons.Default.AccountTree
            ),
            FlowStage(
                stageNumber = "6",
                title = "التسليم المقسّم بحسب الصلاحية (Partitioned Delivery)",
                description = "يتم فك تشفير البيانات وتوزيعها وفق أذونات الموصي الصارمة: كل وريث أو وصي يرى فقط ما يخصه (المنفذ يرى الديون الكلية، وصي القُصّر يرى الحضانة، ناظر الوقف يرى الثلث، صاحب الأمانة يرى أمانته فقط).",
                techDetail = "Role-Based Scoped Envelopes + Zero Leaking between heirs.",
                icon = Icons.Default.Visibility
            )
        )
    }

    val screensList = remember {
        listOf(
            ScreenWireframeSpec(
                id = "SCR-01",
                name = "لوحة التحكم الرئيسية (Dignified Dashboard)",
                category = "الرئيسية",
                purpose = "عرض الحالة الشرعية والأمنية الفورية للوصية، مؤشر نبض الحياة، وإحصائيات الحقوق والديون والثلث.",
                keyElements = listOf("شريط الحديث النبوي الشريف", "بطاقة نبض الحياة والعد التنازلي", "مؤشر هاش التشفير والإصدار", "شبكة إحصائيات الديون والأمانات والثلث", "روابط الخدمات والأقسام")
            ),
            ScreenWireframeSpec(
                id = "SCR-02",
                name = "معالج الوصية - الافتتاحية والشهادة الشرعية",
                category = "معالج الصياغة",
                purpose = "كتابة الشهادتين، والوصية بالتقوى وإصلاح ذات البين، وتوجيهات الجنازة والدفن الشرعي.",
                keyElements = listOf("الاسم ورقم الهوية الوطنية", "محرر نص الشهادتين المسنون", "توجيهات التجهيز والدفن على السنة", "إرشادات فقهية وسند شرعي")
            ),
            ScreenWireframeSpec(
                id = "SCR-03",
                name = "معالج الوصية - الديون التي على الموصي",
                category = "معالج الصياغة",
                purpose = "حصر الديون الواجب سدادها من رأس التركة قبل الوصية والميراث إبراءً لذمة الميت.",
                keyElements = listOf("اسم الدائن والمبلغ وتاريخ الاستحقاق", "طرق الإثبات والسندات لأمر", "أرقام التواصل", "تنبيه أولوية الدين على الميراث والوصية")
            ),
            ScreenWireframeSpec(
                id = "SCR-04",
                name = "معالج الوصية - الديون التي للموصي عند الغير",
                category = "معالج الصياغة",
                purpose = "إثبات الأموال المستحقة للموصي في ذمم الآخرين لحفظ حقوق الورثة الشرعيين من الضياع.",
                keyElements = listOf("اسم المدين والمبلغ والإثباتات", "تعليمات الاستيفاء للورثة", "إجمالي المبالغ القابلة للتحصيل")
            ),
            ScreenWireframeSpec(
                id = "SCR-05",
                name = "معالج الوصية - الأمانات والودائع",
                category = "معالج الصياغة",
                purpose = "توثيق الأمانات المستودعة عند الموصي ومكان حفظها وطريقة ردها لأصحابها دون اختلاطها بالتركة.",
                keyElements = listOf("اسم صاحب الأمانة ورقم هاتفه", "وصف الأمانة ومكان الخزنة بدقة", "تعليمات الرد الشرعي")
            ),
            ScreenWireframeSpec(
                id = "SCR-06",
                name = "معالج الوصية - الحقوق والوصاية على القُصّر",
                category = "معالج الصياغة",
                purpose = "تعيين الوصي على الأطفال وتحديد النذور والكفارات والعهود الأخلاقية وكفالة الأيتام.",
                keyElements = listOf("بيانات وصي القُصّر المختار", "جدول النذور والكفارات الواجبة", "كفالات الأيتام المستمرة", "توجيهات تربوية خاصة")
            ),
            ScreenWireframeSpec(
                id = "SCR-07",
                name = "معالج الوصية - الوصية بالثلث الشرعي وحاسبتها",
                category = "معالج الصياغة",
                purpose = "حساب نسبة الثلث الشرعي ومصرفه (لغير الورثة) ومنع الوصية لوارث مع تنبيه تجاوز الثلث.",
                keyElements = listOf("حقل تقدير إجمالي الثروة", "حاسبة نسبة الثلث التفاعلية الفورية", "بيانات الجهة الخيرية أو المستفيد", "إقرار إجازة الورثة في حال الزيادة")
            ),
            ScreenWireframeSpec(
                id = "SCR-08",
                name = "المراجعة والتوقيع الرقمي وإرفاق الشاهدين",
                category = "التوثيق والأمان",
                purpose = "مراجعة شاملة لكافة البنود، وتوقيع الموصي إلكترونياً، وتوثيق بيانات شاهدي عدل وختم الوثيقة.",
                keyElements = listOf("ملخص شامل لكافة الحقوق", "لوحة التوقيع الإلكتروني للموصي", "بيانات وهوية الشاهد الأول والشاهد الثاني", "زر التوثيق والتشفير والختم النهائي")
            ),
            ScreenWireframeSpec(
                id = "SCR-09",
                name = "نبض الحياة وتوزيع المفاتيح (Shamir SSS)",
                category = "التشفير والأمان",
                purpose = "إدارة فترات الفحص الدوري، وتعيين الأوصياء وحصص مفاتيحهم، ومحاكاة إجماع النصاب المشفر.",
                keyElements = listOf("مؤشر نبض الحياة وزر 'أنا بخير'", "حاسبة نصاب 2 من 3 لفك التشفير", "قائمة الأوصياء ومفاتيح التشفير التفاعلية", "الربط مع شهادة الوفاة الرسمية")
            ),
            ScreenWireframeSpec(
                id = "SCR-10",
                name = "التسليم المشروط المقسّم (Partitioned Delivery)",
                category = "التسليم والخصوصية",
                purpose = "معاينة مغلفات التسليم المعزولة لكل مستفيد (المحامي، وصي القُصّر، الورثة، ناظر الوقف، صاحب الأمانة).",
                keyElements = listOf("مبدّل منظور المستفيد", "محتوى المغلف المصرح به", "مؤشر الحقول المحجوبة أمنياً", "بطاقة حماية الأسرار العائلية")
            )
        )
    }

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
                            text = "المخطط المعماري وتدفق النظام والواجهات",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        SecurityChip(text = "هندسة أمنية وشرعية", icon = Icons.Default.Layers)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "توثيق شامل لمخطط تدفق المستخدم الكامل (User Flow)، والنموذج التقني المقترح (Zero-Knowledge Data Flow)، وتوصيف كامل لـ 10 شاشات رئيسية.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD2E8E0),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Section Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldPrimary
            ) {
                listOf(
                    "مخطط تدفق المستخدم (User Flow)",
                    "توصيف الشاشات (10 Wireframes)",
                    "النموذج التقني (Zero-Knowledge)"
                ).forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(text = label, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal)
                        },
                        modifier = Modifier.testTag("arch_tab_$index")
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // User Flow Stages
                item {
                    Text(
                        text = "خريطة التدفق الكاملة: من صياغة الوصية حتى تسليمها بعد الوفاة",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(flowStages) { stage ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stage.stageNumber,
                                    color = GoldLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stage.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stage.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(shape = RoundedCornerShape(6.dp), color = CyanCipher.copy(alpha = 0.1f)) {
                                    Text(
                                        text = "المستوى التقني: ${stage.techDetail}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyanCipher,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(6.dp),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Wireframe Catalog (10 screens)
                item {
                    Text(
                        text = "كتالوج شاشات التطبيق الرئيسية (10 شاشات متكاملة)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(screensList) { scr ->
                    var isExpanded by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded },
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
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(shape = RoundedCornerShape(4.dp), color = EmeraldContainer) {
                                            Text(
                                                text = scr.id,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = EmeraldPrimary,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(text = scr.category, style = MaterialTheme.typography.labelSmall, color = GoldSecondary)
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = scr.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = EmeraldPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = scr.purpose,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Divider()
                                    Text(text = "أبرز عناصر الواجهة والوظائف التفاعلية:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                    scr.keyElements.forEach { elem ->
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                            Text(text = elem, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Technical Zero-Knowledge Architecture
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(text = "معمارية المعرفة الصفرية (Zero-Knowledge Architecture)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                            Divider()
                            Text(
                                text = "1. لا يتم تخزين المفاتيح على أي خادم سحابي: المفتاح المشفر يُشتق محلياً على جهاز الموصي عبر كلمة مروره وبيانات البصمة الحيوية (Biometrics).",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "2. تشفير أجزاء البيانات بمفاتيح فرعية: كل قسم من الوصية (الديون، الأمانات، القُصّر، الثلث) يتم تشفيره بمفتاح فرعي (Sub-key) مستمد من المفتاح الأصلي.",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "3. خوارزمية شامير لمشاركة السر (Shamir's Secret Sharing): يتم تفكيك المفتاح الرئيسي رياضياً إلى 3 حدود في معادلة متعددة الحدود من الدرجة الأولى f(x) = ax + S. يتطلب فكها نقطتين على الأقل (2 of 3).",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "4. الحفظ المحلي الصارم (Local Room DB): كافة الجداول والمستندات مخزنة محلياً في قاعدة بيانات أندرويد المشفرة دون اتصال بالإنترنت (Offline-First).",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "5. توقيع الهاش غير القابل للتزوير: أي تعديل في نص الوصية يُبطل الهاش التشفيري السابق ويولد إصداراً جديداً مؤرخاً مع بصمة SHA-256 جديدة.",
                                style = MaterialTheme.typography.bodySmall
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
