package com.umc_msmg.frontend.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class WaveformView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val paint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 6f
        style = Paint.Style.FILL
    }

    private var waveform = List(100) { 0.1f }

    fun updateWaveform(newWaveform: List<Float>) {
        waveform = newWaveform
        postInvalidate() // ✅ UI 스레드에서 안전하게 업데이트
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val widthStep = width / waveform.size.toFloat()
        val centerY = height / 2f

        for (i in waveform.indices) {
            val x = i * widthStep
            val y = centerY - (waveform[i] * centerY * 2) // ✅ 더 강한 효과 적용
            canvas.drawLine(x, centerY, x, y, paint)
        }
    }
}
