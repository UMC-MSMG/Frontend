package com.umc_msmg.frontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.umc_msmg.frontend.databinding.FragmentSignUpBasicInfoBinding

class SignUpBasicInfoFragment : Fragment() {
    private var _binding: FragmentSignUpBasicInfoBinding? = null
    private val binding get() = _binding!!
    private var currentStep = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBasicInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvNext.setOnClickListener {
            when (currentStep) {
                1 -> showGenderInput()
                2 -> showBirthdateInput()
                3 -> showPhoneInput()
                4 -> finishSignUp()
            }
        }
    }

    private fun showGenderInput() {
        binding.layoutName.visibility = View.GONE
        binding.layoutGender.visibility = View.VISIBLE
        currentStep = 2
    }

    private fun showBirthdateInput() {
        binding.layoutGender.visibility = View.GONE
        binding.layoutBirthdate.visibility = View.VISIBLE
        currentStep = 3
    }

    private fun showPhoneInput() {
        binding.layoutBirthdate.visibility = View.GONE
        binding.layoutPhone.visibility = View.VISIBLE
        currentStep = 4
    }

    private fun finishSignUp() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SignUpAddInfoFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
