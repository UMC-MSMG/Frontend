// WorkoutViewModel.kt
package com.umc_msmg.frontend.fragment

import androidx.lifecycle.ViewModel
import com.umc_msmg.frontend.interfaces.UserServiceRetrofitClient
import com.umc_msmg.frontend.interfaces.WorkoutCompletionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WorkoutViewModel(private val apiService: UserServiceRetrofitClient.ApiService) : ViewModel() {
    private val _completedWorkouts = MutableStateFlow<List<Int>>(emptyList())
    val completedWorkouts: StateFlow<List<Int>> = _completedWorkouts.asStateFlow()

    fun completeWorkout(workoutId: Int) {
        if (!_completedWorkouts.value.contains(workoutId)) {
            _completedWorkouts.value = _completedWorkouts.value + workoutId
        }
    }

    suspend fun submitCompletedWorkouts(token: String) {
        val workouts = _completedWorkouts.value
        if (workouts.size == 4) {
            try {
                val response = apiService.completeWorkouts(
                    "Bearer $token",
                    WorkoutCompletionRequest(workouts)
                )
                if (response.isSuccessful) {
                    // 성공 처리: 포인트 업데이트 등 추가 로직 필요
                    _completedWorkouts.value = emptyList()
                } else {
                    // 오류 처리: 서버 응답에 따라 사용자에게 메시지 표시 등
                }
            } catch (e: Exception) {
                // 예외 처리: 로그 기록 또는 사용자에게 알림 표시 등
            }
        }
    }
}
