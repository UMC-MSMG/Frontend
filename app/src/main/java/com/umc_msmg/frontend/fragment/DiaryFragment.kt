package com.umc_msmg.frontend.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.data.WeeklyExerciseSummary
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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

    private fun updateWeeklyCheckboxes(summary: WeeklyExerciseSummary) {
        binding.calendarCheckMonday.setImageResource(if (summary.monday) R.drawable.calendar_checked else R.drawable.calendar_unchecked)
        binding.calendarCheckTuesday.setImageResource(if (summary.tuesday) R.drawable.calendar_checked else R.drawable.calendar_unchecked)
        binding.calendarCheckWednesday.setImageResource(if (summary.wednesday) R.drawable.calendar_checked else R.drawable.calendar_unchecked)
        binding.calendarCheckThursday.setImageResource(if (summary.thursday) R.drawable.calendar_checked else R.drawable.calendar_unchecked)
        binding.calendarCheckFriday.setImageResource(if (summary.friday) R.drawable.calendar_checked else R.drawable.calendar_unchecked)
        binding.calendarCheckSaturday.setImageResource(if (summary.saturday) R.drawable.calendar_checked else R.drawable.calendar_unchecked)
        binding.calendarCheckSunday.setImageResource(if (summary.sunday) R.drawable.calendar_checked else R.drawable.calendar_unchecked)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}