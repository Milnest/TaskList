package com.milnest.tasklist.presentation.element

import android.support.v7.view.ActionMode

interface ActModeInterface {
    var mActionMode : ActionMode?
    fun showActionBar(title : Int)
    fun closeActionBar()
    fun startTaskActivity(activityClass: Class<*>?, itemId: Int, actResType: Int,
                          task: Array<String>)
}