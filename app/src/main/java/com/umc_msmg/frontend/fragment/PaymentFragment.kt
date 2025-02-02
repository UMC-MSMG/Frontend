package com.umc_msmg.frontend.fragment

import ShopItem
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.umc_msmg.frontend.databinding.DialogPaymentBinding
import com.umc_msmg.frontend.databinding.FragmentPaymentBinding

class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_ITEM_NAME = "item_name"
        private const val ARG_ITEM_OFFICE = "item_office"
        private const val ARG_ITEM_PRICE = "item_price"

        fun newInstance(item: ShopItem) = PaymentFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ITEM_NAME, item.name)
                putString(ARG_ITEM_OFFICE, item.purchasingOffice)
                putInt(ARG_ITEM_PRICE, item.price)
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

        arguments?.let { args ->
            binding.itemName.text = args.getString(ARG_ITEM_NAME)
            binding.itemOffice.text = args.getString(ARG_ITEM_OFFICE)
            binding.itemPrice.text = "${args.getInt(ARG_ITEM_PRICE)}원"
        }

        binding.paymentButton.setOnClickListener {
            showPaymentDialog()
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showPaymentDialog() {
        val dialogBinding = DialogPaymentBinding.inflate(layoutInflater)
        dialogBinding.titleText.text = "${binding.itemName.text} (${binding.itemOffice.text})를\n${binding.itemPrice.text.toString().replace("원", "").trim()}포인트로\n결제하시겠습니까?"
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

        dialogBinding.confirmButton.setOnClickListener {
            dialogBinding.titleText.text = "${binding.itemName.text} (${binding.itemPrice.text})가\n결제되었습니다."
            dialogBinding.confirmButton.text = "내 상점 바로가기"
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}