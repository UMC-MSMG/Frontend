package com.umc_msmg.frontend.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.adapter.DifficultyAdapter
import com.umc_msmg.frontend.data.UpdateProfileResponse
import com.umc_msmg.frontend.data.WorkoutLevelRequest
import com.umc_msmg.frontend.databinding.FragmentEditLevelBinding
import com.umc_msmg.frontend.interfaces.UserServiceRetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs

class EditLevelFragment : Fragment() {

    private var _binding: FragmentEditLevelBinding? = null
    private val binding get() = _binding!!
    private lateinit var difficultySlider: RecyclerView
    private lateinit var difficultyAdapter: DifficultyAdapter
    private val difficulties = listOf("HIGH", "MEDIUM", "LOW")
    private val prefName = "app_prefs"
    private val difficultyKey = "user_diff"
    private var selectedPosition: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        difficultySlider = binding.difficultySlider

        val sharedPreferences = requireContext().getSharedPreferences(prefName, Context.MODE_PRIVATE)

        val initialDifficulty = sharedPreferences.getString(difficultyKey, "MEDIUM") ?: "MEDIUM"
        selectedPosition = difficulties.indexOf(initialDifficulty)
        if (selectedPosition == -1) {
            selectedPosition = 1
        }

        difficultyAdapter = DifficultyAdapter(requireContext(), difficulties, selectedPosition)

        difficultySlider.adapter = difficultyAdapter
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        difficultySlider.layoutManager = layoutManager

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(difficultySlider)


        layoutManager.scrollToPosition(selectedPosition)

        difficultySlider.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateHighlight()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val recyclerViewCenterY = difficultySlider.height / 2
                    var closestPosition = -1
                    var minDistance = Float.MAX_VALUE

                    for (i in 0 until difficultySlider.childCount) {
                        val child = recyclerView.getChildAt(i)
                        val childCenterY = child.top + child.height / 2
                        val distance = abs(recyclerViewCenterY - childCenterY)

                        if (distance < minDistance) {
                            minDistance = distance.toFloat()
                            closestPosition = recyclerView.getChildAdapterPosition(child)
                        }
                    }

                    if (closestPosition != -1) {
                        difficultyAdapter.setSelectedPosition(closestPosition)
                        updateHighlight()
                    }
                }
            }
        })

        binding.cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.saveButton.setOnClickListener {
            val difficultyPreferences = requireContext().getSharedPreferences(prefName, Context.MODE_PRIVATE)
            val authorization = "Bearer " + difficultyPreferences.getString("access_token", null)
            val selectedDifficulty = difficultyAdapter.getSelectedDifficulty()

            val request = WorkoutLevelRequest(selectedDifficulty)

            UserServiceRetrofitClient.apiService.updateWorkoutLevel(authorization, request)
                .enqueue(object : Callback<UpdateProfileResponse> {
                    override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                        if (response.isSuccessful) {
                            Log.d("EditLevelFragment", "난이도 API 업데이트 성공")

                            val editor = difficultyPreferences.edit()
                            editor.putString(difficultyKey, selectedDifficulty)
                            editor.apply()

                            parentFragmentManager.popBackStack()
                        } else {
                            Log.e("EditLevelFragment", "난이도 API 업데이트 실패: ${response.code()}")

                        }
                    }

                    override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                        Log.e("EditLevelFragment", "난이도 API 호출 실패: ${t.message}")
                    }
                })
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        updateHighlight()
    }

    private fun updateHighlight() {
        val recyclerViewCenterY = difficultySlider.height / 2

        for (i in 0 until difficultySlider.childCount) {
            val child = difficultySlider.getChildAt(i)
            val childCenterY = child.top + child.height / 2

            val distance = abs(recyclerViewCenterY - childCenterY)

            val isHighlighted = distance < 150

            val holder = difficultySlider.getChildViewHolder(child) as? DifficultyAdapter.DifficultyViewHolder

            holder?.let {
                if (isHighlighted) {
                    it.binding.difficultyText.textSize = 36f
                    it.binding.difficultyText.typeface = ResourcesCompat.getFont(requireContext(), R.font.pretendard_bold)
                    it.binding.difficultyText.alpha = 1.0f

                } else {
                    it.binding.difficultyText.textSize = 24f
                    it.binding.difficultyText.typeface = ResourcesCompat.getFont(requireContext(), R.font.pretendard_medium)
                    it.binding.difficultyText.alpha = 0.4f
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}