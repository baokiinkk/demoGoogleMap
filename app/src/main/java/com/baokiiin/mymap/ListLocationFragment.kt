package com.baokiiin.mymap

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import org.greenrobot.eventbus.EventBus

class ListLocationFragment : DialogFragment() {

    private lateinit var locationAdapter: ItemLocationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list_location, container, false)
        setup(view)
        getData()
        return view
    }
    private fun setup(view: View){
        locationAdapter = ItemLocationAdapter {
            dismiss()
            EventBus.getDefault().post(it)
        }
        view.findViewById<RecyclerView>(R.id.recycleView).apply {
            adapter = locationAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun getData(){
        val HCM1 = LatLng(10.762622, 106.660172)
        val HCM2 = LatLng(10.810583, 106.709145)
        val HCM3 = LatLng(10.822340, 106.631541)
        val HCM4 = LatLng(10.824079, 106.630189)
        locationAdapter.submitList(
            mutableListOf(
                Market("HCM", "Hồ Chí Minh", HCM1),
                Market(
                    "Bình thạnh",
                    "Phường 26, Bình Thạnh, Thành phố Hồ Chí Minh 700000, Việt Nam",
                    HCM2
                ),
                Market(
                    "Quận 11",
                    "33-15 Nguyễn Phúc Chu, Phường 15, Quận 11, Thành phố Hồ Chí Minh, Việt Nam",
                    HCM3
                ),
                Market("Tân Bình","13-1 Phan Huy Ích, Phường 15, Tân Bình, Thành phố Hồ Chí Minh, Việt Nam",HCM4)
            )
        )
    }
}