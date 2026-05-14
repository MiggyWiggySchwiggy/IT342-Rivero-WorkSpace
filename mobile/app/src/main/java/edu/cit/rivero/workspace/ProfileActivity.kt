package edu.cit.rivero.workspace

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import edu.cit.rivero.workspace.api.ApiClient

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Back button
        findViewById<ImageButton>(R.id.btnBackProfile)?.setOnClickListener { finish() }

        val tvAvatarInitials = findViewById<TextView>(R.id.tvAvatarInitials)
        val tvProfileName = findViewById<TextView>(R.id.tvProfileName)
        val tvProfileRole = findViewById<TextView>(R.id.tvProfileRole)
        val tvProfileEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val tvProfileRoleDetail = findViewById<TextView>(R.id.tvProfileRoleDetail)
        val btnMyReservations = findViewById<TextView>(R.id.btnMyReservations)
        val btnLogout = findViewById<TextView>(R.id.btnLogout)


        // Load session data from SessionManager
        val firstName = SessionManager.getFirstName(this)
        val lastName = SessionManager.getLastName(this)
        val email = SessionManager.getEmail(this)
        val role = SessionManager.getRole(this) ?: "USER"

        val fullName = "$firstName $lastName".trim().ifEmpty { "Unknown User" }
        val initials = buildInitials(firstName, lastName)

        tvAvatarInitials.text = initials
        tvProfileName.text = fullName
        tvProfileEmail.text = email
        tvProfileRole.text = role
        tvProfileRoleDetail.text = if (role == "ADMIN") "Administrator" else "Standard User"

        btnMyReservations.setOnClickListener {
            startActivity(Intent(this, ReservationListActivity::class.java))
        }

        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Logout") { _, _ ->
                    SessionManager.clearSession(this)
                    ApiClient.init(null) // Clear token from client
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun buildInitials(firstName: String, lastName: String): String {
        val first = firstName.firstOrNull()?.uppercaseChar()?.toString() ?: ""
        val last = lastName.firstOrNull()?.uppercaseChar()?.toString() ?: ""
        return "$first$last".ifEmpty { "?" }
    }
}
