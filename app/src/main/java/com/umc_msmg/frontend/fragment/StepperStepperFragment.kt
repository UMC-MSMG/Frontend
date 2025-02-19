package com.umc_msmg.frontend.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.LayoutStepperStepperBinding

class StepperStepperFragment : Fragment() {
    private var _binding: LayoutStepperStepperBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutStepperStepperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTargetText()
        binding.btn.setOnClickListener()
        {
            clearLoc()
            val mapFragment = childFragmentManager.findFragmentById(R.id.map_frame) as? MapFragment
            mapFragment?.removeAllMarkers()
            mapFragment?.getCurrentLocation()
            if (mapFragment == null) {
                Log.e("!!!!!!!!!", "tlqkf!!!!")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // ✅ 중복 추가 방지 (이미 존재하는지 확인)
        if (childFragmentManager.findFragmentById(R.id.map_frame) == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.map_frame, MapFragment(), "MAP")
                .commitAllowingStateLoss() // ✅ 예외 방지 (Activity가 죽었다가 다시 생성되는 경우)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun setTargetText()
    {
        val sharedPreferences =
            requireContext().getSharedPreferences("LP", Context.MODE_PRIVATE)
        val savedTargetLocation = sharedPreferences.getString("targetLocation", "")
        requireActivity().runOnUiThread {
            binding.destTv.text = savedTargetLocation + "에 가면"
        }
    }

    private fun clearLoc()
    {
        val sharedPreferences = requireContext().getSharedPreferences("LP", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply() // ✅ 모든 데이터 삭제
        Log.d("SharedPreferences", "모든 위치 데이터가 삭제되었습니1다.")
    }


    fun arrived()
    {
        //binding.arrived.visibility = VISIBLE
        binding.arrivedTv.visibility = VISIBLE
        binding.arrivedTv1.visibility = VISIBLE
    }

}
