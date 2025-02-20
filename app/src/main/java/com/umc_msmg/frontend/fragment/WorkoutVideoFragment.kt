// WorkoutVideoFragment.kt
package com.umc_msmg.frontend.fragment

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.LayoutWorkoutVideoBinding

class WorkoutVideoFragment : Fragment() {
    private var _binding: LayoutWorkoutVideoBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())
    private var workoutType: String? = null
    private var currentVideoIndex = 0
    private var videoList: List<VideoInfo> = emptyList()
    private var timeCount = 0
    private var exerciseCount = 0
    private var repeatCount = 0

    data class VideoInfo(
        val resourceId: Int,
        val repeatTimes: Int,
        val maxCount: Int,
        val countInterval: Int,
        val isTimeCount: Boolean,
        val showSetNumber: Boolean,
        val title: String
    )



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutWorkoutVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workoutType = arguments?.getString("workoutType")
        binding.exerciseTitle.text = workoutType ?: "운동"
        binding.exerciseCompleBtn.visibility = View.GONE

        setupVideoList()
        setupVideoPlayer()

        binding.exerciseCompleBtn.setOnClickListener {
            showExerciseCompleteMessage()
        }
    }

    private fun setupVideoList() {
        videoList = when (workoutType) {
            "유산소" -> listOf(
                VideoInfo(R.raw.low_cardio_brisk_walking, 3, 180, 1, true, false, "빠르게 걷기")
            )
            "근력" -> listOf(
                VideoInfo(R.raw.low_slow_chair_stand_ups, 3, 10, 5, false, true, "의자에서 천천히 일어나기"),
                VideoInfo(R.raw.low_strength_heel_raises, 1, 12, 4, false, false, "발뒤꿈치 올리기"),
                VideoInfo(R.raw.low_strength_leg_raises, 1, 8, 8, false, false, "다리 차올리기"),
                VideoInfo(R.raw.low_side_leg_raises, 1, 8, 5, false, false, "다리 옆으로 올리기")
            )
            else -> emptyList()
        }
    }



    private fun setupVideoPlayer() {
        if (videoList.isEmpty()) {
            binding.exerciseCompleBtn.visibility = View.VISIBLE
            return
        }

        val currentVideo = videoList[currentVideoIndex]
        val videoPath = "android.resource://${requireActivity().packageName}/${currentVideo.resourceId}"

        binding.exerciseVideo.apply {
            setVideoURI(Uri.parse(videoPath))
            setMediaController(MediaController(context).apply { setAnchorView(this@apply) })
            setOnCompletionListener { onVideoComplete() }
            setOnPreparedListener { mp ->
                mp.isLooping = false
                adjustVideoSize()
            }
            start()
        }

        resetCounters()
        updateUI(currentVideo)
        startCounting(currentVideo)
    }

    private fun adjustVideoSize() {
        binding.exerciseVideo.post {
            val videoView = binding.exerciseVideo
            val parentWidth = (videoView.parent as View).width
            val parentHeight = (videoView.parent as View).height
            val videoWidth = videoView.width
            val videoHeight = videoView.height
            val aspectRatio = videoWidth.toFloat() / videoHeight.toFloat()
            val newWidth = (parentHeight * aspectRatio).toInt()

            val params = videoView.layoutParams as ConstraintLayout.LayoutParams
            params.width = newWidth
            params.height = parentHeight
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            videoView.layoutParams = params
        }
    }

    private fun resetCounters() {
        timeCount = 0
        exerciseCount = 0
        repeatCount = 0
    }

    private fun updateUI(video: VideoInfo) {
        binding.exerciseTitle.text = video.title
        binding.exerciseSetNumber.visibility = if (video.showSetNumber) View.VISIBLE else View.GONE
        if (video.showSetNumber) {
            binding.exerciseSetNumber.text = "${repeatCount + 1} 세트"
        }
    }

    private fun startCounting(video: VideoInfo) {
        handler.post(object : Runnable {
            override fun run() {
                if (video.isTimeCount) {
                    timeCount++
                    binding.countText.text = "$timeCount 초"
                } else {
                    if (timeCount % video.countInterval == 0) {
                        exerciseCount++
                        binding.countText.text = "$exerciseCount / ${video.maxCount}"
                    }
                    timeCount++
                }

                if ((video.isTimeCount && timeCount < video.maxCount) || (!video.isTimeCount && exerciseCount < video.maxCount)) {
                    handler.postDelayed(this, 1000)
                } else {
                    repeatCount++
                    if (repeatCount < video.repeatTimes) {
                        timeCount = 0
                        exerciseCount = 0
                        updateUI(video)
                        binding.exerciseVideo.start()
                        handler.post(this)
                    }
                }
            }
        })
    }

    private fun onVideoComplete() {
        currentVideoIndex++
        if (currentVideoIndex < videoList.size) {
            setupVideoPlayer()
        } else {
            binding.exerciseCompleBtn.visibility = View.VISIBLE
        }
    }

    private fun showExerciseCompleteMessage() {
        // 운동 완료 로직
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.exerciseVideo.stopPlayback()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
