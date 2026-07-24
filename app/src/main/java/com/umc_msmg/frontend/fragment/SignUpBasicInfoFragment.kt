package com.umc_msmg.frontend

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import com.umc_msmg.frontend.activity.MainActivity
import com.umc_msmg.frontend.databinding.FragmentSignUpBasicInfoBinding
import com.umc_msmg.frontend.fragment.GptFragment
import com.umc_msmg.frontend.interfaces.RetrofitClient
import com.umc_msmg.frontend.interfaces.codeVerifyData
import com.umc_msmg.frontend.interfaces.phoneVerifyData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpBasicInfoFragment : Fragment() {
    private var _binding: FragmentSignUpBasicInfoBinding? = null
    private val binding get() = _binding!!
    private var currentStep = 1
    private lateinit var sharedPreferences : SharedPreferences
    private var phone = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBasicInfoBinding.inflate(inflater, container, false)
        return binding.root

    }


    private fun enableBtn()
    {
        binding.tvNext.text = "다음"
        binding.tvNext.setBackgroundResource(R.drawable.round_corner_primary) // 배경 리소스 변경
        binding.tvNext.backgroundTintList =
            context?.let { ContextCompat.getColorStateList(it, R.color.color_primary) } // Tint 변경
        binding.tvNext.isEnabled = true
    }

    private fun disableBtn()
    {
        binding.tvNext.text = "다음"
        binding.tvNext.setBackgroundResource(R.drawable.round_corner_grey) // 배경 리소스 변경
        binding.tvNext.backgroundTintList =
            context?.let { ContextCompat.getColorStateList(it, R.color.gray) } // Tint 변경
        binding.tvNext.isEnabled = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rbMale.setOnClickListener() {enableBtn()}
        binding.rbFemale.setOnClickListener() {enableBtn()}
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enableBtn()
            }
        })

        binding.etPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enableBtn()
            }
        })

        binding.etVerify.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enableBtn()
            }
        })

        sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        disableBtn()
        binding.tvNext.setOnClickListener {
            when (currentStep) {
                1 -> showGenderInput()
                2 -> showBirthdateInput()
                3 -> showPhoneInput()
                4 -> showVerify()
                5 -> finishSignUp()
            }
        }
    }

    private fun showGenderInput() {
        disableBtn()
        sharedPreferences.edit()
            .putString("user_name", binding.etName.text.toString())
            .apply()

        binding.layoutName.visibility = View.GONE
        binding.layoutGender.visibility = View.VISIBLE
        binding.layoutPhone.visibility = View.GONE
        binding.layoutBirthdate.visibility = View.GONE
        binding.layoutVerify.visibility = View.GONE
        currentStep = 2
    }

    private fun showBirthdateInput() {
        if(binding.rbMale.isChecked)
        {
            sharedPreferences.edit()
                .putString("user_gender", "MALE")
                .apply()
        }
        else if(binding.rbFemale.isChecked)
        {
            sharedPreferences.edit()
                .putString("user_gender", "FEMALE")
                .apply()
        }

        binding.layoutName.visibility = View.GONE
        binding.layoutGender.visibility = View.GONE
        binding.layoutPhone.visibility = View.GONE
        binding.layoutBirthdate.visibility = View.VISIBLE
        binding.layoutVerify.visibility = View.GONE
        currentStep = 3
    }



    private fun showPhoneInput() {
        disableBtn()
        val year = binding.dpBirthdate.year
        val month = binding.dpBirthdate.month+1
        val day = binding.dpBirthdate.dayOfMonth
        val selectedDate = "$year-$month-$day"

        sharedPreferences.edit()
            .putString("user_birthday", selectedDate) //YYYY-MM-DD 형식으로 저장
            .apply()

        binding.layoutName.visibility = View.GONE
        binding.layoutGender.visibility = View.GONE
        binding.layoutPhone.visibility = View.VISIBLE
        binding.layoutBirthdate.visibility = View.GONE
        binding.layoutVerify.visibility = View.GONE
        currentStep = 4
    }

    private fun showVerify() {
        CoroutineScope(Dispatchers.IO).launch {
            sendCode()
        }
    }

    private fun finishSignUp() {
        CoroutineScope(Dispatchers.IO).launch {
            checkCode()
        }
        //잘 된다면

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun sendCode()
    {
        Log.e("!!", "여기요")
        phone = binding.etPhone.text.toString()
        if (!phone.isNullOrEmpty())
        {
            val response = RetrofitClient.loginService.sendRegister(phoneVerifyData(binding.etPhone.text.toString())).code()
            if(response == 200)
            {

                sharedPreferences.edit()
                    .putString("user_phone", binding.etPhone.text.toString()) //010-XXXX-XXXX 형식으로 저장
                    .apply()
                currentStep = 5
                withContext(Dispatchers.Main) {
                    binding.layoutName.visibility = View.GONE
                    binding.layoutGender.visibility = View.GONE
                    binding.layoutPhone.visibility = View.GONE
                    binding.layoutBirthdate.visibility = View.GONE
                    binding.layoutVerify.visibility = View.VISIBLE
                    binding.alarmTv.visibility = GONE
                    disableBtn()
                }
            }
            else
            {
                withContext(Dispatchers.Main) {
                    binding.alarmTv.visibility = VISIBLE
                }
            }
        }
    }

    private suspend fun checkCode() {
        try {
            val response = RetrofitClient.loginService.checkRegister(
                codeVerifyData(phone, binding.etVerify.text.toString())

            )
            if (response.code() == 200) {
                val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                sharedPreferences.edit()
                    .putString("access_token", response.body()?.accessToken)
                    .putString("refresh_token", response.body()?.refreshToken)
                    .apply()

                val responseBody = response.body()
                responseBody?.let { data ->

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, GptFragment())
                        .commit()
                } ?: run {
                    Log.e("checkCode", "응답 바디가 null입니다.")

                }
            } else {
                Log.e("checkCode", "응답 실패: ${response.code()}")
                withContext(Dispatchers.Main) {
                    binding.alarm2Tv.visibility = VISIBLE
                }
            }

        } catch (e: Exception) {
            Log.e("checkCode", "에러 발생: ${e.localizedMessage}")
            withContext(Dispatchers.Main) {
                binding.alarm2Tv.visibility = VISIBLE
            }
        }
    }
}
