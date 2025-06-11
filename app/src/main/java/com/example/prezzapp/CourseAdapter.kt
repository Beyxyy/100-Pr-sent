package com.example.prezzapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prezzapp.R
import com.example.prezzapp.model.Cours

class CourseAdapter(
    private val courses: List<Cours>,
    private val onItemClick: (Cours) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val tvCoursTitre: TextView = itemView.findViewById(R.id.tv_cours_titre)
        private val tvClasse: TextView = itemView.findViewById(R.id.tv_classe)

        fun bind(cours: Cours) {
            tvDate.text = cours.jour
            tvCoursTitre.text = "${cours.nomcours} - Salle 3.53"
            tvClasse.text = "Classe de ${cours.annee} ${cours.spe}"

            itemView.setOnClickListener {
                onItemClick(cours)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(courses[position])
    }

    override fun getItemCount(): Int = courses.size
}
