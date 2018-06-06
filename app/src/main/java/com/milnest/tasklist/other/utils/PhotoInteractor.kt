package com.milnest.tasklist.other.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.util.*

object PhotoInteractor {
    fun createFilePath() : File {
        val dateString = Date()
        return File(Environment.getExternalStorageDirectory(), dateString.toString())
    }

    fun createImage(pathName : String) : Bitmap{
        val startImg = BitmapFactory.decodeFile(pathName)
        val width : Int
        val height : Int
        if(startImg.width > 250) width = 250
        else width = startImg.width

        if(startImg.height > 250) height = 250
        else height = startImg.height

        val finalImg = Bitmap.createScaledBitmap(startImg,
                width, height, false)
        return finalImg
    }

    fun saveImageToFile(img : Bitmap) : File{
        val file = createFilePath()
        val fOut = FileOutputStream(file)
        img.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
        fOut.flush()
        fOut.close()
        return file
    }
}