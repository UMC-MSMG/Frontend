package com.umc_msmg.frontend

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.icu.text.IDNA.Info
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.umc_msmg.frontend.activity.MainActivity
import com.umc_msmg.frontend.databinding.FragmentSignUpAddInfoBinding
import com.umc_msmg.frontend.fragment.GptFragment
import com.umc_msmg.frontend.fragment.SignUpDoneFragment
import com.umc_msmg.frontend.interfaces.InfoUpdateData
import com.umc_msmg.frontend.interfaces.RetrofitClient
import kotlin.math.pow
import com.umc_msmg.frontend.viewModel.SignUpViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        if(currentStep == 1) {
            setupHeightPicker()
            setupWeightPicker()
            setupMedicineQuestion()
            setNumberPicker()
        }
        else
        {
            binding.layoutHeight.visibility = View.GONE
            showMedicineQuestion()
        }

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
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putInt("user_weight", weight)
            .putInt("user_height", height)
            .apply()
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
        currentStep = 4
        binding.layoutBMI.visibility = View.GONE
        binding.layoutMedicineExist.visibility = View.VISIBLE
        binding.tvNext.visibility = View.GONE
    }

    private fun handleMedicineResponse() {
        if (isTakingMedicine) {
            showMedicineSchedule()
        } else {
            navigateToSignUpDoneFragment()
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
        logAllPreferences()

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SignUpDoneFragment())
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logAllPreferences() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val allEntries: Map<String, *> = sharedPreferences.all
        for ((key, value) in allEntries) {
            Log.d("SharedPreferencesFinal", "Key: $key, Value: $value")
        }


        val token = "Bearer " + sharedPreferences.getString("access_token", null)
        val name = sharedPreferences.getString("user_name", null)
        val phone = sharedPreferences.getString("user_phone", null)
        val gender = sharedPreferences.getString("user_gender", null)
        val birthday = sharedPreferences.getString("user_birthday", null)
        val image = sharedPreferences.getString("ai_data", null)
        val height = sharedPreferences.getInt("user_height", 0)
        val weight = sharedPreferences.getInt("user_weight", 0)
        val agreed = sharedPreferences.getBoolean("agreed", false)
        var diff = sharedPreferences.getString("user_diff", null)


        if(diff == "h") { diff = "HARD" }
        else if (diff == "m") { diff = "NORMAL" }
        else { diff = "EASY" }

        CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (token != null) {
                        RetrofitClient.loginService.sendInfo(token, InfoUpdateData(name, phone, gender, birthday, height, weight, agreed, diff))
                    }
                    else
                    {
                        logout()
                        clearWebViewData()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                } catch (e: Exception) {
                    Log.e("PATCH", "오류 발생: ${e.message}")
                }
            }
    }

    private fun logout() {
        var sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        sharedPreferences = requireContext().getSharedPreferences("LP", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        clearWebViewData()
        Log.d("Logout", "로그아웃 완료 / 웹뷰 데이터 초기화됨")
    }

    private fun clearWebViewData() {
        val webView = WebView(requireContext())
        webView.clearCache(true)
        webView.clearHistory()
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
        WebStorage.getInstance().deleteAllData()
    }


}
