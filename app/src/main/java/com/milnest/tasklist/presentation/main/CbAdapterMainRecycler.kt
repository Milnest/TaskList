package com.milnest.tasklist.presentation.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.CheckboxTaskListItem

class CbAdapterMainRecycler(val cbList: List<CheckboxTaskListItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_main_cb, null, false)
        return CbItemHolder(v)
    }

    override fun getItemCount(): Int {
       return cbList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cbHolder = holder as CbItemHolder
        val element = cbList[position]
        cbHolder.mCb.isChecked = element.isCbState
        cbHolder.mCb.setText(element.cbText)
        //cbHolder.mCbText.setText(element.cbText)
    }

    inner class CbItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var mCb: CheckBox = itemView.findViewById(R.id.cb_main)
        //internal var mCbText: TextView = itemView.findViewById(R.id.cb_main_text)
    }
}