package com.example.data.local

import com.example.data.local.dao.MadrasaDao
import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.NoticeEntity
import com.example.data.local.entity.ResultEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SyllabusBookEntity
import com.example.data.local.entity.TeacherEntity
import com.example.data.local.entity.UpdateLogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InitialDataLoader {

    val CLASS_LIST = listOf(
        "নূরানী ও নাজেরা বিভাগ",
        "হিফজুল কুরআন বিভাগ",
        "ইবতেদায়ী ১ম শ্রেণি",
        "ইবতেদায়ী ২য় শ্রেণি",
        "ইবতেদায়ী ৩য় শ্রেণি",
        "ইবতেদায়ী ৪র্থ শ্রেণি",
        "ইবতেদায়ী ৫ম শ্রেণি",
        "মুতাওয়াসসিতাহ (মিজান বিভাগ)",
        "সানাবিয়া আম্মাহ (নাহবেমীর)",
        "সানাবিয়া খাসসাহ (হেদায়া)",
        "ফযীলত (জালালাইন)",
        "তাকমীল (দাওরায়ে হাদিস)"
    )

    suspend fun seedDatabaseIfEmpty(dao: MadrasaDao) = withContext(Dispatchers.IO) {
        val updateCount = dao.getUpdateLogCount()
        if (updateCount > 0) return@withContext

        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // 1. Teachers
        val teachers = listOf(
            TeacherEntity(
                name = "মাওলানা মুফতি আব্দুল হক",
                phone = "01911000003",
                pin = "7860",
                designation = "মুহতামিম ও প্রধান শায়খুল হাদিস",
                role = "HEADMASTER"
            ),
            TeacherEntity(
                name = "মাওলানা মুহাম্মাদ যাকারিয়া",
                phone = "01811000002",
                ilhaqNumber = "IL-2024",
                pin = "1234",
                designation = "সিনিয়র সহকারী শিক্ষক (কিতাব বিভাগ)",
                role = "TEACHER"
            ),
            TeacherEntity(
                name = "হাফেজ ক্বারী আহমদ উল্লাহ",
                phone = "01811000005",
                ilhaqNumber = "IL-2025",
                pin = "5555",
                designation = "উস্তাদুল হুফফাজ (হিফজ প্রধান)",
                role = "TEACHER"
            ),
            TeacherEntity(
                name = "মাওলানা সাইফুল ইসলাম",
                phone = "01811000006",
                ilhaqNumber = "IL-2026",
                pin = "7777",
                designation = "সহকারী শিক্ষক (ইবতেদায়ী বিভাগ)",
                role = "TEACHER"
            )
        )
        dao.insertTeachers(teachers)

        // 2. Students
        val students = listOf(
            // ইবতেদায়ী ৪র্থ শ্রেণি
            StudentEntity(
                name = "মুহাম্মাদ তানভীর আহমেদ",
                roll = 1,
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                phone = "01711000001",
                guardianName = "আব্দুর রহিম",
                admissionYear = "২০২৪",
                bloodGroup = "B+"
            ),
            StudentEntity(
                name = "আব্দুল্লাহ আল নোমান",
                roll = 2,
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                phone = "01711000012",
                guardianName = "মোস্তফা কামাল",
                admissionYear = "২০২৪",
                bloodGroup = "A+"
            ),
            StudentEntity(
                name = "মুহাম্মাদ সালমান ফারসি",
                roll = 3,
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                phone = "01711000013",
                guardianName = "মোহাম্মদ রফিক",
                admissionYear = "২০২৪",
                bloodGroup = "O+"
            ),
            StudentEntity(
                name = "মাহমুদুল হাসান",
                roll = 4,
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                phone = "01711000014",
                guardianName = "মোঃ নুরুল ইসলাম",
                admissionYear = "২০২৪",
                bloodGroup = "AB+"
            ),
            // হিফজুল কুরআন বিভাগ
            StudentEntity(
                name = "হাফেজ মোশাররফ হোসাইন",
                roll = 1,
                className = "হিফজুল কুরআন বিভাগ",
                phone = "01711000021",
                guardianName = "মাওলানা মোজাম্মেল",
                admissionYear = "২০২৩",
                bloodGroup = "O+"
            ),
            StudentEntity(
                name = "মুহাম্মাদ তাওহীদুল ইসলাম",
                roll = 2,
                className = "হিফজুল কুরআন বিভাগ",
                phone = "01711000022",
                guardianName = "নুরুল হক",
                admissionYear = "২০২৩",
                bloodGroup = "B+"
            ),
            // তাকমীল (দাওরায়ে হাদিস)
            StudentEntity(
                name = "মাওলানা জুবায়ের হুসাইন",
                roll = 1,
                className = "তাকমীল (দাওরায়ে হাদিস)",
                phone = "01711000031",
                guardianName = "মাওলানা বশীরুল্লাহ",
                admissionYear = "২০২১",
                bloodGroup = "O+"
            ),
            StudentEntity(
                name = "আবু বকর সিদ্দীক",
                roll = 2,
                className = "তাকমীল (দাওরায়ে হাদিস)",
                phone = "01711000032",
                guardianName = "হাফেজ খলিলুর রহমান",
                admissionYear = "২০২১",
                bloodGroup = "A+"
            ),
            // মুতাওয়াসসিতাহ (মিজান)
            StudentEntity(
                name = "মুহাম্মাদ ইমরান নাজির",
                roll = 1,
                className = "মুতাওয়াসসিতাহ (মিজান বিভাগ)",
                phone = "01711000041",
                guardianName = "নাজির আহমেদ",
                admissionYear = "২০২৪",
                bloodGroup = "A+"
            )
        )
        dao.insertStudents(students)

        // 3. Today's Attendance
        val attendances = listOf(
            AttendanceEntity(
                studentId = 1,
                studentName = "মুহাম্মাদ তানভীর আহমেদ",
                roll = 1,
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                date = todayDate,
                status = "উপস্থিত",
                remarks = "সময়ানুবর্তী"
            ),
            AttendanceEntity(
                studentId = 2,
                studentName = "আব্দুল্লাহ আল নোমান",
                roll = 2,
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                date = todayDate,
                status = "উপস্থিত",
                remarks = ""
            ),
            AttendanceEntity(
                studentId = 3,
                studentName = "মুহাম্মাদ সালমান ফারসি",
                roll = 3,
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                date = todayDate,
                status = "অনুপস্থিত",
                remarks = "জ্বর সংক্রান্ত অসুস্থতা"
            ),
            AttendanceEntity(
                studentId = 4,
                studentName = "মাহমুদুল হাসান",
                roll = 4,
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                date = todayDate,
                status = "ছুটি",
                remarks = "পারিবারিক কারণ"
            ),
            AttendanceEntity(
                studentId = 5,
                studentName = "হাফেজ মোশাররফ হোসাইন",
                roll = 1,
                className = "হিফজুল কুরআন বিভাগ",
                date = todayDate,
                status = "উপস্থিত",
                remarks = "আজকের সবক সম্পূর্ণ"
            ),
            AttendanceEntity(
                studentId = 6,
                studentName = "মুহাম্মাদ তাওহীদুল ইসলাম",
                roll = 2,
                className = "হিফজুল কুরআন বিভাগ",
                date = todayDate,
                status = "উপস্থিত",
                remarks = ""
            )
        )
        dao.insertAttendanceList(attendances)

        // 4. Results
        val results = listOf(
            // তানভীর - ১ম সাময়িক
            ResultEntity(
                studentId = 1,
                studentName = "মুহাম্মাদ তানভীর আহমেদ",
                roll = 1,
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                examName = "১ম সাময়িক পরীক্ষা",
                subject = "আল-কুরআন ও তাজবীদ",
                obtainedMarks = 95.0,
                grade = "মুমতাজ"
            ),
            ResultEntity(
                studentId = 1,
                studentName = "মুহাম্মাদ তানভীর আহমেদ",
                roll = 1,
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                examName = "১ম সাময়িক পরীক্ষা",
                subject = "আত-তারিকু ইলাল আরাবিয়্যাহ",
                obtainedMarks = 91.0,
                grade = "মুমতাজ"
            ),
            ResultEntity(
                studentId = 1,
                studentName = "মুহাম্মাদ তানভীর আহমেদ",
                roll = 1,
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                examName = "১ম সাময়িক পরীক্ষা",
                subject = "ইসলাম শিক্ষা ও ফিকহ",
                obtainedMarks = 88.0,
                grade = "মুমতাজ"
            ),
            ResultEntity(
                studentId = 1,
                studentName = "মুহাম্মাদ তানভীর আহমেদ",
                roll = 1,
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                examName = "১ম সাময়িক পরীক্ষা",
                subject = "বাংলা ও সাহিত্য",
                obtainedMarks = 84.0,
                grade = "মুমতাজ"
            ),
            ResultEntity(
                studentId = 1,
                studentName = "মুহাম্মাদ তানভীর আহমেদ",
                roll = 1,
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                examName = "১ম সাময়িক পরীক্ষা",
                subject = "গণিত",
                obtainedMarks = 78.0,
                grade = "জায়্যিদ জিদ্দান"
            ),
            // নোমান - ১ম সাময়িক
            ResultEntity(
                studentId = 2,
                studentName = "আব্দুল্লাহ আল নোমান",
                roll = 2,
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                examName = "১ম সাময়িক পরীক্ষা",
                subject = "আল-কুরআন ও তাজবীদ",
                obtainedMarks = 85.0,
                grade = "মুমতাজ"
            ),
            ResultEntity(
                studentId = 2,
                studentName = "আব্দুল্লাহ আল নোমান",
                roll = 2,
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                examName = "১ম সাময়িক পরীক্ষা",
                subject = "আত-তারিকু ইলাল আরাবিয়্যাহ",
                obtainedMarks = 76.0,
                grade = "জায়্যিদ জিদ্দান"
            ),
            // মোশাররফ - ১ম সাময়িক
            ResultEntity(
                studentId = 5,
                studentName = "হাফেজ মোশাররফ হোসাইন",
                roll = 1,
                className = "হিফজুল কুরআন বিভাগ",
                examName = "১ম সাময়িক পরীক্ষা",
                subject = "হিফজুল কুরআন (তিলাওয়াত ও মুখস্থ)",
                obtainedMarks = 98.0,
                grade = "মুমতাজ"
            ),
            ResultEntity(
                studentId = 5,
                studentName = "হাফেজ মোশাররফ হোসাইন",
                roll = 1,
                className = "হিফজুল কুরআন বিভাগ",
                examName = "১ম সাময়িক পরীক্ষা",
                subject = "তাজবীদুল কুরআন",
                obtainedMarks = 94.0,
                grade = "মুমতাজ"
            )
        )
        dao.insertResults(results)

        // 5. Notices
        val notices = listOf(
            NoticeEntity(
                title = "আসন্ন বার্ষিক পরীক্ষা ২০২৪ এর সময়সূচি ঘোষণা",
                content = "মাযহারুল উলূম মাদ্রাসার সম্মানিত অভিভাবক ও ছাত্রদের অবগতির জন্য জানানো যাচ্ছে যে, আগামী মাসের ১৫ তারিখ থেকে সকল বিভাগের বার্ষিক পরীক্ষা অনুষ্ঠিত হবে। সকল ছাত্রকে যথাসময়ে ফি পরিশোধ করে প্রবেশপত্র সংগ্রহের নির্দেশ দেওয়া হলো।",
                category = "জরুরি",
                date = todayDate,
                authorName = "মাওলানা মুফতি আব্দুল হক",
                authorRole = "মুহতামিম",
                isPinned = true
            ),
            NoticeEntity(
                title = "হিফজ ও নূরানী বিভাগের হুসনে সওত প্রতিযোগিতা",
                content = "আগামী জুমাবার বাদ আসর মাদ্রাসার কেন্দ্রীয় মিলনায়তনে ক্বিরাত ও নাশিদ প্রতিযোগিতা অনুষ্ঠিত হবে। আগ্রহী শিক্ষার্থীদের নিজ নিজ শিক্ষকের নিকট নাম তালিকাভুক্ত করার অনুরোধ করা হলো।",
                category = "সাধারণ",
                date = todayDate,
                authorName = "হাফেজ ক্বারী আহমদ উল্লাহ",
                authorRole = "হিফজ প্রধান",
                isPinned = false
            ),
            NoticeEntity(
                title = "পবিত্র রমাদানুল মুবারক ও ঈদুল ফিতরের ছুটি সংক্রান্ত",
                content = "রমজানের শেষ দশ দিন এবং ঈদুল ফিতর উপলক্ষে মাদ্রাসা বন্ধ থাকবে। ছুটির পূর্বে সকল কিতাবের নিসাব সম্পন্ন করা আবশ্যক।",
                category = "ছুটি",
                date = todayDate,
                authorName = "মুহতামিম কার্যালয়",
                authorRole = "প্রধান শিক্ষক",
                isPinned = false
            ),
            NoticeEntity(
                title = "১ম সাময়িক পরীক্ষার ফলাফল ট্র্যাকিং অ্যাপে আপলোড সম্পন্ন",
                content = "ছাত্র ও অভিভাবকগণ তাদের রোল ও ফোন নম্বর দিয়ে লগইন করে স্ব স্ব ফলাফল পত্র ডাউনলোড ও পর্যবেক্ষণ করতে পারবেন। কোনো সংশোধনের প্রয়োজন হলে সহকারী শিক্ষকের সাথে যোগাযোগ করুন।",
                category = "পরীক্ষা",
                date = todayDate,
                authorName = "পরীক্ষা নিয়ন্ত্রণ বিভাগ",
                authorRole = "সহকারী শিক্ষক",
                isPinned = true
            )
        )
        dao.insertNotices(notices)

        // 6. Syllabus Books (প্রতিটি ক্লাসের বইয়ের তালিকা)
        val books = listOf(
            // নূরানী ও নাজেরা
            SyllabusBookEntity(
                className = "নূরানী ও নাজেরা বিভাগ",
                bookName = "নূরানী কায়দা ও আমপারা",
                subject = "কুরআন শিক্ষা",
                authorName = "মাওলানা ক্বারী বেলায়েত হুসাইন",
                totalMarks = 100,
                description = "মাখরাজ, সিফাত ও শুদ্ধ তিলাওয়াত অনুশীলন"
            ),
            SyllabusBookEntity(
                className = "নূরানী ও নাজেরা বিভাগ",
                bookName = "সহজ দ্বীনিয়াত ও মাসনুন দুআ",
                subject = "ইসলামিয়াত",
                authorName = "মাদানি নেসাব",
                totalMarks = 100,
                description = "দৈনন্দিন নামাজ, অজু ও সুন্নাত দুআ"
            ),
            SyllabusBookEntity(
                className = "নূরানী ও নাজেরা বিভাগ",
                bookName = "ছোটদের বাংলা ও হস্তলিপি",
                subject = "বাংলা",
                authorName = "বোর্ড পাঠ্য",
                totalMarks = 50,
                description = "স্বরবর্ণ, ব্যঞ্জনবর্ণ ও সুন্দর হস্তলিপি"
            ),

            // হিফজুল কুরআন
            SyllabusBookEntity(
                className = "হিফজুল কুরআন বিভাগ",
                bookName = "কুরআনুল কারীম (৩০ পারা মুখস্থ)",
                subject = "হিফজ",
                authorName = "কালামুল্লাহ",
                totalMarks = 100,
                description = "দৈনিক নতুন সবক, সাত সবক ও আমুখতা (দাওর)"
            ),
            SyllabusBookEntity(
                className = "হিফজুল কুরআন বিভাগ",
                bookName = "তুহফাতুল আতফাল (তাজবীদ)",
                subject = "তাজবীদ শাস্ত্র",
                authorName = "আল্লামা সুলাইমান জামযূরী",
                totalMarks = 100,
                description = "নুন সাকিন, মিম সাকিন ও মাদ্দের নিয়মাবলী"
            ),

            // ইবতেদায়ী ৪র্থ শ্রেণি
            SyllabusBookEntity(
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                bookName = "আত-তারিকু ইলাল আরাবিয়্যাহ (১ম ও ২য় খণ্ড)",
                subject = "আরবি ভাষা শিক্ষা",
                authorName = "মাওলানা আবু তাহের মেছবাহ",
                totalMarks = 100,
                description = "ব্যবহারিক আরবি কথোপকথন ও বাক্য গঠন"
            ),
            SyllabusBookEntity(
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                bookName = "আল-কুরআন ও তাজবীদুল কুরআন",
                subject = "কুরআন মাজিদ",
                authorName = "বেফাকুল মাদারিসিল আরাবিয়া",
                totalMarks = 100,
                description = "পারা ১-১০ নাজেরা ও তাজবীদ প্রয়োগ"
            ),
            SyllabusBookEntity(
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                bookName = "তা’লীমুল ইসলাম (৩য় খণ্ড)",
                subject = "আকাইদ ও মাসায়েল",
                authorName = "মুফতি কেফায়াতুল্লাহ দেহলভী",
                totalMarks = 100,
                description = "নামাজ, রোজা ও ঈমানিয়াত বিষয়ক মাসআলা"
            ),
            SyllabusBookEntity(
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                bookName = "বাংলা সাহিত্য পাঠ ও ব্যাকরণ",
                subject = "বাংলা",
                authorName = "এনসিটিবি ও মাদরাসা বোর্ড",
                totalMarks = 100,
                description = "গদ্য, পদ্য ও বাংলা ব্যাকরণ"
            ),
            SyllabusBookEntity(
                className = "ইবতেদায়ী ৪র্থ শ্রেণি",
                bookName = "ব্যবহারিক গণিত ও জ্যামিতি",
                subject = "গণিত",
                authorName = "বোর্ড পাঠ্য",
                totalMarks = 100,
                description = "ভগ্নাংশ, গুণ, ভাগ ও জ্যামিতিক চিত্র"
            ),

            // মুতাওয়াসসিতাহ (মিজান বিভাগ)
            SyllabusBookEntity(
                className = "মুতাওয়াসসিতাহ (মিজান বিভাগ)",
                bookName = "মিজানুছ ছরফ ও মুনশাইব",
                subject = "ছরফ শাস্ত্র (Morphology)",
                authorName = "সিরাজুদ্দীন জন্দী / হামদুল্লাহ",
                totalMarks = 100,
                description = "আরবি ক্রিয়া রূপান্তর ও বাবসমূহ"
            ),
            SyllabusBookEntity(
                className = "মুতাওয়াসসিতাহ (মিজান বিভাগ)",
                bookName = "পাঞ্জেগাঞ্জ ও ফার্সি কাওয়ায়েদ",
                subject = "ফার্সি ভাষা",
                authorName = "আশরাফ আলী থানভী (রহ.)",
                totalMarks = 100,
                description = "ফার্সি শব্দার্থ ও ব্যাকরণ"
            ),

            // সানাবিয়া আম্মাহ (নাহবেমীর)
            SyllabusBookEntity(
                className = "সানাবিয়া আম্মাহ (নাহবেমীর)",
                bookName = "নাহবেমীর (আরবি ব্যাকরণ)",
                subject = "ইলমুন নাহব (Syntax)",
                authorName = "মীর সাইয়্যেদ শরীফ জুরজানী",
                totalMarks = 100,
                description = "ইসম, ফেল, হরফ ও তারকীবের মৌলিক নিয়ম"
            ),
            SyllabusBookEntity(
                className = "সানাবিয়া আম্মাহ (নাহবেমীর)",
                bookName = "ইলমুছ ছীগাহ",
                subject = "উন্নত ছরফ",
                authorName = "মুফতি এনায়েত আহমদ",
                totalMarks = 100,
                description = "তালিল ও তালীলের কায়দা-কানুন"
            ),

            // তাকমীল (দাওরায়ে হাদিস)
            SyllabusBookEntity(
                className = "তাকমীল (দাওরায়ে হাদিস)",
                bookName = "সহীহ আল-বুখারী (১ম ও ২য় খণ্ড)",
                subject = "হাদিস শাস্ত্র",
                authorName = "ইমাম মুহাম্মাদ ইবনে ইসমাইল বুখারী (রহ.)",
                totalMarks = 200,
                description = "কিতাবুল ঈমান, কিতাবুল ইলম ও ফিকহুস সুনান"
            ),
            SyllabusBookEntity(
                className = "তাকমীল (দাওরায়ে হাদিস)",
                bookName = "সহীহ মুসলিম (১ম ও ২য় খণ্ড)",
                subject = "হাদিস শাস্ত্র",
                authorName = "ইমাম মুসলিম ইবনুল হাজ্জাজ (রহ.)",
                totalMarks = 200,
                description = "মুকাদ্দামায়ে সহীহ মুসলিম ও হাদিসের সনদের তাহকীক"
            ),
            SyllabusBookEntity(
                className = "তাকমীল (দাওরায়ে হাদিস)",
                bookName = "জামে তিরমিযী ও সুনানে আবু দাউদ",
                subject = "হাদিস ও ইখতিলাফুল ফুকাহা",
                authorName = "ইমাম তিরমিযী ও ইমাম আবু দাউদ (রহ.)",
                totalMarks = 200,
                description = "মাযহাবসমূহের দলিল ও হাদিসের স্তরভেদ"
            )
        )
        dao.insertSyllabusBooks(books)

        // 7. Update Logs (Admin-Only feature)
        val logs = listOf(
            UpdateLogEntity(
                version = "v1.2.0 (বর্তমান)",
                date = todayDate,
                title = "মাল্টি-রোল অথেনটিকেশন ও পূর্ণাঙ্গ নোটিশ বোর্ড",
                description = "ছাত্র (ফোন+শ্রেণি+রোল), সহকারী শিক্ষক (ফোন+ইলহাক+পিন), প্রধান শিক্ষক (ফোন+পিন) এবং অ্যাপ নির্মাতার জন্য গোপন ইমেল অথেনটিকেশন ও পারমিশন সিস্টেম সক্রিয় করা হয়েছে।",
                category = "নিরাপত্তা ও অ্যাক্সেস"
            ),
            UpdateLogEntity(
                version = "v1.1.0",
                date = "২০২৬-০৯-২০",
                title = "অ্যাটেন্ডেন্স ট্র্যাকিং ও রেজাল্ট শিট ক্যালকুলেটর",
                description = "শ্রেণিভিত্তিক দৈনিক হাজিরা গ্রহণ, উপস্থিতি পারসেন্টেজ হিসাব এবং সাময়িক পরীক্ষার গ্রেডিং (মুমতাজ, জায়্যিদ জিদ্দান, জায়্যিদ, মাকবুল, রাসেল) সংযোজন।",
                category = "ফিচার রিলিজ"
            ),
            UpdateLogEntity(
                version = "v1.0.0",
                date = "২০২৬-০৮-১৫",
                title = "মাযহারুল উলূম মাদ্রাসা অ্যাপের মূল কাঠামো প্রতিষ্ঠা",
                description = "AppSheet আর্কিটেকচারে স্থানীয় রুম ডাটাবেজ, প্রতিটি ক্লাসের বিষয় ও কিতাবের তালিকা এবং সবুজ-সাদা থিমিং সম্পন্ন।",
                category = "সিস্টেম রিলিজ"
            )
        )
        dao.insertUpdateLogs(logs)
    }
}
