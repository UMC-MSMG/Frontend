package com.umc_msmg.frontend.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.LayoutStepperBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StepperFragment : Fragment() {
    private var _binding: LayoutStepperBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var barChart : BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutStepperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        barChart = binding.barChart
        setChart()
        setData()
        binding.area1.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StepperStepperFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.area2.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ShopFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences = requireContext().getSharedPreferences("StepPrefs", Context.MODE_PRIVATE)
        val count = sharedPreferences.getInt("stepCount", 0)
        binding.a1TopTv2.text = count.toString()
    }

    private fun setChart() {

        barChart.description.isEnabled = false
        barChart.axisLeft.setDrawGridLines(false)
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)
        barChart.setDrawValueAboveBar(false)
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawAxisLine(false) // X축 선 제거
        barChart.axisRight.isEnabled = false
        barChart.axisLeft.isEnabled = false
        barChart.animateY(100)
        barChart.legend.isEnabled = false
        barChart.description.isEnabled = false
        barChart.axisLeft.axisMinimum = 0f

    }

        private fun setData()
    {
        val today = getTodayDayOfWeek()
        val stepsList = mutableListOf<Int>()
        var total = 0
        for (day in 1..7) {
            if (day <= today) {
                stepsList.add(getStep(day, today))
                total += getStep(day, today)
            } else {
                stepsList.add(0)  // 나머지 칸을 0으로 채움
            }
        }
        val days = listOf("월", "화", "수", "목", "금", "토", "일")

        val entries = ArrayList<BarEntry>()
        for (i in stepsList.indices) {
            stepsList[i]?.let { BarEntry(i.toFloat(), it.toFloat()) }?.let { entries.add(it) }
        }

        val barDataSet = BarDataSet(entries, "걸음 수")
        barDataSet.color = Color.parseColor("#6DD099")  // 바 색상
        barDataSet.valueTextColor = Color.BLACK         // 값 색상
        barDataSet.valueTextSize = 12f                  // 값 크기
        barDataSet.setDrawValues(false)
        val data = BarData(barDataSet)
        data.barWidth = 0.5f  // 바의 두께 조정
        barChart.data = data

        // X축에 요일 표시
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(days)

        // 차트 갱신
        barChart.invalidate()

        binding.a1BottomTv1.text = "이번주의 평균 걸음 수는\n"+total/today+" 걸음이에요"
    }

    fun getStep(day: Int, target: Int): Int {
        if(day == target)
        {
            sharedPreferences = requireContext().getSharedPreferences("StepPrefs", Context.MODE_PRIVATE)
            return sharedPreferences.getInt("stepCount",0)
        }
        else
        {
            val requestday = LocalDate.now().minusDays(day-1.toLong())
            val requestDate = requestday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            //이부분에 서버통신 추가
            return 10
        }
    }

    fun getTodayDayOfWeek(): Int {
        val today = LocalDate.now()
        return when (today.dayOfWeek) {
            DayOfWeek.MONDAY -> 1
            DayOfWeek.TUESDAY -> 2
            DayOfWeek.WEDNESDAY -> 3
            DayOfWeek.THURSDAY -> 4
            DayOfWeek.FRIDAY -> 5
            DayOfWeek.SATURDAY -> 6
            DayOfWeek.SUNDAY -> 7
        }
    }

}