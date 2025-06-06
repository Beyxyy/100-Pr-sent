package com.example.prezzapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prezzapp.databinding.ItemStudentAttendanceBinding
import com.example.prezzapp.model.User

class StudentAttendanceAdapter :
    RecyclerView.Adapter<StudentAttendanceAdapter.VH>() {

    private val items = mutableListOf<User>()
    private val absentIds = mutableSetOf<Int>()

    fun submitList(list: List<User>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun absentStudentIds(): List<Int> = absentIds.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(
            ItemStudentAttendanceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    inner class VH(private val b: ItemStudentAttendanceBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(u: User) {
            b.checkBox.text = u.name
            b.checkBox.setOnCheckedChangeListener(null)
            b.checkBox.isChecked = absentIds.contains(u.id)
            b.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) absentIds.add(u.id) else absentIds.remove(u.id)
            }
        }
    }
}