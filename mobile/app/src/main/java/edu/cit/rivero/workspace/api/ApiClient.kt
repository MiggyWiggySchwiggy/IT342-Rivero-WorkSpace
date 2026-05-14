package edu.cit.rivero.workspace.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 10.0.2.2 points to your computer's localhost from the Android Emulator
    const val BASE_URL = "http://10.0.2.2:8080/"

    private var token: String? = null

    // Backing field — rebuilt any time the token changes via init()
    private var _instance: WorkSpaceApi? = null

    val instance: WorkSpaceApi
        get() = _instance ?: buildClient().also { _instance = it }

    fun init(t: String?) {
        token = t
        _instance = buildClient() // Rebuild so new token takes effect immediately
    }

    private fun buildClient(): WorkSpaceApi {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                token?.let {
                    newRequest.addHeader("Authorization", "Bearer $it")
                }
                chain.proceed(newRequest.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WorkSpaceApi::class.java)
    }
}