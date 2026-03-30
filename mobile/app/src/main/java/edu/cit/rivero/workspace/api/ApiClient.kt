package edu.cit.rivero.workspace.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 10.0.2.2 points to your computer's localhost from the Android Emulator
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val instance: WorkSpaceApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(WorkSpaceApi::class.java)
    }
}