package com.example.prezzapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prezzapp.adapters.AbsenceAdapter
import com.example.prezzapp.data.Absence
import com.example.prezzapp.databinding.ActivityStudentDashboardBinding

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDashboardBinding
    private lateinit var absenceAdapter: AbsenceAdapter
    private val absencesList = mutableListOf<Absence>() // Liste réelle des absences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadAbsences() // Simule le chargement des données depuis une API/DB

        binding.btnVoirPlus.setOnClickListener {
            Toast.makeText(this, "Chargement de plus d'absences...", Toast.LENGTH_SHORT).show()
            // Implémentez la logique de pagination ici
        }
    }

    private fun setupRecyclerView() {
        absenceAdapter = AbsenceAdapter(this, absencesList) { absence ->
            // Clic sur une absence non justifiée
            if (!absence.isJustified) {
                val intent = Intent(this, JustifyAbsenceActivity::class.java).apply {
                    putExtra("selected_absence", absence) // Passer l'objet Absence
                }
                startActivity(intent)
            }
        }
        binding.rvAbsences.layoutManager = LinearLayoutManager(this)
        binding.rvAbsences.adapter = absenceAdapter
    }

    private fun loadAbsences() {
        // Simule le chargement des absences depuis une base de données ou une API
        // En réalité, vous feriez un appel réseau ici
        absencesList.clear()
        absencesList.add(Absence("1", "Cours de Maths", "01/01/2025", "M. Dupont", true))
        absencesList.add(Absence("2", "Cours de Physique", "02/01/2025", "Mme. Martin", false))
        absencesList.add(Absence("3", "Cours d'Anglais", "03/01/2025", "M. Smith", true))
        absencesList.add(Absence("4", "Cours d'Histoire", "04/01/2025", "Mme. Dubois", false))
        absencesList.add(Absence("5", "Cours de Chimie", "05/01/2025", "M. Leclerc", true))
        absencesList.add(Absence("6", "Cours d'EPS", "06/01/2025", "Mme. Garcia", true))
        absencesList.add(Absence("7", "Cours d'Info", "07/01/2025", "M. Bernard", false))
        absencesList.add(Absence("8", "Cours d'Art", "08/01/2025", "Mme. Petit", true))

        absenceAdapter.notifyDataSetChanged() // Rafraîchit le RecyclerView
    }

    // Si vous revenez de JustifyAbsenceActivity et que l'absence a été justifiée
    override fun onResume() {
        super.onResume()
        // Idéalement, vous rechargeriez les données ou mettrait à jour l'état de l'absence spécifique
        // Pour cet exemple simple, nous allons recharger toutes les absences.
        // Dans une application réelle, vous pourriez passer le ID de l'absence justifiée en retour
        // et mettre à jour seulement cet élément.
        loadAbsences()
    }
}