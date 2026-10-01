package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.ui.auth.AuthSession
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MadrasaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MadrasaViewModel,
    modifier: Modifier = Modifier
) {
    val authSession by viewModel.authSession.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showRoleSwitcherDialog by remember { mutableStateOf(false) }

    // Show status snackbar
    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    // Back button handling
    BackHandler(enabled = authSession.isLoggedIn && currentScreen != AppScreen.DASHBOARD) {
        viewModel.navigateTo(AppScreen.DASHBOARD)
    }

    // If not logged in, show Login Screen
    if (!authSession.isLoggedIn) {
        LoginScreen(viewModel = viewModel)
        return
    }

    Scaffold(
        topBar = {
            // সবুজ ব্যাকগ্রাউন্ডে সাদা টেক্সট (Green TopAppBar with White Text)
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "মাযহারুল উলূম মাদ্রাসা",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MadrasaTextWhite
                        )
                        Text(
                            text = currentScreen.banglaTitle,
                            fontSize = 12.sp,
                            color = MadrasaTextWhite.copy(alpha = 0.85f)
                        )
                    }
                },
                actions = {
                    // Role Badge Pill (Clickable to switch roles)
                    Surface(
                        onClick = { showRoleSwitcherDialog = true },
                        color = Color(authSession.role.badgeColorHex),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("role_badge_pill")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = authSession.role.banglaTitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MadrasaTextWhite
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "রোল পরিবর্তন",
                                tint = MadrasaTextWhite,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Logout Icon Button
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("btn_logout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "লগআউট",
                            tint = MadrasaTextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MadrasaGreenPrimary,
                    titleContentColor = MadrasaTextWhite,
                    actionIconContentColor = MadrasaTextWhite
                )
            )
        },
        bottomBar = {
            // AppSheet Style NavigationBar
            NavigationBar(
                containerColor = MadrasaSurface,
                contentColor = MadrasaGreenPrimary,
                tonalElevation = 6.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                // Dashboard
                NavigationBarItem(
                    selected = currentScreen == AppScreen.DASHBOARD,
                    onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "ড্যাশবোর্ড") },
                    label = { Text("ড্যাশবোর্ড", fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MadrasaGreenPrimary,
                        selectedTextColor = MadrasaGreenPrimary,
                        indicatorColor = MadrasaGreenContainer,
                        unselectedIconColor = MadrasaTextMuted,
                        unselectedTextColor = MadrasaTextMuted
                    ),
                    modifier = Modifier.testTag("nav_dashboard")
                )

                // Attendance
                NavigationBarItem(
                    selected = currentScreen == AppScreen.ATTENDANCE,
                    onClick = { viewModel.navigateTo(AppScreen.ATTENDANCE) },
                    icon = { Icon(Icons.Default.HowToReg, contentDescription = "হাজিরা") },
                    label = { Text("হাজিরা", fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MadrasaGreenPrimary,
                        selectedTextColor = MadrasaGreenPrimary,
                        indicatorColor = MadrasaGreenContainer,
                        unselectedIconColor = MadrasaTextMuted,
                        unselectedTextColor = MadrasaTextMuted
                    ),
                    modifier = Modifier.testTag("nav_attendance")
                )

                // Results
                NavigationBarItem(
                    selected = currentScreen == AppScreen.RESULTS,
                    onClick = { viewModel.navigateTo(AppScreen.RESULTS) },
                    icon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = "ফলাফল") },
                    label = { Text("ফলাফল", fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MadrasaGreenPrimary,
                        selectedTextColor = MadrasaGreenPrimary,
                        indicatorColor = MadrasaGreenContainer,
                        unselectedIconColor = MadrasaTextMuted,
                        unselectedTextColor = MadrasaTextMuted
                    ),
                    modifier = Modifier.testTag("nav_results")
                )

                // Notices
                NavigationBarItem(
                    selected = currentScreen == AppScreen.NOTICES,
                    onClick = { viewModel.navigateTo(AppScreen.NOTICES) },
                    icon = { Icon(Icons.Default.Campaign, contentDescription = "নোটিশ") },
                    label = { Text("নোটিশ", fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MadrasaGreenPrimary,
                        selectedTextColor = MadrasaGreenPrimary,
                        indicatorColor = MadrasaGreenContainer,
                        unselectedIconColor = MadrasaTextMuted,
                        unselectedTextColor = MadrasaTextMuted
                    ),
                    modifier = Modifier.testTag("nav_notices")
                )

                // Syllabus / Books
                NavigationBarItem(
                    selected = currentScreen == AppScreen.SYLLABUS,
                    onClick = { viewModel.navigateTo(AppScreen.SYLLABUS) },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "কিতাবসমূহ") },
                    label = { Text("কিতাবসমূহ", fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MadrasaGreenPrimary,
                        selectedTextColor = MadrasaGreenPrimary,
                        indicatorColor = MadrasaGreenContainer,
                        unselectedIconColor = MadrasaTextMuted,
                        unselectedTextColor = MadrasaTextMuted
                    ),
                    modifier = Modifier.testTag("nav_syllabus")
                )

                // Update Logs (Requirement: ১০. আপডেট লগ: শুধুমাত্র প্রশাসকের জন্য দৃশ্যমান)
                if (authSession.role.canViewUpdateLogs()) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.UPDATE_LOGS,
                        onClick = { viewModel.navigateTo(AppScreen.UPDATE_LOGS) },
                        icon = { Icon(Icons.Default.SecurityUpdateGood, contentDescription = "আপডেট লগ") },
                        label = { Text("আপডেট লগ", fontSize = 10.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF7C3AED),
                            selectedTextColor = Color(0xFF7C3AED),
                            indicatorColor = Color(0xFFEDE9FE),
                            unselectedIconColor = MadrasaTextMuted,
                            unselectedTextColor = MadrasaTextMuted
                        ),
                        modifier = Modifier.testTag("nav_update_logs")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppScreen.ATTENDANCE -> AttendanceScreen(viewModel = viewModel)
                AppScreen.RESULTS -> ResultScreen(viewModel = viewModel)
                AppScreen.NOTICES -> NoticeScreen(viewModel = viewModel)
                AppScreen.SYLLABUS -> SyllabusScreen(viewModel = viewModel)
                AppScreen.UPDATE_LOGS -> AdminUpdateLogScreen(viewModel = viewModel)
            }
        }
    }

    // Role Switcher Dialog
    if (showRoleSwitcherDialog) {
        RoleSwitcherDialog(
            currentRole = authSession.role,
            onDismiss = { showRoleSwitcherDialog = false },
            onSelectRole = { role ->
                viewModel.quickRoleSwitch(role)
                showRoleSwitcherDialog = false
            }
        )
    }
}

@Composable
private fun RoleSwitcherDialog(
    currentRole: UserRole,
    onDismiss: () -> Unit,
    onSelectRole: (UserRole) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ManageAccounts,
                    contentDescription = null,
                    tint = MadrasaGreenPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ব্যবহারকারী রোল পরিবর্তন",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MadrasaTextDark
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "অ্যাপের ৪টি রোল তাৎক্ষণিক যাচাই করতে যেকোনোটিতে ট্যাপ করুন:",
                    fontSize = 12.sp,
                    color = MadrasaTextMuted,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                UserRole.values().forEach { role ->
                    val isSelected = currentRole == role
                    Surface(
                        onClick = { onSelectRole(role) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MadrasaGreenContainer else Color(0xFFF8FAFC),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MadrasaGreenPrimary) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color(role.badgeColorHex))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = role.banglaTitle,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MadrasaGreenPrimary else MadrasaTextDark
                                )
                                Text(
                                    text = when (role) {
                                        UserRole.STUDENT -> "ফোন + শ্রেণি + রোল ভিত্তিক শিক্ষার্থীর প্রোফাইল"
                                        UserRole.ASSISTANT_TEACHER -> "হাজিরা গ্রহণ ও পরীক্ষার নম্বর এন্ট্রি"
                                        UserRole.HEADMASTER -> "পূর্ণাঙ্গ প্রাতিষ্ঠানিক নিয়ন্ত্রণ ও সিদ্ধান্ত"
                                        UserRole.ADMIN -> "গোপন ইমেল দ্বারা সুপার-অ্যাডমিন ও আপডেট লগ"
                                    },
                                    fontSize = 11.sp,
                                    color = MadrasaTextMuted
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MadrasaGreenPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("বন্ধ করুন", color = MadrasaGreenPrimary)
            }
        }
    )
}
