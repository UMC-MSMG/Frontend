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
        // exercise_title 설정
        binding.exerciseTitle.text = workoutType ?: "운동"

        binding.checkbox.visibility = View.VISIBLE

        binding.checkImg.setOnClickListener {
            binding.checkbox.visibility = View.GONE
            setupVideoPlayer()
//            if (isTimeBasedExercise) {
//                startTimeCounter()
//            } else {
//                startExerciseCounter()
//            }
        }

        // 완료 버튼 visivle 전환 시점 고려
        binding.exerciseCompleBtn.setOnClickListener {
            showExerciseCompleteMessage()
        }

        setupVideoPlayer()
    }

    private fun setupVideoPlayer() {
        val videoPath = when (workoutType) {
            "유산소" -> "android.resource://${requireActivity().packageName}/${R.raw.low_cardio_brisk_walking}"
            "근력" -> "android.resource://${requireActivity().packageName}/${R.raw.low_slow_chair_stand_ups}"
            "유연성" -> "android.resource://${requireActivity().packageName}/${R.raw.low_strength_heel_raises}"
            "균형" -> "android.resource://${requireActivity().packageName}/${R.raw.low_strength_leg_raises}"
            else -> "android.resource://${requireActivity().packageName}/${R.raw.low_side_leg_raises}"
        }

        binding.exerciseVideo.apply {
            setVideoURI(Uri.parse(videoPath))
            setMediaController(MediaController(context).apply { setAnchorView(this@apply) })
            setOnPreparedListener { it.isLooping = true }
            start()
        }
    }

    private fun showExerciseCompleteMessage() {
        // 운동 완료
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.exerciseVideo.stopPlayback()
        _binding = null
    }
}
