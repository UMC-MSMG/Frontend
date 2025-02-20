// WorkoutVideoFragment.kt
package com.umc_msmg.frontend.fragment

import android.content.Context
import android.content.SharedPreferences
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
private lateinit var sharedPreferences : SharedPreferences


class WorkoutVideoFragment : Fragment() {
    private var _binding: LayoutWorkoutVideoBinding? = null
    private val binding get() = _binding!!
    private lateinit var videoManager: VideoManager
    private lateinit var countManager: CountManager
    private var workoutType: String? = null
    // private val viewModel: WorkoutViewModel by viewModels()

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
        setupButtons()
        startWorkout()

        binding.exerciseCompleBtn.setOnClickListener {
            showExerciseCompleteMessage()
        }
    }

    private fun setupButtons() {
        binding.exerciseCompleBtn.setOnClickListener {
            showExerciseCompleteMessage()
        }

        binding.skipButton.setOnClickListener {
            skipCurrentVideo()
        }
    }

    private fun setupVideoList() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userDifficulty = sharedPreferences.getString("user_diff", "NORMAL") ?: "NORMAL"

        val videoList = when (workoutType) {
            "유산소" -> getCardioVideoList(userDifficulty)
            "근력" -> getStrengthVideoList(userDifficulty)
            else -> emptyList()
        }

        videoManager.setVideoList(videoList)

        if (videoList.isEmpty()) {
            binding.exerciseCompleBtn.visibility = View.VISIBLE
            binding.skipButton.visibility = View.GONE
        } else {
            binding.exerciseCompleBtn.visibility = View.GONE
            binding.skipButton.visibility = View.VISIBLE
        }
    }

    private fun getCardioVideoList(difficulty: String): List<VideoInfo> {
        return when (difficulty) {
            "EASY" -> listOf(
                VideoInfo(R.raw.low_cardio_brisk_walking, 1, 180, true, false, "빠르게 걷기")
            )
            "NORMAL" -> listOf(
                VideoInfo(R.raw.mid_cardio_jumping_jacks, 1, 20, false, false, "팔벌려뛰기")
            )
            "HARD" -> listOf(
                VideoInfo(R.raw.high_cardio_mountain_climbers, 1, 20, false, false, "엎드려서 무릎가슴닿기")
            )
            else -> emptyList()
        }
    }

    private fun getStrengthVideoList(difficulty: String): List<VideoInfo> {
        return when (difficulty) {
            "EASY" -> listOf(
                VideoInfo(R.raw.low_slow_chair_stand_ups, 3, 10, false, true, "의자에서 천천히 일어나기"),
                VideoInfo(R.raw.low_strength_heel_raises, 1, 12, false, false, "발뒤꿈치 올리기"),
                VideoInfo(R.raw.low_strength_leg_raises, 1, 8, false, false, "다리 차올리기"),
                VideoInfo(R.raw.low_side_leg_raises, 1, 8, false, false, "다리 옆으로 올리기")
            )
            "NORMAL" -> listOf(
                VideoInfo(R.raw.mid_strength_chair_assisted_squats, 3, 15, false, true, "의자 끝에 앉으며 스쿼트"),
                VideoInfo(R.raw.mid_strength_lying_hip_raises, 1, 12, false, false, "누워서 엉덩이 들어올리기"),
                VideoInfo(R.raw.mid_strenth_knee_push_ups, 1, 12, false, false, "무릎 대고 팔굽혀펴기"),
                VideoInfo(R.raw.mid_strength_prone_leg_lifts, 1, 10, false, false, "엎드려 다리 뒤로 차기")
            )
            "HARD" -> listOf(
                VideoInfo(R.raw.high_strength_squats, 3, 15, false, true, "스쿼트"),
                VideoInfo(R.raw.high_strength_lunges, 3, 12, false, true, "런지"),
                VideoInfo(R.raw.high_strength_plank, 1, 45, true, false, "플랭크"),
                VideoInfo(R.raw.high_strength_side_lunges, 3, 12, false, true, "사이드 런지")
            )
            else -> emptyList()
        }
    }



    private fun startWorkout() {
        if (videoManager.getCurrentVideo() == null) {
            binding.exerciseCompleBtn.visibility = View.VISIBLE
            binding.skipButton.visibility = View.GONE
            return
        }

        videoManager.setupVideoPlayer {
            onVideoComplete()
        }
        updateUI()
        startCounting()
    }

    private fun skipCurrentVideo() {
        onVideoComplete()
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
            // viewModel.completeWorkout(workoutId)
            binding.exerciseCompleBtn.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    navigateToPreviousFragment()
                }
            }
            binding.skipButton.visibility = View.GONE
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
