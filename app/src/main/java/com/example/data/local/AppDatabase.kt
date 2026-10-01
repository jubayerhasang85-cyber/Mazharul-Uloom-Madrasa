package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.MadrasaDao
import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.NoticeEntity
import com.example.data.local.entity.ResultEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SyllabusBookEntity
import com.example.data.local.entity.TeacherEntity
import com.example.data.local.entity.UpdateLogEntity

@Database(
    entities = [
        StudentEntity::class,
        TeacherEntity::class,
        AttendanceEntity::class,
        ResultEntity::class,
        NoticeEntity::class,
        SyllabusBookEntity::class,
        UpdateLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun madrasaDao(): MadrasaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mazharul_uloom_madrasa.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
