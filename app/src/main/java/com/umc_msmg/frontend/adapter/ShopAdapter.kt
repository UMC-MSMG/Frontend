import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc_msmg.frontend.databinding.ItemShopBinding

class ShopAdapter(private val onItemClick: (ShopItem) -> Unit) : RecyclerView.Adapter<ShopAdapter.ViewHolder>() {
    private var items = listOf<ShopItem>()

    inner class ViewHolder(private val binding: ItemShopBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShopItem) {
            binding.apply {
                binding.itemName.text = item.name
                binding.itemOffice.text = item.purchasingOffice
                binding.itemPrice.text = "${item.price}원"
                item.imageResId?.let { binding.itemImage.setImageResource(it) }

                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemShopBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<ShopItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
