package com.umc_msmg.frontend.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc_msmg.frontend.data.ShopItem
import com.umc_msmg.frontend.databinding.ItemMyShopBinding
import android.content.Context
import com.bumptech.glide.Glide

class MyShopAdapter(private val context: Context,private val onItemClick: (ShopItem) -> Unit) :
    RecyclerView.Adapter<MyShopAdapter.MyShopViewHolder>() {

    private var items: List<ShopItem> = emptyList()

    inner class MyShopViewHolder(val binding: ItemMyShopBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyShopViewHolder {
        val binding = ItemMyShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyShopViewHolder, position: Int) {
        val item = items[position]
        holder.binding.itemName.text = item.name
        holder.binding.itemPrice.text = item.price.toString() + "원"
        Glide.with(context).load(item.image).into(holder.binding.itemImage)


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