// SignUpAddInfoFragment.kt
package com.umc_msmg.frontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.umc_msmg.frontend.databinding.FragmentSignUpAddInfoBinding
import com.umc_msmg.frontend.fragment.SignUpDoneFragment

class SignUpAddInfoFragment : Fragment() {
    private var _binding: FragmentSignUpAddInfoBinding? = null
    private val binding get() = _binding!!
    private var currentStep = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpAddInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeightPicker()
        setupWeightPicker()

        binding.tvNext.setOnClickListener {
            when (currentStep) {
                1 -> showWeightInput()
                2 -> finishSignUp()
            }
        }

        binding.btnLater.setOnClickListener {
            navigateToSignUpDoneFragment()
        }
    }

    private fun setupHeightPicker() {
        binding.npHeight.apply {
            minValue = 140
            maxValue = 200
            value = 170 // 기본값
            wrapSelectorWheel = false
        }
    }

    private fun setupWeightPicker() {
        binding.npWeight.apply {
            minValue = 30
            maxValue = 150
            value = 60 // 기본값
            wrapSelectorWheel = false
        }
    }

    private fun showWeightInput() {
        binding.layoutHeight.visibility = View.GONE
        binding.layoutWeight.visibility = View.VISIBLE
        binding.tvNext.text = "완료"
        currentStep = 2
    }

    private fun finishSignUp() {
        val height = binding.npHeight.value
        val weight = binding.npWeight.value

        navigateToSignUpDoneFragment()
    }

    private fun navigateToSignUpDoneFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SignUpDoneFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
