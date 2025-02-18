package com.umc_msmg.frontend.interfaces

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
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
}

data class LoginResponse(
    val message: String,    // "카카오 로그인 성공"
    val user: User,         // 사용자 정보
    val accessToken: String,
    val refreshToken: String
)

data class User(
    val id: Int,
    val name: String,
    val image: String
)

