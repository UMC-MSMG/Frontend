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


}
data class StepRequest(
    val steps: Int,
    val date: String
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

