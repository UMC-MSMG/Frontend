// KakaoLoginFragment.kt
package com.umc_msmg.frontend.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umc_msmg.frontend.databinding.FragmentKakaoLoginBinding

class KakaoLoginFragment : Fragment() {

    private var _binding: FragmentKakaoLoginBinding? = null
    private val binding get() = _binding!! // 안전한 언래핑

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKakaoLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 여기에 뷰 초기화 코드 작성 예시:
        // binding.btnLogin.setOnClickListener { ... }
    }

    override fun onDestroyView() {
        _binding = null // 메모리 누수 방지
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = KakaoLoginFragment()
    }
}
