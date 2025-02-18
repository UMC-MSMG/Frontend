package com.umc_msmg.frontend.fragment

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.LayoutWorkoutVideoBinding

class WorkoutVideoFragment : Fragment() {
    private var _binding: LayoutWorkoutVideoBinding? = null
    private val binding get() = _binding!!
    private var playCount = 0
    private var exerciseCount = 0
    private val handler = Handler(Looper.getMainLooper())
    private var setNumber = 1
    private var workoutType: String? = null
    private var exerciseType: String? = null
    private var useSetCounting = false
    private var currentSet = 1
    private var maxSets = 1
    private var timeCount = 0
    private var maxCount = 10
    private var isTimeBasedExercise = false
    private var exerciseDuration = 60

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
        exerciseType = arguments?.getString("exerciseType")

        // exercise_title 설정
        binding.exerciseTitle.text = exerciseType ?: "운동"

        setupExerciseParameters()

        binding.checkImg.setOnClickListener {
            binding.checkbox.visibility = View.GONE
            setupVideoPlayer()
            if (isTimeBasedExercise) {
                startTimeCounter()
            } else {
                startExerciseCounter()
            }
        }
    }

    private fun setupExerciseParameters() {
        when (exerciseType) {
            "빠르게 걷기" -> {
                useSetCounting = true
                maxSets = 3
                isTimeBasedExercise = true
                exerciseDuration = 60
            }
            "의자에서 천천히 일어나기" -> {
                useSetCounting = true
                maxSets = 3
                maxCount = 10
                isTimeBasedExercise = false
            }
            "발뒤꿈치 올리기" -> {
                useSetCounting = false
                maxCount = 12
                isTimeBasedExercise = false
            }
            "다리 차올리기" -> {
                useSetCounting = false
                maxCount = 8
                isTimeBasedExercise = false
            }
            "다리 옆으로 올리기" -> {
                useSetCounting = false
                maxCount = 8
                isTimeBasedExercise = false
            }
            else -> {
                useSetCounting = false
                maxCount = 10
                isTimeBasedExercise = false
            }
        }
        updateSetNumberVisibility()
    }

    private fun updateSetNumberVisibility() {
        binding.exerciseSetNumber.visibility = if (useSetCounting) View.VISIBLE else View.GONE
    }

    private fun setupVideoPlayer() {
        val videoPath = when (exerciseType) {
            "빠르게 걷기" -> "android.resource://${requireActivity().packageName}/${R.raw.low_cardio_brisk_walking}"
            "의자에서 천천히 일어나기" -> "android.resource://${requireActivity().packageName}/${R.raw.low_slow_chair_stand_ups}"
            "발뒤꿈치 올리기" -> "android.resource://${requireActivity().packageName}/${R.raw.low_strength_heel_raises}"
            "다리 차올리기" -> "android.resource://${requireActivity().packageName}/${R.raw.low_strength_leg_raises}"
            "다리 옆으로 올리기" -> "android.resource://${requireActivity().packageName}/${R.raw.low_side_leg_raises}"
            else -> "android.resource://${requireActivity().packageName}/${R.raw.low_side_leg_raises}"
        }

        binding.exerciseVideo.apply {
            setVideoURI(Uri.parse(videoPath))
            setMediaController(MediaController(context).also {
                it.setAnchorView(this)
            })
            setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = isTimeBasedExercise
                mediaPlayer.setVolume(0f, 0f)
                adjustVideoSize(this)
            }
            setOnCompletionListener {
                if (useSetCounting) {
                    handleSetCompletion()
                } else if (!isTimeBasedExercise) {
                    resetExerciseCounter()
                    start()
                }
            }
        }
    }

    private fun handleSetCompletion() {
        currentSet++
        if (currentSet <= maxSets) {
            updateSetNumber()
            if (isTimeBasedExercise) {
                resetTimeCounter()
            } else {
                resetExerciseCounter()
            }
            binding.exerciseVideo.start()
        } else {
            showExerciseCompleteMessage()
        }
    }

    private fun updateSetNumber() {
        activity?.runOnUiThread {
            binding.exerciseSetNumber.text = "${currentSet}세트"
        }
    }

    private fun showExerciseCompleteMessage() {
        // 운동 완료 메시지 표시 로직
    }

    private fun adjustVideoSize(videoView: VideoView) {
        videoView.post {
            val parentWidth = (videoView.parent as View).width
            val parentHeight = (videoView.parent as View).height
            val videoWidth = videoView.width
            val videoHeight = videoView.height
            val aspectRatio = videoWidth.toFloat() / videoHeight.toFloat()
            val newWidth = (parentHeight * aspectRatio).toInt()
            val params = videoView.layoutParams as ConstraintLayout.LayoutParams
            params.width = newWidth
            params.height = parentHeight
            videoView.layoutParams = params
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        }
    }

    private fun startExerciseCounter() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (exerciseCount < maxCount) {
                    exerciseCount++
                    updateCountText()
                    handler.postDelayed(this, 5300)
                }
            }
        }, 5300)
    }

    private fun startTimeCounter() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (timeCount < exerciseDuration) {
                    timeCount++
                    updateTimeText()
                    handler.postDelayed(this, 1000)
                }
            }
        }, 1000)
    }

    private fun resetExerciseCounter() {
        exerciseCount = 0
        updateCountText()
        handler.removeCallbacksAndMessages(null)
        startExerciseCounter()
    }

    private fun resetTimeCounter() {
        timeCount = 0
        updateTimeText()
        handler.removeCallbacksAndMessages(null)
        startTimeCounter()
    }

    private fun updateCountText() {
        activity?.runOnUiThread {
            binding.countText.text = "$exerciseCount/$maxCount"
        }
    }

    private fun updateTimeText() {
        activity?.runOnUiThread {
            binding.countText.text = "${timeCount}초"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.exerciseVideo.stopPlayback()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
