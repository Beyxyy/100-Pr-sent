package com.example.prezzapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prezzapp.adapters.AbsenceAdapter
import com.example.prezzapp.data.Absence
import com.example.prezzapp.databinding.ActivityStudentDashboardBinding
import com.example.prezzapp.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDashboardBinding
    private lateinit var absenceAdapter: AbsenceAdapter

    private val allAbsences = mutableListOf<Absence>()
    private val visibleAbsences = mutableListOf<Absence>()
    private val pageSize = 7

    private var currentUserId: Int = -1

    companion object {
        var isDatabaseInitialized = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUserId = intent.getIntExtra("userId", -1)
        setupRecyclerView()

        lifecycleScope.launch {
            if (!isDatabaseInitialized) {
                populateSampleData()
                isDatabaseInitialized = true
            }
            loadAllAbsencesFromDatabase()
        }

        binding.btnVoirPlus.setOnClickListener { loadNextAbsences() }
    }

    private fun setupRecyclerView() {
        absenceAdapter = AbsenceAdapter(this, visibleAbsences) { absence ->
            if (!absence.isJustified) {
                val intent = Intent(this, JustifyAbsenceActivity::class.java).apply {
                    putExtra("selected_absence", absence)
                }
                startActivity(intent)
            }
        }
        binding.rvAbsences.layoutManager = LinearLayoutManager(this)
        binding.rvAbsences.adapter = absenceAdapter
    }

    private suspend fun populateSampleData() = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(this@StudentDashboardActivity)
        val userDao = db.userDao()
        val coursDao = db.coursDao()
        val presenceDao = db.presenceDao()

        presenceDao.deleteAll()
        userDao.deleteAll()
        coursDao.deleteAll()

        val coursList = listOf(
            Cours(0, "Joel Dion", "01/01/2025", "8h-10h", "CM", "1A", "IR"),
            Cours(0, "Jean Lemoine", "02/01/2025", "10h-12h", "TD", "2A", "IR"),
            Cours(0, "Luc Bernard", "03/01/2025", "13h-15h", "TP", "1A", "IR"),
            Cours(0, "Sophie Marchand", "04/01/2025", "15h-17h", "CM", "3A", "IR")
        )
        coursList.forEach { coursDao.insert(it) }
        val allCours = coursDao.getAll()

        val users = listOf(
            User(101, "joel.dion@uha.fr", "Joel Dion", "azerty1", "", "", "", "", Status.TEACHER),
            User(102, "jean.lemoine@uha.fr", "Jean Lemoine", "azerty2", "", "", "", "", Status.TEACHER),
            User(103, "luc.bernard@uha.fr", "Luc Bernard", "azerty3", "", "", "", "", Status.TEACHER),
            User(104, "sophie.marchand@uha.fr", "Sophie Marchand", "azerty4", "", "", "", "", Status.TEACHER),
            User(1, "amine.martin@uha.fr", "Amine Martin", "pass01", "TD2", "TP3", "1A", "IR", Status.STUDENT),
            User(2, "lina.nguyen@uha.fr", "Lina Nguyen", "pass02", "TD2", "TP3", "2A", "IR", Status.STUDENT),
            User(3, "youssef.diallo@uha.fr", "Youssef Diallo", "pass03", "TD2", "TP2", "1A", "IR", Status.STUDENT),
            User(4, "nina.morel@uha.fr", "Nina Morel", "pass04", "TD1", "TP3", "3A", "IR", Status.STUDENT)
        )
        users.forEach { userDao.insert(it) }

        val students = users.filter { it.status == Status.STUDENT }
        val presences = students.flatMap { student ->
            allCours.filter { it.annee == student.annee && it.spe == student.spe }.map { cours ->
                Presence(
                    id = 0,
                    userId = student.id,
                    coursId = cours.id,
                    estJustifie = false,
                    lien = null,
                    estPresent = (0..1).random() == 1
                )
            }
        }
        presences.forEach { presenceDao.insert(it) }

        Log.d("DB", "Ajout de ${presences.size} présences.")
    }

    private suspend fun loadAllAbsencesFromDatabase() = withContext(Dispatchers.IO) {
        if (currentUserId == -1) return@withContext

        val db = AppDatabase.getDatabase(this@StudentDashboardActivity)
        val presenceDao = db.presenceDao()
        val coursDao = db.coursDao()

        val presences = presenceDao.getByUser(currentUserId)
        Log.d("DB", "Présences trouvées pour user $currentUserId : ${presences.size}")

        val absences = presences.filter { !it.estPresent }.mapNotNull { presence ->
            coursDao.getById(presence.coursId)?.let { cours ->
                Absence(
                    id = presence.id.toString(),
                    courseName = "Cours de ${cours.prof}",
                    date = cours.jour,
                    professorName = cours.prof,
                    isJustified = presence.estJustifie
                )
            }
        }

        withContext(Dispatchers.Main) {
            allAbsences.clear()
            allAbsences.addAll(absences)
            visibleAbsences.clear()
            absenceAdapter.notifyDataSetChanged()
            loadNextAbsences()
        }
    }

    private fun loadNextAbsences() {
        val nextIndex = visibleAbsences.size
        val endIndex = (nextIndex + pageSize).coerceAtMost(allAbsences.size)
        if (nextIndex >= endIndex) return

        val nextItems = allAbsences.subList(nextIndex, endIndex)
        visibleAbsences.addAll(nextItems)
        absenceAdapter.notifyItemRangeInserted(nextIndex, nextItems.size)

        binding.btnVoirPlus.visibility =
            if (visibleAbsences.size >= allAbsences.size) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            loadAllAbsencesFromDatabase()
        }
    }
}
