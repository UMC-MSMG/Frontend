package com.umc_msmg.frontend

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
        disableBtn()
    }

    private fun setupClickListeners() {
        binding.tvAgreeAll.setOnClickListener {
            val newState = !(isRequiredAgreed && isOptionalAgreed)
            updateAllAgreementStates(newState, newState)

            if(isRequiredAgreed){enableBtn()}
            else{disableBtn()}
        }

        binding.tvAgreeRequired.setOnClickListener {
            isRequiredAgreed = !isRequiredAgreed
            it.isSelected = isRequiredAgreed
            updateAllAgreeState()
            updateConfirmButton()

            if(isRequiredAgreed){enableBtn()}
            else{disableBtn()}
        }

        binding.tvAgreeOptional.setOnClickListener {
            isOptionalAgreed = !isOptionalAgreed
            it.isSelected = isOptionalAgreed
            updateAllAgreeState()
        }

        binding.btnConfirm.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            if(isOptionalAgreed) {
                sharedPreferences.edit()
                    .putBoolean("agreed", true)
                    .apply()
            }
            else
            {
                sharedPreferences.edit()
                    .putBoolean("agreed", false)
                    .apply()
            }

            val isKakao = sharedPreferences.getBoolean("kakao", false)
            Log.e("롤하고싶다", isKakao.toString())
                if(isKakao == true)
                {

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, KakaoSignupFragment())
                        .commit()
                }
                else
                {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SignUpBasicInfoFragment())
                        .commit()
                }
        }

        binding.root.setOnClickListener()
        {
            if(isRequiredAgreed){enableBtn()}
            else{disableBtn()}
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

    private fun enableBtn()
    {
        binding.btnConfirm.text = "다음"
        binding.btnConfirm.setBackgroundResource(R.drawable.round_corner_primary) // 배경 리소스 변경
        binding.btnConfirm.backgroundTintList =
            context?.let { ContextCompat.getColorStateList(it, R.color.color_primary) } // Tint 변경
        binding.btnConfirm.isEnabled = true
    }

    private fun disableBtn()
    {
        binding.btnConfirm.text = "다음"
        binding.btnConfirm.setBackgroundResource(R.drawable.round_corner_grey) // 배경 리소스 변경
        binding.btnConfirm.backgroundTintList =
            context?.let { ContextCompat.getColorStateList(it, R.color.gray) } // Tint 변경
        binding.btnConfirm.isEnabled = false
    }

}
