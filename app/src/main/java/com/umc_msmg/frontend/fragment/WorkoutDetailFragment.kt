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
                binding.nameTv.text = "오늘의 유산소 운동이에요."
                binding.content.text = "빠르게 걷기 (3분)"
                binding.btnStartTv.text = "유산소 운동 시작하기"
            }
            "근력" -> {
                binding.a1TopTv1.text = "근력 운동"
                binding.nameTv.text = "오늘의 근력 운동이에요."
                binding.content.text = "의자에서 천천히 일어나기\n발뒤꿈치 올리기\n다리 차올리기\n다리 옆으로 올리기"
                binding.btnStartTv.text = "근력 운동 시작하기"
            }
            "유연성" -> {
                binding.a1TopTv1.text = "유연성 운동"
                binding.nameTv.text = "오늘의 유연성 운동이에요."
                binding.content.text = "스트레칭 1\n스트레칭2\n스트레칭3\n스트레칭4"
                binding.btnStartTv.text = "유연성 운동 시작하기"
            }
            "균형" -> {
                binding.a1TopTv1.text = "균형 운동"
                binding.nameTv.text = "오늘의 균형 운동이에요."
                binding.content.text = "균형운동 1\n균형운동2\n균형운동3\n균형운동4"
                binding.btnStartTv.text = "균형 운동 시작하기"
            }
        }

        binding.startBtn.setOnClickListener {
            navigateToWorkoutVideo()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
