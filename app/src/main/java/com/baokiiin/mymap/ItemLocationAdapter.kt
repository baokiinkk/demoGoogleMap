package com.baokiiin.mymap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baokiiin.mymap.databinding.ItemLocationBinding

class ItemLocationAdapter(private val onClick: (Market) -> Unit) :
    ListAdapter<Market, ItemLocationAdapter.ViewHolder>(
        ItemLocationDiffUtil()
    ) {
    class ViewHolder(private val binding: ItemLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding =
                    ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(
                    binding
                )
            }
        }

        fun bind(item: Market, onClick: ((Market) -> Unit)? = null) {
                binding.data = item
                itemView.setOnClickListener {
                    if (onClick != null) {
                        onClick(item)
                    }
                }
                binding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, onClick) }
    }
}

class ItemLocationDiffUtil : DiffUtil.ItemCallback<Market>() {
    // cung cấp thông tin về cách xác định phần
    override fun areItemsTheSame(
        oldItem: Market,
        newItem: Market
    ): Boolean { // cho máy biết 2 item_detail khi nào giống
        return oldItem.title == newItem.title // dung
    }

    override fun areContentsTheSame(
        oldItem: Market,
        newItem: Market
    ): Boolean { // cho biết item_detail khi nào cùng nội dung
        return oldItem == newItem
    }

}
