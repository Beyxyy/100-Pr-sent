package com.example.prezzapp.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.prezzapp.R
import com.example.prezzapp.data.Absence
import com.example.prezzapp.databinding.ItemAbsenceBinding

class AbsenceAdapter(
    private val context: Context,
    private val absences: List<Absence>,
    private val onAbsenceClick: (Absence) -> Unit
) : RecyclerView.Adapter<AbsenceAdapter.AbsenceViewHolder>() {

    inner class AbsenceViewHolder(private val binding: ItemAbsenceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(absence: Absence) {
            Log.d("AbsenceAdapter", "Bind absence id=${absence.id}, justified=${absence.isJustified}")

            binding.tvCourseDate.text = "${absence.courseName} - ${absence.date}"
            binding.tvProfessor.text = "${absence.professorName}"

            if (absence.isJustified) {
                binding.cardAbsence.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green_justified))
                binding.tvJustifiedStatus.text = "Justifié"
                binding.tvJustifiedStatus.background = ContextCompat.getDrawable(context, R.drawable.rounded_green_background)
                binding.tvJustifiedStatus.setTextColor(Color.WHITE)
            } else {
                binding.cardAbsence.setCardBackgroundColor(ContextCompat.getColor(context, R.color.orange_not_justified))
                binding.tvJustifiedStatus.text = "Non justifié"
                binding.tvJustifiedStatus.background = ContextCompat.getDrawable(context, R.drawable.rounded_orange_background)
                binding.tvJustifiedStatus.setTextColor(Color.WHITE)
            }

            // Clic actif pour tous
            binding.root.setOnClickListener {
                onAbsenceClick(absence)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsenceViewHolder {
        val binding = ItemAbsenceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AbsenceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AbsenceViewHolder, position: Int) {
        holder.bind(absences[position])
    }

    override fun getItemCount(): Int = absences.size
}
