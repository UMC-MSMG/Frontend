package com.umc_msmg.frontend.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.SignUpAddInfoFragment
import com.umc_msmg.frontend.SignUpTermsAgreementFragment
import com.umc_msmg.frontend.activity.MainActivity
import com.umc_msmg.frontend.interfaces.LoginResponse
import com.umc_msmg.frontend.interfaces.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log

class KakaoLoginFragment : Fragment() {
    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.web_view, container, false)
        webView = view.findViewById(R.id.webView)
        Log.e("9999","정상실행")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //clearWebViewData()
        openKakaoLogin()
    }

    private fun openKakaoLogin() {
        webView.settings.javaScriptEnabled = true // JavaScript 활성화
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()

                // 로그인 성공 후 백엔드에서 제공한 콜백 URL을 감지
                if (url.startsWith("http://43.202.104.127:3000/api/auth/kakao/callback?")) {
                    val authCode = Uri.parse(url).getQueryParameter("code")
                    Log.e("DDDDDDD", authCode.toString())
                    if (!authCode.isNullOrEmpty()) {
                        fetchLoginResult(authCode) // 로그인 결과 요청
                        webView.visibility = View.GONE // 로그인 완료 후 WebView 숨기기
                    }
                    return true
                }
                return false
            }
        }
        webView.loadUrl("http://43.202.104.127:3000/api/auth/login/kakao")
    }

    private fun fetchLoginResult(authCode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.loginService.fetchLoginResult(authCode)
                if (response.isSuccessful) {
                    response.body()?.let { handleLoginResponse(it) }
                } else {
                    Log.e("KakaoLogin", "Failed to fetch login result: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("KakaoLogin", "Error fetching login result: ${e.message}")
            }
        }
    }

    private fun handleLoginResponse(loginResponse: LoginResponse) {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putInt("id", loginResponse.user.id)
            .putString("user_name", loginResponse.user.name)
            .putString("user_image", loginResponse.user.image)
            .putString("access_token", loginResponse.accessToken)
            .putString("refresh_token", loginResponse.refreshToken)
            .putBoolean("new", loginResponse.newUser)
            .apply()

        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), "로그인 성공: ${loginResponse.user.name}", Toast.LENGTH_SHORT).show()
        }
        logAllPreferences()
        if(loginResponse.newUser) {
            //이미 회원인경우
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        else {
            //회원가입이 필요한경우
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SignUpTermsAgreementFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun logAllPreferences() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val allEntries: Map<String, *> = sharedPreferences.all

        for ((key, value) in allEntries) {
            Log.d("SharedPreferences", "Key: $key, Value: $value")
        }
    }

    private fun logout() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

    private fun checkLoginStatus() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("access_token", null)

        if (!accessToken.isNullOrEmpty()) {
            val userName = sharedPreferences.getString("user_name", "User")
            val userImage = sharedPreferences.getString("user_image", "")
        }
    }
}
