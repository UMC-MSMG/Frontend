package com.umc_msmg.frontend.interfaces

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.openai.com/" // ✅ OpenAI API 기본 주소
    private const val API_KEY = "openaikey"

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $API_KEY") // ✅ API 키 추가
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()

    private val httpClientTTS = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()



    val apiService: OpenAIApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAIApi::class.java)
    }

    val ttsService: OpenAITTSApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClientTTS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAITTSApi::class.java)
    }
}
