package com.milnest.tasklist.presentation.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.ObserverInterfaces.Observer
import com.milnest.tasklist.entities.ResultOfActivity
import com.milnest.tasklist.entities.TaskListItem
import com.milnest.tasklist.interactor.PhotoInteractor
import com.milnest.tasklist.interactor.TaskDataInteractor
import com.milnest.tasklist.presentation.element.ItemsAdapter
import java.io.File

class Presenter(val activity: PresenterInterface) :
        android.support.v7.widget.SearchView.OnQueryTextListener, View.OnFocusChangeListener,
        Observer{

    val dbMethodsAdapter = TaskDataInteractor.getDBMethodsAdapter()
    lateinit var photoFile : File

    override fun update(notifObject : Any?) {
        if(notifObject is Int) toastToActivity(notifObject)
    }

    fun toastToActivity(toShow : Int) {
        activity.showToast(toShow);
    }



    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        dbMethodsAdapter.retrieve()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            dbMethodsAdapter.search(query)
            return true
        } else {
            dbMethodsAdapter.retrieve()
            return true
        }
    }

    override fun onQueryTextChange(newText: String): Boolean {
        dbMethodsAdapter.searchDynamic(newText)
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
                        dbMethodsAdapter.edit(get_id, name, ItemsAdapter.TYPE_ITEM_TEXT,
                                text)
                    } else {
                        dbMethodsAdapter.save(name, ItemsAdapter.TYPE_ITEM_TEXT, text)
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
                    activity.taskDataInteractor!!.save("1", ItemsAdapter.TYPE_ITEM_IMAGE,
                            imageString)
                    //initRecyclerView()
                } catch (ex: NullPointerException) {
                    toastToActivity(R.string.photo_chanceled)
                }*/

                /*val file = File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "pic.jpg")
                if (!file.mkdirs()) {
                    toastToActivity(R.string.no_external)
                }*/
                try {
                    //TODO получать файлик
                    /*val uri = result.data!!.extras!!.get(MediaStore.EXTRA_OUTPUT) as Uri
                    val file : File = File(uri.path)*/
                    //val file = File(Environment.getExternalStorageDirectory().path, "pic.jpg")
                    /*val uri = Uri.fromFile(file)
                    val bitmap = MediaStore.Images.Media.getBitmap(app.context.contentResolver, uri)
                    */
                    dbMethodsAdapter.save("", TaskListItem.TYPE_ITEM_IMAGE,
                            photoFile.canonicalPath)

                }
                catch (ex : Exception){
                    toastToActivity(R.string.no_external)
                }
                /*val file = result.data!!.extras!!.get("data") as File
                taskDataInteractor.save("", TaskListItem.TYPE_ITEM_IMAGE, file.canonicalPath)
*/            }

            GALLERY_RESULT -> {
                //TODO: сохранение через внешнее хранилище
                /*val imageUri: Uri?
                val imageStream: InputStream?
                try {
                    imageUri = result.data!!.data
                    imageStream = contentResolver.openInputStream(imageUri!!)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    val imgString = bitmapToBase64(selectedImage)
                    taskDataInteractor!!.save("", ItemsAdapter.TYPE_ITEM_IMAGE, imgString)
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
                        dbMethodsAdapter.edit(get_id, "", ItemsAdapter.TYPE_ITEM_LIST, text)
                    } else {
                        dbMethodsAdapter.save("", ItemsAdapter.TYPE_ITEM_LIST, text)
                    }

                }
            }
        }
    }

    fun photoClicked() {
        savePhoto()
    }

    private fun savePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        photoFile = PhotoInteractor.createFilePath()
        /*= File(Environment.getExternalStorageDirectory(), "pic.jpg")*/
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))

        activity.startPhotoActivity(cameraIntent)
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