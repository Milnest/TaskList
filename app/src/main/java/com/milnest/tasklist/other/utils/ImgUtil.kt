package com.milnest.tasklist.other.utils

import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.util.*

object ImgUtil {
    fun createFilePath() : File {
        val dateString = Date()
        return File(Environment.getExternalStorageDirectory(), dateString.toString())
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