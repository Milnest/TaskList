package com.milnest.tasklist.presentation.textScreen

import android.content.Intent
import com.milnest.tasklist.entities.TextActData

interface TextActInterface {
    fun saveText(data : Intent)
    fun setText(titleAndText: Array<String?>)
    fun startShareAct(shareIntent: Intent)
    fun showToast(toShow : Int)
    fun finishView()
}