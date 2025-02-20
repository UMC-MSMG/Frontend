// CountManager.kt
package com.umc_msmg.frontend.utils

import android.os.Handler
import android.os.Looper
import com.umc_msmg.frontend.fragment.WorkoutVideoFragment
import java.util.Locale

class CountManager(private val onCountUpdate: (String, Int) -> Unit) {
    private val handler = Handler(Looper.getMainLooper())
    private var timeCount = 0
    private var exerciseCount = 0
    private var repeatCount = 0

    fun startCounting(video: WorkoutVideoFragment.VideoInfo, videoDuration: Int) {
        resetCounters()

        val countInterval: Int = if (!video.isTimeCount && video.maxCount > 0) {
            videoDuration / (video.maxCount + 1) // +1 to account for the initial delay
        } else {
            1000
        }

        handler.postDelayed({
            onCountUpdate("0", video.maxCount) // 초기 카운트 표시
            handler.post(object : Runnable {
                override fun run() {
                    if (video.isTimeCount) {
                        onCountUpdate(formatTime(timeCount), video.maxCount)
                        timeCount++
                    } else {
                        if (timeCount % countInterval == 0 && exerciseCount < video.maxCount) {
                            exerciseCount++
                            onCountUpdate(exerciseCount.toString(), video.maxCount)
                        }
                        timeCount++
                    }

                    if ((video.isTimeCount && timeCount <= video.maxCount) ||
                        (!video.isTimeCount && exerciseCount < video.maxCount && timeCount < videoDuration)) {
                        handler.postDelayed(this, 1000)
                    } else {
                        repeatCount++
                        if (repeatCount < video.repeatTimes) {
                            resetCounters()
                            handler.postDelayed(this, countInterval.toLong())
                        }
                    }
                }
            })
        }, video.startDelay * 1000L) // startDelay를 사용하여 초기 딜레이 설정
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        return String.format(Locale.getDefault(), "%d", minutes)
    }

    fun resetCounters() {
        timeCount = 0
        exerciseCount = 0
    }

    fun stopCounting() {
        handler.removeCallbacksAndMessages(null)
    }

    fun getRepeatCount() = repeatCount
}
