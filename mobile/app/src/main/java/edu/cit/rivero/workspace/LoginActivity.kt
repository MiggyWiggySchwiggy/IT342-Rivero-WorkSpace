package edu.cit.rivero.workspace

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.rivero.workspace.api.ApiClient
import edu.cit.rivero.workspace.models.ApiResponse
import edu.cit.rivero.workspace.models.AuthResponseData
import edu.cit.rivero.workspace.models.LoginRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If a valid session already exists, skip directly to the correct screen
        if (SessionManager.isLoggedIn(this)) {
            navigateBasedOnRole(SessionManager.getRole(this))
            return
        }

        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etLoginEmail)
        etPass = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvGoToRegister)
        progressBar = findViewById(R.id.progressBarLogin)

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPass.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            setLoading(true)

            val request = LoginRequest(email = email, password = password)

            ApiClient.instance.loginUser(request).enqueue(object : Callback<ApiResponse<AuthResponseData>> {
                override fun onResponse(
                    call: Call<ApiResponse<AuthResponseData>>,
                    response: Response<ApiResponse<AuthResponseData>>
                ) {
                    setLoading(false)
                    if (response.isSuccessful && response.body()?.success == true) {
                        val authData = response.body()?.data ?: return
                        val token = authData.accessToken
                        val user = authData.user

                        // Persist full session info via SessionManager
                        SessionManager.saveSession(
                            context = this@LoginActivity,
                            token = token,
                            role = user.role,
                            userId = user.id,
                            firstName = user.firstname,
                            lastName = user.lastname,
                            email = user.email
                        )

                        // Re-initialize ApiClient with fresh token
                        ApiClient.init(token)

                        Toast.makeText(this@LoginActivity, "Welcome back, ${user.firstname}!", Toast.LENGTH_SHORT).show()
                        navigateBasedOnRole(user.role)
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            response.body()?.error?.message ?: "Invalid credentials",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<AuthResponseData>>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(this@LoginActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun navigateBasedOnRole(role: String?) {
        val destination = when (role?.uppercase()) {
            "ADMIN" -> AdminDashboardActivity::class.java
            else -> DashboardActivity::class.java
        }
        startActivity(Intent(this, destination))
        finish()
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !loading
    }
}