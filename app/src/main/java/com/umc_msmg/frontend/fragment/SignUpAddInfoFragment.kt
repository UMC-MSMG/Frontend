package com.umc_msmg.frontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.umc_msmg.frontend.databinding.FragmentSignUpAddInfoBinding
import com.umc_msmg.frontend.fragment.SignUpDoneFragment
import kotlin.math.max

class SignUpAddInfoFragment : Fragment() {
    private var _binding: FragmentSignUpAddInfoBinding? = null
    private val binding get() = _binding!!
    private var currentStep = 1
    private var isTakingMedicine = false

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
        setupMedicineQuestion()
        setNumberPicker()

        binding.tvNext.setOnClickListener {
            when (currentStep) {
                1 -> showWeightInput()
                2 -> showMedicineQuestion()
                3 -> handleMedicineResponse()
                4 -> showMedicineSchedule()
                5 -> showNotificationConfirm()
                6 -> finishSignUp()
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

    private fun setNumberPicker() {
        binding.morningHour.apply {
            minValue = 0
            maxValue = 23
        }
        binding.morningMin.apply {
            minValue = 0
            maxValue = 59
        }
        binding.lunchHour.apply {
            minValue = 0
            maxValue = 23
        }
        binding.lunchMin.apply {
            minValue = 0
            maxValue = 59
        }
        binding.dinnerHour.apply {
            minValue = 0
            maxValue = 23
        }
        binding.dinnerMin.apply {
            minValue = 0
            maxValue = 59
        }
    }

    private fun setupMedicineQuestion() {
        binding.btnYes.setOnClickListener {
            isTakingMedicine = true
            binding.btnYes.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.color_primary)
            )
            binding.tvNext.visibility = View.VISIBLE
        }
        binding.btnNo.setOnClickListener {
            isTakingMedicine = false
            binding.btnNo.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.color_primary)
            )
            binding.tvNext.visibility = View.VISIBLE
        }
    }

    private fun showWeightInput() {
        binding.layoutHeight.visibility = View.GONE
        binding.layoutWeight.visibility = View.VISIBLE
        binding.tvNext.text = "다음"
        currentStep = 2
    }

    private fun showMedicineQuestion() {
        binding.layoutWeight.visibility = View.GONE
        binding.layoutMedicineExist.visibility = View.VISIBLE
        binding.tvNext.visibility = View.GONE
        currentStep = 3
    }

    private fun handleMedicineResponse() {
        if (isTakingMedicine) {
            showMedicineSchedule()
        } else {
            finishSignUp()
        }
    }

    private fun showMedicineSchedule() {
        binding.layoutMedicineExist.visibility = View.GONE
        binding.layoutMedicineSchedule.visibility = View.VISIBLE
        currentStep = 4
    }

    private fun showNotificationConfirm() {
        binding.layoutMedicineSchedule.visibility = View.GONE
        binding.layoutNotificationConfirm.visibility = View.VISIBLE
        binding.tvNext.text = "확인"
        currentStep = 5
    }

    private fun finishSignUp() {
        val height = binding.npHeight.value
        val weight = binding.npWeight.value
        // TODO: 약 복용 정보 저장 로직 추가

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
