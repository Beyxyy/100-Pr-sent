package com.example.prezzapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prezzapp.adapters.AbsenceAdapterTeacher
import com.example.prezzapp.data.Absence
import com.example.prezzapp.databinding.ActivityTeacherDashboardBinding
import com.example.prezzapp.model.AppDatabase
import com.example.prezzapp.ThemeManager

class TeacherDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherDashboardBinding
    private lateinit var absenceAdapter: AbsenceAdapterTeacher
    private val allAbsences = mutableListOf<Absence>()
    private val visibleAbsences = mutableListOf<Absence>()
    private val pageSize = 7
    private var profLogin: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profLogin = intent.getStringExtra("prof_login") ?: ""

        setupRecyclerView()
        loadAbsencesFromDatabase()

        binding.btnVoirPlusProf.setOnClickListener { loadNextAbsences() }

        binding.btnMesCours.setOnClickListener {
            Thread {
                val db = AppDatabase.getDatabase(this)
                val userDao = db.userDao()
                val coursDao = db.coursDao()
                val profName = userDao.getAll().find { it.login == profLogin }?.name ?: ""
                val courseId = coursDao.getAll().firstOrNull { it.prof == profName }?.id ?: -1
                runOnUiThread {
                    startActivity(Intent(this, TeacherCoursesActivity::class.java).apply {
                        putExtra("prof_login", profLogin)
                        putExtra("course_id", courseId)
                    })
                }
            }.start()
        }

        binding.btnToggleTheme.setOnClickListener {
            ThemeManager.toggleTheme(this)
        }
    }

    private fun setupRecyclerView() {
        absenceAdapter = AbsenceAdapterTeacher(this, visibleAbsences) { absence ->
            if (!absence.isJustified) {
                startActivity(Intent(this, JustifyAbsenceActivity::class.java).apply {
                    putExtra("selected_absence", absence)
                    putExtra("user_role", "teacher")
                })
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

            val users = userDao.getAll()
            val profName = users.find { it.login == profLogin }?.name ?: ""
            val coursDuProf = coursDao.getAll().filter { it.prof == profName }
            val presences = presenceDao.getAll()

            allAbsences.clear()
            allAbsences.addAll(
                presences.mapNotNull { p ->
                    val cours = coursDuProf.find { it.id == p.coursId }
                    val student = users.find { it.id == p.userId }
                    if (cours != null && student != null) {
                        Absence(
                            id = p.id.toString(),
                            courseName = cours.nomcours,
                            date = cours.jour,
                            professorName = student.name,
                            isJustified = p.estJustifie
                        )
                    } else null
                }
            )

            runOnUiThread {
                visibleAbsences.clear()
                absenceAdapter.notifyDataSetChanged()
                loadNextAbsences()
                binding.tvAbsenceCounter.text = "Voir les absences : ${allAbsences.size}"
            }
        }.start()
    }

    private fun loadNextAbsences() {
        val start = visibleAbsences.size
        val end = (start + pageSize).coerceAtMost(allAbsences.size)
        if (start < end) {
            visibleAbsences.addAll(allAbsences.subList(start, end))
            absenceAdapter.notifyItemRangeInserted(start, end - start)
        }
        binding.btnVoirPlusProf.visibility =
            if (visibleAbsences.size >= allAbsences.size) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        visibleAbsences.clear()
        allAbsences.clear()
        absenceAdapter.notifyDataSetChanged()
        loadAbsencesFromDatabase()
    }
}
