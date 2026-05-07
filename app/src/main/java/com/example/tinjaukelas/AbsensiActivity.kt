package com.example.tinjaukelas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tinjaukelas.databinding.ActivityAbsensiBinding
import kotlinx.coroutines.launch
import com.example.tinjaukelas.network.*
import java.util.*
import java.text.SimpleDateFormat


class AbsensiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAbsensiBinding
    private lateinit var adapter: StudentAbsensiAdapter
    private var classId: Int = -1
    private val studentList = mutableListOf<Student>()
    private val absenceMap = mutableMapOf<Int, Pair<String, String?>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsensiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classId = intent.getIntExtra("class_id", -1)
        binding.tvClassName.text = intent.getStringExtra("class_name") ?: ""
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.etDate.setText(sdf.format(Date()))

        adapter = StudentAbsensiAdapter(studentList) { studentId, status, note ->
            if (status == null) absenceMap.remove(studentId)
            else absenceMap[studentId] = Pair(status, note)
        }
        binding.rvStudents.layoutManager = LinearLayoutManager(this)
        binding.rvStudents.adapter = adapter

        fetchStudents()
        binding.btnSimpan.setOnClickListener { submitAttendance() }
    }

    private fun getToken() =
        "Bearer ${getSharedPreferences("auth", MODE_PRIVATE).getString("token", "")}"

    private fun fetchStudents() {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.getStudents(getToken(), classId)
                if (res.isSuccessful) {
                    studentList.clear()
                    studentList.addAll(res.body() ?: emptyList())
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AbsensiActivity, "Gagal memuat siswa", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun submitAttendance() {
        val date = binding.etDate.text.toString()
        if (date.isBlank()) {
            Toast.makeText(this, "Tanggal wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }
        val absences = absenceMap.map { (id, pair) ->
            AbsenceEntry(id, pair.first, pair.second)
        }
        val request = AttendanceRequest(classId, date, absences)

        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.submitAttendance(getToken(), request)
                if (res.isSuccessful) {
                    Toast.makeText(this@AbsensiActivity, "Absensi tersimpan!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AbsensiActivity, "Gagal: ${res.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AbsensiActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}