package com.umc_msmg.frontend.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.FragmentMyShopBinding

class MyShopFragment : Fragment() {
    private var _binding: FragmentMyShopBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyShopBinding.inflate(inflater, container, false)

        binding.cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.item1.setOnClickListener {
            navigateToFragment(CouponFragment())
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}