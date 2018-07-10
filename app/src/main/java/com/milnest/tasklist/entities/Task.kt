package com.milnest.tasklist.entities

class Task(var type: Int) {
    var id: Int = -1
    var title: String = ""
    var data: String = ""
    constructor(id: Int, title:String, type:Int, data:String) : this(type){
        this.id = id
        this.title = title
        this.data = data
    }
    companion object {
        const val TYPE_ITEM_TEXT = 0
        const val TYPE_ITEM_IMAGE = 1
        const val TYPE_ITEM_LIST = 2
    }
}