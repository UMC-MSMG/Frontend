package com.umc_msmg.frontend.interfaces

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName

interface OpenAIApi {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    fun getChatResponse(
        @Body request: ChatRequest
    ): Call<ChatResponse>
}



interface OpenAITTSApi {
    @Headers("Content-Type: application/json")
    @POST("v1/audio/speech")
    fun getSpeech(@Body request: TTSRequest): Call<ResponseBody>
}

interface ApiService {
    @GET("auth/kakao/login")
    suspend fun requestKakaoLogin(): Response<Void> // 로그인 페이지 요청 (WebView에서 사용)

    @GET("api/auth/kakao/callback")
    suspend fun fetchLoginResult(@Query("code") code: String): Response<LoginResponse>

    @GET("/api/steps/")
    suspend fun loadResult(
        @Header("Authorization")token:String,
        @Query("date") date:String): Response<Int>

    @PUT("/api/steps/add")
    suspend fun putStep(
        @Header("Authorization") token: String,
        @Body request: StepRequest
    ): Response<Void>

    @POST("/api/auth/login/phone/verify-request")
    suspend fun sendLogin(@Query("phoneNum") pn: String): Response<String>

    @POST("/api/auth/login/phone/verify-check")
    suspend fun checkLogin(@Query("phoneNum") pn : String,
                           @Query("code") code : Int) : Response<normalLoginData>

    @GET("place/nearbysearch/json") // ✅ Google Places API - Nearby Search
    fun getNearbyParks(
        @Query("location") location: String, // ✅ 위도, 경도 (예: "37.5444,127.0370")
        @Query("radius") radius: Int, // ✅ 검색 반경 (미터 단위)
        @Query("type") type: String, // ✅ "공원"만 검색
        @Query("key") apiKey: String // ✅ Google API Key
    ): Call<PlacesResponse> // ✅ 응답을 PlacesResponse 클래스로 받음


}
data class StepRequest(
    val steps: Int,
    val date: String
)



data class normalLoginData(
    val userId: Int,
    val accessToken: String,
    val refreshToken: String
)


data class LoginResponse(
    val message: String,    // "카카오 로그인 성공"
    val user: User,
    val newUser: Boolean,
    val accessToken: String,
    val refreshToken: String
)

data class User(
    val id: Int,
    val name: String,
    val image: String
)




data class PlacesResponse(
    @SerializedName("results") val results: List<PlaceResult>
)

data class PlaceResult(
    @SerializedName("name") val name: String,
    @SerializedName("geometry") val geometry: Geometry
)

data class Geometry(
    @SerializedName("location") val location: LocationData
)

data class LocationData(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)
