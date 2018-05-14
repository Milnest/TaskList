package com.milnest.tasklist

/**
 * Created by t-yar on 17.04.2018.
 */

/**Общий класс задач для списка
 */
open class TaskListItem/*public TaskListItem(String name, int type) {
        mName = name;
        mType = type;
    }*/
(var id: Int, var name: String?, var type: Int) {
    var isSelected: Boolean = false

    companion object {
        val TYPE_ITEM_TEXT = 0
        val TYPE_ITEM_IMAGE = 1
        val TYPE_ITEM_LIST = 2
    }
}
