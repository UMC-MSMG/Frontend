// WorkoutFragment.kt
package com.umc_msmg.frontend.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.LayoutWorkoutMainBinding

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
