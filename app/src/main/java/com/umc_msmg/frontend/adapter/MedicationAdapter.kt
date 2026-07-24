package com.umc_msmg.frontend.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc_msmg.frontend.data.Medication
import com.umc_msmg.frontend.databinding.ItemMedicationBinding

class MedicationAdapter(private val medications: MutableList<Medication>, private val onDeleteClick: (Int) -> Unit) :
    RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder>() {

    class MedicationViewHolder(val binding: ItemMedicationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val binding = ItemMedicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        val medication = medications[position]
        holder.binding.medNameEditText.setText(medication.medName)

        holder.binding.deleteButton.setOnClickListener {
            onDeleteClick(position)
        }
        // 복용 시간, 요일 표시 TextView 추가
    }

    override fun getItemCount(): Int {
        return medications.size
    }

    fun removeItem(position: Int) {
        medications.removeAt(position)
        notifyItemRemoved(position)
    }
}