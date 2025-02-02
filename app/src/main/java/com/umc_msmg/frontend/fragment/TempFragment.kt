package com.umc_msmg.frontend.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.LayoutTempBinding

class TempFragment : Fragment() {

    private var _binding: LayoutTempBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutTempBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button1.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WorkoutFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.button2.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WalkOutFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}