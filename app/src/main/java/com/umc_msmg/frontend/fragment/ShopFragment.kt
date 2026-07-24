package com.umc_msmg.frontend.fragment

import android.content.Context
import com.umc_msmg.frontend.adapter.ShopAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.data.GetProductsResponse
import com.umc_msmg.frontend.data.ShopItem
import com.umc_msmg.frontend.databinding.FragmentShopBinding
import com.umc_msmg.frontend.interfaces.UserServiceRetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        loadMyPoint()
        loadProducts()

        binding.btnBack.setOnClickListener {
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

    private fun loadMyPoint() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val myPoint = sharedPreferences.getInt("user_point", 0)
        binding.myPoint.text = myPoint.toString()
    }

    private fun loadProducts() {
        UserServiceRetrofitClient.apiService.getProducts()
            .enqueue(object : Callback<GetProductsResponse> {
                override fun onResponse(
                    call: Call<GetProductsResponse>,
                    response: Response<GetProductsResponse>
                ) {
                    Log.d("ShopFragment", "API 요청 URL: ${call.request().url}")
                    if (response.isSuccessful) {
                        val products = response.body()?.products ?: emptyList()
                        val shopAdapter = ShopAdapter { selectedItem: ShopItem ->
                            parentFragmentManager.beginTransaction()
                                .replace(
                                    R.id.fragment_container,
                                    PaymentFragment.newInstance(selectedItem)
                                )
                                .addToBackStack(null)
                                .commit()
                        }

                        binding.shopRecyclerView.apply {
                            adapter = shopAdapter
                            layoutManager = LinearLayoutManager(context)
                        }
                        shopAdapter.submitList(products)
                        Log.d("ShopFragment", "전체 기프티콘 목록 api 호출 성공")
                    } else {
                        Log.e("ShopFragment", "전체 기프티콘 목록 api 호출 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<GetProductsResponse>, t: Throwable) {
                    Log.e("ShopFragment", "전체 기프티콘 목록 API 호출 실패: ${t.message}")
                }
            })
    }
}
