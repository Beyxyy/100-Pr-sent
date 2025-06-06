package com.example.prezzapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prezzapp.data.StudentItem
import com.example.prezzapp.databinding.ItemStudentBinding

class StudentAdapter(
    private val items: List<StudentItem>,
    private val onChecked: (StudentItem, Boolean) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(val bind: ItemStudentBinding) :
        RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StudentViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val item = items[position]
        holder.bind.tvStudentName.text = item.fullName
        holder.bind.cbAbsent.isChecked = item.isAbsent
        holder.bind.cbAbsent.setOnCheckedChangeListener { _, isChecked ->
            item.isAbsent = isChecked
            onChecked(item, isChecked)
        }
    }
}
