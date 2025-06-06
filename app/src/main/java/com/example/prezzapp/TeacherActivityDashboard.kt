package com.example.prezzapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prezzapp.data.Absence
import com.example.prezzapp.databinding.ActivityTeacherDashboardBinding
import com.example.prezzapp.adapters.AbsenceAdapterTeacher
import com.example.prezzapp.model.AppDatabase

class TeacherDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherDashboardBinding
    private lateinit var absenceAdapter: AbsenceAdapterTeacher
    private val allAbsences = mutableListOf<Absence>()
    private val visibleAbsences = mutableListOf<Absence>()
    private val pageSize = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadAbsencesFromDatabase()

        binding.btnVoirPlusProf.setOnClickListener {
            loadNextAbsences()
        }
    }

    private fun setupRecyclerView() {
        absenceAdapter = AbsenceAdapterTeacher(this, visibleAbsences) { absence ->
            if (absence.isJustified) {
                val intent = Intent(this, JustifyAbsenceActivity::class.java).apply {
                    putExtra("selected_absence", absence)
                    putExtra("user_role", "teacher")
                }
                startActivity(intent)
            }
        }
        binding.rvAbsencesProf.layoutManager = LinearLayoutManager(this)
        binding.rvAbsencesProf.adapter = absenceAdapter
    }

    private fun loadAbsencesFromDatabase() {
        Thread {
            val db = AppDatabase.getDatabase(this)
            val presenceDao = db.presenceDao()
            val coursDao = db.coursDao()
            val userDao = db.userDao()
            val coursList = coursDao.getAll()
            val userList = userDao.getAll()
            val presences = presenceDao.getAll()

            allAbsences.clear()
            allAbsences.addAll(
                presences.mapNotNull { presence ->
                    val cours = coursList.find { it.id == presence.coursId }
                    val student = userList.find { it.id == presence.userId }
                    if (cours != null && student != null) {
                        Absence(
                            id = presence.id.toString(),
                            courseName = cours.prof,
                            date = cours.jour,
                            professorName = student.name,
                            isJustified = presence.estJustifie
                        )
                    } else null
                }
            )

            runOnUiThread {
                loadNextAbsences()
            }
        }.start()
    }

    private fun loadNextAbsences() {
        val nextIndex = visibleAbsences.size
        val endIndex = (nextIndex + pageSize).coerceAtMost(allAbsences.size)
        val nextItems = allAbsences.subList(nextIndex, endIndex)

        visibleAbsences.addAll(nextItems)
        absenceAdapter.notifyItemRangeInserted(nextIndex, nextItems.size)

        binding.btnVoirPlusProf.visibility = if (visibleAbsences.size >= allAbsences.size) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        visibleAbsences.clear()
        allAbsences.clear()
        loadAbsencesFromDatabase()
    }
}
