package com.milnest.tasklist.presentation.text

import android.content.Intent

interface TextTaskView {
    fun setText(title: String?, text: String?)
    fun startShareAct(shareIntent: Intent)
    fun showToast(toShow : Int)
    fun finish()
}