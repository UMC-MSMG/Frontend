// WorkoutDetailFragment.kt
package com.umc_msmg.frontend.fragment

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

        // 운동 종류에 따라 상세 정보 설정
        when (workoutType) {
            "유산소" -> {
                binding.a1TopTv1.text = "유산소 운동"
                binding.a3LeftTv.text = "빠르게 걷기"
                binding.a4LeftTv.text = "빠르게 걷기"
                binding.a5LeftTv.text = "빠르게 걷기"
                binding.a6LeftTv.text = "빠르게 걷기"
            }
            "근력" -> {
                binding.a1TopTv1.text = "근력 운동"
                binding.a3LeftTv.text = "의자에서 천천히 일어나기"
                binding.a4LeftTv.text = "발뒤꿈치 올리기"
                binding.a5LeftTv.text = "다리 차올리기"
                binding.a6LeftTv.text = "다리 옆으로 올리기"
            }
            "유연성" -> {
                binding.a1TopTv1.text = "유연성 운동"
                binding.a3LeftTv.text = "스트레칭 1"
                binding.a4LeftTv.text = "스트레칭 2"
                binding.a5LeftTv.text = "스트레칭 3"
                binding.a6LeftTv.text = "스트레칭 4"
            }
            "균형" -> {
                binding.a1TopTv1.text = "균형 운동"
                binding.a3LeftTv.text = "균형운동 1"
                binding.a4LeftTv.text = "균형운동 2"
                binding.a5LeftTv.text = "균형운동 3"
                binding.a6LeftTv.text = "균형운동 4"
            }
        }

        binding.area3.setOnClickListener {
            navigateToWorkoutVideo(binding.a3LeftTv.text.toString())
        }

        binding.area4.setOnClickListener {
            navigateToWorkoutVideo(binding.a4LeftTv.text.toString())
        }

        binding.area5.setOnClickListener {
            navigateToWorkoutVideo(binding.a5LeftTv.text.toString())
        }

        binding.area6.setOnClickListener {
            navigateToWorkoutVideo(binding.a6LeftTv.text.toString())
        }
    }

    private fun navigateToWorkoutVideo(exerciseType: String) {
        val bundle = Bundle().apply {
            putString("workoutType", workoutType)
            putString("exerciseType", exerciseType)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
