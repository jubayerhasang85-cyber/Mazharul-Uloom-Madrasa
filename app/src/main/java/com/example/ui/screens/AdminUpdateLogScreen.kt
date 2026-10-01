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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UpdateLogEntity
import com.example.ui.auth.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.MadrasaViewModel

@Composable
fun AdminUpdateLogScreen(
    viewModel: MadrasaViewModel,
    modifier: Modifier = Modifier
) {
    val authSession by viewModel.authSession.collectAsState()
    val updateLogs by viewModel.allUpdateLogs.collectAsState()
    val studentCount by viewModel.studentCount.collectAsState()
    val teacherCount by viewModel.teacherCount.collectAsState()
    val allNotices by viewModel.allNotices.collectAsState()
    val allBooks by viewModel.allSyllabusBooks.collectAsState()

    var showAddLogDialog by remember { mutableStateOf(false) }

    // Security check: Only Admin can access
    if (authSession.role != UserRole.ADMIN) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MadrasaBackground)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MadrasaSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MadrasaError.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MadrasaError,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "অ্যাক্সেস সীমাবদ্ধ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MadrasaTextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "১০. আপডেট লগ শুধুমাত্র মাদ্রাসার সফটওয়্যার প্রশাসক ও নির্মাতার জন্য দৃশ্যমান। আপনার বর্তমান ভূমিকা: ${authSession.role.banglaTitle}।",
                        fontSize = 14.sp,
                        color = MadrasaTextMuted,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.quickRoleSwitch(UserRole.ADMIN) },
                        colors = ButtonDefaults.buttonColors(containerColor = MadrasaGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("প্রশাসক (অ্যাডমিন) হিসেবে স্যুইচ করুন", color = MadrasaTextWhite)
                    }
                }
            }
        }
        return
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddLogDialog = true },
                containerColor = MadrasaGreenPrimary,
                contentColor = MadrasaTextWhite,
                modifier = Modifier.testTag("fab_add_update_log")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "নতুন আপডেট লগ")
            }
        },
        containerColor = MadrasaBackground,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Admin Security Banner (সবুজ ব্যাকগ্রাউন্ডে সাদা টেক্সট)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MadrasaGreenPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = MadrasaGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "প্রশাসক ও নির্মাতা কনসোল",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MadrasaTextWhite
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "স্বাগতম ${authSession.userName} (${authSession.email.ifBlank { "সুপার অ্যাডমিন" }})। মাদ্রাসার ডেটাবেজ ও আপডেট লগসমূহ সুরক্ষিতভাবে সংরক্ষিত রয়েছে।",
                            fontSize = 12.sp,
                            color = MadrasaTextWhite.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // System Metrics Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MadrasaSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "ডাটাবেজ ও সিস্টেম সারসংক্ষেপ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MadrasaTextDark,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            MetricItem("ছাত্র", "$studentCount")
                            MetricItem("শিক্ষক", "$teacherCount")
                            MetricItem("নোটিশ", "${allNotices.size}")
                            MetricItem("সিলেবাস কিতাব", "${allBooks.size}")
                            MetricItem("লগ রেকর্ড", "${updateLogs.size}")
                        }
                    }
                }
            }

            // Section Header
            item {
                Text(
                    text = "সিস্টেম রিলিজ ও আপডেট লগ হিস্ট্রি",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasaTextDark,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            items(updateLogs) { log ->
                UpdateLogCard(log = log)
            }
        }
    }

    // Add Log Dialog
    if (showAddLogDialog) {
        AddUpdateLogDialog(
            onDismiss = { showAddLogDialog = false },
            onSave = { version, title, desc, category ->
                viewModel.addUpdateLog(version, title, desc, category)
                showAddLogDialog = false
            }
        )
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MadrasaGreenPrimary
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MadrasaTextMuted
        )
    }
}

@Composable
private fun UpdateLogCard(log: UpdateLogEntity) {
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF7C3AED).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = log.version,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7C3AED),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = log.date,
                    fontSize = 11.sp,
                    color = MadrasaTextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = log.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MadrasaTextDark
            )

            Text(
                text = log.description,
                fontSize = 13.sp,
                color = MadrasaTextMuted,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = MadrasaGreenContainer,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "ক্যাটেগরি: ${log.category}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MadrasaGreenPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun AddUpdateLogDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var version by remember { mutableStateOf("v1.3.0") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("ফিচার রিলিজ") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "নতুন আপডেট লগ লিপিবদ্ধ করুন",
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
                    value = version,
                    onValueChange = { version = it },
                    label = { Text("ভার্সন ট্যাগ (যেমন: v1.3.0)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("আপডেটের শিরোনাম") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("পরিবর্তন ও বিবরণের তালিকা") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("ক্যাটেগরি (যেমন: নিরাপত্তা, ফিচার, বাগ ফিক্স)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        onSave(version, title, description, category)
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
