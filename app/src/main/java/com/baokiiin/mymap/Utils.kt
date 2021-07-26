package com.baokiiin.mymap

import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

object  Utils {

    const val TAG = "tag"
    fun showDialog(fragmentActivity: FragmentActivity) {
        val diaLogFragment = ListLocationFragment()
        diaLogFragment.show(fragmentActivity.supportFragmentManager,TAG)
    }

}