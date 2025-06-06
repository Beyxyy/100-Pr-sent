package com.example.prezzapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prezzapp.adapters.AbsenceAdapter
import com.example.prezzapp.data.Absence
import com.example.prezzapp.databinding.ActivityStudentDashboardBinding

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDashboardBinding
    private lateinit var absenceAdapter: AbsenceAdapter
    private val allAbsences = mutableListOf<Absence>()
    private val visibleAbsences = mutableListOf<Absence>()
    private val pageSize = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadAllAbsences()
        loadNextAbsences()

        binding.btnVoirPlus.setOnClickListener {
            loadNextAbsences()
        }
    }

    private fun setupRecyclerView() {
        absenceAdapter = AbsenceAdapter(this, visibleAbsences) { absence ->
            if (!absence.isJustified) {
                val intent = Intent(this, JustifyAbsenceActivity::class.java).apply {
                    putExtra("selected_absence", absence)
                    putExtra("user_role", "student")
                }
                startActivity(intent)
            }
        }
        binding.rvAbsences.layoutManager = LinearLayoutManager(this)
        binding.rvAbsences.adapter = absenceAdapter
    }

    private fun loadAllAbsences() {
        allAbsences.clear()
        allAbsences.addAll(
            listOf(
                Absence("1", "Maths", "01/01/2025", "M. Dupont", true),
                Absence("2", "Physique", "02/01/2025", "Mme. Martin", false),
                Absence("3", "Anglais", "03/01/2025", "M. Smith", true),
                Absence("4", "Histoire", "04/01/2025", "Mme. Dubois", false),
                Absence("5", "Chimie", "05/01/2025", "M. Leclerc", true),
                Absence("6", "EPS", "06/01/2025", "Mme. Garcia", true),
                Absence("7", "Info", "07/01/2025", "M. Bernard", false),
                Absence("8", "Art", "08/01/2025", "Mme. Petit", true),
                Absence("9", "SVT", "09/01/2025", "Mme. Blanc", true),
                Absence("10", "Philo", "10/01/2025", "M. Noir", false)
            )
        )
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
        absenceAdapter.notifyDataSetChanged()
        loadNextAbsences()
    }
}
