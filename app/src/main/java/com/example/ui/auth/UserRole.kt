package com.example.ui.auth

enum class UserRole(val banglaTitle: String, val badgeColorHex: Long) {
    STUDENT("ছাত্র", 0xFF0284C7),
    ASSISTANT_TEACHER("সহকারী শিক্ষক", 0xFF059669),
    HEADMASTER("প্রধান শিক্ষক / মুহতামিম", 0xFFD97706),
    ADMIN("অ্যাপ নির্মাতা / অ্যাডমিন", 0xFF7C3AED);

    fun canManageAttendance(): Boolean = this == ASSISTANT_TEACHER || this == HEADMASTER || this == ADMIN
    fun canManageResults(): Boolean = this == ASSISTANT_TEACHER || this == HEADMASTER || this == ADMIN
    fun canPostNotice(): Boolean = this == ASSISTANT_TEACHER || this == HEADMASTER || this == ADMIN
    fun canManageSyllabus(): Boolean = this == HEADMASTER || this == ADMIN
    fun canViewUpdateLogs(): Boolean = this == ADMIN
}

data class AuthSession(
    val isLoggedIn: Boolean = false,
    val role: UserRole = UserRole.HEADMASTER,
    val userName: String = "",
    val userPhone: String = "",
    val studentRoll: Int = 0,
    val studentClass: String = "",
    val studentId: Long = 0,
    val ilhaqNumber: String = "",
    val email: String = ""
)
