package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.local.entity.NoticeEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MadrasaViewModel

@Composable
fun NoticeScreen(
    viewModel: MadrasaViewModel,
    modifier: Modifier = Modifier
) {
    val authSession by viewModel.authSession.collectAsState()
    val allNotices by viewModel.allNotices.collectAsState()
    val selectedCategory by viewModel.selectedNoticeCategory.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedNoticeForDetail by remember { mutableStateOf<NoticeEntity?>(null) }

    val filteredNotices = remember(allNotices, selectedCategory) {
        if (selectedCategory == "সকল") allNotices
        else allNotices.filter { it.category == selectedCategory }
    }

    Scaffold(
        floatingActionButton = {
            if (authSession.role.canPostNotice()) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MadrasaGreenPrimary,
                    contentColor = MadrasaTextWhite,
                    modifier = Modifier.testTag("fab_add_notice")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "নতুন নোটিশ দিন")
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
            // Category Filter Chips
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
                        text = "নোটিশের বিভাগ অনুযায়ী দেখুন",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MadrasaTextMuted,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        viewModel.noticeCategories.forEach { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectNoticeCategory(category) },
                                label = { Text(category, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MadrasaGreenPrimary,
                                    selectedLabelColor = MadrasaTextWhite
                                )
                            )
                        }
                    }
                }
            }

            if (filteredNotices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "এই বিভাগে কোনো নোটিশ পাওয়া যায়নি।",
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
                    items(filteredNotices) { notice ->
                        NoticeCardItem(
                            notice = notice,
                            canDelete = authSession.role.canPostNotice(),
                            onClick = { selectedNoticeForDetail = notice },
                            onDelete = { viewModel.deleteNotice(notice.id) }
                        )
                    }
                }
            }
        }
    }

    // Detail Dialog
    selectedNoticeForDetail?.let { notice ->
        NoticeDetailDialog(
            notice = notice,
            onDismiss = { selectedNoticeForDetail = null }
        )
    }

    // Add Notice Dialog
    if (showAddDialog) {
        AddNoticeDialog(
            onDismiss = { showAddDialog = false },
            onPublish = { title, content, category, isPinned ->
                viewModel.addNotice(title, content, category, isPinned)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun NoticeCardItem(
    notice: NoticeEntity,
    canDelete: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (notice.isPinned) {
                        Surface(
                            color = MadrasaGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null,
                                    tint = MadrasaGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "পিন করা",
                                    color = MadrasaGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Surface(
                        color = when (notice.category) {
                            "জরুরি" -> MadrasaError.copy(alpha = 0.15f)
                            "পরীক্ষা" -> MadrasaGold.copy(alpha = 0.15f)
                            "ছুটি" -> MadrasaGreenPrimary.copy(alpha = 0.15f)
                            else -> Color(0xFF0284C7).copy(alpha = 0.15f)
                        },
                        shape = RoundedCornerShape(6.dp)
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
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = notice.date,
                        fontSize = 11.sp,
                        color = MadrasaTextMuted
                    )
                    if (canDelete) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "মুছুন",
                                tint = MadrasaError,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notice.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MadrasaTextDark,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = notice.content,
                fontSize = 13.sp,
                color = MadrasaTextMuted,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "প্রকাশক: ${notice.authorName} (${notice.authorRole})",
                    fontSize = 11.sp,
                    color = MadrasaGreenPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "বিস্তারিত পড়ুন →",
                    fontSize = 12.sp,
                    color = MadrasaGreenPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun NoticeDetailDialog(
    notice: NoticeEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Surface(
                    color = MadrasaGreenContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${notice.category} নোটিশ",
                        color = MadrasaGreenPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = notice.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasaTextDark
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "তারিখ: ${notice.date}",
                    fontSize = 12.sp,
                    color = MadrasaTextMuted,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    text = notice.content,
                    fontSize = 14.sp,
                    color = MadrasaTextDark,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MadrasaCardBorder)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "বিজ্ঞপ্তি নির্দেশক: ${notice.authorName}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MadrasaGreenPrimary
                )
                Text(
                    text = "পদবি: ${notice.authorRole}, মাযহারুল উলূম মাদ্রাসা",
                    fontSize = 11.sp,
                    color = MadrasaTextMuted
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MadrasaGreenPrimary)
            ) {
                Text("বন্ধ করুন", color = MadrasaTextWhite)
            }
        }
    )
}

@Composable
private fun AddNoticeDialog(
    onDismiss: () -> Unit,
    onPublish: (String, String, String, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("জরুরি") }
    var isPinned by remember { mutableStateOf(false) }

    val categories = listOf("জরুরি", "পরীক্ষা", "ছুটি", "সাধারণ")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "নতুন নোটিশ প্রকাশ",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MadrasaGreenPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("নোটিশের শিরোনাম") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("বিস্তারিত বিবরণ") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "ক্যাটেগরি নির্বাচন করুন:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MadrasaTextDark
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MadrasaGreenPrimary,
                                selectedLabelColor = MadrasaTextWhite
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it }
                    )
                    Text(
                        text = "নোটিশটি শীর্ষে পিন করে রাখুন",
                        fontSize = 13.sp,
                        color = MadrasaTextDark
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onPublish(title, content, category, isPinned)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MadrasaGreenPrimary)
            ) {
                Text("প্রকাশ করুন", color = MadrasaTextWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = MadrasaTextMuted)
            }
        }
    )
}
