package com.umc_msmg.frontend.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.widget.CalendarView
import java.util.*
import java.text.SimpleDateFormat

class CustomCalendarView(context: Context, attrs: AttributeSet? = null) : CalendarView(context, attrs) {

    private val circlePaint = Paint().apply {
        color = Color.RED // 강조 표시 색상
        style = Paint.Style.FILL
    }
    private val highlightedDates = mutableSetOf<Long>()

    init {
        setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            val date = calendar.timeInMillis
            toggleHighlight(date)
        }
    }

    fun setHighlightedDates(dates: List<Long>) {
        highlightedDates.clear()
        highlightedDates.addAll(dates)
        invalidate()
    }

    private fun toggleHighlight(date: Long) {
        if (highlightedDates.contains(date)) {
            highlightedDates.remove(date)
            Log.d("com.umc_msmg.frontend.ui.view.CustomCalendarView", "remove Highlight")
        } else {
            highlightedDates.add(date)
            Log.d("com.umc_msmg.frontend.ui.view.CustomCalendarView", "add Highlight")
        }
        invalidate()
    }


    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.date
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val monthStart = calendar.timeInMillis
        calendar.add(Calendar.MONTH, 1)
        val nextMonthStart = calendar.timeInMillis

        for (date in highlightedDates) {

            if (date in monthStart..nextMonthStart) {
                val day = Calendar.getInstance()
                day.timeInMillis = date

                val dayFormatted = SimpleDateFormat("d", Locale.getDefault()).format(day.time)

                Log.d("com.umc_msmg.frontend.ui.view.CustomCalendarView", "Highlighted Date: $dayFormatted")
            }

        }
    }
}