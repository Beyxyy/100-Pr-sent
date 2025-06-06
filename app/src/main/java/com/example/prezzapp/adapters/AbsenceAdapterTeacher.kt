package com.example.prezzapp.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.prezzapp.R
import com.example.prezzapp.data.Absence
import com.example.prezzapp.databinding.ItemAbsenceTeacherBinding
import com.example.prezzapp.ViewJustificatifTeacherActivity  // ✅ Ajouté

class AbsenceAdapterTeacher(
    private val context: Context,
    private val absences: List<Absence>,
    private val onAbsenceClick: (Absence) -> Unit
) : RecyclerView.Adapter<AbsenceAdapterTeacher.AbsenceViewHolder>() {

    inner class AbsenceViewHolder(private val binding: ItemAbsenceTeacherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(absence: Absence) {
            binding.tvCourseDate.text = "${absence.courseName} - ${absence.date}"
            binding.tvStudentName.text = "Élève : ${absence.professorName}"  // Nom élève
            binding.tvJustifiedStatus.text = if (absence.isJustified) "Justifié" else "Non justifié"
            binding.tvJustifiedStatus.setTextColor(Color.WHITE)
            binding.tvJustifiedStatus.background = ContextCompat.getDrawable(
                context,
                if (absence.isJustified) R.drawable.rounded_green_background else R.drawable.rounded_orange_background
            )

            binding.cardAbsence.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    if (absence.isJustified) R.color.green_justified else R.color.orange_not_justified
                )
            )

            if (absence.isJustified) {
                binding.btnVoirJustificatif.visibility = View.VISIBLE
                binding.btnVoirJustificatif.setOnClickListener {
                    val intent = Intent(context, ViewJustificatifTeacherActivity::class.java)
                    intent.putExtra("selected_absence", absence)
                    context.startActivity(intent)
                }
            } else {
                binding.btnVoirJustificatif.visibility = View.GONE
                binding.root.setOnClickListener {
                    onAbsenceClick(absence)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsenceViewHolder {
        val binding = ItemAbsenceTeacherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AbsenceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AbsenceViewHolder, position: Int) {
        holder.bind(absences[position])
    }

    override fun getItemCount(): Int = absences.size
}
