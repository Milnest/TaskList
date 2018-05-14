package com.milnest.tasklist

/**
 * Created by t-yar on 17.04.2018.
 */

class TextTaskListItem(id: Int, name: String, var text: String?) : TaskListItem(id, name, TaskListItem.TYPE_ITEM_TEXT)
