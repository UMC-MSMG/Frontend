// CountManager.kt
package com.umc_msmg.frontend.utils

import android.os.Handler
import android.os.Looper
import com.umc_msmg.frontend.fragment.WorkoutVideoFragment

class CountManager(private val onCountUpdate: (Int, Int) -> Unit) {
    private val handler = Handler(Looper.getMainLooper())
    private var timeCount = 0
    private var exerciseCount = 0
    private var repeatCount = 0

    fun startCounting(video: WorkoutVideoFragment.VideoInfo) {
        handler.post(object : Runnable {
            override fun run() {
                if (video.isTimeCount) {
                    timeCount++
                    onCountUpdate(timeCount, video.maxCount)
                } else {
                    if (timeCount % video.countInterval == 0) {
                        exerciseCount++
                        onCountUpdate(exerciseCount, video.maxCount)
                    }
                    timeCount++
                }

                if ((video.isTimeCount && timeCount < video.maxCount) || (!video.isTimeCount && exerciseCount < video.maxCount)) {
                    handler.postDelayed(this, 1000)
                } else {
                    repeatCount++
                    if (repeatCount < video.repeatTimes) {
                        resetCounters()
                        handler.post(this)
                    }
                }
            }
        })
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
