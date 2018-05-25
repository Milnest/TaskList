package com.milnest.tasklist.presentation.main

import android.content.Intent
import com.milnest.tasklist.entities.ResultOfActivity
import com.milnest.tasklist.interactor.TaskDataInteractor

interface PresenterInterface{
    var taskDataInteractor : TaskDataInteractor?
    fun showToast(toShow : Int)
    fun getResult() : ResultOfActivity?
    fun startPhotoActivity(cameraIntent: Intent)
}