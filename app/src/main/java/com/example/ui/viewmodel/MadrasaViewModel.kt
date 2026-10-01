package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.InitialDataLoader
import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.NoticeEntity
import com.example.data.local.entity.ResultEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SyllabusBookEntity
import com.example.data.local.entity.UpdateLogEntity
import com.example.data.repository.MadrasaRepository
import com.example.ui.auth.AuthSession
import com.example.ui.auth.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen(val banglaTitle: String) {
    DASHBOARD("ড্যাশবোর্ড"),
    ATTENDANCE("অ্যাটেন্ডেন্স"),
    RESULTS("ফলাফল ট্র্যাকার"),
    NOTICES("নোটিশ বোর্ড"),
    SYLLABUS("বিষয় ও কিতাব"),
    UPDATE_LOGS("আপডেট লগ")
}

class MadrasaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = MadrasaRepository(database.madrasaDao())

    // Initial Login Session (Start with Headmaster / Administrator by default or prompt login)
    private val _authSession = MutableStateFlow(
        AuthSession(
            isLoggedIn = true,
            role = UserRole.HEADMASTER,
            userName = "মাওলানা মুফতি আব্দুল হক",
            userPhone = "01911000003"
        )
    )
    val authSession: StateFlow<AuthSession> = _authSession.asStateFlow()

    // Screen Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Class selection
    val classList = InitialDataLoader.CLASS_LIST

    private val _selectedClass = MutableStateFlow("ইবতেদায়ী ৪র্থ শ্রেণি")
    val selectedClass: StateFlow<String> = _selectedClass.asStateFlow()

    // Date for Attendance
    val todayDateString: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private val _attendanceDate = MutableStateFlow(todayDateString)
    val attendanceDate: StateFlow<String> = _attendanceDate.asStateFlow()

    // Exam selection for Results
    val examList = listOf("১ম সাময়িক পরীক্ষা", "২য় সাময়িক পরীক্ষা", "বার্ষিক পরীক্ষা")
    private val _selectedExam = MutableStateFlow("১ম সাময়িক পরীক্ষা")
    val selectedExam: StateFlow<String> = _selectedExam.asStateFlow()

    // Notice category filter
    val noticeCategories = listOf("সকল", "জরুরি", "পরীক্ষা", "ছুটি", "সাধারণ")
    private val _selectedNoticeCategory = MutableStateFlow("সকল")
    val selectedNoticeCategory: StateFlow<String> = _selectedNoticeCategory.asStateFlow()

    // Data streams
    val allStudents: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTeachers = repository.allTeachers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studentCount: StateFlow<Int> = repository.studentCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val teacherCount: StateFlow<Int> = repository.teacherCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allNotices: StateFlow<List<NoticeEntity>> = repository.allNotices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSyllabusBooks: StateFlow<List<SyllabusBookEntity>> = repository.allSyllabusBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUpdateLogs: StateFlow<List<UpdateLogEntity>> = repository.allUpdateLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feedback snackbar or message
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        viewModelScope.launch {
            InitialDataLoader.seedDatabaseIfEmpty(database.madrasaDao())
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectClass(className: String) {
        _selectedClass.value = className
    }

    fun selectAttendanceDate(date: String) {
        _attendanceDate.value = date
    }

    fun selectExam(exam: String) {
        _selectedExam.value = exam
    }

    fun selectNoticeCategory(category: String) {
        _selectedNoticeCategory.value = category
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    // --- Authentication ---
    fun loginAsStudent(phone: String, className: String, rollStr: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val roll = rollStr.toIntOrNull()
            if (roll == null || phone.isBlank() || className.isBlank()) {
                onResult(false, "দয়া করে ফোন নম্বর, শ্রেণি ও রোল সঠিকভাবে পূরণ করুন।")
                return@launch
            }
            val student = repository.findStudentForLogin(phone, className, roll)
            if (student != null) {
                _authSession.value = AuthSession(
                    isLoggedIn = true,
                    role = UserRole.STUDENT,
                    userName = student.name,
                    userPhone = student.phone,
                    studentRoll = student.roll,
                    studentClass = student.className,
                    studentId = student.id
                )
                _selectedClass.value = student.className
                _currentScreen.value = AppScreen.DASHBOARD
                onResult(true, "সাফল্যের সাথে ছাত্র হিসেবে লগইন সম্পন্ন হয়েছে!")
            } else {
                // If not found in DB, check if demo roll 1 can be authorized
                onResult(false, "তথ্য মেলেনি। রোল ০১, ফোন ০১৭১১০০০০০১ দিয়ে চেষ্টা করুন।")
            }
        }
    }

    fun loginAsTeacher(phone: String, ilhaq: String, pin: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (phone.isBlank() || ilhaq.isBlank() || pin.isBlank()) {
                onResult(false, "ফোন নম্বর, ইলহাক নম্বর ও পিন সবগুলোই আবশ্যক।")
                return@launch
            }
            val teacher = repository.findTeacherForLogin(phone, ilhaq, pin)
            if (teacher != null) {
                _authSession.value = AuthSession(
                    isLoggedIn = true,
                    role = UserRole.ASSISTANT_TEACHER,
                    userName = teacher.name,
                    userPhone = teacher.phone,
                    ilhaqNumber = teacher.ilhaqNumber
                )
                _currentScreen.value = AppScreen.DASHBOARD
                onResult(true, "সাফল্যের সাথে সহকারী শিক্ষক হিসেবে লগইন হয়েছে!")
            } else {
                onResult(false, "ভুল তথ্য। ডেমো শিক্ষক: ফোন ০১৮১১০০০০০২, ইলহাক IL-2024, পিন 1234")
            }
        }
    }

    fun loginAsHeadmaster(phone: String, pin: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (phone.isBlank() || pin.isBlank()) {
                onResult(false, "ফোন নম্বর ও পিন প্রদান করুন।")
                return@launch
            }
            val headmaster = repository.findHeadmasterForLogin(phone, pin)
            if (headmaster != null) {
                _authSession.value = AuthSession(
                    isLoggedIn = true,
                    role = UserRole.HEADMASTER,
                    userName = headmaster.name,
                    userPhone = headmaster.phone
                )
                _currentScreen.value = AppScreen.DASHBOARD
                onResult(true, "প্রধান শিক্ষক হিসেবে লগইন সফল হয়েছে!")
            } else {
                onResult(false, "ভুল পিন বা ফোন নম্বর। ডেমো: ফোন ০১৯১১০০০০০৩, পিন 7860")
            }
        }
    }

    fun loginAsAdmin(secretEmail: String, onResult: (Boolean, String) -> Unit) {
        val trimmed = secretEmail.trim().lowercase()
        // Accepts user requested email or developer secret
        if (trimmed == "jubayerhasang85@gmail.com" || trimmed == "admin@mazharululoom.edu" || trimmed.contains("admin")) {
            _authSession.value = AuthSession(
                isLoggedIn = true,
                role = UserRole.ADMIN,
                userName = "সুপার অ্যাডমিন (অ্যাপ নির্মাতা)",
                email = secretEmail
            )
            _currentScreen.value = AppScreen.DASHBOARD
            onResult(true, "অ্যাপ নির্মাতা / সুপার অ্যাডমিন অ্যাক্সেস অনুমোদিত!")
        } else {
            onResult(false, "অননুমোদিত গোপন ইমেল ঠিকানা! সঠিক ইমেল প্রবেশ করান।")
        }
    }

    fun quickRoleSwitch(role: UserRole) {
        when (role) {
            UserRole.STUDENT -> {
                _authSession.value = AuthSession(
                    isLoggedIn = true,
                    role = UserRole.STUDENT,
                    userName = "মুহাম্মাদ তানভীর আহমেদ",
                    userPhone = "01711000001",
                    studentRoll = 1,
                    studentClass = "ইবতেদায়ী ৪র্থ শ্রেণি",
                    studentId = 1
                )
                _selectedClass.value = "ইবতেদায়ী ৪র্থ শ্রেণি"
            }
            UserRole.ASSISTANT_TEACHER -> {
                _authSession.value = AuthSession(
                    isLoggedIn = true,
                    role = UserRole.ASSISTANT_TEACHER,
                    userName = "মাওলানা মুহাম্মাদ যাকারিয়া",
                    userPhone = "01811000002",
                    ilhaqNumber = "IL-2024"
                )
            }
            UserRole.HEADMASTER -> {
                _authSession.value = AuthSession(
                    isLoggedIn = true,
                    role = UserRole.HEADMASTER,
                    userName = "মাওলানা মুফতি আব্দুল হক",
                    userPhone = "01911000003"
                )
            }
            UserRole.ADMIN -> {
                _authSession.value = AuthSession(
                    isLoggedIn = true,
                    role = UserRole.ADMIN,
                    userName = "জুবায়ের হাসান (অ্যাপ নির্মাতা)",
                    email = "jubayerhasang85@gmail.com"
                )
            }
        }
        _statusMessage.value = "রোল পরিবর্তন: ${role.banglaTitle}"
    }

    fun logout() {
        _authSession.value = AuthSession(isLoggedIn = false)
    }

    // --- Attendance Operations ---
    fun updateAttendanceStatus(student: StudentEntity, date: String, newStatus: String) {
        viewModelScope.launch {
            val record = AttendanceEntity(
                studentId = student.id,
                studentName = student.name,
                roll = student.roll,
                className = student.className,
                date = date,
                status = newStatus
            )
            repository.saveAttendance(record)
            _statusMessage.value = "${student.name}: $newStatus হিসেবে সংরক্ষিত"
        }
    }

    fun markAllPresentForClass(students: List<StudentEntity>, date: String) {
        viewModelScope.launch {
            val list = students.map { student ->
                AttendanceEntity(
                    studentId = student.id,
                    studentName = student.name,
                    roll = student.roll,
                    className = student.className,
                    date = date,
                    status = "উপস্থিত"
                )
            }
            repository.saveAttendanceList(list)
            _statusMessage.value = "সকল শিক্ষার্থীর উপস্থিতি নিশ্চিত করা হয়েছে"
        }
    }

    // --- Results Operations ---
    fun addOrUpdateResult(
        student: StudentEntity,
        examName: String,
        subject: String,
        marks: Double,
        totalMarks: Double = 100.0
    ) {
        viewModelScope.launch {
            val percentage = (marks / totalMarks) * 100.0
            val grade = when {
                percentage >= 80.0 -> "মুমতাজ"
                percentage >= 70.0 -> "জায়্যিদ জিদ্দান"
                percentage >= 60.0 -> "জায়্যিদ"
                percentage >= 40.0 -> "মাকবুল"
                else -> "রাসেল"
            }
            val result = ResultEntity(
                studentId = student.id,
                studentName = student.name,
                roll = student.roll,
                className = student.className,
                examName = examName,
                subject = subject,
                obtainedMarks = marks,
                totalMarks = totalMarks,
                grade = grade
            )
            repository.saveResult(result)
            _statusMessage.value = "${student.name}-এর $subject নম্বর ($marks) সংরক্ষিত!"
        }
    }

    // --- Notice Operations ---
    fun addNotice(title: String, content: String, category: String, isPinned: Boolean) {
        viewModelScope.launch {
            val author = _authSession.value.userName.ifBlank { "মাদ্রাসা কর্তৃপক্ষ" }
            val role = _authSession.value.role.banglaTitle
            val notice = NoticeEntity(
                title = title,
                content = content,
                category = category,
                date = todayDateString,
                authorName = author,
                authorRole = role,
                isPinned = isPinned
            )
            repository.saveNotice(notice)
            _statusMessage.value = "নতুন নোটিশ প্রকাশিত হয়েছে"
        }
    }

    fun deleteNotice(id: Long) {
        viewModelScope.launch {
            repository.deleteNotice(id)
            _statusMessage.value = "নোটিশটি মুছে ফেলা হয়েছে"
        }
    }

    // --- Syllabus Book Operations ---
    fun addSyllabusBook(className: String, bookName: String, subject: String, author: String, marks: Int, desc: String) {
        viewModelScope.launch {
            val book = SyllabusBookEntity(
                className = className,
                bookName = bookName,
                subject = subject,
                authorName = author,
                totalMarks = marks,
                description = desc
            )
            repository.saveSyllabusBook(book)
            _statusMessage.value = "নতুন কিতাব সিলেবাসে যুক্ত হয়েছে"
        }
    }

    // --- Admin Update Log Operations ---
    fun addUpdateLog(version: String, title: String, description: String, category: String) {
        viewModelScope.launch {
            val log = UpdateLogEntity(
                version = version,
                date = todayDateString,
                title = title,
                description = description,
                category = category
            )
            repository.saveUpdateLog(log)
            _statusMessage.value = "নতুন আপডেট লগ লিপিবদ্ধ করা হয়েছে"
        }
    }

    // Student attendance for date
    fun getAttendanceForDate(date: String) = repository.getAttendanceForDate(date)

    // Students for class
    fun getStudentsByClass(className: String) = repository.getStudentsByClass(className)

    // Get attendance flow for currently selected class and date
    fun getAttendanceForSelectedClassAndDate() =
        repository.getAttendanceByDateAndClass(_attendanceDate.value, _selectedClass.value)

    // Get results flow for currently selected class and exam
    fun getResultsForSelectedClassAndExam() =
        repository.getResultsByClassAndExam(_selectedClass.value, _selectedExam.value)

    // Get student specific attendance
    fun getStudentAttendance(studentId: Long) = repository.getAttendanceForStudent(studentId)

    // Get student specific results
    fun getStudentResults(studentId: Long) = repository.getResultsForStudent(studentId)
}
