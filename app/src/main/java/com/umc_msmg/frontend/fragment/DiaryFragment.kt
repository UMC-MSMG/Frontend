package com.umc_msmg.frontend.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.umc_msmg.frontend.databinding.FragmentDiaryBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val today = Calendar.getInstance().time
        val formatter = SimpleDateFormat("MM / dd / yyyy", Locale.getDefault())
        val formattedDate = formatter.format(today)
        binding.todayDate.text = formattedDate

        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val difficulty = sharedPreferences.getString("difficulty", "MEDIUM")
        binding.difficultyText.text = if (difficulty == "HIGH") "상" else if (difficulty == "MEDIUM") "중" else "하"
        val summary = sharedPreferences.getString("ai_data", "")
        binding.summaryText.text = summary
        Log.d("DiaryFragment", "ai_data: $summary")

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}