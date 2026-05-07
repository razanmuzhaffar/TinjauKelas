package com.example.tinjaukelas

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tinjaukelas.network.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RoomActivity : AppCompatActivity() {

    private lateinit var adapter: RoomAdapter
    private lateinit var btnRoomUsage: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton

    private var selectedRoom: com.example.tinjaukelas.network.Room? = null
    private var userId: Int = -1
    private var userRole: String = "siswa"
    private val roomList = mutableListOf<com.example.tinjaukelas.network.Room>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        userId = prefs.getInt("userId", -1)
        userRole = prefs.getString("userRole", "siswa") ?: "siswa"

        val userName = prefs.getString("userName", "User") ?: "User"
        findViewById<TextView>(R.id.tvUsername).text = userName

        btnRoomUsage = findViewById(R.id.btnRoomUsage)
        recyclerView = findViewById(R.id.recyclerView)
        fabAdd = findViewById(R.id.fabAdd)

        fabAdd.visibility = if (userRole == "admin") View.VISIBLE else View.GONE

        findViewById<Button>(R.id.btnLogout).setOnClickListener { logout() }

        setupRecyclerView()
        loadRooms()
        setupButtons()
    }

    private fun getToken() =
        "Bearer ${getSharedPreferences("auth", MODE_PRIVATE).getString("token", "")}"

    private fun setupRecyclerView() {
        adapter = RoomAdapter(roomList) { room ->
            selectedRoom = room
            updateButtonState(room)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadRooms() {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.getRooms(getToken())
                if (res.isSuccessful) {
                    roomList.clear()
                    roomList.addAll(res.body() ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else if (res.code() == 401) {
                    startActivity(Intent(this@RoomActivity, LoginActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RoomActivity, "Gagal memuat ruangan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateButtonState(room: com.example.tinjaukelas.network.Room) {
        btnRoomUsage.isEnabled = true
        if (room.is_occupied == 1) {
            btnRoomUsage.text = "Kosongkan Ruangan"
            btnRoomUsage.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#F44336")
                )
        } else {
            btnRoomUsage.text = "Gunakan Ruangan"
            btnRoomUsage.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#6200EE")
                )
        }
    }

    private fun setupButtons() {
        btnRoomUsage.setOnClickListener {
            val room = selectedRoom ?: return@setOnClickListener
            val newStatus = if (room.is_occupied == 1) 0 else 1

            lifecycleScope.launch {
                try {
                    val res = RetrofitClient.instance.updateRoomStatus(
                        getToken(), room.id, UpdateRoomRequest(newStatus == 1)
                    )
                    if (res.isSuccessful) {
                        selectedRoom = room.copy(is_occupied = newStatus)
                        Toast.makeText(
                            this@RoomActivity,
                            if (newStatus == 1) "${room.name} sedang digunakan"
                            else "${room.name} sudah kosong",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadRooms()
                        updateButtonState(selectedRoom!!)
                    } else if (res.code() == 422 || res.code() == 403) {
                        val error = res.errorBody()?.string()
                        val msg = try {
                            org.json.JSONObject(error ?: "{}").optString("message", "Gagal")
                        } catch (e: Exception) {
                            "Gagal"
                        }
                        Toast.makeText(this@RoomActivity, msg, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@RoomActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        fabAdd.setOnClickListener {
            showAddRoomDialog()
        }
    }

    private fun showAddRoomDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogform, null)
        val etNama = dialogView.findViewById<TextInputEditText>(R.id.etNama)
        val btnBatal = dialogView.findViewById<Button>(R.id.btnBatal)
        val btnSimpan = dialogView.findViewById<Button>(R.id.btnSimpan)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnBatal.setOnClickListener { dialog.dismiss() }

        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            if (nama.isEmpty()) {
                etNama.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }
            lifecycleScope.launch {
                try {
                    val res = RetrofitClient.instance.createRoom(
                        getToken(), CreateRoomRequest(nama)
                    )
                    if (res.isSuccessful) {
                        Toast.makeText(this@RoomActivity, "$nama berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        loadRooms()
                    } else {
                        Toast.makeText(this@RoomActivity, "Gagal: ${res.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@RoomActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    private fun logout() {
        getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}