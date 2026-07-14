package com.umc_msmg.frontend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umc_msmg.frontend.databinding.FragmentSignUpTermsAgreementBinding

class SignUpTermsAgreementFragment : Fragment() {
    private var _binding: FragmentSignUpTermsAgreementBinding? = null
    private val binding get() = _binding!!

    private var isRequiredAgreed = false
    private var isOptionalAgreed = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignUpTermsAgreementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        updateConfirmButton()
    }

    private fun setupClickListeners() {
        binding.tvAgreeAll.setOnClickListener {
            val newState = !(isRequiredAgreed && isOptionalAgreed)
            updateAllAgreementStates(newState, newState)
        }

        binding.tvAgreeRequired.setOnClickListener {
            isRequiredAgreed = !isRequiredAgreed
            it.isSelected = isRequiredAgreed
            updateAllAgreeState()
            updateConfirmButton()
        }

        binding.tvAgreeOptional.setOnClickListener {
            isOptionalAgreed = !isOptionalAgreed
            it.isSelected = isOptionalAgreed
            updateAllAgreeState()
        }

        binding.btnConfirm.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SignUpBasicInfoFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun updateAgreementStates(required: Boolean, optional: Boolean) {
        isRequiredAgreed = required
        isOptionalAgreed = optional
        binding.tvAgreeRequired.isSelected = required
        binding.tvAgreeOptional.isSelected = optional
    }

    private fun updateAllAgreeState() {
        binding.tvAgreeAll.isSelected = isRequiredAgreed && isOptionalAgreed
    }

    private fun updateConfirmButton() {
        binding.btnConfirm.isEnabled = isRequiredAgreed
    }

    private fun updateAllAgreementStates(required: Boolean, optional: Boolean) {
        updateAgreementStates(required, optional)
        updateAllAgreeState()
        updateConfirmButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
