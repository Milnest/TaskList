package com.milnest.tasklist.presenter

import android.support.v7.app.AppCompatActivity
import com.milnest.tasklist.entities.ResultOfActivity
import com.milnest.tasklist.interactor.DBMethodsAdapter

interface PresenterInterface{
    var dbMethodsAdapter : DBMethodsAdapter?
    fun showToast(toShow : Int)
    fun getResult() : ResultOfActivity?
}