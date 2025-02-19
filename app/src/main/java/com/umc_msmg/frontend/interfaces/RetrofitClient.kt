package com.umc_msmg.frontend.interfaces
import android.util.Log
import com.umc_msmg.frontend.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.openai.com/" // ✅ OpenAI API 기본 주소
    private const val API_KEY = BuildConfig.OPENAI_API
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



    val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("OkHttp", message) // ✅ 요청 & 응답 데이터를 Raw로 찍어줌!
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY // ✅ 요청 & 응답 전체 출력
    }


    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // ✅ 요청 & 응답 로그 출력 추가
        .build()

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
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAITTSApi::class.java)
    }

    val loginService: ApiService = Retrofit.Builder()
        .baseUrl("http://43.202.104.127:3000/")
        .addConverterFactory(GsonConverterFactory.create()) // JSON 변환
        .client(client)
        .build()
        .create(ApiService::class.java)
}
