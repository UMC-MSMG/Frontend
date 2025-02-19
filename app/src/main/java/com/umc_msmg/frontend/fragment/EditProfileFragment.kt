package com.umc_msmg.frontend.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.umc_msmg.frontend.databinding.FragmentEditProfileBinding

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
        binding.editGender.setText(gender)
        binding.editHeight.setText(height.toString())
        binding.editWeight.setText(weight.toString())
        binding.editPhone.setText(phone)
        Log.d("EditProfileFragment", "사용자 정보 조회 성공")
    }

    private fun saveUserProfile() {
        val sharedPreferences =
            requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_name", binding.editName.text.toString())
        editor.putString("user_gender", binding.editGender.text.toString())
        editor.putInt("user_height", binding.editHeight.text.toString().replace(" cm", "").toIntOrNull() ?: 0)
        editor.putInt("user_weight", binding.editWeight.text.toString().replace(" kg", "").toIntOrNull() ?: 0)
        editor.putString("user_phone", binding.editPhone.text.toString())
        editor.apply()
        Log.d("EditProfileFragment", "사용자 정보 저장 성공")

        parentFragmentManager.popBackStack()
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