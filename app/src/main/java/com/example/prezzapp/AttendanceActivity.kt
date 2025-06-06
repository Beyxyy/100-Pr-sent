package com.example.prezzapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prezzapp.databinding.ActivityAttendanceBinding
import com.example.prezzapp.adapters.StudentAttendanceAdapter
import com.example.prezzapp.model.*

class AttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceBinding
    private lateinit var adapter: StudentAttendanceAdapter
    private var courseId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        courseId = intent.getIntExtra("course_id", -1)

        adapter = StudentAttendanceAdapter()
        binding.studentList.layoutManager = LinearLayoutManager(this)
        binding.studentList.adapter = adapter

        Thread {
            val db = AppDatabase.getDatabase(this)
            val students =
                db.coursDao().getPromobyCours(courseId, Status.STUDENT)
            runOnUiThread { adapter.submitList(students) }
        }.start()

        binding.btnSaveAttendance.setOnClickListener {
            saveAttendance()
            finish()
        }
    }

    private fun saveAttendance() {
        val selectedIds = adapter.absentStudentIds()
        Thread {
            val db = AppDatabase.getDatabase(this)
            val presenceDao = db.presenceDao()
            selectedIds.forEach {
                presenceDao.insert(
                    Presence(
                        id = 0,
                        userId = it,
                        coursId = courseId,
                        estJustifie = false,
                        lien = null,
                        estPresent = false
                    )
                )
            }
        }.start()
    }
}
