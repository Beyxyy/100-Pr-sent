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
        ThemeManager.applyTheme(this)

        super.onCreate(savedInstanceState)
        binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getIntExtra("user_id", -1)

        setupRecyclerView()
        loadAbsencesFromDatabase()

        binding.btnVoirPlus.setOnClickListener {
            loadNextAbsences()
        }

        binding.btnToggleTheme.setOnClickListener {
            ThemeManager.toggleTheme(this)
        }
    }

    private fun setupRecyclerView() {
        absenceAdapter = AbsenceAdapter(this, visibleAbsences) { absence ->
            val intent = Intent(this, JustifyAbsenceActivity::class.java).apply {
                putExtra("selected_absence", absence)
                putExtra("user_role", "student")
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
            val coursList = coursDao.getAll()
            val presences = presenceDao.getByUser(userId)

            allAbsences.clear()
            allAbsences.addAll(
                presences.mapNotNull { presence ->
                    val cours = coursList.find { it.id == presence.coursId }
                    cours?.let {
                        Absence(
                            id = presence.id.toString(),
                            courseName = it.nomcours,
                            date = it.jour,
                            professorName = it.prof,
                            isJustified = presence.estJustifie
                        )
                    }
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

        binding.btnVoirPlus.visibility = if (visibleAbsences.size >= allAbsences.size) {
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
