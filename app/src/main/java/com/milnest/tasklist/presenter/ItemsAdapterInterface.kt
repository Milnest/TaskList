package com.milnest.tasklist.presenter

import android.support.v7.widget.RecyclerView

interface ItemsAdapterInterface {
//    fun addSelection(viewHolder: RecyclerView.ViewHolder)
    fun removeSelection()
    fun addSelection(position:Int)
    /*fun removeItem(position: Int)*/
}