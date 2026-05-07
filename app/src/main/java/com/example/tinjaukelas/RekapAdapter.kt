package com.example.tinjaukelas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tinjaukelas.network.AttendanceDetail

class RekapAdapter(
    private val data: List<AttendanceDetail>
) : RecyclerView.Adapter<RekapAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAbsences: TextView = view.findViewById(R.id.tvAbsences)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rekap, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.tvDate.text = item.date

        if (item.absences.isEmpty()) {
            holder.tvAbsences.text = "Semua hadir"
        } else {
            holder.tvAbsences.text = item.absences.joinToString("\n") { absence ->
                val ket = if (!absence.note.isNullOrBlank()) " — ${absence.note}" else ""
                "• ${absence.student_name} (${absence.status})$ket"
            }
        }
    }

    override fun getItemCount() = data.size
}