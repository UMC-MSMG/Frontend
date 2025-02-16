package com.umc_msmg.frontend.interfaces

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIApi {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    fun getChatResponse(
        @Body request: ChatRequest
    ): Call<ChatResponse>
}
interface OpenAITTSApi {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer openaikey"
    )
    @POST("v1/audio/speech")
    fun getSpeech(@Body request: TTSRequest): Call<ResponseBody>
}

