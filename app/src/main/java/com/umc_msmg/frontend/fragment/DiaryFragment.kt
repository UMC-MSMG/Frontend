package com.umc_msmg.frontend.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.data.DayOfWeek
import com.umc_msmg.frontend.data.DefaultWorkoutPlan
import com.umc_msmg.frontend.data.Difficulty
import com.umc_msmg.frontend.data.WeeklyExerciseSummary
import com.umc_msmg.frontend.databinding.FragmentDiaryBinding
import com.umc_msmg.frontend.interfaces.UserServiceRetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val today = Calendar.getInstance().time
        val formatter = SimpleDateFormat("MM / dd / yyyy", Locale.getDefault())
        val formattedDate = formatter.format(today)
        binding.todayDate.text = formattedDate

        loadWorkoutList()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadWorkoutList() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val difficultyString = sharedPreferences.getString("difficulty", "중") ?: "중"
        val difficulty = when (difficultyString) {
            "상" -> Difficulty.상
            "중" -> Difficulty.중
            "하" -> Difficulty.하
            else -> Difficulty.중
        }

        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val dayOfWeek = when (today) {
            Calendar.MONDAY -> DayOfWeek.MONDAY
            Calendar.TUESDAY -> DayOfWeek.TUESDAY
            Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
            Calendar.THURSDAY -> DayOfWeek.THURSDAY
            Calendar.FRIDAY -> DayOfWeek.FRIDAY
            Calendar.SATURDAY -> DayOfWeek.SATURDAY
            Calendar.SUNDAY -> DayOfWeek.SUNDAY
            else -> DayOfWeek.MONDAY
        }

        val workoutList = DefaultWorkoutPlan.getWorkoutList(dayOfWeek, difficulty)

        binding.exercise1Name.text = workoutList.유산소.joinToString("\n") { it.name }
        binding.exercise2Name.text = workoutList.근력.joinToString("\n") { it.name }
        binding.exercise3Name.text = workoutList.유연성.joinToString("\n") { it.name }
        binding.exercise4Name.text = workoutList.균형.joinToString("\n") { it.name }

        binding.exercise1Time.text = workoutList.유산소.joinToString("\n") { it.set.toString() }
        binding.exercise2Time.text = workoutList.근력.joinToString("\n") { it.set.toString() }
        binding.exercise3Time.text = workoutList.유연성.joinToString("\n") { it.set.toString() }
        binding.exercise4Time.text = workoutList.균형.joinToString("\n") { it.set.toString() }

        Log.d("DiaryFragment", "오늘의 운동 계획 로드 성공")
    }

    private fun loadWeeklyExerciseSummary() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val authorization = sharedPreferences.getString("access_token", null) ?: ""

        UserServiceRetrofitClient.apiService.getWeeklyExerciseSummary(authorization).enqueue(object : Callback<WeeklyExerciseSummary> {
            override fun onResponse(call: Call<WeeklyExerciseSummary>, response: Response<WeeklyExerciseSummary>) {
                if (response.isSuccessful) {
                    val summary = response.body()
                    if (summary != null) {
                        val consecutiveDays = summary.sequence_days
                        binding.consecutiveDaysText.text = "${consecutiveDays}일 연속 운동했어요."
                        updateWeeklyCheckboxes(summary)
                    }
                    Log.d("DiaryFragment", "주간 운동 요약 정보 로드 성공: $summary")
                } else {
                    Log.e("DiaryFragment", "주간 운동 요약 정보 로드 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeeklyExerciseSummary>, t: Throwable) {
                Log.e("DiaryFragment", "주간 운동 요약 정보 API 호출 실패: ${t.message}")
            }
        })
    }

    private fun updateWeeklyCheckboxes(summary: WeeklyExerciseSummary) {
        binding.calendarCheckMonday.setImageResource(if (summary.monday) R.drawable.calendar_checked else R.drawable.calendar_unchecked)
        binding.calendarCheckTuesday.setImageResource(if (summary.tuesday) R.drawable.calendar_checked else R.drawable.calendar_unchecked)
        binding.calendarCheckWednesday.setImageResource(if (summary.wednesday) R.drawable.calendar_checked else R.drawable.calendar_unchecked)
        binding.calendarCheckThursday.setImageResource(if (summary.thursday) R.drawable.calendar_checked else R.drawable.calendar_unchecked)
        binding.calendarCheckFriday.setImageResource(if (summary.friday) R.drawable.calendar_checked else R.drawable.calendar_unchecked)
        binding.calendarCheckSaturday.setImageResource(if (summary.saturday) R.drawable.calendar_checked else R.drawable.calendar_unchecked)
        binding.calendarCheckSunday.setImageResource(if (summary.sunday) R.drawable.calendar_checked else R.drawable.calendar_unchecked)
    }

    private fun loadMyPoints() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val authorization = sharedPreferences.getString("access_token", null) ?: ""

        UserServiceRetrofitClient.apiService.getMyPoints(authorization).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val points = response.body()
                    binding.points.text = "${points}원"
                    Log.d("DiaryFragment", "사용자 포인트 정보 로드 성공: $points")
                } else {
                    Log.e("DiaryFragment", "사용자 포인트 정보 로드 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("DiaryFragment", "사용자 포인트 정보 API 호출 실패: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}