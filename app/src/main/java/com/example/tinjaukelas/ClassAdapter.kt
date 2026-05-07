package com.example.tinjaukelas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tinjaukelas.network.SchoolClass

class ClassAdapter(
    private val classes: List<SchoolClass>,
    private val onClick: (SchoolClass) -> Unit
) : RecyclerView.Adapter<ClassAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaKelas: TextView = view.findViewById(R.id.tvNamaKelas)
        val tvKapasitas: TextView = view.findViewById(R.id.tvKapasitas)
        val tvStatusBadge: TextView = view.findViewById(R.id.tvStatusBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sc = classes[position]
        holder.tvNamaKelas.text = sc.name
        holder.tvKapasitas.text = sc.room?.name ?: "Belum ada ruangan"
        holder.tvStatusBadge.text = if (sc.attendance_percentage != null)
            "${sc.attendance_percentage}% hadir"
        else
            "${sc.members_count} siswa"
        holder.itemView.setOnClickListener { onClick(sc) }
    }

    override fun getItemCount() = classes.size
}