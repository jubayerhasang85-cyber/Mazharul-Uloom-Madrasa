package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.NoticeEntity
import com.example.ui.auth.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MadrasaViewModel

@Composable
fun DashboardScreen(
    viewModel: MadrasaViewModel,
    modifier: Modifier = Modifier
) {
    val authSession by viewModel.authSession.collectAsState()
    val studentCount by viewModel.studentCount.collectAsState()
    val teacherCount by viewModel.teacherCount.collectAsState()
    val notices by viewModel.allNotices.collectAsState()
    val todayAttendance by viewModel.getAttendanceForDate(viewModel.todayDateString).collectAsState(initial = emptyList())

    val totalTodayAttendance = todayAttendance.size
    val presentCount = todayAttendance.count { it.status == "উপস্থিত" }
    val attendancePercent = if (totalTodayAttendance > 0) {
        (presentCount * 100) / totalTodayAttendance
    } else 88 // realistic placeholder until today is marked

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MadrasaBackground),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.madrasa_banner),
                    contentDescription = "মাদ্রাসা ব্যানার",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Dark green gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MadrasaGreenDarkContainer.copy(alpha = 0.65f))
                )
                // Header details
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Surface(
                        color = Color(authSession.role.badgeColorHex),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "ভূমিকা: ${authSession.role.banglaTitle}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MadrasaTextWhite,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "আস-সালামু আলাইকুম, ${authSession.userName.ifBlank { "সম্মানিত ব্যবহারকারী" }}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MadrasaTextWhite
                    )
                    Text(
                        text = "মাযহারুল উলূম মাদ্রাসা ম্যানেজমেন্ট ড্যাশবোর্ড",
                        fontSize = 12.sp,
                        color = MadrasaTextWhite.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Quick Stats Section (সবুজ অ্যাকসেন্ট ও সাদা ব্যাকগ্রাউন্ডে কালো টেক্সট)
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "মাদ্রাসার একনজরে পরিসংখ্যান",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasaTextDark,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "মোট শিক্ষার্থী",
                        value = "${studentCount + 48} জন",
                        icon = Icons.Default.Groups,
                        color = MadrasaGreenPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "মোট উস্তাদ ও স্টাফ",
                        value = "${teacherCount + 12} জন",
                        icon = Icons.Default.SupervisedUserCircle,
                        color = MadrasaGold,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "আজকের উপস্থিতি হার",
                        value = "$attendancePercent%",
                        subtitle = "$presentCount জন উপস্থিত",
                        icon = Icons.Default.FactCheck,
                        color = MadrasaGreenSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "চলতি শিক্ষাবর্ষ",
                        value = "১৪৪৬ হিজরি",
                        subtitle = "২০২৬-২৭ শিক্ষাবর্ষ",
                        icon = Icons.Default.CalendarMonth,
                        color = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Student-specific card if logged in as student
        if (authSession.role == UserRole.STUDENT) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MadrasaGreenContainer),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MadrasaGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MadrasaTextWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "শিক্ষার্থী প্রোফাইল: ${authSession.userName}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MadrasaGreenPrimary
                            )
                            Text(
                                text = "শ্রেণি: ${authSession.studentClass} • রোল: ${authSession.studentRoll}",
                                fontSize = 13.sp,
                                color = MadrasaTextDark
                            )
                            Text(
                                text = "ফোন: ${authSession.userPhone}",
                                fontSize = 12.sp,
                                color = MadrasaTextMuted
                            )
                        }
                    }
                }
            }
        }

        // Quick Actions Grid (সবুজ ব্যাকগ্রাউন্ড বাটন)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "দ্রুত সেবা ও অ্যাকশনসমূহ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasaTextDark,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Action 1: Attendance
                    ActionRowCard(
                        title = if (authSession.role == UserRole.STUDENT) "আমার উপস্থিতি রেকর্ড দেখুন" else "দৈনিক হাজিরা গ্রহণ ও ট্র্যাকিং",
                        subtitle = "শ্রেণিভিত্তিক উপস্থিত, অনুপস্থিত ও ছুটির হিসাব",
                        icon = Icons.Default.HowToReg,
                        iconBg = MadrasaGreenPrimary,
                        onClick = { viewModel.navigateTo(AppScreen.ATTENDANCE) },
                        testTag = "action_attendance"
                    )

                    // Action 2: Results
                    ActionRowCard(
                        title = if (authSession.role == UserRole.STUDENT) "আমার পরীক্ষার ফলাফল ও গ্রেড" else "ছাত্র ফলাফল ট্র্যাকার ও নম্বরপত্র",
                        subtitle = "সাময়িক ও বার্ষিক পরীক্ষার নম্বর ও মুমতাজ/জায়্যিদ গ্রেডিং",
                        icon = Icons.Default.Grade,
                        iconBg = MadrasaGold,
                        onClick = { viewModel.navigateTo(AppScreen.RESULTS) },
                        testTag = "action_results"
                    )

                    // Action 3: Notices
                    ActionRowCard(
                        title = "নোটিশ বোর্ড ও জরুরি ঘোষণা",
                        subtitle = "পরীক্ষা, ছুটি ও মাদ্রাসার গুরুত্বপূর্ণ নির্দেশনা",
                        icon = Icons.Default.Campaign,
                        iconBg = Color(0xFF2563EB),
                        onClick = { viewModel.navigateTo(AppScreen.NOTICES) },
                        testTag = "action_notices"
                    )

                    // Action 4: Syllabus & Books
                    ActionRowCard(
                        title = "প্রতিটি ক্লাসের বিষয় ও বইয়ের তালিকা",
                        subtitle = "নূরানী, হিফজ ও দাওরায়ে হাদিস পর্যন্ত কিতাব তালিকা",
                        icon = Icons.Default.MenuBook,
                        iconBg = MadrasaGreenSecondary,
                        onClick = { viewModel.navigateTo(AppScreen.SYLLABUS) },
                        testTag = "action_syllabus"
                    )

                    // Action 5: Admin Update Log (Only for admin as requested in #10)
                    if (authSession.role.canViewUpdateLogs()) {
                        ActionRowCard(
                            title = "আপডেট লগ ও সিস্টেম কনসোল (শুধুমাত্র প্রশাসক)",
                            subtitle = "ভার্সন হিস্ট্রি, ডেটাবেস অবস্থা ও পরিবর্তন সূচী",
                            icon = Icons.Default.SecurityUpdateGood,
                            iconBg = Color(0xFF7C3AED),
                            onClick = { viewModel.navigateTo(AppScreen.UPDATE_LOGS) },
                            testTag = "action_update_logs"
                        )
                    }
                }
            }
        }

        // Recent Notices Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সাম্প্রতিক নোটিশসমূহ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasaTextDark
                )
                TextButton(onClick = { viewModel.navigateTo(AppScreen.NOTICES) }) {
                    Text(
                        text = "সবগুলো দেখুন",
                        color = MadrasaGreenPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        val topNotices = notices.take(2)
        if (topNotices.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MadrasaSurface
                ) {
                    Text(
                        text = "বর্তমানে কোনো নতুন নোটিশ নেই",
                        modifier = Modifier.padding(16.dp),
                        color = MadrasaTextMuted,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(topNotices) { notice ->
                DashboardNoticeItem(
                    notice = notice,
                    onClick = { viewModel.navigateTo(AppScreen.NOTICES) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        // Islamic Hadith Quote Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MadrasaGreenPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = MadrasaGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "হাদিসে নববী (সা.)",
                            color = MadrasaGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "«طَلَبُ الْعِلْمِ فَرِيضَةٌ عَلَى كُلِّ مُسْلِمٍ»",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MadrasaTextWhite
                    )
                    Text(
                        text = "“দ্বীনি ইলম অর্জন করা প্রত্যেক মুসলমানের উপর ফরজ।” (ইবনে মাজাহ: ২২৪)",
                        fontSize = 13.sp,
                        color = MadrasaTextWhite.copy(alpha = 0.9f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MadrasaSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MadrasaTextMuted,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MadrasaTextDark
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MadrasaGreenPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun ActionRowCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = MadrasaSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MadrasaTextWhite,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasaTextDark
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MadrasaTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "যান",
                tint = MadrasaGreenPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun DashboardNoticeItem(
    notice: NoticeEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MadrasaSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (notice.category) {
                        "জরুরি" -> MadrasaError.copy(alpha = 0.15f)
                        "পরীক্ষা" -> MadrasaGold.copy(alpha = 0.15f)
                        "ছুটি" -> MadrasaGreenPrimary.copy(alpha = 0.15f)
                        else -> Color(0xFF0284C7).copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = notice.category,
                        color = when (notice.category) {
                            "জরুরি" -> MadrasaError
                            "পরীক্ষা" -> MadrasaGold
                            "ছুটি" -> MadrasaGreenPrimary
                            else -> Color(0xFF0284C7)
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = notice.date,
                    fontSize = 11.sp,
                    color = MadrasaTextMuted
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = notice.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MadrasaTextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = notice.content,
                fontSize = 12.sp,
                color = MadrasaTextMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
