package com.umc_msmg.frontend.interfaces

import com.umc_msmg.frontend.data.AddPointRequest
import com.umc_msmg.frontend.data.AddPointResponse
import com.umc_msmg.frontend.data.BuyProductRequest
import com.umc_msmg.frontend.data.BuyProductResponse
import com.umc_msmg.frontend.data.GetGiftIconsResponse
import com.umc_msmg.frontend.data.GetProductsResponse
import com.umc_msmg.frontend.data.MainPageResponse
import com.umc_msmg.frontend.data.MyPointsResponse
import com.umc_msmg.frontend.data.UpdateMedicationsRequest
import com.umc_msmg.frontend.data.UpdateMedicationsResponse
import com.umc_msmg.frontend.data.UpdateProfileResponse
import com.umc_msmg.frontend.data.UserProfileUpdateRequest
import com.umc_msmg.frontend.data.WeeklyExerciseSummary
import com.umc_msmg.frontend.data.WorkoutLevelRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.PUT

object UserServiceRetrofitClient {
    private const val BASE_URL = "http://43.202.104.127:3000/"

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    interface ApiService {
        @GET("api/settings/profile/{id}")

        @PATCH("api/settings/medications")
        fun updateMedications(
            @Header("authorization") authorization: String,
            @Body medications: UpdateMedicationsRequest
        ): Call<UpdateMedicationsResponse>

        @PUT("/api/settings/profile")
        fun updateProfile(
            @Header("authorization") authorization: String,
            @Body userProfile: UserProfileUpdateRequest
        ): Call<UpdateProfileResponse>

        @PATCH("/api/settings/workout-level")
        fun updateWorkoutLevel(
            @Header("authorization") authorization: String,
            @Body workoutLevelRequest: WorkoutLevelRequest
        ): Call<UpdateProfileResponse>

        @GET("/api/histories/main-page")
        fun getMainPage(
            @Header("authorization") authorization: String
        ): Call<MainPageResponse>

        @GET("/api/histories/summary")
        fun getWeeklyExerciseSummary(
            @Header("authorization") authorization: String
        ): Call<WeeklyExerciseSummary>

        @GET("/api/points/my-points")
        fun getMyPoints(
            @Header("authorization") authorization: String
        ): Call<MyPointsResponse>

        @GET("/api/shops/products")
        fun getProducts(): Call<GetProductsResponse>

        @GET("/api/shops/my-gifticons")
        fun getMyGiftIcons(
            @Header("authorization") authorization: String
        ): Call<GetGiftIconsResponse>

        @PATCH("/api/points/buy-product")
        fun buyProduct(
            @Header("authorization") authorization: String,
            @Body buyProductRequest: BuyProductRequest
        ): Call<BuyProductResponse>

        @PATCH("/api/points/add")
        fun addPoints(
            @Header("authorization") authorization: String,
            @Body addPointRequest: AddPointRequest
        ): Call<AddPointResponse>
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}