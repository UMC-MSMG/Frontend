// WorkoutDetailFragment.kt
package com.umc_msmg.frontend.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.LayoutWorkoutDetailBinding

class WorkoutDetailFragment : Fragment() {

    private var _binding: LayoutWorkoutDetailBinding? = null
    private val binding get() = _binding!!
    private var workoutType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutWorkoutDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workoutType = arguments?.getString("workoutType")

        // SharedPreferences에서 user_diff 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userDifficulty = sharedPreferences.getString("user_diff", "NORMAL") ?: "NORMAL"

        // 운동 종류 및 난이도에 따라 상세 정보 설정
        when (workoutType) {
            "유산소" -> {
                binding.a1TopTv1.text = "유산소 운동"
                binding.nameTv.text = "오늘의 유산소 운동이에요."
                binding.content.text = when (userDifficulty) {
                    "EASY" -> "빠르게 걷기 (3분)"
                    "NORMAL" -> "팔벌려뛰기 20회"
                    "HARD" -> "엎드려서 무릎가슴닿기"
                    else -> "운동 정보 없음"
                }
                binding.btnStartTv.text = "유산소 운동 시작하기"
            }
            "근력" -> {
                binding.a1TopTv1.text = "근력 운동"
                binding.nameTv.text = "오늘의 근력 운동이에요."
                binding.content.text = when (userDifficulty) {
                    "EASY" -> """
                        의자에서 천천히 일어나기 (3세트)
                        발뒤꿈치 올리기 (12회)
                        다리 차올리기 (8회)
                        다리 옆으로 올리기 (8회)
                    """.trimIndent()
                    "NORMAL" -> """
                        의자 끝에 앉으며 스쿼트 (3세트)
                        누워서 엉덩이 들어올리기 (12회)
                        무릎 대고 팔굽혀펴기 (12회)
                        엎드려 다리 뒤로 차기 (10회)
                    """.trimIndent()
                    "HARD" -> """
                        스쿼트 (3세트)
                        런지 (3세트 x 12회)
                        플랭크 (45초)
                        사이드 런지 (3세트 x 12회)
                    """.trimIndent()
                    else -> "운동 정보 없음"
                }
                binding.btnStartTv.text = "근력 운동 시작하기"
            }
            "유연성" -> {
                binding.a1TopTv1.text = "유연성 운동"
                binding.nameTv.text = "오늘의 유연성 운동이에요."
                binding.content.text = "스트레칭 1\n스트레칭 2\n스트레칭 3\n스트레칭 4"
                binding.btnStartTv.text = "유연성 운동 시작하기"
            }
            "균형" -> {
                binding.a1TopTv1.text = "균형 운동"
                binding.nameTv.text = "오늘의 균형 운동이에요."
                binding.content.text = "균형운동 1\n균형운동 2\n균형운동 3\n균형운동 4"
                binding.btnStartTv.text = "균형 운동 시작하기"
            }
        }

        // 버튼 클릭 리스너 설정
        binding.startBtn.setOnClickListener {
            navigateToWorkoutVideo()
        }

        binding.changeBtn.setOnClickListener {
            navigateToPreviousFragment()
        }
    }

    private fun navigateToWorkoutVideo() {
        val bundle = Bundle().apply {
            putString("workoutType", workoutType)
        }

        val workoutVideoFragment = WorkoutVideoFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            .replace(R.id.fragment_container, workoutVideoFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToPreviousFragment() {
        requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
