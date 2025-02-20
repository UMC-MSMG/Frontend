// VideoManager.kt
package com.umc_msmg.frontend.utils

import android.media.MediaPlayer
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.FragmentActivity
import com.umc_msmg.frontend.fragment.WorkoutVideoFragment

class VideoManager(private val activity: FragmentActivity, private val videoView: VideoView) {
    private var currentVideoIndex = 0
    private var videoList: List<WorkoutVideoFragment.VideoInfo> = emptyList()

    fun setVideoList(list: List<WorkoutVideoFragment.VideoInfo>) {
        videoList = list
        currentVideoIndex = 0
    }

    fun setupVideoPlayer(onCompletion: () -> Unit) {
        if (videoList.isEmpty()) return

        val currentVideo = videoList[currentVideoIndex]
        val videoPath = "android.resource://${activity.packageName}/${currentVideo.resourceId}"

        videoView.apply {
            setVideoURI(Uri.parse(videoPath))
            setMediaController(MediaController(context).apply { setAnchorView(this@apply) })
            setOnCompletionListener { onCompletion() }
            setOnPreparedListener { mp ->
                mp.isLooping = false
                adjustVideoSize(mp)
            }
            start()
        }
    }

    private fun adjustVideoSize(mediaPlayer: MediaPlayer) {
        val videoWidth = mediaPlayer.videoWidth
        val videoHeight = mediaPlayer.videoHeight
        val parentWidth = (videoView.parent as View).width
        val parentHeight = (videoView.parent as View).height

        val aspectRatio = videoWidth.toFloat() / videoHeight.toFloat()
        val newWidth = (parentHeight * aspectRatio).toInt()

        val layoutParams = videoView.layoutParams
        layoutParams.height = parentHeight
        layoutParams.width = newWidth

        if (newWidth > parentWidth) {
            layoutParams.width = parentWidth
            layoutParams.height = (parentWidth / aspectRatio).toInt()
        }

        videoView.layoutParams = layoutParams

        // 비디오를 중앙에 배치
        (videoView.parent as? FrameLayout)?.let { parent ->
            val params = FrameLayout.LayoutParams(layoutParams)
            params.gravity = Gravity.CENTER
            videoView.layoutParams = params
        }
    }

    fun getCurrentVideo(): WorkoutVideoFragment.VideoInfo? {
        return if (currentVideoIndex < videoList.size) videoList[currentVideoIndex] else null
    }

    fun moveToNextVideo(): Boolean {
        currentVideoIndex++
        return currentVideoIndex < videoList.size
    }
}
