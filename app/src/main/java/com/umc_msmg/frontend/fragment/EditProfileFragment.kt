package com.umc_msmg.frontend.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.data.UpdateProfileResponse
import com.umc_msmg.frontend.data.UserProfileUpdateRequest
import com.umc_msmg.frontend.databinding.FragmentEditProfileBinding
import com.umc_msmg.frontend.interfaces.UserServiceRetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserProfile()

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.cancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.save.setOnClickListener {
            saveUserProfile()
        }

        binding.editPhone.formatPhoneNumber()
        binding.editHeight.formatHeight(binding)
        binding.editWeight.formatWeight(binding)
    }

    private fun loadUserProfile() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("user_name", "")
        val gender = sharedPreferences.getString("user_gender", "")
        val height = sharedPreferences.getInt("user_height", 156)
        val weight = sharedPreferences.getInt("user_weight", 56)
        val phone = sharedPreferences.getString("user_phone", "")

        binding.editName.setText(name)
        if (gender == "MALE") {
            binding.maleRadioButton.isChecked = true
        } else if (gender == "FEMALE") {
            binding.femaleRadioButton.isChecked = true
        }
        binding.editHeight.setText(height.toString())
        binding.editWeight.setText(weight.toString())
        binding.editPhone.setText(phone)
        Log.d("EditProfileFragment", "사용자 정보 조회 성공")
    }

    private fun saveUserProfile() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val authorization = "Bearer " + sharedPreferences.getString("access_token", null)
        Log.d("tokenName", authorization)

        val name = binding.editName.text.toString()
        val gender = when (binding.genderRadioGroup.checkedRadioButtonId) {
            R.id.femaleRadioButton -> "FEMALE"
            else -> "MALE"
        }
        val height = binding.editHeight.text.toString().replace(" cm", "").toIntOrNull()
        val weight = binding.editWeight.text.toString().replace(" kg", "").toIntOrNull()
        val phoneNumber = binding.editPhone.text.toString()
        if (name.isEmpty() || gender.isEmpty() || height == null || weight == null) {
            Toast.makeText(context, "모든 필수 정보를 입력해야 합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val userProfile = UserProfileUpdateRequest(
            name = name,
            gender = gender,
            height = height,
            weight = weight,
            phoneNumber = phoneNumber
        )

        UserServiceRetrofitClient.apiService.updateProfile(authorization, userProfile)
            .enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                    if (response.isSuccessful) {
                        val updateProfileResponse = response.body()
                        Log.d(
                            "EditProfileFragment",
                            "사용자 정보 업데이트 성공: ${updateProfileResponse?.message}"
                        )
                        Toast.makeText(context, "사용자 정보 업데이트 성공", Toast.LENGTH_SHORT).show()

                        val editor = sharedPreferences.edit()
                        editor.putString("user_name", name)
                        editor.putString("user_gender", gender)
                        editor.putInt("user_height", height)
                        editor.putInt("user_weight", weight)
                        editor.putString("user_phone", phoneNumber)
                        editor.apply()
                        Log.d("EditProfileFragment", "사용자 정보 저장 성공")

                        parentFragmentManager.popBackStack()
                    } else {
                        Log.e("EditProfileFragment", "사용자 정보 업데이트 실패: ${response.code()}")
                        Log.d("EditProfileFragment", "사용자 정보 업데이트 API 요청 헤더: ${call.request().headers}")
                        Toast.makeText(context, "사용자 정보 업데이트 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    Log.e("EditProfileFragment", "api 호출 실패: ${t.message}")
                    Toast.makeText(context, "api 호출 실패", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun TextInputEditText.formatPhoneNumber() {
    addTextChangedListener(object : TextWatcher {
        private var editing = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (editing) return

            editing = true

            val digits = s?.toString()?.replace("-", "") ?: ""
            if (digits.length >= 11) {
                val formatted = StringBuilder()
                formatted.append(digits.substring(0, 3))
                formatted.append("-")
                formatted.append(digits.substring(3, 7))
                formatted.append("-")
                formatted.append(digits.substring(7, 11))
                s?.clear()
                s?.append(formatted)
            }

            editing = false
        }
    })
}

fun TextInputEditText.formatHeight(binding: FragmentEditProfileBinding) {
    addTextChangedListener(object : TextWatcher {
        private var editing = false
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            if (editing) return
            editing = true
            var value = s.toString()
            if (value.isNotEmpty()) {
                value = value.replace(" cm", "")
                binding.editHeight.setText(value)
                binding.editHeight.setSelection(value.length)
            }
            editing = false
        }
    })
}

fun TextInputEditText.formatWeight(binding: FragmentEditProfileBinding) {
    addTextChangedListener(object : TextWatcher {
        private var editing = false
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            if (editing) return
            editing = true
            var value = s.toString()
            if (value.isNotEmpty()) {
                value = value.replace(" kg", "")
                binding.editWeight.setText(value)
                binding.editWeight.setSelection(value.length)
            }
            editing = false
        }
    })
}