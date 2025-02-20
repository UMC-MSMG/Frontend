package com.umc_msmg.frontend.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umc_msmg.frontend.data.ShopItem
import com.umc_msmg.frontend.databinding.ItemShopBinding


class ShopAdapter(private val onItemClick: (ShopItem) -> Unit) :
    RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {

    private var items: List<ShopItem> = emptyList()

    inner class ShopViewHolder(val binding: ItemShopBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val binding = ItemShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        val item = items[position]
        holder.binding.itemName.text = item.name
        holder.binding.itemPrice.text = "${item.price}원"
        Glide.with(holder.itemView.context).load(item.image).into(holder.binding.itemImage)
        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(list: List<ShopItem>) {
        items = list
        notifyDataSetChanged()
    }
}