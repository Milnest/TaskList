package com.milnest.tasklist.presentation.mainScreen

import android.content.Intent
import android.widget.Button
import com.milnest.tasklist.entities.ResultOfActivity

interface PresenterInterface{
    fun showNotif(toShow : Int)
    fun getResult() : ResultOfActivity?
    fun startPhotoActivity(cameraIntent: Intent)
    fun createTaskActivity(createTaskIntent : Intent, taskType : Int)
    fun createTaskActivity(taskType: Int, taskClass: Class<*>)
    fun setColorButtons(positiveButton: Button, negativeButton: Button)
}