package com.baokiiin.mymap

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baokiiin.mymap.Utils.SEEKBAR
import com.baokiiin.mymap.Utils.SPINNER
import com.baokiiin.mymap.Utils.defaultZoom
import com.google.android.material.slider.Slider
import org.greenrobot.eventbus.EventBus

class AdapterFilter :
    ListAdapter<DataFilter, AdapterFilter.ViewHolder>(
        MainDIff()
    ) {
    class ViewHolder(private val v: View) :
        RecyclerView.ViewHolder(v) {
        companion object {
            fun from(parent: ViewGroup, type: Int): ViewHolder {

                val view = LayoutInflater.from(parent.context)
                    .inflate(
                        when (type) {
                            SPINNER -> R.layout.item_spinner
                            else -> R.layout.item_seekbar
                        },
                        parent,
                        false
                    )
                return ViewHolder(
                    view
                )
            }
        }

        fun bind(
            item: DataFilter,
        ) {
            if (itemViewType == SPINNER) {
                itemView.findViewById<TextView>(R.id.textView).text = item.title
                item.data?.let {
                    itemView.findViewById<Spinner>(R.id.spMarker).apply {
                        adapter = ArrayAdapter(
                            itemView.context,
                            android.R.layout.simple_spinner_item,
                            it
                        )
                        setSelection(item.dataCallBack?.toInt()?:0)
                        onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                item.dataCallBack = position.toFloat()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }

                        }
                    }
                }

            } else {
                    itemView.findViewById<Slider>(R.id.slider).apply {
                        value = item.dataCallBack?:defaultZoom
                        addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                            override fun onStartTrackingTouch(slider: Slider) {}

                            override fun onStopTrackingTouch(slider: Slider) {
                                item.dataCallBack = slider.value
                            }
                        })
                    }

            }

        }

    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).type) {
            Type.SPINNER -> SPINNER
            else -> SEEKBAR
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent, viewType
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

}

class MainDIff : DiffUtil.ItemCallback<DataFilter>() {
    // cung cấp thông tin về cách xác định phần
    override fun areItemsTheSame(
        oldItem: DataFilter,
        newItem: DataFilter
    ): Boolean { // cho máy biết 2 item_detail khi nào giống
        return oldItem.title == newItem.title // dung
    }

    override fun areContentsTheSame(
        oldItem: DataFilter,
        newItem: DataFilter
    ): Boolean { // cho biết item_detail khi nào cùng nội dung
        return oldItem == newItem
    }

}
