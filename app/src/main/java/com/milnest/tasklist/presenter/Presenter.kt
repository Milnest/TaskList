package com.milnest.tasklist.presenter

import android.annotation.SuppressLint
import android.app.Activity
import android.database.SQLException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.ResultOfActivity
import com.milnest.tasklist.entities.TaskListItem
import com.milnest.tasklist.interactor.DBMethodsAdapter
import com.milnest.tasklist.view.ItemsAdapter
import com.milnest.tasklist.view.MainActivity
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.ArrayList

class Presenter(val activity: PresenterInterface) :
        android.support.v7.widget.SearchView.OnQueryTextListener, View.OnFocusChangeListener/*,
        android.support.v7.view.ActionMode.Callback*/ {

    fun toastToActivity(toShow : Int) {
        activity.showToast(toShow);
    }



    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        activity.dbMethodsAdapter!!.retrieve()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            activity.dbMethodsAdapter!!.search(query)
            return true
        } else {
            activity.dbMethodsAdapter!!.retrieve()
            return true
        }
    }

    override fun onQueryTextChange(newText: String): Boolean {
        activity.dbMethodsAdapter!!.searchDynamic(newText)
        return true
    }

    //Срабатывает, когда активити получает результат
    fun resultActivityRecieved(){
        processRes(activity.getResult())
    }

    private fun processRes(result: ResultOfActivity?) {
        when (result!!.requestCode) {
            TEXT_RESULT -> if (result.resultCode == Activity.RESULT_OK) {
                val extras = result.data!!.extras /*if (data!!.extras != null) data.extras else null*/
                if (extras != null) {
                    val name = result.data.getStringExtra(NAME)
                    val text = result.data.getStringExtra(TEXT)
                    val get_id = result.data.getIntExtra(ID, -1)
                    if (get_id != -1) {
                        activity.dbMethodsAdapter!!.edit(get_id, name, ItemsAdapter.TYPE_ITEM_TEXT,
                                text)
                    } else {
                        activity.dbMethodsAdapter!!.save(name, ItemsAdapter.TYPE_ITEM_TEXT, text)
                    }
                    //initRecyclerView()
                }
            } else {
                toastToActivity(R.string.save_canceled)
            }
            CAMERA_RESULT -> {
                //TODO: сохранение через внешнее хранилище
                /*try {
                    val thumbnail = result.data!!.extras!!.get("data") as Bitmap
                    val imageString = bitmapToBase64(thumbnail)
                    activity.dbMethodsAdapter!!.save("1", ItemsAdapter.TYPE_ITEM_IMAGE,
                            imageString)
                    //initRecyclerView()
                } catch (ex: NullPointerException) {
                    toastToActivity(R.string.photo_chanceled)
                }*/
            }

            GALLERY_RESULT -> {
                //TODO: сохранение через внешнее хранилище
                /*val imageUri: Uri?
                val imageStream: InputStream?
                try {
                    imageUri = result.data!!.data
                    imageStream = contentResolver.openInputStream(imageUri!!)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    val imgString = bitmapToBase64(selectedImage)
                    dbMethodsAdapter!!.save("", ItemsAdapter.TYPE_ITEM_IMAGE, imgString)
                    //initRecyclerView()
                } catch (e: FileNotFoundException) {
                    Toast.makeText(this, "Изображение не получено",
                            Toast.LENGTH_SHORT).show()
                } catch (ex: NullPointerException) {
                    Toast.makeText(this, "Изображение не получено",
                            Toast.LENGTH_SHORT).show()
                } catch (exc: SQLException) {
                    Toast.makeText(this, "Некорректное изображение",
                            Toast.LENGTH_SHORT).show()
                }*/
            }
            LIST_RESULT -> if (result.resultCode == Activity.RESULT_OK) {
                val extras = result.data!!.extras
                if (extras != null) {
                    val text = result.data.getStringExtra(LIST)
                    val get_id = result.data.getIntExtra(ID, -1)
                    if (get_id != -1) {
                        activity.dbMethodsAdapter!!.edit(get_id, "", ItemsAdapter.TYPE_ITEM_LIST, text)
                    } else {
                        activity.dbMethodsAdapter!!.save("", ItemsAdapter.TYPE_ITEM_LIST, text)
                    }

                }
            }
        }
    }

    companion object {

        val NAME = "NAME"
        val TEXT = "TEXT"
        val ID = "ID"
        val LIST = "LIST"
        private val TEXT_RESULT = 1
        private val CAMERA_RESULT = 2
        private val GALLERY_RESULT = 3
        private val LIST_RESULT = 4

    }

}