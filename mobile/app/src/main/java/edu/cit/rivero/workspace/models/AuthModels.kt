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

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: ApiError?
)

data class ApiError(
    val code: String?,
    val message: String?,
    val details: Any?
)

data class AuthResponseData(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String?
)

data class UserDto(
    val id: Int,
    val email: String,
    val firstname: String,
    val lastname: String,
    val role: String
)