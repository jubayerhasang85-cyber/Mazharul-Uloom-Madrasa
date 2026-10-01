package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SyllabusBookEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MadrasaViewModel

@Composable
fun SyllabusScreen(
    viewModel: MadrasaViewModel,
    modifier: Modifier = Modifier
) {
    val authSession by viewModel.authSession.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val allBooks by viewModel.allSyllabusBooks.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddBookDialog by remember { mutableStateOf(false) }

    val filteredBooks = remember(allBooks, selectedClass, searchQuery) {
        allBooks.filter { book ->
            book.className == selectedClass &&
                    (searchQuery.isBlank() ||
                            book.bookName.contains(searchQuery, ignoreCase = true) ||
                            book.subject.contains(searchQuery, ignoreCase = true) ||
                            book.authorName.contains(searchQuery, ignoreCase = true))
        }
    }

    Scaffold(
        floatingActionButton = {
            if (authSession.role.canManageSyllabus()) {
                FloatingActionButton(
                    onClick = { showAddBookDialog = true },
                    containerColor = MadrasaGreenPrimary,
                    contentColor = MadrasaTextWhite,
                    modifier = Modifier.testTag("fab_add_book")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "নতুন কিতাব যোগ")
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
            // Class selector horizontal scroll row
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MadrasaSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "শ্রেণি বা বিভাগ নির্বাচন করুন:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MadrasaTextMuted,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(viewModel.classList) { className ->
                            val isSelected = selectedClass == className
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectClass(className) },
                                label = { Text(className, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MadrasaGreenPrimary,
                                    selectedLabelColor = MadrasaTextWhite
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("কিতাব বা বিষয়ের নাম দিয়ে খুঁজুন...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Summary Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$selectedClass এর কিতাব তালিকা (${filteredBooks.size} টি)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasaTextDark
                )
            }

            if (filteredBooks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "এই শ্রেণির জন্য কোনো কিতাব পাওয়া যায়নি।",
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
                    items(filteredBooks) { book ->
                        BookItemCard(book = book)
                    }
                }
            }
        }
    }

    // Add Book Dialog
    if (showAddBookDialog) {
        AddBookDialog(
            currentClass = selectedClass,
            onDismiss = { showAddBookDialog = false },
            onSave = { className, bookName, subject, author, marks, desc ->
                viewModel.addSyllabusBook(className, bookName, subject, author, marks, desc)
                showAddBookDialog = false
            }
        )
    }
}

@Composable
private fun BookItemCard(book: SyllabusBookEntity) {
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
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MadrasaGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = MadrasaGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = book.bookName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MadrasaTextDark
                    )
                    Text(
                        text = "বিষয়: ${book.subject}",
                        fontSize = 13.sp,
                        color = MadrasaGreenPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    if (book.authorName.isNotBlank()) {
                        Text(
                            text = "মুসান্নিফ/লেখক: ${book.authorName}",
                            fontSize = 12.sp,
                            color = MadrasaTextMuted
                        )
                    }
                }

                Surface(
                    color = MadrasaGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${book.totalMarks} নম্বর",
                        color = MadrasaGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (book.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "সিলেবাস বিবরণ: ${book.description}",
                        fontSize = 12.sp,
                        color = MadrasaTextMuted,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddBookDialog(
    currentClass: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Int, String) -> Unit
) {
    var bookName by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var marksText by remember { mutableStateOf("100") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "নতুন কিতাব সংযোজন ($currentClass)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MadrasaGreenPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = bookName,
                    onValueChange = { bookName = it },
                    label = { Text("কিতাব / বইয়ের নাম") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("বিষয় (যেমন: ছরফ, ফিকহ, বাংলা)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("লেখক / মুসান্নিফের নাম") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = marksText,
                    onValueChange = { marksText = it },
                    label = { Text("পূর্ণমান (নম্বর)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("সংক্ষিপ্ত সিলেবাস / অধ্যায়") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (bookName.isNotBlank() && subject.isNotBlank()) {
                        val marks = marksText.toIntOrNull() ?: 100
                        onSave(currentClass, bookName, subject, author, marks, desc)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MadrasaGreenPrimary)
            ) {
                Text("যুক্ত করুন", color = MadrasaTextWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = MadrasaTextMuted)
            }
        }
    )
}
