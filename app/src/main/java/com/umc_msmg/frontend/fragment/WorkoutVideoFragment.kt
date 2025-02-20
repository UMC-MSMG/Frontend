// WorkoutVideoFragment.kt
package com.umc_msmg.frontend.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.LayoutWorkoutVideoBinding
import com.umc_msmg.frontend.utils.VideoManager
import com.umc_msmg.frontend.utils.CountManager
import java.util.Locale
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class WorkoutVideoFragment : Fragment() {
    private var _binding: LayoutWorkoutVideoBinding? = null
    private val binding get() = _binding!!
    private lateinit var videoManager: VideoManager
    private lateinit var countManager: CountManager
    private var workoutType: String? = null
    private val viewModel: WorkoutViewModel by viewModels()

    data class VideoInfo(
        val resourceId: Int,
        val repeatTimes: Int,
        val maxCount: Int,
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

        val frameLayout = FrameLayout(requireContext())
        (binding.exerciseVideo.parent as ViewGroup).apply {
            removeView(binding.exerciseVideo)
            addView(frameLayout, binding.exerciseVideo.layoutParams)
        }
        frameLayout.addView(binding.exerciseVideo, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        ))

        videoManager = VideoManager(requireActivity(), binding.exerciseVideo)
        countManager = CountManager { count, maxCount ->
            if (videoManager.getCurrentVideo()?.isTimeCount == true) {
                binding.countText.text = "$count / ${formatTime(maxCount)}"
            } else {
                binding.countText.text = "$count / $maxCount"
            }
        }


        setupVideoList()
        startWorkout()

        binding.exerciseCompleBtn.setOnClickListener {
            showExerciseCompleteMessage()
        }
    }

    private fun setupVideoList() {
        val videoList = when (workoutType) {
            "유산소" -> listOf(
                VideoInfo(R.raw.low_cardio_brisk_walking, 3, 180, true, true, "빠르게 걷기")
            )
            "근력" -> listOf(
                VideoInfo(R.raw.low_slow_chair_stand_ups, 3, 10, false, true, "의자에서 천천히 일어나기"),
                VideoInfo(R.raw.low_strength_heel_raises, 1, 12, false, false, "발뒤꿈치 올리기"),
                VideoInfo(R.raw.low_strength_leg_raises, 1, 8, false, false, "다리 차올리기"),
                VideoInfo(R.raw.low_side_leg_raises, 1, 8, false, false, "다리 옆으로 올리기")
            )
            else -> emptyList()
        }
        videoManager.setVideoList(videoList)
    }

    private fun startWorkout() {
        videoManager.setupVideoPlayer {
            onVideoComplete()
        }
        updateUI()
        startCounting()
    }

    private fun updateUI() {
        val currentVideo = videoManager.getCurrentVideo() ?: return
        binding.exerciseTitle.text = currentVideo.title
        binding.exerciseSetNumber.visibility = if (currentVideo.showSetNumber) View.VISIBLE else View.GONE
        if (currentVideo.showSetNumber) {
            binding.exerciseSetNumber.text = "${countManager.getRepeatCount() + 1} 세트"
        }
    }

    private fun startCounting() {
        val currentVideo = videoManager.getCurrentVideo() ?: return

        binding.exerciseVideo.setOnPreparedListener { mp ->
            val durationInSeconds = mp.duration / 1000 // 비디오 길이(초 단위)

            countManager.startCounting(currentVideo, durationInSeconds)
        }
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        return String.format(Locale.getDefault(), "%d", minutes,)
    }

    private fun onVideoComplete() {
        if (videoManager.moveToNextVideo()) {
            startWorkout()
        } else {
            val workoutId = when (workoutType) {
                "유산소" -> 1
                "근력" -> 2
                "유연성" -> 3
                "균형" -> 4
                else -> return
            }
            viewModel.completeWorkout(workoutId)
            binding.exerciseCompleBtn.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    viewModel.submitCompletedWorkouts(
                        onSuccess = {
                            navigateToPreviousFragment() // 성공 시 이전 화면으로 이동
                        },
                        onError = { errorMessage ->
                            // 오류 메시지 표시 (예: Toast)
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }


    private fun navigateToPreviousFragment() {
        requireActivity().onBackPressed()
    }


    private fun showExerciseCompleteMessage() {
        // 운동 완료 로직
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.exerciseVideo.stopPlayback()
        countManager.stopCounting()
        _binding = null
    }
}
