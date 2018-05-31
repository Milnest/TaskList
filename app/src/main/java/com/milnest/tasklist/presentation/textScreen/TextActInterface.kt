package com.milnest.tasklist.presentation.textScreen

import android.content.Intent
import com.milnest.tasklist.entities.TextActData

interface TextActInterface {
    fun getText() : TextActData
    fun saveText(data : Intent)
    fun setText(strings: Array<String>/*, id: Int?*/)
    fun getStartText() : Intent?
    fun startShareAct(shareIntent: Intent)
    fun showToast(toShow : Int)
}