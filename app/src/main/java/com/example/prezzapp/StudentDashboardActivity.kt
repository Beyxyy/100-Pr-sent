package com.example.prezzapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prezzapp.adapters.AbsenceAdapter
import com.example.prezzapp.data.Absence
import com.example.prezzapp.databinding.ActivityStudentDashboardBinding
import com.example.prezzapp.model.AppDatabase

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDashboardBinding
    private lateinit var absenceAdapter: AbsenceAdapter

    private val visibleAbsences = mutableListOf<Absence>()
    private val allAbsences = mutableListOf<Absence>()
    private val pageSize = 7
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getIntExtra("user_id", -1)

        setupRecyclerView()
        loadAbsencesFromDatabase()

        binding.btnVoirPlus.setOnClickListener {
            loadNextAbsences()
        }
    }

    private fun setupRecyclerView() {
        absenceAdapter = AbsenceAdapter(this, visibleAbsences) { absence ->
            val intent = if (absence.isJustified) {
                Intent(this, ViewJustificationActivity::class.java).apply {
                    putExtra("selected_absence", absence)
                }
            } else {
                Intent(this, JustifyAbsenceActivity::class.java).apply {
                    putExtra("selected_absence", absence)
                    putExtra("user_role", "student")
                }
            }
            startActivity(intent)
        }
        binding.rvAbsences.layoutManager = LinearLayoutManager(this)
        binding.rvAbsences.adapter = absenceAdapter
    }

    private fun loadAbsencesFromDatabase() {
        Thread {
            val db = AppDatabase.getDatabase(this)
            val presenceDao = db.presenceDao()
            val coursDao = db.coursDao()

            // Fetch all presences for the user
            val presences = presenceDao.getByUser(userId)
            val coursList = coursDao.getAll()

            // Map Presence entities to Absence data class
            val absences = presences.mapNotNull { presence ->
                val cours = coursList.find { it.id == presence.coursId }
                cours?.let {
                    Absence(
                        id = presence.id.toString(),
                        courseName = it.nomcours,
                        date = it.jour,
                        professorName = it.prof,
                        isJustified = presence.estJustifie,
                        justificationLink = presence.lien
                    )
                }
            }.sortedByDescending { it.date }

            runOnUiThread {
                val oldSize = visibleAbsences.size
                visibleAbsences.clear()
                absenceAdapter.notifyItemRangeRemoved(0, oldSize)

                allAbsences.clear()
                allAbsences.addAll(absences)

                loadNextAbsences()
            }

        }.start()
    }

    private fun loadNextAbsences() {
        val startIndex = visibleAbsences.size
        val endIndex = (startIndex + pageSize).coerceAtMost(allAbsences.size)

        if (startIndex >= endIndex) {
            binding.btnVoirPlus.visibility = View.GONE
            return
        }

        val nextAbsences = allAbsences.subList(startIndex, endIndex)
        visibleAbsences.addAll(nextAbsences)
        absenceAdapter.notifyItemRangeInserted(startIndex, nextAbsences.size)

        binding.btnVoirPlus.visibility = if (visibleAbsences.size >= allAbsences.size) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        loadAbsencesFromDatabase()
    }
}