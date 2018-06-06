package com.milnest.tasklist.entities

open class Task(var id: Int, var title: String, var type: Int, var data: String) {
    var isSelected: Boolean = false
    companion object {
        val TYPE_ITEM_TEXT = 0
        val TYPE_ITEM_IMAGE = 1
        val TYPE_ITEM_LIST = 2
    }
}