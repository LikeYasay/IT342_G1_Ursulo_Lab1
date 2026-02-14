package com.example.userregistrationandauthentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val session = SessionManager(this)
        val token = session.getToken()

        // ✅ Protected: if no token, go login
        if (token.isNullOrBlank()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_dashboard)

        val tvId = findViewById<TextView>(R.id.tvId)
        val tvFullName = findViewById<TextView>(R.id.tvFullName)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvMsg = findViewById<TextView>(R.id.tvMsg)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnLogout.setOnClickListener {
            session.clear()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Load profile
        thread {
            val (ok, json) = ApiClient.me(token)
            runOnUiThread {
                if (!ok || json == null) {
                    tvMsg.text = "Failed to load profile."
                } else {
                    tvId.text = json.optString("id", "-")
                    tvFullName.text = json.optString("fullName", "-")
                    tvEmail.text = json.optString("email", "-")
                }
            }
        }
    }
}
