// WorkoutVideoFragment.kt
package com.umc_msmg.frontend.fragment

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.LayoutWorkoutVideoBinding

class WorkoutVideoFragment : Fragment() {

    private var _binding: LayoutWorkoutVideoBinding? = null
    private val binding get() = _binding!!
    private var playCount = 0
    private var exerciseCount = 0
    private val handler = Handler(Looper.getMainLooper())
    private var setNumber = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutWorkoutVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.checkImg.setOnClickListener {
            binding.checkbox.visibility = View.GONE
        }

        setupVideoPlayer()
        startExerciseCounter()
    }

    private fun setupVideoPlayer() {
        val videoPath = "android.resource://${requireActivity().packageName}/${R.raw.low_slow_chair_stand_ups}"
        binding.exerciseVideo.apply {
            setVideoURI(Uri.parse(videoPath))
            setMediaController(MediaController(context).also {
                it.setAnchorView(this)
            })
            setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = false
                mediaPlayer.setVolume(0f, 0f)

                // 비디오 크기 조정
                adjustVideoSize(this)
            }

            setOnCompletionListener {
                playCount++
                if (playCount < 3) {
                    start()
                    setNumber++
                    updateSetNumber()
                    resetExerciseCounter()
                }
            }

            start()
        }
        updateSetNumber()
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

            // 비디오를 가운데 정렬
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        }
    }

    private fun startExerciseCounter() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (exerciseCount < 10) {
                    exerciseCount++
                    updateCountText()
                    handler.postDelayed(this, 5300) // 5.3초마다 실행
                }
            }
        }, 5300) // 처음 5.3초 후 시작
    }

    private fun resetExerciseCounter() {
        exerciseCount = 0
        updateCountText()
        handler.removeCallbacksAndMessages(null)
        startExerciseCounter()
    }

    private fun updateCountText() {
        activity?.runOnUiThread {
            binding.countText.text = "$exerciseCount/10"
        }
    }

    private fun updateSetNumber() {
        activity?.runOnUiThread {
            binding.exerciseSetNumber.text = "${setNumber}세트"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.exerciseVideo.stopPlayback()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
