package com.example.prezzapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prezzapp.adapters.AbsenceAdapterTeacher
import com.example.prezzapp.adapters.CourseAdapter
import com.example.prezzapp.data.Absence
import com.example.prezzapp.databinding.ActivityTeacherDashboardBinding
import com.example.prezzapp.model.AppDatabase
import com.example.prezzapp.model.Cours
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TeacherDashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityTeacherDashboardBinding
    private lateinit var absenceAdapter: AbsenceAdapterTeacher
    private lateinit var courseAdapter: CourseAdapter

    private val allAbsences = mutableListOf<Absence>()
    private val visibleAbsences = mutableListOf<Absence>()
    private val teacherCourses = mutableListOf<Cours>()

    private val pageSize = 7
    private var profLogin: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profLogin = intent.getStringExtra("prof_login") ?: ""

        binding.btnLogout.setOnClickListener { logout() }
        binding.btnToggleTheme.setOnClickListener { ThemeManager.toggleTheme(this) }
        binding.btnVoirPlusProf.setOnClickListener { loadNextAbsences() }

        setupCourseRecyclerView()
        setupAbsenceRecyclerView()

        loadCoursesFromDatabase()
        loadTodayCourses()
        loadAbsencesFromDatabase()
    }

    private fun setupCourseRecyclerView() {
        courseAdapter = CourseAdapter(teacherCourses) { cours ->
            startActivity(Intent(this, TeacherCoursesActivity::class.java).apply {
                putExtra("prof_login", profLogin)
                putExtra("course_id", cours.id)
            })
        }
        binding.rvTodayCourses.layoutManager = LinearLayoutManager(this)
        binding.rvTodayCourses.adapter = courseAdapter
    }

    private fun setupAbsenceRecyclerView() {
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

    private fun loadCoursesFromDatabase() {
        Thread {
            val db = AppDatabase.getDatabase(this)
            val coursDao = db.coursDao()
            val userDao = db.userDao()
            val profName = userDao.getAll().find { it.login.equals(profLogin, ignoreCase = true) }?.name ?: ""
            val courses = coursDao.findCourByProf(profName)

            runOnUiThread {
                teacherCourses.clear()
                teacherCourses.addAll(courses)
                courseAdapter.notifyDataSetChanged()
            }
        }.start()
    }

    private fun loadAbsencesFromDatabase() {
        Thread {
            val db = AppDatabase.getDatabase(this)
            val presenceDao = db.presenceDao()
            val coursDao = db.coursDao()
            val userDao = db.userDao()

            val users = userDao.getAll()
            val profName = users.find { it.login.equals(profLogin, ignoreCase = true) }?.name ?: ""
            val coursDuProf = coursDao.getAll().filter { it.prof.equals(profName, ignoreCase = true) }
            val presences = presenceDao.getAll()

            allAbsences.clear()

            allAbsences.addAll(
                presences
                    .filter { !it.estPresent }
                    .distinctBy { it.userId to it.coursId }
                    .mapNotNull { p ->
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

    fun filterFutureCourses(courses: List<Cours>): List<Cours> {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return courses.filter {
            try {
                val date = sdf.parse(it.jour)
                date != null && !date.before(today)
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun loadTodayCourses() {
        Thread {
            val db = AppDatabase.getDatabase(this)
            val coursDao = db.coursDao()
            val userDao = db.userDao()
            val profLoginLower = profLogin.lowercase()
            val profUser = userDao.getAll().find { it.login.lowercase() == profLoginLower }
            val profNameDb = profUser?.name ?: ""

            val allCourses = coursDao.getAll().filter {
                it.prof.equals(profNameDb, ignoreCase = true)
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val upcomingCourses = allCourses.filter {
                try {
                    val date = sdf.parse(it.jour)
                    date != null && !date.before(today)
                } catch (e: Exception) {
                    false
                }
            }.sortedBy {
                try {
                    sdf.parse(it.jour)
                } catch (e: Exception) {
                    null
                }
            }

            runOnUiThread {
                courseAdapter = CourseAdapter(upcomingCourses) { cours ->
                    startActivity(Intent(this, TeacherCoursesActivity::class.java).apply {
                        putExtra("prof_login", profLogin)
                        putExtra("course_id", cours.id)
                    })
                }

                binding.rvTodayCourses.layoutManager = LinearLayoutManager(this)
                binding.rvTodayCourses.adapter = courseAdapter
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
        teacherCourses.clear()
        absenceAdapter.notifyDataSetChanged()
        courseAdapter.notifyDataSetChanged()
        loadCoursesFromDatabase()
        loadAbsencesFromDatabase()
    }
}
