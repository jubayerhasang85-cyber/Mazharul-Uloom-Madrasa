package com.example.data.repository

import com.example.data.local.dao.MadrasaDao
import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.NoticeEntity
import com.example.data.local.entity.ResultEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SyllabusBookEntity
import com.example.data.local.entity.TeacherEntity
import com.example.data.local.entity.UpdateLogEntity
import kotlinx.coroutines.flow.Flow

class MadrasaRepository(private val dao: MadrasaDao) {

    // Students
    val allStudents: Flow<List<StudentEntity>> = dao.getAllStudents()
    val studentCount: Flow<Int> = dao.getStudentCount()
    val teacherCount: Flow<Int> = dao.getTeacherCount()

    fun getStudentsByClass(className: String): Flow<List<StudentEntity>> =
        dao.getStudentsByClass(className)

    suspend fun findStudentForLogin(phone: String, className: String, roll: Int): StudentEntity? =
        dao.findStudentForLogin(phone.trim(), className.trim(), roll)

    suspend fun insertStudent(student: StudentEntity): Long = dao.insertStudent(student)

    // Teachers
    val allTeachers: Flow<List<TeacherEntity>> = dao.getAllTeachers()

    suspend fun findHeadmasterForLogin(phone: String, pin: String): TeacherEntity? =
        dao.findHeadmasterForLogin(phone.trim(), pin.trim())

    suspend fun findTeacherForLogin(phone: String, ilhaq: String, pin: String): TeacherEntity? =
        dao.findTeacherForLogin(phone.trim(), ilhaq.trim(), pin.trim())

    suspend fun insertTeacher(teacher: TeacherEntity): Long = dao.insertTeacher(teacher)

    // Attendance
    fun getAttendanceByDateAndClass(date: String, className: String): Flow<List<AttendanceEntity>> =
        dao.getAttendanceByDateAndClass(date, className)

    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceEntity>> =
        dao.getAttendanceForStudent(studentId)

    fun getAttendanceForDate(date: String): Flow<List<AttendanceEntity>> =
        dao.getAttendanceForDate(date)

    suspend fun saveAttendance(attendance: AttendanceEntity): Long =
        dao.insertAttendance(attendance)

    suspend fun saveAttendanceList(list: List<AttendanceEntity>) =
        dao.insertAttendanceList(list)

    // Results
    fun getResultsByClassAndExam(className: String, examName: String): Flow<List<ResultEntity>> =
        dao.getResultsByClassAndExam(className, examName)

    fun getResultsForStudent(studentId: Long): Flow<List<ResultEntity>> =
        dao.getResultsForStudent(studentId)

    suspend fun saveResult(result: ResultEntity): Long =
        dao.insertResult(result)

    // Notices
    val allNotices: Flow<List<NoticeEntity>> = dao.getAllNotices()

    suspend fun saveNotice(notice: NoticeEntity): Long = dao.insertNotice(notice)

    suspend fun deleteNotice(id: Long) = dao.deleteNoticeById(id)

    // Syllabus
    val allSyllabusBooks: Flow<List<SyllabusBookEntity>> = dao.getAllSyllabusBooks()

    fun getSyllabusBooksByClass(className: String): Flow<List<SyllabusBookEntity>> =
        dao.getSyllabusBooksByClass(className)

    suspend fun saveSyllabusBook(book: SyllabusBookEntity): Long = dao.insertSyllabusBook(book)

    // Update Logs
    val allUpdateLogs: Flow<List<UpdateLogEntity>> = dao.getAllUpdateLogs()

    suspend fun saveUpdateLog(log: UpdateLogEntity): Long = dao.insertUpdateLog(log)
}
