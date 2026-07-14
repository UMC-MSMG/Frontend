package com.umc_msmg.frontend

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.umc_msmg.frontend.databinding.FragmentSignUpAddInfoBinding
import com.umc_msmg.frontend.fragment.SignUpDoneFragment
import kotlin.math.pow

class SignUpAddInfoFragment : Fragment() {
    private var _binding: FragmentSignUpAddInfoBinding? = null
    private val binding get() = _binding!!
    private var currentStep = 1
    private var isTakingMedicine = false
    private val selectedDays = mutableSetOf<String>()

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
                2 -> showBMIResults()
                3 -> showMedicineQuestion()
                4 -> handleMedicineResponse()
                5 -> showNotificationConfirm()
                6 -> finishSignUp()
            }
        }

        binding.btnLater.setOnClickListener {
            navigateToSignUpDoneFragment()
        }

        val dayButtons = listOf(
            binding.mon, binding.tue, binding.wed,
            binding.thu, binding.fri, binding.sat, binding.sun
        )

        dayButtons.forEach { button ->
            button.setOnClickListener {
                toggleDaySelection(button)
            }
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
            updateButtonStyles(binding.btnYes, binding.btnNo)
            binding.tvNext.visibility = View.VISIBLE
        }
        binding.btnNo.setOnClickListener {
            isTakingMedicine = false
            updateButtonStyles(binding.btnNo, binding.btnYes)
            binding.tvNext.visibility = View.VISIBLE
        }
    }

    private fun updateButtonStyles(selectedButton: TextView, unselectedButton: TextView) {
        selectedButton.setBackgroundResource(R.drawable.rounded_button)
        selectedButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_primary))
        selectedButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        unselectedButton.setBackgroundResource(R.drawable.rounded_button)
        unselectedButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        unselectedButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
    }


    private fun showWeightInput() {
        binding.layoutHeight.visibility = View.GONE
        binding.layoutWeight.visibility = View.VISIBLE
        currentStep = 2
    }

    private fun showBMIResults() {
        val height = binding.npHeight.value
        val weight = binding.npWeight.value
        val bmi = weight / ((height/100f).pow(2))

        binding.layoutWeight.visibility = View.GONE
        binding.layoutBMI.visibility = View.VISIBLE

        val resultStringRes = when {
            bmi < 18.5 -> R.string.BMI_underweight
            bmi < 23 -> R.string.BMI_normal
            bmi < 25 -> R.string.BMI_overweight
            else -> R.string.BMI_obese
        }

        binding.tvBmiResult.text = getString(resultStringRes)
        binding.bmiBoxHeight.text = height.toString()
        binding.bmiBoxWeight.text = weight.toString()
        binding.bmiBoxBmi.text = "%.1f".format(bmi)

        currentStep = 3
    }


    private fun showMedicineQuestion() {
        binding.layoutBMI.visibility = View.GONE
        binding.layoutMedicineExist.visibility = View.VISIBLE
        binding.tvNext.visibility = View.GONE
        currentStep = 4
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
        currentStep = 5
    }

    private fun toggleDaySelection(button: TextView) {
        val day = button.text.toString()
        if (selectedDays.contains(day)) {
            selectedDays.remove(day)
            button.setBackgroundResource(R.drawable.day_unselected)
        } else {
            selectedDays.add(day)
            button.setBackgroundResource(R.drawable.day_selected)
        }
    }

    private fun saveMedicineSchedule() {
        val morningTime = "${binding.morningHour.value}:${binding.morningMin.value}"
        val lunchTime = "${binding.lunchHour.value}:${binding.lunchMin.value}"
        val dinnerTime = "${binding.dinnerHour.value}:${binding.dinnerMin.value}"
    }

    private fun showNotificationConfirm() {
        binding.layoutMedicineSchedule.visibility = View.GONE
        binding.layoutNotificationConfirm.visibility = View.VISIBLE
        binding.tvNext.text = "확인"
        currentStep = 6
    }

    private fun finishSignUp() {
        val height = binding.npHeight.value
        val weight = binding.npWeight.value
        saveMedicineSchedule()

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
