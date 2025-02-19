// LoginStartFragment.kt
package com.umc_msmg.frontend.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.SignUpStartFragment
import com.umc_msmg.frontend.SignUpTermsAgreementFragment
import com.umc_msmg.frontend.activity.MainActivity
import com.umc_msmg.frontend.databinding.FragmentLoginLoginBinding
import com.umc_msmg.frontend.databinding.FragmentLoginStartBinding
import com.umc_msmg.frontend.interfaces.RetrofitClient
import com.umc_msmg.frontend.interfaces.normalLoginData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class LoginLoginFragment : Fragment() {
    private var _binding: FragmentLoginLoginBinding? = null
    private val binding get() = _binding!!
    private var phone = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.verifyBtn.setOnClickListener() {
            CoroutineScope(Dispatchers.IO).launch {
                sendCode()
            }
        }

        binding.et2.isEnabled = false

        binding.btnLogin.setOnClickListener() {

            CoroutineScope(Dispatchers.IO).launch {
                checkCode()
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun sendCode()
    {
        Log.e("!!", "여기요")
        phone = binding.et1.text.toString()
        if (!phone.isNullOrEmpty())
        {
            val response = RetrofitClient.loginService.sendLogin(binding.et1.text.toString()).code()
            if(response == 200)
            {
                withContext(Dispatchers.Main) {
                    binding.verifyAlarm1.visibility = GONE
                    binding.et1.isEnabled = false
                    binding.et2.isEnabled = true
                }

            }
            else
            {
                withContext(Dispatchers.Main) {
                    binding.verifyAlarm1.visibility = VISIBLE
                }


            }
        }
    }

    private suspend fun checkCode() {
        try {
            val response = RetrofitClient.loginService.checkLogin(
                phone,
                binding.et2.text.toString().toInt()
            )

            if (response.isSuccessful) {
                val responseBody = response.body()

                responseBody?.let { data ->
                    val sharedPreferences =
                        requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit()
                        .putString("user_id", data.userId.toString())
                        .putString("access_token", data.accessToken)
                        .putString("refresh_token", data.refreshToken)
                        .apply()

                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } ?: run {
                    Log.e("checkCode", "응답 바디가 null입니다.")
                }
            } else {
                Log.e("checkCode", "응답 실패: ${response.code()}")

                binding.verifyAlarm2.visibility = VISIBLE
            }

        } catch (e: Exception) {
            Log.e("checkCode", "에러 발생: ${e.localizedMessage}")
            binding.verifyAlarm2.visibility = VISIBLE
        }
    }

}
