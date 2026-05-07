package com.example.tinjaukelas

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.tinjaukelas.network.Room

class RoomAdapter(
    private var rooms: List<Room>,
    private val onRoomClick: (Room) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    private var selectedRoomId: Int = -1

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView      = itemView.findViewById(R.id.cardView)
        val tvNamaKelas: TextView   = itemView.findViewById(R.id.tvNamaKelas)
        val tvKapasitas: TextView   = itemView.findViewById(R.id.tvKapasitas)
        val tvStatusBadge: TextView = itemView.findViewById(R.id.tvStatusBadge)
        val imgStatus: ImageView    = itemView.findViewById(R.id.imgStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]

        holder.tvNamaKelas.text = room.name
        holder.tvKapasitas.text = "Kapasitas: ${room.capacity}"

        if (room.is_occupied == 1) {
            holder.tvStatusBadge.text = "Digunakan"
            holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#F44336"))
        } else {
            holder.tvStatusBadge.text = "Tersedia"
            holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#4CAF50"))
        }

        if (room.id == selectedRoomId) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#EDE7F6"))
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE)
        }

        holder.cardView.setOnClickListener {
            selectedRoomId = room.id
            notifyDataSetChanged()
            onRoomClick(room)
        }
    }

    override fun getItemCount() = rooms.size

    fun updateData(newRooms: List<Room>) {
        rooms = newRooms
        notifyDataSetChanged()
    }
}