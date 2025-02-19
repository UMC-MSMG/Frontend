package com.umc_msmg.frontend.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.adapter.DifficultyAdapter
import com.umc_msmg.frontend.databinding.FragmentEditLevelBinding
import kotlin.math.abs

class EditLevelFragment : Fragment() {

    private var _binding: FragmentEditLevelBinding? = null
    private val binding get() = _binding!!
    private lateinit var difficultySlider: RecyclerView
    private lateinit var difficultyAdapter: DifficultyAdapter
    private val difficulties = listOf("상", "중", "하")
    private val prefName = "DifficultyPrefs"
    private val difficultyKey = "difficulty"
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

        val initialDifficulty = sharedPreferences.getString(difficultyKey, "중") ?: "중"
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
            val editor = sharedPreferences?.edit() ?: return@setOnClickListener
            val selectedDifficulty = difficultyAdapter.getSelectedDifficulty()
            editor.putString(difficultyKey, selectedDifficulty)
            editor.apply()

            Log.d("EditLevelFragment", "난이도 저장: $selectedDifficulty")
            Toast.makeText(requireContext(), "난이도 저장 성공", Toast.LENGTH_SHORT).show()

            parentFragmentManager.popBackStack()
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

    fun getSelectedDifficulty(): String {
        return difficultyAdapter.getSelectedDifficulty()

    }
}