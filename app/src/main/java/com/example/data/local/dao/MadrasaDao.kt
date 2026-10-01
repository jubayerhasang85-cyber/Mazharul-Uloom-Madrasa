package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.NoticeEntity
import com.example.data.local.entity.ResultEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SyllabusBookEntity
import com.example.data.local.entity.TeacherEntity
import com.example.data.local.entity.UpdateLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MadrasaDao {

    // --- Students ---
    @Query("SELECT * FROM students ORDER BY className, roll ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE className = :className ORDER BY roll ASC")
    fun getStudentsByClass(className: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE phone = :phone AND className = :className AND roll = :roll LIMIT 1")
    suspend fun findStudentForLogin(phone: String, className: String, roll: Int): StudentEntity?

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: Long): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCount(): Flow<Int>

    // --- Teachers ---
    @Query("SELECT * FROM teachers ORDER BY role DESC, name ASC")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    @Query("SELECT * FROM teachers WHERE phone = :phone AND pin = :pin AND role = 'HEADMASTER' LIMIT 1")
    suspend fun findHeadmasterForLogin(phone: String, pin: String): TeacherEntity?

    @Query("SELECT * FROM teachers WHERE phone = :phone AND ilhaqNumber = :ilhaq AND pin = :pin LIMIT 1")
    suspend fun findTeacherForLogin(phone: String, ilhaq: String, pin: String): TeacherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: TeacherEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeachers(teachers: List<TeacherEntity>)

    @Query("SELECT COUNT(*) FROM teachers")
    fun getTeacherCount(): Flow<Int>

    // --- Attendance ---
    @Query("SELECT * FROM attendances WHERE date = :date AND className = :className ORDER BY roll ASC")
    fun getAttendanceByDateAndClass(date: String, className: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendances WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendances WHERE date = :date")
    fun getAttendanceForDate(date: String): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(attendances: List<AttendanceEntity>)

    // --- Results ---
    @Query("SELECT * FROM results WHERE className = :className AND examName = :examName ORDER BY roll ASC")
    fun getResultsByClassAndExam(className: String, examName: String): Flow<List<ResultEntity>>

    @Query("SELECT * FROM results WHERE studentId = :studentId ORDER BY examName ASC")
    fun getResultsForStudent(studentId: Long): Flow<List<ResultEntity>>

    @Query("SELECT * FROM results ORDER BY id DESC")
    fun getAllResults(): Flow<List<ResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(results: List<ResultEntity>)

    // --- Notices ---
    @Query("SELECT * FROM notices ORDER BY isPinned DESC, id DESC")
    fun getAllNotices(): Flow<List<NoticeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: NoticeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotices(notices: List<NoticeEntity>)

    @Query("DELETE FROM notices WHERE id = :id")
    suspend fun deleteNoticeById(id: Long)

    // --- Syllabus & Books ---
    @Query("SELECT * FROM syllabus_books ORDER BY className, id ASC")
    fun getAllSyllabusBooks(): Flow<List<SyllabusBookEntity>>

    @Query("SELECT * FROM syllabus_books WHERE className = :className ORDER BY id ASC")
    fun getSyllabusBooksByClass(className: String): Flow<List<SyllabusBookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyllabusBook(book: SyllabusBookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyllabusBooks(books: List<SyllabusBookEntity>)

    // --- Update Logs (Admin only) ---
    @Query("SELECT * FROM update_logs ORDER BY id DESC")
    fun getAllUpdateLogs(): Flow<List<UpdateLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdateLog(log: UpdateLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdateLogs(logs: List<UpdateLogEntity>)

    @Query("SELECT COUNT(*) FROM update_logs")
    suspend fun getUpdateLogCount(): Int
}
