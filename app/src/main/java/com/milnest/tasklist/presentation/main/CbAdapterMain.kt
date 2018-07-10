package com.milnest.tasklist.presentation.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.CheckboxTaskListItem
import kotlinx.android.synthetic.main.item_main_cb.view.*

class CbAdapterMain(val cbList: List<CheckboxTaskListItem>): BaseAdapter(){
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view : View = LayoutInflater.from(p2!!.context).inflate(R.layout.item_main_cb, p2, false)
        view.cb_main.isChecked = cbList[p0].isCbState
        //view.cb_main_text.setText(cbList[p0].cbText)
        return view
    }

    override fun getItem(p0: Int): Any {
        return cbList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return cbList.size
    }

}