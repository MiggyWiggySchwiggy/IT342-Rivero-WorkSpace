package edu.cit.rivero.workspace.models

data class RegisterRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponseData(
    val token: String?,
    val error: String?
)