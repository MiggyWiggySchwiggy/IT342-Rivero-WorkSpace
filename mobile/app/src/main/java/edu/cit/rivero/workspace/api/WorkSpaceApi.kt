package edu.cit.rivero.workspace.api

import edu.cit.rivero.workspace.models.AdminReservationItem
import edu.cit.rivero.workspace.models.ApiResponse
import edu.cit.rivero.workspace.models.AuthResponseData
import edu.cit.rivero.workspace.models.AvailabilitySlot
import edu.cit.rivero.workspace.models.BookedSlot
import edu.cit.rivero.workspace.models.LoginRequest
import edu.cit.rivero.workspace.models.RegisterRequest
import edu.cit.rivero.workspace.models.ReservationCheckoutRequest
import edu.cit.rivero.workspace.models.ReservationResponseData
import edu.cit.rivero.workspace.models.Space
import edu.cit.rivero.workspace.models.SpaceRequest
import edu.cit.rivero.workspace.models.GoogleLoginRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface WorkSpaceApi {

    // ── Auth ──
    @POST("api/v1/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<ApiResponse<AuthResponseData>>

    @POST("api/v1/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<ApiResponse<AuthResponseData>>

    @POST("api/v1/auth/google")
    fun googleLoginUser(@Body request: GoogleLoginRequest): Call<ApiResponse<AuthResponseData>>

    // ── Spaces (Public) ──
    @GET("api/v1/spaces")
    fun getSpaces(): Call<ApiResponse<List<Space>>>

    @GET("api/v1/spaces/{spaceId}")
    fun getSpaceById(@Path("spaceId") spaceId: String): Call<ApiResponse<Space>>

    @GET("api/v1/spaces/{spaceId}/availability")
    fun getSpaceAvailability(@Path("spaceId") spaceId: String): Call<ApiResponse<List<AvailabilitySlot>>>

    @PUT("api/v1/spaces/{spaceId}/availability/replace")
    fun replaceSpaceAvailability(
        @Path("spaceId") spaceId: String,
        @Body slots: List<AvailabilitySlot>
    ): Call<ApiResponse<List<AvailabilitySlot>>>

    @GET("api/v1/spaces/{spaceId}/bookings")
    fun getSpaceBookings(@Path("spaceId") spaceId: String): Call<ApiResponse<List<BookedSlot>>>

    // ── Spaces (Admin CRUD) ──
    @POST("api/v1/spaces")
    fun createSpace(@Body request: SpaceRequest): Call<ApiResponse<Space>>

    @PUT("api/v1/spaces/{id}")
    fun updateSpace(@Path("id") id: String, @Body request: SpaceRequest): Call<ApiResponse<Space>>

    @DELETE("api/v1/spaces/{id}")
    fun deleteSpace(@Path("id") id: String): Call<ApiResponse<Void>>

    @Multipart
    @POST("api/v1/spaces/{id}/image")
    fun uploadSpaceImages(
        @Path("id") id: String,
        @Part files: List<MultipartBody.Part>
    ): Call<ApiResponse<Space>>

    // ── Reservations (User) ──
    @POST("api/v1/reservations/checkout")
    fun createReservation(@Body request: ReservationCheckoutRequest): Call<ApiResponse<ReservationResponseData>>

    @GET("api/v1/reservations/my")
    fun getMyReservations(): Call<ApiResponse<List<ReservationResponseData>>>

    // ── Reservations (Admin) ──
    @GET("api/v1/reservations/all")
    fun getAllReservations(): Call<ApiResponse<List<AdminReservationItem>>>

    @PATCH("api/v1/reservations/{id}/cancel")
    fun cancelReservation(@Path("id") id: Long): Call<ApiResponse<Void>>
}