package com.umc_msmg.frontend.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.SignUpFragment
import com.umc_msmg.frontend.databinding.LayoutStepperBinding

class StepperFragment : Fragment() {
    private var _binding: LayoutStepperBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutStepperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.area1.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StepperStepperFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.area2.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PointFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.area3.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, InfoFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}