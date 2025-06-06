package com.example.prezzapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prezzapp.databinding.ItemCourseBinding
import com.example.prezzapp.model.Cours

class CourseAdapter(
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<CourseAdapter.VH>() {

    private val items = mutableListOf<Cours>()

    fun submitList(list: List<Cours>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(
            ItemCourseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class VH(private val b: ItemCourseBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(c: Cours) {
            b.tvCourseName.text = c.nomcours
            b.tvCourseDate.text = c.jour
            b.root.setOnClickListener { onClick(c.id) }
        }
    }
}
