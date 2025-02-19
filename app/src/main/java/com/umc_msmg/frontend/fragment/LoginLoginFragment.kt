// LoginStartFragment.kt
package com.umc_msmg.frontend.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.SignUpStartFragment
import com.umc_msmg.frontend.SignUpTermsAgreementFragment
import com.umc_msmg.frontend.activity.MainActivity
import com.umc_msmg.frontend.databinding.FragmentLoginLoginBinding
import com.umc_msmg.frontend.databinding.FragmentLoginStartBinding
import com.umc_msmg.frontend.interfaces.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class LoginLoginFragment : Fragment() {
    private var _binding: FragmentLoginLoginBinding? = null
    private val binding get() = _binding!!

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


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun sendCode()
    {
        if (!binding.et1.toString().isNullOrEmpty())
        {
            val response = RetrofitClient.loginService.sendLogin(binding.et1.toString())
            Log.e("SharedPreferences", response.toString())
        }
    }
}
