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

    private var isAllAgreed = false
    private var isRequiredAgreed = false
    private var isOptionalAgreed = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignUpTermsAgreementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // 전체 동의 버튼 클릭
        binding.tvAgreeAll.setOnClickListener {
            isRequiredAgreed = !isAllAgreed
            isOptionalAgreed = !isAllAgreed
            binding.tvAgreeRequired.isSelected = isRequiredAgreed
            binding.tvAgreeOptional.isSelected = isOptionalAgreed
            updateAgreeStates()
        }

        // 필수 동의 클릭
        binding.tvAgreeRequired.setOnClickListener {
            isRequiredAgreed = !isRequiredAgreed
            it.isSelected = isRequiredAgreed
            updateAgreeStates()
        }

        // 선택 동의 클릭
        binding.tvAgreeOptional.setOnClickListener {
            isOptionalAgreed = !isOptionalAgreed
            it.isSelected = isOptionalAgreed
            updateAgreeStates()
        }

        // 확인 버튼 클릭
        binding.btnConfirm.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SignUpBasicInfoFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun updateAgreeStates() {
        isAllAgreed = isRequiredAgreed && isOptionalAgreed
        binding.tvAgreeAll.isSelected = isAllAgreed
        binding.btnConfirm.isEnabled = isRequiredAgreed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
