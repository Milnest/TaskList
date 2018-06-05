package com.milnest.tasklist.presentation.mainScreen

import android.content.Intent
import android.widget.Button
import com.milnest.tasklist.entities.ResultOfActivity

interface PresenterInterface{
    var mActionMode : android.support.v7.view.ActionMode?
    fun showActionBar(title : Int)
    fun closeActionBar()
    fun startTaskActivity(activityClass: Class<*>?, itemId: Int, actResType: Int)
    fun showNotif(toShow : Int)
    /*fun getResult() : ResultOfActivity?*/
    fun startPhotoActivity(cameraIntent: Intent)
    /*fun startImageActivity(galleryIntent: Intent)*/
    fun createTaskActivity(createTaskIntent : Intent, taskType : Int)
    fun createTaskActivity(taskType: Int, taskClass: Class<*>)
    fun showDialog()
    fun finishActionMode()
}