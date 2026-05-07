package com.example.tinjaukelas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tinjaukelas.network.CreateClassRequest
import com.example.tinjaukelas.network.RetrofitClient
import com.example.tinjaukelas.network.SchoolClass
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userRole: String
    private lateinit var adapter: ClassAdapter
    private val classList = mutableListOf<SchoolClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val userName = prefs.getString("userName", "User") ?: "User"
        userRole = prefs.getString("userRole", "") ?: ""

        findViewById<TextView>(R.id.tvUsername).text = userName

        recyclerView = findViewById(R.id.recyclerView)
        adapter = ClassAdapter(classList) { schoolClass ->
            if (userRole == "guru_piket") {
                startActivity(Intent(this, RekapAbsensiActivity::class.java).apply {
                    putExtra("class_id", schoolClass.id)
                    putExtra("class_name", schoolClass.name)
                })
            } else {
                startActivity(Intent(this, AbsensiActivity::class.java).apply {
                    putExtra("class_id", schoolClass.id)
                    putExtra("class_name", schoolClass.name)
                })
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // FAB beda fungsi per role
        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)
        when (userRole) {
            "guru" -> {
                fab.show()
                fab.setOnClickListener {
                    if (classList.isEmpty()) {
                        Toast.makeText(this, "Belum ada kelas", Toast.LENGTH_SHORT).show()
                    } else {
                        val namaKelas = classList.map { it.name }.toTypedArray()
                        android.app.AlertDialog.Builder(this)
                            .setTitle("Pilih Kelas untuk Absen")
                            .setItems(namaKelas) { _, index ->
                                startActivity(Intent(this, AbsensiActivity::class.java).apply {
                                    putExtra("class_id", classList[index].id)
                                    putExtra("class_name", classList[index].name)
                                })
                            }
                            .show()
                    }
                }
            }
            "admin" -> {
                fab.show()
                fab.setOnClickListener {
                    val dialogView = layoutInflater.inflate(R.layout.dialogform, null)
                    val etNama = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNama)
                    val etDeskripsi = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etDeskripsi)
                    val btnBatal = dialogView.findViewById<Button>(R.id.btnBatal)
                    val btnSimpan = dialogView.findViewById<Button>(R.id.btnSimpan)

                    val dialog = android.app.AlertDialog.Builder(this)
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
                                val res = RetrofitClient.instance.createClass(
                                    getToken(),
                                    CreateClassRequest(nama)
                                )
                                if (res.isSuccessful) {
                                    Toast.makeText(this@MainActivity, "Kelas '$nama' berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                    fetchClasses() // refresh list
                                } else {
                                    Toast.makeText(this@MainActivity, "Gagal: ${res.code()}", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    dialog.show()
                }
            }
            else -> fab.hide()
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            prefs.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        fetchClasses()
    }

    private fun getToken() =
        "Bearer ${getSharedPreferences("auth", MODE_PRIVATE).getString("token", "")}"

    private fun fetchClasses() {
        lifecycleScope.launch {
            try {
                val response = if (userRole == "guru_piket") {
                    RetrofitClient.instance.getAllClasses(getToken())
                } else {
                    RetrofitClient.instance.getClasses(getToken())
                }
                if (response.isSuccessful) {
                    classList.clear()
                    classList.addAll(response.body() ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else if (response.code() == 401) {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Gagal memuat kelas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}