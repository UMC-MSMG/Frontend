package com.umc_msmg.frontend.fragment

import com.umc_msmg.frontend.adapter.ShopAdapter
import com.umc_msmg.frontend.data.ShopItem
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.FragmentShopBinding

class ShopFragment : Fragment() {
    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shopAdapter = ShopAdapter { selectedItem ->
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.fragment_container, PaymentFragment.newInstance(selectedItem))
                .addToBackStack(null)
                .commit()
        }

        binding.shopRecyclerView.apply {
            adapter = shopAdapter
            layoutManager = LinearLayoutManager(context)
        }

        val items = listOf(
            ShopItem("레쓰비", "GS25", 800),
            ShopItem("싼커피", "CU", 1000),
            ShopItem("좀비싼커피", "CU", 3000),
            ShopItem("비타오백", "", 500),
            ShopItem("비싼게르마늄팔찌", "", 20000),
            ShopItem("빼빼로", "노인정에서 빼빼로게임하면 나도 슈퍼인싸", 1000),
        )

        shopAdapter.submitList(items)

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
