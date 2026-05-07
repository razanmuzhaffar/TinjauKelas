package com.example.tinjaukelas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.example.tinjaukelas.databinding.ItemStudentAbsensiBinding
import com.example.tinjaukelas.network.Student

class StudentAbsensiAdapter(
    private val students: List<Student>,
    private val onStatusChanged: (Int, String?, String?) -> Unit
) : RecyclerView.Adapter<StudentAbsensiAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemStudentAbsensiBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStudentAbsensiBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.binding.tvStudentName.text = student.name
        holder.binding.checkboxHadir.isChecked = true
        holder.binding.layoutAbsence.visibility = View.GONE

        holder.binding.checkboxHadir.setOnCheckedChangeListener { _, isChecked ->
            holder.binding.layoutAbsence.visibility = if (isChecked) View.GONE else View.VISIBLE
            if (isChecked) onStatusChanged(student.id, null, null)
        }

        holder.binding.spinnerStatus.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    val status = listOf("izin", "sakit", "alpha")[pos]
                    val note = holder.binding.etNote.text.toString().ifEmpty { null }
                    onStatusChanged(student.id, status, note)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    override fun getItemCount() = students.size
}