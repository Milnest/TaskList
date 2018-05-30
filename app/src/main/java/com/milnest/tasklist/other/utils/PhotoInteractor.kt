package com.milnest.tasklist.other.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.util.*

object PhotoInteractor {
    fun createFilePath() : File {
        val dateString = Date()
        return File(Environment.getExternalStorageDirectory(), dateString.toString())
    }
    fun createImage(pathName : String) : Bitmap{
        val startImg = BitmapFactory.decodeFile(pathName)
        val finalImg = Bitmap.createScaledBitmap(startImg,
                startImg.width/10, startImg.height/10, false)
        return finalImg
    }
}