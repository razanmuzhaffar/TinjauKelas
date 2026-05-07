package com.example.tinjaukelas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tinjaukelas.network.LoginRequest
import com.example.tinjaukelas.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val role = prefs.getString("userRole", null)
        if (token != null && role != null) {
            val intent = when (role) {
                "guru", "admin" -> Intent(this, MainActivity::class.java)
                else -> Intent(this, RoomActivity::class.java)
            }
            startActivity(intent)
            finish()
            return
        }
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.login(LoginRequest(email, password))
                    if (response.isSuccessful) {
                        val body = response.body()!!

                        // Simpan token dan info user
                        getSharedPreferences("auth", MODE_PRIVATE).edit()
                            .putString("token", body.token)
                            .putInt("userId", body.user.id)
                            .putString("userRole", body.user.role)
                            .putString("userName", body.user.name)
                            .apply()

                        // Arahkan berdasarkan role
                        val intent = when (body.user.role) {
                            "guru"       -> Intent(this@LoginActivity, MainActivity::class.java)
                            "siswa"      -> Intent(this@LoginActivity, RoomActivity::class.java)
                            "admin"      -> Intent(this@LoginActivity, MainActivity::class.java)
                            "guru_piket" -> Intent(this@LoginActivity, MainActivity::class.java)
                            else         -> Intent(this@LoginActivity, RoomActivity::class.java)
                        }
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(this@LoginActivity, "Email atau password salah", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Gagal koneksi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}