package com.milnest.tasklist.presenter

import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode

interface ActModeInterface {
    var mActionMode : ActionMode?
    fun showActionBar(title : Int)
    fun closeActionBar()
    fun startTaskActivity(activityClass : Class<*>?, itemId : Int, actResType : Int)
}