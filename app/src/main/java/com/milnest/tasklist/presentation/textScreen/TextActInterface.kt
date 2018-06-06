package com.milnest.tasklist.presentation.textScreen

import android.content.Intent

interface TextActInterface {
    fun saveText(data : Intent)
    fun setText(title: String?, text: String?)
    fun startShareAct(shareIntent: Intent)
    fun showToast(toShow : Int)
    fun finish()
}