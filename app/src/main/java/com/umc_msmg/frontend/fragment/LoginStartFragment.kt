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
import com.umc_msmg.frontend.databinding.FragmentLoginStartBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class LoginStartFragment : Fragment() {
    private var _binding: FragmentLoginStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkLoginStatus()

        binding.btnKakaoLogin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, KakaoLoginFragment())
                .commit()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun checkLoginStatus() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("access_token", null)


        if (!accessToken.isNullOrEmpty()) {
            Log.e("!!!!", "기록 있음")
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        else
        {
            Log.e("!!!!", "기록 없음")
        }
    }
}
