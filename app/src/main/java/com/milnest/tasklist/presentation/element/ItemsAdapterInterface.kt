package com.milnest.tasklist.presentation.element

import android.support.v7.widget.RecyclerView

interface ItemsAdapterInterface {
    fun removeSelection()
    fun addSelection(position:Int)
    fun retrieveAdapter()
}