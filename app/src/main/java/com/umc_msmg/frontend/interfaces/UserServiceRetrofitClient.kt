package com.umc_msmg.frontend.interfaces

import com.umc_msmg.frontend.data.DailySteps
import com.umc_msmg.frontend.data.MyPointsResponse
import com.umc_msmg.frontend.data.UpdateMedicationsRequest
import com.umc_msmg.frontend.data.UpdateMedicationsResponse
import com.umc_msmg.frontend.data.WeeklyExerciseSummary
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Query

object UserServiceRetrofitClient {
    private const val BASE_URL = "http://43.202.104.127:3000/"

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    interface ApiService {
        @PATCH("api/settings/medications")
        fun updateMedications(
            @Header("Authorization") authorization: String,
            @Body medications: UpdateMedicationsRequest
        ): Call<UpdateMedicationsResponse>

        @GET("/api/steps")
        fun getSteps(
            @Header("authorization") authorization: String,
            @Query("date") date: String
        ): Call<DailySteps>

        @GET("/api/histories/summary")
        fun getWeeklyExerciseSummary(
            @Header("authorization") authorization: String
        ): Call<WeeklyExerciseSummary>

        @GET("/api/points/my-points")
        fun getMyPoints(
            @Header("authorization") authorization: String
        ): Call<MyPointsResponse>
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}