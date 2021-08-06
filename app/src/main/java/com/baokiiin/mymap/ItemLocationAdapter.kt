package com.baokiiin.mymap

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baokiiin.mymap.databinding.ItemLocationBinding
import com.google.android.gms.maps.model.Marker

class ItemLocationAdapter(private val onClick: (Marker) -> Unit) :
    ListAdapter<Marker, ItemLocationAdapter.ViewHolder>(
        ItemLocationDiffUtil()
    ) {

    var prevMarker = "-1"
     inner class ViewHolder(private val binding: ItemLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Marker, onClick: ((Marker) -> Unit)? = null) {
            binding.data = item
            if(item.id == prevMarker){
                itemView.setBackgroundColor(Color.YELLOW)
            }
            else
                itemView.setBackgroundColor(Color.WHITE)
            itemView.setOnClickListener {
                if (item.id != prevMarker) {
                    prevMarker = item.id
                    itemView.setBackgroundColor(Color.YELLOW)
                }
                if (onClick != null) {
                    onClick(item)
                }
            }
            binding.executePendingBindings()
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, onClick) }
    }
}

class ItemLocationDiffUtil : DiffUtil.ItemCallback<Marker>() {
    // cung cấp thông tin về cách xác định phần
    override fun areItemsTheSame(
        oldItem: Marker,
        newItem: Marker
    ): Boolean { // cho máy biết 2 item_detail khi nào giống
        return oldItem.id == newItem.id // dung
    }

    override fun areContentsTheSame(
        oldItem: Marker,
        newItem: Marker
    ): Boolean { // cho biết item_detail khi nào cùng nội dung
        return oldItem == newItem
    }

}
