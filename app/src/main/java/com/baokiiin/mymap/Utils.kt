package com.baokiiin.mymap

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.random.Random

object Utils {

    const val TAG = "tag"
    const val SEEKBAR = 1
    const val SPINNER = 0
    const val CODE = 111
    const val defaultZoom = 17f
    fun getData(): MutableList<Market> {
        val data = mutableListOf<Market>()
        for (i in 0..50) {
            val randomLatitude = Random.nextDouble(-76.000001, 76.999999)
            val randomLongitude = Random.nextDouble(-176.000001, 175.999999)
            data.add(
                Market(
                    "HCM $i",
                    "Hồ chí minh, vị trí thứ $i",
                    LatLng(randomLatitude, randomLongitude)
                )
            )
        }
        return data
    }

    fun getDataChan(): MutableList<String> {
        return mutableListOf("None", "True", "False")
    }

    fun getDataCountMarker(): MutableList<String> {
        return mutableListOf("None", "10", "20")
    }

    fun diaLogBottom(
        context: Context,
        layoutInflater: LayoutInflater,
        filter: Filter?,
        action: (Filter) -> Unit
    ): BottomSheetDialog {
        val sheetDialog = BottomSheetDialog(context, R.style.SheetDialog)
        val viewDialog = layoutInflater.inflate(R.layout.user_dialog, null)
        viewDialog.apply {
            val adapterFilter = AdapterFilter()
            viewDialog.findViewById<RecyclerView>(R.id.recyclerView).apply {
                adapter = adapterFilter
                layoutManager = LinearLayoutManager(context)
                adapterFilter.submitList(getDataFilter(filter))
            }
            viewDialog.findViewById<Button>(R.id.btnOk).apply{
                setOnClickListener {
                    action(dataFilterCallBackAdapter(adapterFilter))
                    sheetDialog.dismiss()
                }

            }
            viewDialog.findViewById<Button>(R.id.btnClear).setOnClickListener {
                val valueZoom = Utils.defaultZoom
                action(Filter(valueZoom,0,1))
                sheetDialog.dismiss()
            }
        }
        sheetDialog.setContentView(viewDialog)
        return sheetDialog
    }
    private fun dataFilterCallBackAdapter(adapterFilter:AdapterFilter):Filter{
        val filters = Filter(defaultZoom)
        adapterFilter.currentList.forEach {
            when(it.type){
                Type.SEEKBAR-> filters.zoom = it.dataCallBack?: defaultZoom
                Type.SPINNER->{
                    when(it.category){
                        FilterCategory.CHAN->filters.chan = it.dataCallBack?.toInt()?:0
                        FilterCategory.SOLUONG->filters.count = it.dataCallBack?.toInt()?:1
                    }

                }
            }
        }
        return filters
    }
    private fun getDataFilter(filter: Filter?):MutableList<DataFilter> =  mutableListOf(
        DataFilter(
            type = Type.SEEKBAR,
            dataCallBack = filter?.zoom?:defaultZoom
        ),
        DataFilter(
            type = Type.SPINNER,
            dataCallBack = filter?.chan?.toFloat(),
            title = "Lấy marker chẵn",
            data = getDataChan(),
            category = FilterCategory.CHAN
        ),
        DataFilter(
            type = Type.SPINNER,
            dataCallBack = filter?.count?.toFloat(),
            title = "Số lượng marker",
            data = getDataCountMarker(),
            category = FilterCategory.SOLUONG
        )
    )
}
