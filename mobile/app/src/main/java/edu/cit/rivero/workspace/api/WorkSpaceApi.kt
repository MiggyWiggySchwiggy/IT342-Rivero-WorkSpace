package edu.cit.rivero.workspace.api

import edu.cit.rivero.workspace.models.AuthResponseData
import edu.cit.rivero.workspace.models.LoginRequest
import edu.cit.rivero.workspace.models.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface WorkSpaceApi {
    @POST("api/v1/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<AuthResponseData>

    @POST("api/v1/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<AuthResponseData>
}