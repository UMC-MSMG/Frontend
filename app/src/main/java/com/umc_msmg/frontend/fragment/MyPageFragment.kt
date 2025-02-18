// MyPageFragment.kt
package com.umc_msmg.frontend.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.activity.MainActivity
import com.umc_msmg.frontend.activity.StartActivity
import com.umc_msmg.frontend.databinding.FragmentLoginBinding
import com.umc_msmg.frontend.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        binding.boxLogout.setOnClickListener()
        {

            sharedPreferences.edit().clear().apply()
            clearWebViewData() // 🔥 웹뷰 데이터 삭제 (쿠키, 캐시, 히스토리)
            Log.d("Logout", "✅ 로그아웃 완료 & 웹뷰 데이터 초기화됨")
            val intent = Intent(requireContext(), StartActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        binding.name.text = sharedPreferences.getString("user_name", null)
        binding.gender.text = sharedPreferences.getString("user_gender", null)
        binding.height.text = sharedPreferences.getInt("user_height", 0).toString() + "cm"
        binding.weight.text = sharedPreferences.getInt("user_weight", 0).toString() + "kg"
    }

    private fun clearWebViewData() {
        val webView = WebView(requireContext())

        // 1️⃣ 웹뷰 캐시 삭제
        webView.clearCache(true)

        // 2️⃣ 웹뷰 히스토리 삭제 (뒤로 가기 방지)
        webView.clearHistory()
        // 3️⃣ 쿠키 삭제
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush() // 비동기 삭제
        // 4️⃣ 웹 스토리지 삭제
        WebStorage.getInstance().deleteAllData()

        Log.d("WebView", "✅ WebView 캐시 및 쿠키 삭제 완료")
    }




}
