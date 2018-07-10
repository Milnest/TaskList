package com.milnest.tasklist.presentation.main

import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager

interface MainView{
    var mActionMode : android.support.v7.view.ActionMode?
    val mLinearLayoutManager : LinearLayoutManager
    val mGridLayoutManager : GridLayoutManager
    fun showActionBar(title : Int)
    fun setSplitIcon(iconResource: Int)
    fun closeActionBar()
    fun startTaskActivity(activityClass: Class<*>?, itemId: Int, actResType: Int)
    fun showNotif(toShow : Int)
    fun startPhotoActivity(cameraIntent: Intent)
    fun createTaskActivity(createTaskIntent : Intent, taskType : Int)
    fun createTaskActivity(taskType: Int, taskClass: Class<*>)
    fun showDialog()
    fun finishActionMode()
}