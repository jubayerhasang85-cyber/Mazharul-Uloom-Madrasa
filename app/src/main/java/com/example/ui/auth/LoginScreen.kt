package com.example.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.MadrasaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: MadrasaViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("ছাত্র", "সহকারী শিক্ষক", "প্রধান শিক্ষক", "অ্যাপ নির্মাতা")

    // Form states
    // Student
    var studentPhone by remember { mutableStateOf("01711000001") }
    var studentClass by remember { mutableStateOf("ইবতেদায়ী ৪র্থ শ্রেণি") }
    var studentRoll by remember { mutableStateOf("1") }
    var classDropdownExpanded by remember { mutableStateOf(false) }

    // Assistant Teacher
    var teacherPhone by remember { mutableStateOf("01811000002") }
    var teacherIlhaq by remember { mutableStateOf("IL-2024") }
    var teacherPin by remember { mutableStateOf("1234") }

    // Headmaster
    var headmasterPhone by remember { mutableStateOf("01911000003") }
    var headmasterPin by remember { mutableStateOf("7860") }

    // Admin / Creator
    var adminEmail by remember { mutableStateOf("jubayerhasang85@gmail.com") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MadrasaBackground)
            .verticalScroll(scrollState)
    ) {
        // Top Header Banner (সবুজ ব্যাকগ্রাউন্ডে সাদা টেক্সট)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MadrasaGreenPrimary)
                .padding(top = 40.dp, bottom = 28.dp, start = 20.dp, end = 20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Crest / App Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MadrasaGreenSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_icon_fg_1790886093135),
                        contentDescription = "মাদ্রাসা লোগো",
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "মাযহারুল উলূম মাদ্রাসা",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasaTextWhite,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "দীনী ও আধুনিক শিক্ষার সমন্বিত দ্বীনি শিক্ষাপ্রতিষ্ঠান",
                    fontSize = 13.sp,
                    color = MadrasaTextWhite.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MadrasaGold,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "লগইন ও পোর্টাল অ্যাক্সেস",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MadrasaTextWhite,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Role Tabs (সাদা ব্যাকগ্রাউন্ডে কালো টেক্সট ও সবুজ হাইলাইট)
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MadrasaSurface,
            contentColor = MadrasaGreenPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        selectedTab = index
                        errorMessage = null
                    },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    },
                    modifier = Modifier.testTag("tab_role_$index")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Login Card (সাদা ব্যাকগ্রাউন্ডে কালো টেক্সট)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MadrasaSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Role description pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = when (selectedTab) {
                            0 -> Icons.Default.School
                            1 -> Icons.Default.Person
                            2 -> Icons.Default.AccountBalance
                            else -> Icons.Default.AdminPanelSettings
                        },
                        contentDescription = null,
                        tint = MadrasaGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (selectedTab) {
                            0 -> "ছাত্র লগইন (ফোন, শ্রেণি ও রোল)"
                            1 -> "সহকারী শিক্ষক লগইন (ফোন, ইলহাক ও পিন)"
                            2 -> "প্রধান শিক্ষক লগইন (ফোন ও পিন)"
                            else -> "অ্যাপ নির্মাতা / অ্যাডমিন (গোপন ইমেল)"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MadrasaTextDark
                    )
                }

                // Error alert
                if (errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Tab Specific Inputs
                when (selectedTab) {
                    0 -> {
                        // ছাত্র লগইন: ফোন নাম্বার, শ্রেণি ও রোল
                        OutlinedTextField(
                            value = studentPhone,
                            onValueChange = { studentPhone = it },
                            label = { Text("অভিভাবক / ছাত্রের ফোন নম্বর") },
                            placeholder = { Text("01711000001") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("student_phone_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // শ্রেণি ড্রপডাউন
                        ExposedDropdownMenuBox(
                            expanded = classDropdownExpanded,
                            onExpandedChange = { classDropdownExpanded = !classDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = studentClass,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("শ্রেণি বা বিভাগ নির্বাচন করুন") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = classDropdownExpanded,
                                onDismissRequest = { classDropdownExpanded = false }
                            ) {
                                viewModel.classList.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item) },
                                        onClick = {
                                            studentClass = item
                                            classDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = studentRoll,
                            onValueChange = { studentRoll = it },
                            label = { Text("শ্রেণি রোল নম্বর") },
                            placeholder = { Text("1") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("student_roll_input")
                        )
                    }

                    1 -> {
                        // সহকারী শিক্ষক লগইন: ফোন নাম্বার, ইলহাক নাম্বার, ও পিন
                        OutlinedTextField(
                            value = teacherPhone,
                            onValueChange = { teacherPhone = it },
                            label = { Text("শিক্ষকের ফোন নম্বর") },
                            placeholder = { Text("01811000002") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("teacher_phone_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = teacherIlhaq,
                            onValueChange = { teacherIlhaq = it },
                            label = { Text("মাদ্রাসার ইলহাক নম্বর") },
                            placeholder = { Text("IL-2024") },
                            leadingIcon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("teacher_ilhaq_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = teacherPin,
                            onValueChange = { teacherPin = it },
                            label = { Text("গোপন ৪ সংখ্যার পিন") },
                            placeholder = { Text("1234") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("teacher_pin_input")
                        )
                    }

                    2 -> {
                        // প্রধান শিক্ষক লগইন: ফোন নাম্বার ও পিন
                        OutlinedTextField(
                            value = headmasterPhone,
                            onValueChange = { headmasterPhone = it },
                            label = { Text("প্রধান শিক্ষকের ফোন নম্বর") },
                            placeholder = { Text("01911000003") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("headmaster_phone_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = headmasterPin,
                            onValueChange = { headmasterPin = it },
                            label = { Text("গোপন পিন কোড") },
                            placeholder = { Text("7860") },
                            leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("headmaster_pin_input")
                        )
                    }

                    3 -> {
                        // অ্যাপ নির্মাতার লগইন: গোপন ইমেল ঠিকানা
                        OutlinedTextField(
                            value = adminEmail,
                            onValueChange = { adminEmail = it },
                            label = { Text("অ্যাপ নির্মাতার গোপন ইমেল ঠিকানা") },
                            placeholder = { Text("jubayerhasang85@gmail.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_email_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "পরামর্শ: 'jubayerhasang85@gmail.com' প্রবেশ করিয়ে সরাসরি সুপার অ্যাডমিন ও আপডেট লগ অ্যাক্সেস করুন।",
                            fontSize = 12.sp,
                            color = MadrasaTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Login Button (সবুজ ব্যাকগ্রাউন্ডে সাদা টেক্সট)
                Button(
                    onClick = {
                        when (selectedTab) {
                            0 -> viewModel.loginAsStudent(studentPhone, studentClass, studentRoll) { ok, msg ->
                                if (!ok) errorMessage = msg
                            }
                            1 -> viewModel.loginAsTeacher(teacherPhone, teacherIlhaq, teacherPin) { ok, msg ->
                                if (!ok) errorMessage = msg
                            }
                            2 -> viewModel.loginAsHeadmaster(headmasterPhone, headmasterPin) { ok, msg ->
                                if (!ok) errorMessage = msg
                            }
                            3 -> viewModel.loginAsAdmin(adminEmail) { ok, msg ->
                                if (!ok) errorMessage = msg
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MadrasaGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_submit_button")
                ) {
                    Text(
                        text = "লগইন করুন",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MadrasaTextWhite
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fast One-Click Demo Login
                OutlinedButton(
                    onClick = {
                        when (selectedTab) {
                            0 -> viewModel.quickRoleSwitch(UserRole.STUDENT)
                            1 -> viewModel.quickRoleSwitch(UserRole.ASSISTANT_TEACHER)
                            2 -> viewModel.quickRoleSwitch(UserRole.HEADMASTER)
                            3 -> viewModel.quickRoleSwitch(UserRole.ADMIN)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = MadrasaGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "এক ক্লিকে ডেমো প্রবেশ (${tabs[selectedTab]})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MadrasaTextDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Info footer
        Text(
            text = "মাযহারুল উলূম মাদ্রাসা তথ্য ব্যবস্থাপনা সিস্টেম • সংস্করণ ১.২.০",
            fontSize = 12.sp,
            color = MadrasaTextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}
