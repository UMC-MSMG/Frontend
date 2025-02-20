// ApiService.kt
package com.umc_msmg.frontend.interfaces

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface ApiService {
    @POST("workouts/complete")
    suspend fun completeWorkouts(@Header("Authorization") token: String, @Body request: WorkoutCompletionRequest): Response<WorkoutCompletionResponse>
}

data class WorkoutCompletionRequest(val workoutId: List<Int>)
data class WorkoutCompletionResponse(val success: Boolean, val points: Int)