//// WorkoutViewModel.kt
//package com.umc_msmg.frontend.fragment
//
//import android.content.Context
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.umc_msmg.frontend.interfaces.UserServiceRetrofitClient
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class WorkoutViewModel(
//    private val apiService: UserServiceRetrofitClient.ApiService,
//    private val context: Context // Context를 전달받아 SharedPreferences 사용
//) : ViewModel() {
//
//    private val _completedWorkouts = MutableStateFlow<List<Int>>(emptyList())
//    val completedWorkouts: StateFlow<List<Int>> = _completedWorkouts.asStateFlow()
//
//    fun completeWorkout(workoutId: Int) {
//        if (!_completedWorkouts.value.contains(workoutId)) {
//            _completedWorkouts.value = _completedWorkouts.value + workoutId
//        }
//    }
//
//    fun submitCompletedWorkouts(onSuccess: () -> Unit, onError: (String) -> Unit) {
//        val workouts = _completedWorkouts.value
//        if (workouts.size == 4) {
//            viewModelScope.launch(Dispatchers.IO) {
//                try {
//                    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//                    val token = "Bearer " + (sharedPreferences.getString("access_token", null) ?: "")
//                    val response = apiService.completeWorkouts(token, WorkoutCompletionRequest(workouts))
//                    if (response.isSuccessful) {
//                        _completedWorkouts.value = emptyList()
//                        onSuccess() // 성공 콜백 호출
//                    } else {
//                        onError("응답 실패: ${response.code()}")
//                    }
//                } catch (e: Exception) {
//                    onError("오류 발생: ${e.message}")
//                }
//            }
//        } else {
//            onError("운동이 모두 완료되지 않았습니다.")
//        }
//    }
//}
