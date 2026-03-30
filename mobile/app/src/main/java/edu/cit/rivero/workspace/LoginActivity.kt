package edu.cit.rivero.workspace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.rivero.workspace.api.ApiClient
import edu.cit.rivero.workspace.models.AuthResponseData
import edu.cit.rivero.workspace.models.LoginRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etPass = findViewById<EditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvGoToRegister)

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener {
            val request = LoginRequest(
                email = etEmail.text.toString().trim(),
                password = etPass.text.toString().trim()
            )

            ApiClient.instance.loginUser(request).enqueue(object : Callback<AuthResponseData> {
                override fun onResponse(call: Call<AuthResponseData>, response: Response<AuthResponseData>) {
                    if (response.isSuccessful && response.body() != null) {
                        val token = response.body()?.token

                        // Securely store the JWT token
                        val sharedPreferences = getSharedPreferences("WorkSpacePrefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().putString("JWT_TOKEN", token).apply()

                        Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

                        // Redirect to Dashboard
                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponseData>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}