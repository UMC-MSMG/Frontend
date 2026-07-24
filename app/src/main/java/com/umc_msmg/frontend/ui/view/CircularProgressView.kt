package com.umc_msmg.frontend.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CircularProgressView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val outerCirclePaint = Paint().apply {
        color = Color.parseColor("#336DD099")
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val innerCirclePaint = Paint().apply {
        color = Color.parseColor("#6DD099")
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val progressPaint = Paint().apply {
        color = Color.parseColor("#FFFFFF")
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 30f
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        textSize = 60f
        textAlign = Paint.Align.CENTER
    }

    private val rectF = RectF()

    var progress = 75
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (width / 2f)
        val gap = 40f

        canvas.drawCircle(centerX, centerY, radius, outerCirclePaint)

        canvas.drawCircle(centerX, centerY, radius - gap, innerCirclePaint)

        val sweepAngle = -(progress / 100f) * 360
        rectF.set(
            centerX - radius + gap, centerY - radius + gap,
            centerX + radius - gap, centerY + radius - gap
        )
        canvas.drawArc(rectF, -90f, sweepAngle, false, progressPaint)

        canvas.drawText("$progress%", centerX, centerY + (textPaint.textSize / 3), textPaint)
    }
}
