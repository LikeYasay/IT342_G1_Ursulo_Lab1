package com.example.userregistrationandauthentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ If already logged in, go to dashboard
        val session = SessionManager(this)
        if (!session.getToken().isNullOrBlank()) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val tvMsg = findViewById<TextView>(R.id.tvMsg)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoRegister = findViewById<TextView>(R.id.tvGoRegister)

        tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener {
            tvMsg.text = ""
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                tvMsg.text = "Enter email and password."
                return@setOnClickListener
            }

            thread {
                val (ok, result) = ApiClient.login(email, password)
                runOnUiThread {
                    if (!ok) {
                        tvMsg.text = result
                    } else {
                        session.saveToken(result)
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}
