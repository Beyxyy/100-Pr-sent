// src/main/java/com/example/prezzapp/StudentDashboardActivity.kt
package com.example.prezzapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prezzapp.adapters.AbsenceAdapter
import com.example.prezzapp.data.Absence // Assurez-vous d'utiliser votre classe Absence mise à jour
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
                    putExtra("user_role", "student") // Assurez-vous que c'est toujours "student" ici
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
            val coursList = coursDao.getAll() // Fetch all courses to match with presences

            // Map Presence entities to Absence data class
            val absences = presences.mapNotNull { presence ->
                val cours = coursList.find { it.id == presence.coursId }
                cours?.let {
                    Absence(
                        id = presence.id.toString(),
                        courseName = it.nomcours,
                        date = it.jour,
                        professorName = it.prof,
                        isJustified = presence.estJustifie, // Correctly use estJustifie from Presence
                        justificationLink = presence.lien // <-- PASSEZ LE LIEN ICI
                    )
                }
            }.sortedByDescending { it.date } // Sort if needed, e.g., by date

            runOnUiThread {
                val oldSize = visibleAbsences.size
                visibleAbsences.clear()
                absenceAdapter.notifyItemRangeRemoved(0, oldSize) // Notify adapter for removal

                allAbsences.clear()
                allAbsences.addAll(absences) // Populate the full list

                loadNextAbsences() // Load the first page of absences
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

        // Hide "Voir plus" button if all absences are loaded
        binding.btnVoirPlus.visibility = if (visibleAbsences.size >= allAbsences.size) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        // Reload data from the database every time the activity comes to foreground
        // This ensures the list is up-to-date after returning from JustifyAbsenceActivity
        loadAbsencesFromDatabase()
    }
}