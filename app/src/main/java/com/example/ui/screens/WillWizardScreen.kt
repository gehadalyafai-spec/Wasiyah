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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DebtItem
import com.example.data.model.SpecialRightItem
import com.example.data.model.TrustDepositItem
import com.example.data.model.WasiyyahDocument
import com.example.ui.components.SecurityChip
import com.example.ui.components.ShariaAlertBanner
import com.example.ui.components.StepProgressRow
import com.example.ui.theme.CyanCipher
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import java.text.NumberFormat
import java.util.Locale

@Composable
fun WillWizardScreen(
    document: WasiyyahDocument?,
    debts: List<DebtItem>,
    trusts: List<TrustDepositItem>,
    specialRights: List<SpecialRightItem>,
    currentStep: Int,
    onStepSelect: (Int) -> Unit,
    onNextStep: () -> Unit,
    onPrevStep: () -> Unit,
    onSaveDocument: (WasiyyahDocument) -> Unit,
    onSignAndSeal: (String, String, String, String, String, String, String) -> Unit,
    onAddDebt: (DebtItem) -> Unit,
    onDeleteDebt: (Int) -> Unit,
    onAddTrust: (TrustDepositItem) -> Unit,
    onDeleteTrust: (Int) -> Unit,
    onAddSpecialRight: (SpecialRightItem) -> Unit,
    onDeleteSpecialRight: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val stepTitles = listOf(
        "الشهادة والافتتاحية",
        "ديون عليّ",
        "ديون لي عند الغير",
        "الأمانات والودائع",
        "الحقوق والوصاية",
        "الوصية بالثلث",
        "المراجعة والتوقيع"
    )

    var showAddDebtDialog by remember { mutableStateOf<String?>(null) } // "ON_ME" or "FOR_ME"
    var showAddTrustDialog by remember { mutableStateOf(false) }
    var showAddRightDialog by remember { mutableStateOf(false) }

    val currentDoc = document ?: WasiyyahDocument()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Step progress header
        StepProgressRow(
            currentStep = currentStep,
            totalSteps = 7,
            stepTitles = stepTitles,
            onStepSelect = onStepSelect,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Wizard Step Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentStep) {
                0 -> StepPreamble(currentDoc, onSaveDocument)
                1 -> StepDebts(
                    title = "الديون التي عليّ للآخرين",
                    debts = debts.filter { it.type == "ON_ME" },
                    type = "ON_ME",
                    shariaNote = "الدين مقدّم على الوصية باتفاق الفقهاء: قال تعالى: {مِنْ بَعْدِ وَصِيَّةٍ يُوصَىٰ بِهَا أَوْ دَيْنٍ}، وقد قضى النبي ﷺ بالدين قبل الوصية.",
                    onAddDebtClick = { showAddDebtDialog = "ON_ME" },
                    onDeleteDebt = onDeleteDebt
                )
                2 -> StepDebts(
                    title = "الديون التي لي عند الآخرين",
                    debts = debts.filter { it.type == "FOR_ME" },
                    type = "FOR_ME",
                    shariaNote = "هذه الديون حق خالص لورثتك، بيانها وإثبات مستنداتها أمانة لضمان استيفاء حقوقهم بعد وفاتك دون نزاع.",
                    onAddDebtClick = { showAddDebtDialog = "FOR_ME" },
                    onDeleteDebt = onDeleteDebt
                )
                3 -> StepTrusts(
                    trusts = trusts,
                    onAddTrustClick = { showAddTrustDialog = true },
                    onDeleteTrust = onDeleteTrust
                )
                4 -> StepSpecialRights(
                    specialRights = specialRights,
                    onAddRightClick = { showAddRightDialog = true },
                    onDeleteRight = onDeleteSpecialRight
                )
                5 -> StepThirdBequest(currentDoc, onSaveDocument)
                6 -> StepReviewAndSign(
                    doc = currentDoc,
                    debts = debts,
                    trusts = trusts,
                    specialRights = specialRights,
                    onSignAndSeal = onSignAndSeal
                )
            }
        }

        // Navigation Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 0) {
                OutlinedButton(
                    onClick = onPrevStep,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("wizard_prev_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "السابق")
                }
            } else {
                Spacer(modifier = Modifier.width(10.dp))
            }

            if (currentStep < 6) {
                Button(
                    onClick = onNextStep,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("wizard_next_btn")
                ) {
                    Text(text = "التالي: ${stepTitles.getOrElse(currentStep + 1) { "" }}")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    // Add Debt Dialog
    if (showAddDebtDialog != null) {
        AddDebtDialog(
            debtType = showAddDebtDialog!!,
            onDismiss = { showAddDebtDialog = null },
            onConfirm = { debt ->
                onAddDebt(debt)
                showAddDebtDialog = null
            }
        )
    }

    // Add Trust Dialog
    if (showAddTrustDialog) {
        AddTrustDialog(
            onDismiss = { showAddTrustDialog = false },
            onConfirm = { trust ->
                onAddTrust(trust)
                showAddTrustDialog = false
            }
        )
    }

    // Add Special Right Dialog
    if (showAddRightDialog) {
        AddSpecialRightDialog(
            onDismiss = { showAddRightDialog = false },
            onConfirm = { right ->
                onAddSpecialRight(right)
                showAddRightDialog = false
            }
        )
    }
}

// STEP 1: PREAMBLE
@Composable
fun StepPreamble(
    doc: WasiyyahDocument,
    onSave: (WasiyyahDocument) -> Unit
) {
    var name by remember(doc) { mutableStateOf(doc.testatorName) }
    var nationalId by remember(doc) { mutableStateOf(doc.testatorNationalId) }
    var opening by remember(doc) { mutableStateOf(doc.testimonyOpening) }
    var directives by remember(doc) { mutableStateOf(doc.spiritualDirectives) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ShariaAlertBanner(
                title = "التوجيه الشرعي للافتتاحية",
                message = "تُفتتح الوصية بالشهادتين والوصية بتقوى الله وإصلاح ذات البين، واتباع سنة النبي ﷺ في الجنازة وترك البدع، وهي سنة نبوية متوارثة."
            )
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    onSave(doc.copy(testatorName = it))
                },
                label = { Text("الاسم الثلاثي أو الرباعي للموصي") },
                placeholder = { Text("مثال: فلان ابن فلان") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("testator_name_input"),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
            )
        }

        item {
            OutlinedTextField(
                value = nationalId,
                onValueChange = {
                    nationalId = it
                    onSave(doc.copy(testatorNationalId = it))
                },
                label = { Text("رقم الهوية الوطنية / السجل المدني") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("testator_id_input"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        item {
            Text(
                text = "صيغة الشهادة والافتتاحية الشرعية",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = opening,
                onValueChange = {
                    opening = it
                    onSave(doc.copy(testimonyOpening = it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .testTag("opening_text_input"),
                textStyle = MaterialTheme.typography.bodyMedium
            )
        }

        item {
            Text(
                text = "الوصايا الدينية والتجهيز والدفن",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = directives,
                onValueChange = {
                    directives = it
                    onSave(doc.copy(spiritualDirectives = it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .testTag("directives_text_input"),
                textStyle = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// STEP 2 & 3: DEBTS
@Composable
fun StepDebts(
    title: String,
    debts: List<DebtItem>,
    type: String,
    shariaNote: String,
    onAddDebtClick: () -> Unit,
    onDeleteDebt: (Int) -> Unit
) {
    val numberFormatter = NumberFormat.getNumberInstance(Locale("ar", "SA"))
    val totalAmount = debts.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ShariaAlertBanner(
                title = "حكم شرعي في الديون",
                message = shariaNote,
                isWarning = (type == "ON_ME")
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "الإجمالي: ${numberFormatter.format(totalAmount)} ر.س (${debts.size} بنود مسجلة)",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (type == "ON_ME") DangerRed else SuccessGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Button(
                    onClick = onAddDebtClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == "ON_ME") DangerRed else SuccessGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_debt_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "إضافة دين")
                }
            }
        }

        if (debts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لا توجد ديون مسجلة حالياً في هذا القسم",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(debts) { debt ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("debt_item_${debt.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = debt.counterpartyName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "المبلغ: ${numberFormatter.format(debt.amount)} ${debt.currency} • الاستحقاق: ${debt.dueDate}",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (type == "ON_ME") DangerRed else SuccessGreen,
                                fontWeight = FontWeight.Bold
                            )
                            if (debt.evidenceNotes.isNotBlank()) {
                                Text(
                                    text = "الإثبات: ${debt.evidenceNotes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (debt.contactInfo.isNotBlank()) {
                                Text(
                                    text = "التواصل: ${debt.contactInfo}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(
                            onClick = { onDeleteDebt(debt.id) },
                            modifier = Modifier.testTag("delete_debt_${debt.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "حذف",
                                tint = DangerRed.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// STEP 4: TRUSTS & DEPOSITS
@Composable
fun StepTrusts(
    trusts: List<TrustDepositItem>,
    onAddTrustClick: () -> Unit,
    onDeleteTrust: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ShariaAlertBanner(
                title = "وجوب رد الأمانات",
                message = "قال الله تعالى: {إِنَّ اللَّهَ يَأْمُرُكُمْ أَن تُؤَدُّوا الْأَمَانَاتِ إِلَىٰ أَهْلِهَا}. بيان الأمانات والودائع التي بحوزتك وتفاصيل أماكنها يمنع ضياع أموال الناس أو اختلاطها بالتركة."
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الأمانات والودائع بحوزتي (${trusts.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onAddTrustClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldSecondary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_trust_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "إضافة أمانة")
                }
            }
        }

        if (trusts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(text = "لا توجد أمانات أو ودائع مسجلة حالياً")
                    }
                }
            }
        } else {
            items(trusts) { trust ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "صاحب الأمانة: ${trust.ownerName}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "الوصف: ${trust.description}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "مكان الحفظ: ${trust.locationDetails}",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "تعليمات الرد: ${trust.returnInstructions}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onDeleteTrust(trust.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = DangerRed.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }
    }
}

// STEP 5: SPECIAL RIGHTS & GUARDIANSHIP
@Composable
fun StepSpecialRights(
    specialRights: List<SpecialRightItem>,
    onAddRightClick: () -> Unit,
    onDeleteRight: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ShariaAlertBanner(
                title = "الحقوق الخاصة والوصاية على القُصّر",
                message = "يشمل هذا البند تعيين الوصي المختار على الأولاد القُصّر، والنذور والكفارات، والعهود الأخلاقية، والشهادات، لضمان استمرار رعاية دينهم ودنياهم وأموالهم."
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الحقوق والوصايا الخاصة (${specialRights.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onAddRightClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanCipher),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_special_right_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "إضافة حق/وصاية")
                }
            }
        }

        items(specialRights) { right ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val catBadge = when (right.category) {
                                "GUARDIANSHIP" -> "وصاية على قُصّر"
                                "VOW" -> "نذر أو كفارة"
                                "SPONSORSHIP" -> "كفالة أيتام"
                                else -> "عهد وشهادة"
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldContainer
                            ) {
                                Text(
                                    text = catBadge,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = right.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = right.details,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "المسؤول المكلّف: ${right.designatedPerson}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = { onDeleteRight(right.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = DangerRed.copy(alpha = 0.8f))
                    }
                }
            }
        }
    }
}

// STEP 6: THIRD BEQUEST (الوصية بالثلث)
@Composable
fun StepThirdBequest(
    doc: WasiyyahDocument,
    onSave: (WasiyyahDocument) -> Unit
) {
    var wealthInput by remember(doc) { mutableStateOf(if (doc.totalEstimatedWealth > 0) doc.totalEstimatedWealth.toInt().toString() else "") }
    var bequestInput by remember(doc) { mutableStateOf(if (doc.thirdBequestAmount > 0) doc.thirdBequestAmount.toInt().toString() else "") }
    var beneficiary by remember(doc) { mutableStateOf(doc.thirdBequestBeneficiary) }
    var purpose by remember(doc) { mutableStateOf(doc.thirdBequestPurpose) }
    var consentObtained by remember(doc) { mutableStateOf(doc.heirsConsentObtained) }

    val wealth = wealthInput.toDoubleOrNull() ?: 0.0
    val bequest = bequestInput.toDoubleOrNull() ?: 0.0
    val maxThird = if (wealth > 0) wealth / 3.0 else 0.0
    val percentage = if (wealth > 0) (bequest / wealth) * 100 else 0.0
    val isExceedingThird = wealth > 0 && bequest > maxThird

    val numberFormatter = NumberFormat.getNumberInstance(Locale("ar", "SA"))

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ShariaAlertBanner(
                title = "قاعدة شرعية: لا وصية لوارث، والثلث والثلث كثير",
                message = "عن سعد بن أبي وقاص رضي الله عنه قال: قلت: يا رسول الله، أُوصِي بِمَالِي كُلِّهِ؟ قَالَ: «لَا»، قُلْتُ: فَالشَّطْرِ؟ قَالَ: «لَا»، قُلْتُ: فَالثُّلُثِ؟ قَالَ: «فَالثُّلُثُ، وَالثُّلُثُ كَثِيرٌ». كما لا تجوز الوصية لوارث إلا بموافقة جميع الورثة الراشدين بعد الوفاة.",
                isWarning = false
            )
        }

        // Live Calculator Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isExceedingThird) WarningAmber else EmeraldPrimary,
                        RoundedCornerShape(14.dp)
                    ),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isExceedingThird) WarningAmber.copy(alpha = 0.08f) else GoldContainer.copy(alpha = 0.4f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "حاسبة نسبة الثلث الشرعي",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${"%.1f".format(percentage)}% من التركة",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isExceedingThird) DangerRed else SuccessGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "الحد الأقصى للثلث المسموح به شرعاً: ${numberFormatter.format(maxThird)} ر.س",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isExceedingThird) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚠️ تنبيه شرعي صريح: المبلغ المحدد يتجاوز الثلث بمقدار ${numberFormatter.format(bequest - maxThird)} ر.س. الزائد عن الثلث يتوقف نفاذه شرعاً على إجازة وموافقة الورثة.",
                            style = MaterialTheme.typography.labelSmall,
                            color = DangerRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = wealthInput,
                onValueChange = {
                    wealthInput = it
                    val w = it.toDoubleOrNull() ?: 0.0
                    onSave(doc.copy(totalEstimatedWealth = w))
                },
                label = { Text("تقدير إجمالي الثروة / التركة التقريبية (ر.س)") },
                placeholder = { Text("مثال: 500000") },
                modifier = Modifier.fillMaxWidth().testTag("wealth_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = bequestInput,
                onValueChange = {
                    bequestInput = it
                    val b = it.toDoubleOrNull() ?: 0.0
                    val isExceed = b > (wealth / 3.0)
                    onSave(doc.copy(thirdBequestAmount = b, heirsConsentRequired = isExceed))
                },
                label = { Text("مبلغ أو قيمة الوصية بالثلث (ر.س)") },
                placeholder = { Text("مثال: 100000") },
                modifier = Modifier.fillMaxWidth().testTag("bequest_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = beneficiary,
                onValueChange = {
                    beneficiary = it
                    onSave(doc.copy(thirdBequestBeneficiary = it))
                },
                label = { Text("المستفيد (جهة خيرية، وقف، شخص من غير الورثة)") },
                placeholder = { Text("مثال: جمعية الأيتام / فلان ابن فلان") },
                modifier = Modifier.fillMaxWidth().testTag("beneficiary_input")
            )
        }

        item {
            OutlinedTextField(
                value = purpose,
                onValueChange = {
                    purpose = it
                    onSave(doc.copy(thirdBequestPurpose = it))
                },
                label = { Text("مصرف الوصية (صدقة جارية، بناء مسجد، طباعة مصاحف...)") },
                placeholder = { Text("مثال: بناء مسجد أو صدقة جارية") },
                modifier = Modifier.fillMaxWidth().testTag("purpose_input")
            )
        }

        if (isExceedingThird) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(WarningAmber.copy(alpha = 0.12f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = consentObtained,
                        onCheckedChange = {
                            consentObtained = it
                            onSave(doc.copy(heirsConsentObtained = it))
                        },
                        colors = CheckboxDefaults.colors(checkedColor = WarningAmber)
                    )
                    Text(
                        text = "أقر بأنني أطلعت الورثة على الزيادة عن الثلث، أو أن نفاذ ما زاد مشروط برضاهم بعد وفاتي.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// STEP 7: REVIEW AND SIGN
@Composable
fun StepReviewAndSign(
    doc: WasiyyahDocument,
    debts: List<DebtItem>,
    trusts: List<TrustDepositItem>,
    specialRights: List<SpecialRightItem>,
    onSignAndSeal: (String, String, String, String, String, String, String) -> Unit
) {
    var signatureName by remember { mutableStateOf(doc.electronicSignature.ifBlank { doc.testatorName }) }
    var w1Name by remember { mutableStateOf(doc.witness1Name) }
    var w1Id by remember { mutableStateOf(doc.witness1NationalId) }
    var w1Phone by remember { mutableStateOf(doc.witness1Phone) }
    var w2Name by remember { mutableStateOf(doc.witness2Name) }
    var w2Id by remember { mutableStateOf(doc.witness2NationalId) }
    var w2Phone by remember { mutableStateOf(doc.witness2Phone) }

    var isConfirmedLegal by remember { mutableStateOf(true) }

    val numberFormatter = NumberFormat.getNumberInstance(Locale("ar", "SA"))

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "المراجعة النهائية والتوقيع الرقمي وإرفاق الشاهدين",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "بإتمام هذه الخطوة يتم تشفير الوصية وتوليد بصمة الهاش الرقمية وتفعيل بروتوكول نبض الحياة.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD3E8DF)
                    )
                }
            }
        }

        // Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "ملخص بنود الوثيقة",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Divider()
                    val displayName = doc.testatorName.ifBlank { "(فلان ابن فلان)" }
                    val displayId = doc.testatorNationalId.ifBlank { "غير مسجلة" }
                    val displayBeneficiary = doc.thirdBequestBeneficiary.ifBlank { "غير محدد" }
                    Text(text = "• الموصي: $displayName (هوية: $displayId)")
                    Text(text = "• الديون التي عليه: ${debts.filter { it.type == "ON_ME" }.size} بنود بإجمالي ${numberFormatter.format(debts.filter { it.type == "ON_ME" }.sumOf { it.amount })} ر.س")
                    Text(text = "• الديون التي له عند الغير: ${debts.filter { it.type == "FOR_ME" }.size} بنود بإجمالي ${numberFormatter.format(debts.filter { it.type == "FOR_ME" }.sumOf { it.amount })} ر.س")
                    Text(text = "• الأمانات والودائع: ${trusts.size} أمانة مسجلة")
                    Text(text = "• الحقوق والوصايا الخاصة: ${specialRights.size} بنود")
                    Text(text = "• الوصية بالثلث: ${numberFormatter.format(doc.thirdBequestAmount)} ر.س لصالح ($displayBeneficiary)")
                }
            }
        }

        // Signature Canvas simulation
        item {
            Text(
                text = "التوقيع الإلكتروني للموصي",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = signatureName,
                onValueChange = { signatureName = it },
                label = { Text("اكتب اسمك الكامل كتوقيع إلكتروني موثق") },
                modifier = Modifier.fillMaxWidth().testTag("electronic_signature_input"),
                leadingIcon = { Icon(Icons.Default.Draw, contentDescription = null, tint = EmeraldPrimary) }
            )
        }

        // Two Upright Witnesses
        item {
            Text(
                text = "بيانات الشاهدين العدلين (شهادة عدل موثقة)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "استحباب الإشهاد على الوصية: {يَا أَيُّهَا الَّذِينَ آمَنُوا شَهَادَةُ بَيْنِكُمْ إِذَا حَضَرَ أَحَدَكُمُ الْمَوْتُ حِينَ الْوَصِيَّةِ اثْنَانِ ذَوَا عَدْلٍ مِّنكُمْ}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Witness 1
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "الشاهد الأول", fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                    OutlinedTextField(
                        value = w1Name,
                        onValueChange = { w1Name = it },
                        label = { Text("اسم الشاهد الأول") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = w1Id,
                            onValueChange = { w1Id = it },
                            label = { Text("رقم الهوية") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = w1Phone,
                            onValueChange = { w1Phone = it },
                            label = { Text("رقم الجوال") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Witness 2
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "الشاهد الثاني", fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                    OutlinedTextField(
                        value = w2Name,
                        onValueChange = { w2Name = it },
                        label = { Text("اسم الشاهد الثاني") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = w2Id,
                            onValueChange = { w2Id = it },
                            label = { Text("رقم الهوية") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = w2Phone,
                            onValueChange = { w2Phone = it },
                            label = { Text("رقم الجوال") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isConfirmedLegal,
                    onCheckedChange = { isConfirmedLegal = it },
                    colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                )
                Text(
                    text = "أقر بصحة جميع البيانات الواردة، وأعلم أن التطبيق أداة تنظيمية مشفرة وليس صكاً قضائياً ملزماً بدون مصادقة رسمية.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item {
            Button(
                onClick = {
                    onSignAndSeal(signatureName, w1Name, w1Id, w1Phone, w2Name, w2Id, w2Phone)
                },
                enabled = isConfirmedLegal && signatureName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("sign_and_seal_btn")
            ) {
                Icon(Icons.Default.Security, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "توثيق وتشفير وختم الوصية رسمياً",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// DIALOGS
@Composable
fun AddDebtDialog(
    debtType: String,
    onDismiss: () -> Unit,
    onConfirm: (DebtItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (debtType == "ON_ME") "تسجيل دين عليّ للآخرين" else "تسجيل دين لي عند الغير",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (debtType == "ON_ME") "اسم الدائن (صاحب الحق)" else "اسم المدين") },
                    placeholder = { Text("مثال: فلان ابن فلان") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("المبلغ (ر.س)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("تاريخ الاستحقاق (هجري أو ميلادي)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("المستندات والإثباتات (سند لأمر، شهود، عقد)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("رقم التواصل") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && amt > 0) {
                        onConfirm(
                            DebtItem(
                                type = debtType,
                                counterpartyName = name,
                                amount = amt,
                                currency = "SAR",
                                dueDate = dueDate.ifBlank { "غير محدد" },
                                evidenceNotes = notes,
                                contactInfo = contact
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("حفظ الدين")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun AddTrustDialog(
    onDismiss: () -> Unit,
    onConfirm: (TrustDepositItem) -> Unit
) {
    var ownerName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة أمانة أو وديعة بحوزتك", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("اسم صاحب الأمانة") },
                    placeholder = { Text("مثال: فلان ابن فلان") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("وصف الأمانة (صك، مجوهرات، مبلغ نقدي)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("مكان حفظها بالتفصيل والرمز السري") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("تعليمات التسليم والرد") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم تواصل صاحب الأمانة") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (ownerName.isNotBlank() && description.isNotBlank()) {
                        onConfirm(
                            TrustDepositItem(
                                ownerName = ownerName,
                                description = description,
                                locationDetails = location,
                                returnInstructions = instructions,
                                ownerPhone = phone
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldSecondary)
            ) {
                Text("حفظ الأمانة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun AddSpecialRightDialog(
    onDismiss: () -> Unit,
    onConfirm: (SpecialRightItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var designatedPerson by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("GUARDIANSHIP") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة حق خاص أو وصاية", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "اختر التصنيف:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        "GUARDIANSHIP" to "وصاية قُصّر",
                        "SPONSORSHIP" to "كفالة يتيم",
                        "VOW" to "نذر/كفارة"
                    ).forEach { (cat, label) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (category == cat) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { category = cat }
                        ) {
                            Text(
                                text = label,
                                color = if (category == cat) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان البند") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("التفاصيل والتعليمات الشرعية") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = designatedPerson,
                    onValueChange = { designatedPerson = it },
                    label = { Text("الشخص المكلّف بالتنفيذ (الوصي / الناظر)") },
                    placeholder = { Text("مثال: فلان ابن فلان") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            SpecialRightItem(
                                category = category,
                                title = title,
                                details = details,
                                designatedPerson = designatedPerson
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanCipher)
            ) {
                Text("حفظ الحق")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
