// WorkoutFragment.kt
package com.umc_msmg.frontend.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.LayoutWorkoutMainBinding
import com.umc_msmg.frontend.interfaces.PointBody
import com.umc_msmg.frontend.interfaces.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkoutFragment : Fragment() {

    private var _binding: LayoutWorkoutMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutWorkoutMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.a3LeftTv.text = "유산소"
        binding.a4LeftTv.text = "근력"
        binding.a5LeftTv.text = "유연성"
        binding.a6LeftTv.text = "균형"

        binding.area3.setOnClickListener {
            navigateToWorkoutDetail("유산소")
        }

        binding.area4.setOnClickListener {
            navigateToWorkoutDetail("근력")
        }

        binding.area5.setOnClickListener {
            navigateToWorkoutDetail("유연성")
        }

        binding.area6.setOnClickListener {
            navigateToWorkoutDetail("균형")
        }

        // area2 클릭 시 SharedPreferences 확인 후 조건에 따라 동작
        binding.area2.setOnClickListener {
            checkAndCallPointUp()
        }
    }

    private fun navigateToWorkoutDetail(workoutType: String) {
        val bundle = Bundle().apply {
            putString("workoutType", workoutType)
        }

        val workoutDetailFragment = WorkoutDetailFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            .replace(R.id.fragment_container, workoutDetailFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun checkAndCallPointUp() {
        // SharedPreferences에서 운동 상태 리스트 가져오기
        val sharedPreferences =
            requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        val json = sharedPreferences.getString("workout_status", null)

        if (json != null) {
            val typeToken = object : TypeToken<List<Boolean>>() {}.type
            val statusList: List<Boolean> = Gson().fromJson(json, typeToken)

            // 모든 값이 true인지 확인
            if (statusList.all { it }) {
                CoroutineScope(Dispatchers.Main).launch {
                    pointUp(25)
                } // 모든 운동 완료 시 API 호출
                Toast.makeText(requireContext(), "운동 완료! 포인트를 획득했습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "모든 운동을 완료해야 합니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "운동 상태를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun pointUp(pt : Int)
    {
        var currentPoint = 0
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPreferences.getString("access_token", null)
        try {
            val response  = RetrofitClient.loginService.loadInfo(token)
            if (response.isSuccessful) {
                currentPoint = response.body()?.point!!
            } else {
                Log.e("MainActivity", "가져오기 실패: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "API 호출 실패: ${e.message}")
        }

        try {
            val newResponse  = RetrofitClient.loginService.addPoint(token, PointBody(currentPoint+pt))
            if (newResponse.isSuccessful) {
                Log.e("LOG", "전송성공")
            } else {
                Log.e("MainActivity", "가져오기 실패: ${newResponse.code()}")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "API 호출 실패: ${e.message}")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
