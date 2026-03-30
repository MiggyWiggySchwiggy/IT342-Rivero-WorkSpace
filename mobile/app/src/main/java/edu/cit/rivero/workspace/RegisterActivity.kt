package edu.cit.rivero.workspace

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.rivero.workspace.api.ApiClient
import edu.cit.rivero.workspace.models.AuthResponseData
import edu.cit.rivero.workspace.models.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etFirst = findViewById<EditText>(R.id.etFirstName)
        val etLast = findViewById<EditText>(R.id.etLastName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPass = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvGoToLogin)

        // Navigate back to Login if the user already has an account
        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Handle the Registration API Call
        btnRegister.setOnClickListener {
            val request = RegisterRequest(
                firstname = etFirst.text.toString().trim(),
                lastname = etLast.text.toString().trim(),
                email = etEmail.text.toString().trim(),
                password = etPass.text.toString().trim()
            )

            // Basic Client-Side Validation
            if (request.firstname.isEmpty() || request.lastname.isEmpty() ||
                request.email.isEmpty() || request.password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Send data to Spring Boot Backend
            ApiClient.instance.registerUser(request).enqueue(object : Callback<AuthResponseData> {
                override fun onResponse(call: Call<AuthResponseData>, response: Response<AuthResponseData>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Registration Successful!", Toast.LENGTH_LONG).show()

                        // Redirect to Login Screen so they can sign in with their new account
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration Failed: Email might be taken", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponseData>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}