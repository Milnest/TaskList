package com.milnest.tasklist.presentation.main

import android.content.Intent

interface MainView{
    var mActionMode : android.support.v7.view.ActionMode?
    fun showActionBar(title : Int)
    fun closeActionBar()
    fun startTaskActivity(activityClass: Class<*>?, itemId: Int, actResType: Int)
    fun showNotif(toShow : Int)
    fun startPhotoActivity(cameraIntent: Intent)
    fun createTaskActivity(createTaskIntent : Intent, taskType : Int)
    fun createTaskActivity(taskType: Int, taskClass: Class<*>)
    fun showDialog()
    fun finishActionMode()
}