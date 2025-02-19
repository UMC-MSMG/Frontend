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
        val view = binding.root

        loadUserProfile()

        binding.cancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.save.setOnClickListener {
            saveUserProfile()
        }

        binding.editPhone.formatPhoneNumber()
        binding.editHeight.formatHeight()
        binding.editWeight.formatWeight()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editPhone.formatPhoneNumber()
        binding.editHeight.formatHeight()
        binding.editWeight.formatWeight()
    }

    private fun loadUserProfile() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "")
        val gender = sharedPreferences.getString("gender", "")
        val height = sharedPreferences.getInt("height", 0)
        val weight = sharedPreferences.getInt("weight", 0)
        val phone = sharedPreferences.getString("phone", "")

        binding.editName.setText(name)
        binding.editGender.setText(gender)
        binding.editHeight.setText(height.toString())
        binding.editWeight.setText(weight.toString())
        binding.editPhone.setText(phone)
        Log.d("EditProfileFragment", "사용자 정보 조회 성공")
    }

    private fun saveUserProfile() {
        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("name", binding.editName.text.toString())
        editor.putString("gender", binding.editGender.text.toString())
        editor.putInt("height", binding.editHeight.text.toString().replace(" cm", "").toIntOrNull() ?: 0)
        editor.putInt("weight", binding.editWeight.text.toString().replace(" cm", "").toIntOrNull() ?: 0)
        editor.putString("phone", binding.editPhone.text.toString())
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

fun TextInputEditText.formatHeight() {
    addTextChangedListener(object : TextWatcher {
        private var editing: Boolean = false
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            if (editing) return
            editing = true
            val value = s.toString()
            if (value.isNotEmpty()) {
                val formattedValue = "$value cm"
                s.clear()
                s.append(formattedValue)
            }
            editing = false
        }
    })
}

fun TextInputEditText.formatWeight() {
    addTextChangedListener(object : TextWatcher {
        private var editing: Boolean = false
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            if (editing) return
            editing = true
            val value = s.toString()
            if (value.isNotEmpty()) {
                val formattedValue = "$value kg"
                s.clear()
                s.append(formattedValue)
            }
            editing = false
        }
    })
}