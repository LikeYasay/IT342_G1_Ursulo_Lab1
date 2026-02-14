package com.example.userregistrationandauthentication

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object ApiClient {
    // Emulator connects to your PC backend
    private const val BASE_URL = "http://10.0.2.2:8080"

    private fun readResponse(conn: HttpURLConnection): String {
        val stream = try { conn.inputStream } catch (e: Exception) { conn.errorStream }
        val reader = BufferedReader(InputStreamReader(stream))
        val sb = StringBuilder()
        var line: String? = reader.readLine()
        while (line != null) { sb.append(line); line = reader.readLine() }
        reader.close()
        return sb.toString()
    }

    fun register(fullName: String, email: String, password: String): Pair<Boolean, String> {
        val conn = (URL("$BASE_URL/api/auth/register").openConnection() as HttpURLConnection)
        return try {
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val body = JSONObject()
                .put("fullName", fullName)
                .put("email", email)
                .put("password", password)

            OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }

            val res = readResponse(conn)
            val ok = conn.responseCode in 200..299
            if (ok) Pair(true, "Registered successfully!")
            else Pair(false, extractMessage(res))
        } catch (e: Exception) {
            Pair(false, "Network error: ${e.message}")
        } finally { conn.disconnect() }
    }

    fun login(email: String, password: String): Pair<Boolean, String> {
        val conn = (URL("$BASE_URL/api/auth/login").openConnection() as HttpURLConnection)
        return try {
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val body = JSONObject().put("email", email).put("password", password)
            OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }

            val res = readResponse(conn)
            val ok = conn.responseCode in 200..299
            if (!ok) return Pair(false, extractMessage(res))

            val json = JSONObject(res)
            val token = when {
                json.has("token") -> json.getString("token")
                json.has("accessToken") -> json.getString("accessToken")
                json.has("jwt") -> json.getString("jwt")
                else -> ""
            }

            if (token.isBlank()) Pair(false, "Login ok but token not found.")
            else Pair(true, token)
        } catch (e: Exception) {
            Pair(false, "Network error: ${e.message}")
        } finally { conn.disconnect() }
    }

    fun me(token: String): Pair<Boolean, JSONObject?> {
        val conn = (URL("$BASE_URL/api/user/me").openConnection() as HttpURLConnection)
        return try {
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "Bearer $token")
            val res = readResponse(conn)
            val ok = conn.responseCode in 200..299
            if (!ok) Pair(false, null) else Pair(true, JSONObject(res))
        } catch (e: Exception) {
            Pair(false, null)
        } finally { conn.disconnect() }
    }

    private fun extractMessage(raw: String): String {
        return try {
            val json = JSONObject(raw)
            if (json.has("message")) json.getString("message") else raw
        } catch (_: Exception) {
            raw.ifBlank { "Request failed." }
        }
    }
}
