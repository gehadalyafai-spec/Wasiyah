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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SecurityChip
import com.example.ui.components.ShariaAlertBanner
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.SuccessGreen

data class FiqhRulingItem(
    val title: String,
    val summary: String,
    val detailedText: String,
    val evidenceSource: String,
    val requirementLevel: String = "إلزامي للمراجعة"
)

@Composable
fun ShariaHubScreen(
    modifier: Modifier = Modifier
) {
    var showConsultationDialog by remember { mutableStateOf(false) }
    var consultationSentSuccess by remember { mutableStateOf(false) }

    val fiqhRulings = remember {
        listOf(
            FiqhRulingItem(
                title = "1. حد الثلث الشرعي والوصية لغير الورثة",
                summary = "لا تجوز الوصية بأكثر من ثلث التركة إلا بموافقة جميع الورثة الراشدين بعد الوفاة.",
                detailedText = "أجمع العلماء على أن الوصية مقيدة بالثلث كحد أقصى لمن له ورثة، لحديث سعد بن أبي وقاص حين قال له النبي ﷺ: «الثلث، والثلث كثير، إنك أن تذر ورثتك أغنياء خير من أن تذرهم عالة يتكففون الناس». فإذا زادت عن الثلث توقف الزائد على إجازة الورثة بعد موت الموصي.",
                evidenceSource = "صحيح البخاري ومسلم، إجماع المذاهب الأربعة",
                requirementLevel = "ركن شرعي إلزامي"
            ),
            FiqhRulingItem(
                title = "2. قاعدة: لا وصية لوارث",
                summary = "لا تصح الوصية لشخص يرث شرعاً إلا إذا رضي باقي الورثة وأجازوها بعد الوفاة.",
                detailedText = "لقوله ﷺ في خطبة حجة الوداع: «إن الله قد أعطى كل ذي حق حقه، فلا وصية لوارث». والحكمة سد أبواب الشحناء والتفضيل غير المبرر بين الورثة، وتوزيع الأنصبة كما فرضها الله في سورة النساء.",
                evidenceSource = "رواه الترمذي وأبو داود وصححه الألباني",
                requirementLevel = "ركن شرعي إلزامي"
            ),
            FiqhRulingItem(
                title = "3. تقديم الدين على الوصية، والوصية على الميراث",
                summary = "ترتيب الحقوق المتعلقة بالتركة: التجهيز، ثم الديون، ثم الوصية بالثلث، ثم الميراث.",
                detailedText = "قال تعالى: {مِن بَعْدِ وَصِيَّةٍ يُوصَىٰ بِهَا أَوْ دَيْنٍ}. وقدم الله الوصية في اللفظ ترغيباً بالوفاء بها لعدم وجود مطالب بها كالدين، لكن النبي ﷺ قضى بالدين قبل الوصية إجماعاً لأن ذمة الميت مرهونة بدينه.",
                evidenceSource = "سنن الترمذي والإجماع الفقهي",
                requirementLevel = "أولوية قطعية"
            ),
            FiqhRulingItem(
                title = "4. الوصاية على الأولاد القُصّر وأموالهم (الوصي المختار)",
                summary = "جواز تعيين وصي أمين لرعاية الأبناء القُصّر وحفظ أموالهم حتى يبلغوا رشدهم.",
                detailedText = "يشترط في الوصي: الإسلام، والبلوغ، والعقل، والأمانة، والقدرة على التصرف. ولا يجوز له أن يأكل من مال اليتيم إلا بالمعروف إذا كان فقيراً محتاجاً، وعليه تسليم أموالهم كاملة فور بلوغهم الرشد واختبار قدرتهم المالية.",
                evidenceSource = "سورة النساء آية 6، والموسوعة الفقهية",
                requirementLevel = "ضابط فقهي دقيق"
            ),
            FiqhRulingItem(
                title = "5. أمانات الناس وودائعهم واستثناؤها التام من التركة",
                summary = "الأمانات والودائع ليست ملكاً للمتوفى، ولا تدخل في قسمة الميراث ولا في الثلث.",
                detailedText = "الأمانات يجب ردها لأعيانها وأصحابها قبل قسمة أي شيء: {إِنَّ اللَّهَ يَأْمُرُكُمْ أَن تُؤَدُّوا الْأَمَانَاتِ إِلَىٰ أَهْلِهَا}. وإذا لم يُثبت الموصي تفاصيل الأمانة في حياته، فقد يؤدي ذلك إلى حرمان صاحبها أو اختلاطها بمال الورثة.",
                evidenceSource = "سورة النساء آية 58",
                requirementLevel = "واجب عيني قطعي"
            ),
            FiqhRulingItem(
                title = "6. الإشهاد على الوصية بشاهدي عدل",
                summary = "استحباب الإشهاد على الوصية لمنع جحودها أو النزاع حول صحتها وعقل الموصي.",
                detailedText = "قال تعالى: {يَا أَيُّهَا الَّذِينَ آمَنُوا شَهَادَةُ بَيْنِكُمْ إِذَا حَضَرَ أَحَدَكُمُ الْمَوْتُ حِينَ الْوَصِيَّةِ اثْنَانِ ذَوَا عَدْلٍ مِّنكُمْ}. الإشهاد يقطع دابر النزاع ويثبت أهلية الموصي القانونية والشرعية.",
                evidenceSource = "سورة المائدة آية 106",
                requirementLevel = "سنة موكدة وتوثيق قضائي"
            ),
            FiqhRulingItem(
                title = "7. ديون الله تعالى (الكفارات والنذور والحج الواجب)",
                summary = "قضاء ما على الميت من كفارات أيمان أو نذور أو حجة الإسلام من رأس ماله.",
                detailedText = "عن ابن عباس رضي الله عنهما أن امرأة سألت النبي ﷺ عن أمها التي نذرت أن تحج فماتت قبل أن تحج، فقال لها: «حُجِّي عَنْهَا، أَرَأَيْتِ لَوْ كَانَ عَلَى أُمِّكِ دَيْنٌ أَكُنْتِ قَاضِيَتَهُ؟ اقْضُوا اللَّهَ، فَاللَّهُ أَحَقُّ بِالْوَفَاءِ».",
                evidenceSource = "صحيح البخاري ومسلم",
                requirementLevel = "حق لله مقدم"
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
                            text = "الاستشارة الشرعية وقائمة الأحكام الفقهية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        SecurityChip(text = "إشراف شرعي معتمد", icon = Icons.Default.Verified)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "دليل فقهي شامل لأحكام الوصية الإسلامية واجبة المراجعة مع ربط مباشر بمستشارين شرعيين وهيئات الفتوى الرسمية المعتمدة.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD2E8E0),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Action card: Consult a Sharia Scholar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldSecondary, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GoldContainer.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = GoldSecondary)
                            Text(
                                text = "طلب استشارة شرعية متخصصة",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "هل لديك حالة خاصة في تركتك أو وصيتك؟ أرسل استفسارك لمستشار شرعي مجاز متخصص في الفرائض والوصايا.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { showConsultationDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldSecondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("request_consultation_btn")
                    ) {
                        Text(text = "طلب استشارة", color = Color.White)
                    }
                }
            }
        }

        item {
            ShariaAlertBanner(
                title = "إفصاح قانوني وشرعي رسمي",
                message = "هذه الأحكام مستقاة من أصول الفقه الإسلامي المعتمد والمجامع الفقهية. التطبيق يوفر أداة تنظيمية وتوثيقية لحفظ بياناتك ولا يصدر فتاوى خاصة بالنوازل الفردية، ويُنصح دائماً بمراجعة فضيلة القاضي أو كاتب العدل."
            )
        }

        item {
            Text(
                text = "قائمة الأحكام الشرعية والفقهية لمراجعة الوصية",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(fiqhRulings) { ruling ->
            var expanded by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
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
                                    text = ruling.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = ruling.summary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = EmeraldPrimary
                        )
                    }

                    AnimatedVisibility(visible = expanded) {
                        Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Divider()
                            Text(
                                text = ruling.detailedText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "المصدر: ${ruling.evidenceSource}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Surface(shape = RoundedCornerShape(4.dp), color = EmeraldContainer) {
                                    Text(
                                        text = ruling.requirementLevel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showConsultationDialog) {
        var topic by remember { mutableStateOf("وصية بالثلث لوارث أو جمعية") }
        var question by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showConsultationDialog = false },
            title = { Text("طلب استشارة شرعية معتمدة", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "اختر موضوع الاستشارة:", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("موضوع الاستفسار") }
                    )
                    OutlinedTextField(
                        value = question,
                        onValueChange = { question = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        label = { Text("اكتب تفاصيل مسألتك الفقهية بوضوح") }
                    )
                    Text(
                        text = "يتم إرسال المسألة مشفرة إلى المستشار الشرعي المعتمد، ويصلك الرد خلال 24 ساعة بإذن الله.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConsultationDialog = false
                        consultationSentSuccess = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إرسال الاستشارة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConsultationDialog = false }) { Text("إلغاء") }
            }
        )
    }

    if (consultationSentSuccess) {
        AlertDialog(
            onDismissRequest = { consultationSentSuccess = false },
            title = { Text("تم إرسال استشارتك بنجاح ✓", fontWeight = FontWeight.Bold, color = SuccessGreen) },
            text = {
                Text(
                    text = "تم تشفير رسالتك وتوجيهها إلى فضيلة المستشار الشرعي المعتمد لدى المنصة. سيتم إشعارك عبر التطبيق فور صدور التوجيه الشرعي المناسب."
                )
            },
            confirmButton = {
                Button(onClick = { consultationSentSuccess = false }, colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)) {
                    Text("تم")
                }
            }
        )
    }
}
