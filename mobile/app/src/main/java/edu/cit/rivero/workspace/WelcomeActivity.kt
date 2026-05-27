package edu.cit.rivero.workspace

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import edu.cit.rivero.workspace.api.ApiClient
import edu.cit.rivero.workspace.models.ApiResponse
import edu.cit.rivero.workspace.models.AuthResponseData
import edu.cit.rivero.workspace.models.GoogleLoginRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WelcomeActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    loginWithGoogle(idToken)
                } else {
                    Toast.makeText(this, "Google login failed: ID Token was null", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Session check: skip directly if logged in
        if (SessionManager.isLoggedIn(this)) {
            navigateBasedOnRole(SessionManager.getRole(this))
            return
        }

        setContentView(R.layout.activity_welcome)

        // Initialize Google options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Button clicks
        findViewById<View>(R.id.btnWelcomeGoogle).setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        findViewById<View>(R.id.btnWelcomeLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<TextView>(R.id.tvWelcomeRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginWithGoogle(idToken: String) {
        val request = GoogleLoginRequest(idToken = idToken)
        ApiClient.instance.googleLoginUser(request).enqueue(object : Callback<ApiResponse<AuthResponseData>> {
            override fun onResponse(
                call: Call<ApiResponse<AuthResponseData>>,
                response: Response<ApiResponse<AuthResponseData>>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()?.data ?: return
                    val token = authData.accessToken
                    val user = authData.user

                    // Save session data
                    SessionManager.saveSession(
                        context = this@WelcomeActivity,
                        token = token,
                        role = user.role,
                        userId = user.id,
                        firstName = user.firstname,
                        lastName = user.lastname,
                        email = user.email
                    )

                    // Re-initialize ApiClient with token
                    ApiClient.init(token)

                    Toast.makeText(this@WelcomeActivity, "Welcome, ${user.firstname}!", Toast.LENGTH_SHORT).show()
                    navigateBasedOnRole(user.role)
                } else {
                    Toast.makeText(
                        this@WelcomeActivity,
                        response.body()?.error?.message ?: "Google login failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<AuthResponseData>>, t: Throwable) {
                Toast.makeText(this@WelcomeActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateBasedOnRole(role: String?) {
        val destination = when (role?.uppercase()) {
            "ADMIN" -> AdminDashboardActivity::class.java
            else -> DashboardActivity::class.java
        }
        startActivity(Intent(this, destination))
        finish()
    }
}
