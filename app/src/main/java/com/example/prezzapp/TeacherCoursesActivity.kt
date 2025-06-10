package com.example.prezzapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prezzapp.adapters.StudentAdapter
import com.example.prezzapp.data.StudentItem
import com.example.prezzapp.databinding.ActivityTeacherCoursesBinding
import com.example.prezzapp.model.AppDatabase
import com.example.prezzapp.model.Presence
import com.example.prezzapp.model.Status
import com.example.prezzapp.ThemeManager

class TeacherCoursesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherCoursesBinding
    private lateinit var adapter: StudentAdapter
    private val students = mutableListOf<StudentItem>()
    private var courseId: Int = -1
    private var profLogin: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherCoursesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        courseId = intent.getIntExtra("course_id", -1)
        profLogin = intent.getStringExtra("prof_login") ?: ""

        adapter = StudentAdapter(students) { student, isChecked ->
            if (isChecked) insertAbsence(student.id) else deleteAbsence(student.id)
            updateAbsentCounter()
        }

        binding.rvStudents.layoutManager = LinearLayoutManager(this)
        binding.rvStudents.adapter = adapter

        binding.btnToggleTheme.setOnClickListener {
            ThemeManager.toggleTheme(this)
        }

        loadStudentsForCourse()
    }

    private fun loadStudentsForCourse() {
        Thread {
            val db = AppDatabase.getDatabase(this)
            val presenceDao = db.presenceDao()
            val userDao = db.userDao()
            val coursDao = db.coursDao()

            val course = coursDao.getAll().find { it.id == courseId }
            val studentUsers = if (course != null) {
                userDao.getAll().filter {
                    it.status == Status.STUDENT &&
                            it.annee == course.annee &&
                            it.spe == course.spe
                }
            } else emptyList()

            val presences = presenceDao.getAll().filter { it.coursId == courseId }

            students.clear()
            studentUsers.forEach { user ->
                val presence = presences.find { it.userId == user.id && !it.estPresent }
                students.add(
                    StudentItem(
                        id = user.id,
                        fullName = user.name,
                        isAbsent = presence != null
                    )
                )
            }

            runOnUiThread {
                adapter.notifyDataSetChanged()
                updateAbsentCounter()
            }
        }.start()
    }

    private fun updateAbsentCounter() {
        val count = students.count { it.isAbsent }
        binding.tvAbsentCount.text = "$count absent(s)"
    }

    private fun insertAbsence(studentId: Int) {
        Thread {
            val db = AppDatabase.getDatabase(this)
            val presenceDao = db.presenceDao()
            val exists = presenceDao.getAll()
                .any { it.userId == studentId && it.coursId == courseId && !it.estPresent }
            if (!exists) {
                presenceDao.insert(
                    Presence(
                        id = 0,
                        userId = studentId,
                        coursId = courseId,
                        estJustifie = false,
                        lien = null,
                        estPresent = false
                    )
                )
            }
        }.start()
    }

    private fun deleteAbsence(studentId: Int) {
        Thread {
            val db = AppDatabase.getDatabase(this)
            val presenceDao = db.presenceDao()
            val toDelete = presenceDao.getAll()
                .find { it.userId == studentId && it.coursId == courseId && !it.estPresent }
            toDelete?.let { presenceDao.deleteById(it.id) }
        }.start()
    }
}
