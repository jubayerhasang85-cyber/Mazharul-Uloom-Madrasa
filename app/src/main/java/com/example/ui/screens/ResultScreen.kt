package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ResultEntity
import com.example.data.local.entity.StudentEntity
import com.example.ui.auth.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.MadrasaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: MadrasaViewModel,
    modifier: Modifier = Modifier
) {
    val authSession by viewModel.authSession.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val selectedExam by viewModel.selectedExam.collectAsState()

    val studentsInClass by viewModel.getStudentsByClass(selectedClass).collectAsState(initial = emptyList())
    val resultsForClassAndExam by viewModel.getResultsForSelectedClassAndExam().collectAsState(initial = emptyList())

    // If student role, get personal results
    val studentResults by viewModel.getStudentResults(authSession.studentId).collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var classDropdownExpanded by remember { mutableStateOf(false) }
    var examDropdownExpanded by remember { mutableStateOf(false) }

    // Dialog state for adding/editing results
    var showAddResultDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (authSession.role.canManageResults()) {
                FloatingActionButton(
                    onClick = { showAddResultDialog = true },
                    containerColor = MadrasaGreenPrimary,
                    contentColor = MadrasaTextWhite,
                    modifier = Modifier.testTag("fab_add_result")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "নম্বর এন্ট্রি")
                }
            }
        },
        containerColor = MadrasaBackground,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter Header (সাদা কার্ডে ফিল্টার অপশন)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MadrasaSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Exam selector tabs / chips
                    Text(
                        text = "পরীক্ষার পর্ব নির্বাচন করুন",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MadrasaTextMuted,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.examList.forEach { exam ->
                            val isSelected = selectedExam == exam
                            Surface(
                                onClick = { viewModel.selectExam(exam) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MadrasaGreenPrimary else Color(0xFFF1F5F9),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = exam.replace(" পরীক্ষা", ""),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MadrasaTextWhite else MadrasaTextDark,
                                        maxLines = 1
                                    )
                                }
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
                                label = { Text("শ্রেণি") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
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

                        Spacer(modifier = Modifier.height(8.dp))

                        // Search box
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("নাম বা রোল দিয়ে খুঁজুন...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Student View: Personal Marksheet
            if (authSession.role == UserRole.STUDENT) {
                val myFilteredResults = studentResults.filter { it.examName == selectedExam }
                val totalObtained = myFilteredResults.sumOf { it.obtainedMarks }
                val totalPossible = myFilteredResults.sumOf { it.totalMarks }
                val overallPercent = if (totalPossible > 0) (totalObtained * 100.0) / totalPossible else 0.0

                val overallGrade = when {
                    overallPercent >= 80.0 -> "মুমতাজ (A+)"
                    overallPercent >= 70.0 -> "জায়্যিদ জিদ্দান (A)"
                    overallPercent >= 60.0 -> "জায়্যিদ (B)"
                    overallPercent >= 40.0 -> "মাকবুল (C)"
                    else -> "রাসেল (অকৃতকার্য)"
                }

                // Summary Marksheet Banner (সবুজ ব্যাকগ্রাউন্ডে সাদা টেক্সট)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MadrasaGreenPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "$selectedExam এর নম্বরপত্র",
                                    fontSize = 13.sp,
                                    color = MadrasaTextWhite.copy(alpha = 0.85f)
                                )
                                Text(
                                    text = "প্রাপ্ত মোট নম্বর: ${totalObtained.toInt()} / ${totalPossible.toInt()}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MadrasaTextWhite
                                )
                            }
                            Surface(
                                color = MadrasaGold,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = overallGrade,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MadrasaTextWhite,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "বিষয়ভিত্তিক নম্বর বিবরণী",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasaTextDark,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                if (myFilteredResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "এই পর্বের ফলাফল এখনও প্রকাশিত হয়নি।",
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
                        items(myFilteredResults) { res ->
                            SubjectResultCard(result = res)
                        }
                    }
                }
            } else {
                // Teacher / Headmaster / Admin: Class-wide Results
                // Group results by student
                val resultsByStudent = resultsForClassAndExam.groupBy { it.studentId }
                val filteredStudents = studentsInClass.filter { student ->
                    searchQuery.isBlank() ||
                            student.name.contains(searchQuery, ignoreCase = true) ||
                            student.roll.toString().contains(searchQuery)
                }

                if (filteredStudents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "কোনো শিক্ষার্থীর তথ্য পাওয়া যায়নি।",
                            color = MadrasaTextMuted,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredStudents) { student ->
                            val studentResList = resultsByStudent[student.id] ?: emptyList()
                            StudentClassResultItem(
                                student = student,
                                results = studentResList,
                                onAddMarksClick = { showAddResultDialog = true }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Result Dialog
    if (showAddResultDialog) {
        AddResultDialog(
            students = studentsInClass,
            examName = selectedExam,
            onDismiss = { showAddResultDialog = false },
            onSave = { student, subject, marks, totalMarks ->
                viewModel.addOrUpdateResult(student, selectedExam, subject, marks, totalMarks)
                showAddResultDialog = false
            }
        )
    }
}

@Composable
private fun SubjectResultCard(result: ResultEntity) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.subject,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasaTextDark
                )
                Text(
                    text = "পূর্ণমান: ${result.totalMarks.toInt()}",
                    fontSize = 12.sp,
                    color = MadrasaTextMuted
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${result.obtainedMarks.toInt()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasaGreenPrimary
                )
                Surface(
                    color = when (result.grade) {
                        "মুমতাজ" -> MadrasaSuccess.copy(alpha = 0.15f)
                        "জায়্যিদ জিদ্দান" -> MadrasaGold.copy(alpha = 0.15f)
                        "জায়্যিদ" -> Color(0xFF0284C7).copy(alpha = 0.15f)
                        else -> MadrasaError.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = result.grade,
                        color = when (result.grade) {
                            "মুমতাজ" -> MadrasaSuccess
                            "জায়্যিদ জিদ্দান" -> MadrasaGold
                            "জায়্যিদ" -> Color(0xFF0284C7)
                            else -> MadrasaError
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentClassResultItem(
    student: StudentEntity,
    results: List<ResultEntity>,
    onAddMarksClick: () -> Unit
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
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MadrasaTextDark
                    )
                    Text(
                        text = "শ্রেণি: ${student.className}",
                        fontSize = 11.sp,
                        color = MadrasaTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (results.isEmpty()) {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "কোনো নম্বর যুক্ত করা হয়নি",
                            fontSize = 12.sp,
                            color = MadrasaTextMuted
                        )
                        TextButton(onClick = onAddMarksClick) {
                            Text("+ নম্বর দিন", color = MadrasaGreenPrimary, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    results.forEach { res ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = res.subject,
                                fontSize = 13.sp,
                                color = MadrasaTextDark
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${res.obtainedMarks.toInt()}/${res.totalMarks.toInt()}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MadrasaGreenPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "(${res.grade})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MadrasaGold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddResultDialog(
    students: List<StudentEntity>,
    examName: String,
    onDismiss: () -> Unit,
    onSave: (StudentEntity, String, Double, Double) -> Unit
) {
    var selectedStudentIndex by remember { mutableStateOf(0) }
    var studentDropdownExpanded by remember { mutableStateOf(false) }

    var subject by remember { mutableStateOf("আল-কুরআন ও তাজবীদ") }
    var marksText by remember { mutableStateOf("85") }
    var totalMarksText by remember { mutableStateOf("100") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "পরীক্ষার নম্বর এন্ট্রি ($examName)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MadrasaGreenPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (students.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = studentDropdownExpanded,
                        onExpandedChange = { studentDropdownExpanded = !studentDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = "রোল ${students[selectedStudentIndex].roll}: ${students[selectedStudentIndex].name}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("শিক্ষার্থী নির্বাচন করুন") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = studentDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = studentDropdownExpanded,
                            onDismissRequest = { studentDropdownExpanded = false }
                        ) {
                            students.forEachIndexed { index, student ->
                                DropdownMenuItem(
                                    text = { Text("রোল ${student.roll}: ${student.name}") },
                                    onClick = {
                                        selectedStudentIndex = index
                                        studentDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("বিষয়ের নাম") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = marksText,
                        onValueChange = { marksText = it },
                        label = { Text("প্রাপ্ত নম্বর") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = totalMarksText,
                        onValueChange = { totalMarksText = it },
                        label = { Text("পূর্ণমান") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (students.isNotEmpty()) {
                        val marks = marksText.toDoubleOrNull() ?: 0.0
                        val total = totalMarksText.toDoubleOrNull() ?: 100.0
                        onSave(students[selectedStudentIndex], subject, marks, total)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MadrasaGreenPrimary)
            ) {
                Text("সংরক্ষণ করুন", color = MadrasaTextWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = MadrasaTextMuted)
            }
        }
    )
}
