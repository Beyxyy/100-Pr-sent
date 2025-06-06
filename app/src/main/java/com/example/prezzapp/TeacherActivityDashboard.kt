package com.example.prezzapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prezzapp.data.Absence
import com.example.prezzapp.databinding.ActivityTeacherDashboardBinding
import com.example.prezzapp.adapters.AbsenceAdapterTeacher

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
        loadAllAbsences()
        loadNextAbsences()

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

    private fun loadAllAbsences() {
        allAbsences.clear()
        allAbsences.addAll(
            listOf(
                Absence("1", "Maths", "01/01/2025", "Élève 1", true),
                Absence("2", "Physique", "02/01/2025", "Élève 2", false),
                Absence("3", "Anglais", "03/01/2025", "Élève 3", true),
                Absence("4", "Histoire", "04/01/2025", "Élève 4", false),
                Absence("5", "EPS", "05/01/2025", "Élève 5", true)
            )
        )
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
}
