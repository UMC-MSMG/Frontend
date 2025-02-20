package com.umc_msmg.frontend.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.activity.MainActivity
import com.umc_msmg.frontend.activity.StartActivity
import com.umc_msmg.frontend.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        val view = binding.root

        loadUserProfile()

        binding.boxExercise.setOnClickListener {
            binding.switchExercise.isChecked = !binding.switchExercise.isChecked
        }

        binding.boxMedicine.setOnClickListener {
            binding.switchMedicine.isChecked = !binding.switchMedicine.isChecked
        }

        binding.boxEditProfile.setOnClickListener {
            navigateToFragment(EditProfileFragment())
        }

        binding.boxEditMedicine.setOnClickListener {
            navigateToFragment(EditMedicineFragment())
        }

        binding.boxPoint.setOnClickListener {
            navigateToFragment(MyShopFragment())
        }

        binding.boxLevel.setOnClickListener {
            navigateToFragment(EditLevelFragment())
        }

        binding.boxTerms.setOnClickListener {
            navigateToFragment(TermsFragment())
        }

        binding.logout.setOnClickListener {
            logout()
            val intent = Intent(requireContext(), StartActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }


        return view
    }

    private fun loadUserProfile() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("user_name", "사람1")
        val gender = sharedPreferences.getString("user_gender", "남성")
        val height = sharedPreferences.getInt("user_height", 156)
        val weight = sharedPreferences.getInt("user_weight", 56)
        val difficulty = sharedPreferences.getString("difficulty", "중") ?: "중"

        binding.name.text = name
        binding.gender.text = gender
        binding.height.text = "$height cm"
        binding.weight.text = "$weight kg"
        Log.d("MyPageFragment", "사용자 정보 조회 성공, 난이도: $difficulty")
    }


    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun logout() {
        var sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        sharedPreferences = requireContext().getSharedPreferences("LP", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        clearWebViewData()
        Log.d("Logout", "로그아웃 완료 / 웹뷰 데이터 초기화됨")
    }

    private fun clearWebViewData() {
        val webView = WebView(requireContext())
        webView.clearCache(true)
        webView.clearHistory()
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
        WebStorage.getInstance().deleteAllData()
    }
}