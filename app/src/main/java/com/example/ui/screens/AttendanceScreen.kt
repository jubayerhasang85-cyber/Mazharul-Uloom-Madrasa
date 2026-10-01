package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.StudentEntity
import com.example.ui.auth.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.MadrasaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    viewModel: MadrasaViewModel,
    modifier: Modifier = Modifier
) {
    val authSession by viewModel.authSession.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val selectedDate by viewModel.attendanceDate.collectAsState()

    val studentsInClass by viewModel.getStudentsByClass(selectedClass).collectAsState(initial = emptyList())
    val attendanceRecords by viewModel.getAttendanceForSelectedClassAndDate().collectAsState(initial = emptyList())

    // If student role, get personal attendance
    val studentAttendanceHistory by viewModel.getStudentAttendance(authSession.studentId).collectAsState(initial = emptyList())

    // Map of studentId -> status
    val attendanceMap = remember(attendanceRecords) {
        attendanceRecords.associate { it.studentId to it.status }
    }

    var classDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MadrasaBackground)
    ) {
        // Top Filter Bar (AppSheet style)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MadrasaSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = MadrasaGreenPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "তারিখ: $selectedDate",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MadrasaTextDark
                        )
                    }

                    if (selectedDate == viewModel.todayDateString) {
                        Surface(
                            color = MadrasaGreenContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "আজকের দিন",
                                color = MadrasaGreenPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (authSession.role != UserRole.STUDENT) {
                    Spacer(modifier = Modifier.height(10.dp))
                    // Class Selector Dropdown
                    ExposedDropdownMenuBox(
                        expanded = classDropdownExpanded,
                        onExpandedChange = { classDropdownExpanded = !classDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedClass,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("শ্রেণি নির্বাচন করুন") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("attendance_class_dropdown")
                        )
                        ExposedDropdownMenu(
                            expanded = classDropdownExpanded,
                            onDismissRequest = { classDropdownExpanded = false }
                        ) {
                            viewModel.classList.forEach { className ->
                                DropdownMenuItem(
                                    text = { Text(className) },
                                    onClick = {
                                        viewModel.selectClass(className)
                                        classDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Student View vs Teacher/Headmaster/Admin View
        if (authSession.role == UserRole.STUDENT) {
            // Student personal attendance summary
            val totalDays = studentAttendanceHistory.size.coerceAtLeast(1)
            val presentDays = studentAttendanceHistory.count { it.status == "উপস্থিত" }
            val studentPercent = (presentDays * 100) / totalDays

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MadrasaGreenPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "আমার ব্যক্তিগত উপস্থিতি হার",
                        fontSize = 14.sp,
                        color = MadrasaTextWhite.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$studentPercent%",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MadrasaTextWhite
                        )
                        Surface(
                            color = MadrasaGold,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "সন্তোষজনক উপস্থিতি",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MadrasaTextWhite,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { studentPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MadrasaGold,
                        trackColor = MadrasaGreenDarkContainer
                    )
                }
            }

            Text(
                text = "বিগত দিনসমূহের রেকর্ড",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MadrasaTextDark,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(studentAttendanceHistory) { record ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MadrasaSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = record.date,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MadrasaTextDark
                                )
                                if (record.remarks.isNotBlank()) {
                                    Text(
                                        text = record.remarks,
                                        fontSize = 12.sp,
                                        color = MadrasaTextMuted
                                    )
                                }
                            }
                            StatusChip(status = record.status)
                        }
                    }
                }
            }
        } else {
            // Teacher / Headmaster / Admin: Class Attendance Management
            val totalInClass = studentsInClass.size
            val presentInClass = attendanceRecords.count { it.status == "উপস্থিত" }
            val absentInClass = attendanceRecords.count { it.status == "অনুপস্থিত" }
            val leaveInClass = attendanceRecords.count { it.status == "ছুটি" }

            // Summary metrics bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MadrasaSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AttendanceCountPill(label = "উপস্থিত", count = presentInClass, color = MadrasaSuccess)
                    AttendanceCountPill(label = "অনুপস্থিত", count = absentInClass, color = MadrasaError)
                    AttendanceCountPill(label = "ছুটি", count = leaveInClass, color = MadrasaWarning)
                    AttendanceCountPill(label = "মোট ছাত্র", count = totalInClass, color = MadrasaGreenPrimary)
                }
            }

            // Quick Mark All Present button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "শিক্ষার্থীদের তালিকা ($totalInClass জন)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasaTextDark
                )

                if (studentsInClass.isNotEmpty()) {
                    Button(
                        onClick = { viewModel.markAllPresentForClass(studentsInClass, selectedDate) },
                        colors = ButtonDefaults.buttonColors(containerColor = MadrasaGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_mark_all_present")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = MadrasaTextWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "সবাই উপস্থিত",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MadrasaTextWhite
                        )
                    }
                }
            }

            if (studentsInClass.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "এই শ্রেণিতে এখনও কোনো শিক্ষার্থী নিবন্ধিত হয়নি।",
                        color = MadrasaTextMuted,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(studentsInClass) { student ->
                        val currentStatus = attendanceMap[student.id] ?: "উপস্থিত"
                        StudentAttendanceCard(
                            student = student,
                            currentStatus = currentStatus,
                            onStatusSelected = { newStatus ->
                                viewModel.updateAttendanceStatus(student, selectedDate, newStatus)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceCountPill(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MadrasaTextMuted,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StudentAttendanceCard(
    student: StudentEntity,
    currentStatus: String,
    onStatusSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MadrasaSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Roll number badge (Green circle)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MadrasaGreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${student.roll}",
                        color = MadrasaTextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MadrasaTextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "ফোন: ${student.phone}",
                        fontSize = 11.sp,
                        color = MadrasaTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Segmented Status Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusOptionButton(
                    label = "উপস্থিত",
                    isSelected = currentStatus == "উপস্থিত",
                    selectedColor = MadrasaSuccess,
                    onClick = { onStatusSelected("উপস্থিত") },
                    modifier = Modifier.weight(1f),
                    testTag = "btn_present_${student.id}"
                )
                StatusOptionButton(
                    label = "অনুপস্থিত",
                    isSelected = currentStatus == "অনুপস্থিত",
                    selectedColor = MadrasaError,
                    onClick = { onStatusSelected("অনুপস্থিত") },
                    modifier = Modifier.weight(1f),
                    testTag = "btn_absent_${student.id}"
                )
                StatusOptionButton(
                    label = "ছুটি",
                    isSelected = currentStatus == "ছুটি",
                    selectedColor = MadrasaWarning,
                    onClick = { onStatusSelected("ছুটি") },
                    modifier = Modifier.weight(1f),
                    testTag = "btn_leave_${student.id}"
                )
            }
        }
    }
}

@Composable
private fun StatusOptionButton(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(34.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) selectedColor else Color(0xFFF1F5F9),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MadrasaCardBorder)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MadrasaTextWhite else MadrasaTextDark
            )
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (color, bgColor) = when (status) {
        "উপস্থিত" -> MadrasaSuccess to MadrasaSuccess.copy(alpha = 0.15f)
        "অনুপস্থিত" -> MadrasaError to MadrasaError.copy(alpha = 0.15f)
        else -> MadrasaWarning to MadrasaWarning.copy(alpha = 0.15f)
    }
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
