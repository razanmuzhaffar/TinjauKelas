package com.example.tinjaukelas

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tinjaukelas.network.AttendanceDetail
import com.example.tinjaukelas.network.RetrofitClient
import kotlinx.coroutines.launch

class RekapAbsensiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rekap_absensi)

        val classId = intent.getIntExtra("class_id", -1)
        val className = intent.getStringExtra("class_name") ?: ""

        findViewById<TextView>(R.id.tvClassName).text = className

        val rv = findViewById<RecyclerView>(R.id.rvRekap)
        rv.layoutManager = LinearLayoutManager(this)

        val token = "Bearer ${getSharedPreferences("auth", MODE_PRIVATE).getString("token", "")}"

        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.getAttendanceDetail(token, classId)
                if (res.isSuccessful) {
                    val data = res.body() ?: emptyList()
                    rv.adapter = RekapAdapter(data)
                } else {
                    Toast.makeText(this@RekapAbsensiActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RekapAbsensiActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}