package com.baokiiin.mymap

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputLayout

object  Utils {

    const val TAG = "tag"
    fun showDialog(fragmentActivity: FragmentActivity) {
        val diaLogFragment = ListLocationFragment()
        diaLogFragment.show(fragmentActivity.supportFragmentManager,TAG)
    }
    fun diaLogBottom(
        context: Context,
        layoutInflater: LayoutInflater,
        market: Market,
        action:()->Unit
    ): BottomSheetDialog {
        val sheetDialog = BottomSheetDialog(context, R.style.SheetDialog)
        val viewDialog = layoutInflater.inflate(R.layout.user_dialog, null)
        viewDialog.apply {
            findViewById<TextView>(R.id.txtTitleMarket).text = market.title
            findViewById<TextView>(R.id.txtDescription).text = market.description
            findViewById<ExtendedFloatingActionButton>(R.id.btnDirection).setOnClickListener {
                action()
                sheetDialog.dismiss()
            }
        }
        sheetDialog.setContentView(viewDialog)
        return sheetDialog
    }

}