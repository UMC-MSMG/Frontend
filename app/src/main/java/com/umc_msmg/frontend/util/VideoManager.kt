// VideoManager.kt
package com.umc_msmg.frontend.utils

import android.media.MediaPlayer
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.FragmentActivity
import com.umc_msmg.frontend.fragment.WorkoutVideoFragment

// VideoManager.kt
class VideoManager(private val activity: FragmentActivity, private val videoView: VideoView) {
    private var currentVideoIndex = 0
    private var videoList: List<WorkoutVideoFragment.VideoInfo> = emptyList()
    private var currentRepeatCount = 0

    fun setVideoList(list: List<WorkoutVideoFragment.VideoInfo>) {
        videoList = list
        currentVideoIndex = 0
        currentRepeatCount = 0
    }

    fun setupVideoPlayer(onCompletion: () -> Unit) {
        if (videoList.isEmpty()) return

        val currentVideo = videoList[currentVideoIndex]
        val videoPath = "android.resource://${activity.packageName}/${currentVideo.resourceId}"

        videoView.apply {
            setVideoURI(Uri.parse(videoPath))
            setMediaController(MediaController(context).apply { setAnchorView(this@apply) })
            setOnCompletionListener {
                if (currentRepeatCount < currentVideo.repeatTimes - 1) {
                    currentRepeatCount++
                    start()
                } else {
                    currentRepeatCount = 0
                    onCompletion()
                }
            }
            setOnPreparedListener { mp ->
                mp.isLooping = false
            }
            start()
        }
    }


    fun getCurrentVideo(): WorkoutVideoFragment.VideoInfo? {
        return if (currentVideoIndex < videoList.size) videoList[currentVideoIndex] else null
    }

    fun moveToNextVideo(): Boolean {
        currentVideoIndex++
        currentRepeatCount = 0
        return currentVideoIndex < videoList.size
    }
}
