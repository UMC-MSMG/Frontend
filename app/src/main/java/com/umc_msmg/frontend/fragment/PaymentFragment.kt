package com.umc_msmg.frontend.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.WRAP_CONTENT
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.activity.MainActivity
import com.umc_msmg.frontend.data.BuyProductRequest
import com.umc_msmg.frontend.data.BuyProductResponse
import com.umc_msmg.frontend.data.ShopItem
import com.umc_msmg.frontend.databinding.DialogPaymentBinding
import com.umc_msmg.frontend.databinding.FragmentPaymentBinding
import com.umc_msmg.frontend.interfaces.UserServiceRetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    private lateinit var selectedItem: ShopItem

    companion object {
        private const val ARG_ITEM = "item"

        fun newInstance(item: ShopItem) = PaymentFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_ITEM, item)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedItem = arguments?.getParcelable(ARG_ITEM) ?: ShopItem(1, "Unknown", 0, "")

        binding.itemName.text = selectedItem.name
        binding.itemPrice.text = "${selectedItem.price}원"
        Glide.with(this).load(selectedItem.image).into(binding.itemImage)

        loadMyPoint()

        binding.paymentButton.setOnClickListener {
            showPaymentDialog()
        }

        binding.exerciseButton.setOnClickListener {
            navigateToFragment(WorkoutFragment())
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showPaymentDialog() {
        val dialogBinding = DialogPaymentBinding.inflate(layoutInflater)
        val itemName = selectedItem.name
        val itemPrice = selectedItem.price
        val productId = selectedItem.id

        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val myPoints = sharedPreferences.getInt("user_point", 0)
        val authorization = "Bearer " + sharedPreferences.getString("access_token", null)

        dialogBinding.titleText.text = "${itemName}을/를\n${itemPrice}포인트로 결제하시겠습니까?"
        dialogBinding.confirmButton.text = "결제하기"
        val dialog = Dialog(requireContext()).apply {
            setContentView(dialogBinding.root)
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val displayMetrics = resources.displayMetrics
                val width = displayMetrics.widthPixels
                val margin = (40 * displayMetrics.density).toInt()
                setLayout(width - (margin * 2), WRAP_CONTENT)
            }
        }

        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        if (myPoints < itemPrice) {
            dialogBinding.titleText.text = "잔액이 부족합니다."
            dialogBinding.confirmButton.visibility = View.GONE
        } else {
            dialogBinding.confirmButton.setOnClickListener {
                val buyProductRequest = BuyProductRequest(productId)

                UserServiceRetrofitClient.apiService.buyProduct(authorization, buyProductRequest)
                    .enqueue(object : Callback<BuyProductResponse> {
                        override fun onResponse(call: Call<BuyProductResponse>, response: Response<BuyProductResponse>) {
                            if (response.isSuccessful) {
                                val buyProductResponse = response.body()
                                val updatedPoint = buyProductResponse?.updatedPoint ?: 0

                                val editor = sharedPreferences.edit()
                                editor.putInt("user_point", updatedPoint)
                                editor.apply()
                                Log.d("PaymentFragment", "포인트 변경 $updatedPoint")

                                dialogBinding.titleText.text = "${itemName}이/가\n결제되었습니다."
                                dialogBinding.confirmButton.text = "내 상점 바로가기"
                                dialogBinding.confirmButton.setOnClickListener {
                                    val activity = requireActivity() as MainActivity
                                    activity.loadUserInfoAndSteps()

                                    val myShopFragment = MyShopFragment()
                                    parentFragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, myShopFragment)
                                        .addToBackStack(null)
                                        .commit()
                                    dialog.dismiss()
                                }
                            } else {
                                Log.e("PaymentFragment", "아이템 구매 실패: ${response.code()}")
                                Toast.makeText(context, "아이템 구매에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                        }

                        override fun onFailure(call: Call<BuyProductResponse>, t: Throwable) {
                            Log.e("PaymentFragment", "아이템 구매 API 호출 실패: ${t.message}")
                            Toast.makeText(context, "API 호출에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    })
            }
        }

        dialog.show()
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
}