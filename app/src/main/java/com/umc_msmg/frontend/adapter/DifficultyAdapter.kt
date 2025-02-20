package com.umc_msmg.frontend.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.umc_msmg.frontend.R
import com.umc_msmg.frontend.databinding.DifficultyItemBinding

class DifficultyAdapter(
    private val context: Context,
    private val difficulties: List<String>,
    private var selectedPosition: Int = 1,
) : RecyclerView.Adapter<DifficultyAdapter.DifficultyViewHolder>() {

    inner class DifficultyViewHolder(val binding: DifficultyItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DifficultyViewHolder {
        val binding = DifficultyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DifficultyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DifficultyViewHolder, position: Int) {
        val difficulty = difficulties[position]
        holder.binding.difficultyText.text = if (position == 0) "상" else if (position == 1) "중" else "하"

        if (position == selectedPosition) {
            holder.binding.difficultyText.textSize = 36f
            holder.binding.difficultyText.typeface = ResourcesCompat.getFont(context, R.font.pretendard_bold)
            holder.binding.difficultyText.alpha = 1.0f
        } else {
            holder.binding.difficultyText.textSize = 24f
            holder.binding.difficultyText.typeface = ResourcesCompat.getFont(context, R.font.pretendard_medium)
            holder.binding.difficultyText.alpha = 0.4f
        }
    }

    override fun getItemCount(): Int {
        return difficulties.size
    }

    fun getSelectedDifficulty(): String {
        return difficulties[selectedPosition]
    }

    fun setSelectedPosition(position: Int) {
        val previousSelectedPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousSelectedPosition)
        notifyItemChanged(selectedPosition)
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }
}