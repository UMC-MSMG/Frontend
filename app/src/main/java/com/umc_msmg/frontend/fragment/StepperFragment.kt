package com.umc_msmg.frontend.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.data.DailySteps
import com.umc_msmg.frontend.databinding.LayoutStepperBinding
import com.umc_msmg.frontend.interfaces.RetrofitClient
import com.umc_msmg.frontend.interfaces.UserServiceRetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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



        binding.area1.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StepperStepperFragment(), "StepTag")
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


        lifecycleScope.launch(Dispatchers.Main) {
            setData()
            binding.a1TopTv2.text = getStep(getTodayDayOfWeek()).toString()
        }
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

        private suspend fun setData()
        {
        val today = getTodayDayOfWeek()
        val stepsList = mutableListOf<Int>()
        var total = 0
        for (day in 1..7) {
            if (day <= today) {
                stepsList.add(getStep(day))
                total += getStep(day)
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


    suspend fun getStep(day: Int): Int {
        var steps = 0
        return withContext(Dispatchers.IO) {
                val requestDay = LocalDate.now().minusDays((5-day.toLong()))
                val requestDate = requestDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val newPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = "Bearer " + newPreferences.getString("access_token", null)

                try {
                    val response = RetrofitClient.loginService.getSteps(token, requestDate)
                    if (response.isSuccessful) {
                        val dailySteps = response.body()
                        steps = dailySteps?.steps ?: 0
                    } else {
                        Log.e("MainActivity", "걸음수 가져오기 실패: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "걸음 수 API 호출 실패: ${e.message}")
                }
            Log.e("OHNO", steps.toString())
            steps // withContext의 결과로 반환
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