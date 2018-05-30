package com.milnest.tasklist.presentation.main

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.view.View
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.ResultOfActivity
import com.milnest.tasklist.entities.TaskListItem
import com.milnest.tasklist.other.utils.PhotoInteractor
import com.milnest.tasklist.presentation.element.ItemsAdapterInterface
import com.milnest.tasklist.presentation.list.ListTaskActivity
import com.milnest.tasklist.presentation.text.TextTaskActivity
import com.milnest.tasklist.repository.DBRepository
import java.io.File

class Presenter(val activity: PresenterInterface) :
        android.support.v7.widget.SearchView.OnQueryTextListener, View.OnFocusChangeListener, View.OnClickListener {

    val dbRepo = DBRepository.getDBRepository()
    private lateinit var dialog: android.support.v7.app.AlertDialog
    private lateinit var builder : android.support.v7.app.AlertDialog.Builder/*AlertDialog.Builder*/
    var adapter: ItemsAdapterInterface? = null
    lateinit var photoFile : File

    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_task_text -> {
                activity.createTaskActivity(TEXT_RESULT, TextTaskActivity::class.java)
            }
            R.id.add_task_photo -> {
                dialog = builder.show()
                val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                activity.setColorButtons(positiveButton, negativeButton)
            }
            R.id.add_task_list -> {
                activity.createTaskActivity(LIST_RESULT, ListTaskActivity::class.java)
            }
        }
    }

    fun toastToActivity(toShow : Int) {
        activity.showToast(toShow);
    }

    fun initPhotoDialog(context: Context) {
        builder = AlertDialog.Builder(context)
        builder.setTitle("Добавление фото")
                .setMessage("Выберите источник")
                .setPositiveButton("Сделать новое") { dialog, id ->
                    photoClicked()
                }
                .setNegativeButton("Взять из галереи") { dialog, id ->
                    val photoPickerIntent = Intent(Intent.ACTION_PICK)
                    photoPickerIntent.type = "image/*"
                    activity.createTaskActivity(photoPickerIntent, CAMERA_RESULT)
                }
        dialog = builder.create()
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        //dbRepo.retrieve()
        adapter!!.setData(dbRepo.getAllTasks())
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            adapter!!.setData(dbRepo.search(query))
            return true
        } else {
            adapter!!.setData(dbRepo.getAllTasks())
            //dbRepo.retrieve()
            return true
        }
    }

    override fun onQueryTextChange(newText: String): Boolean {
        adapter!!.setData(dbRepo.searchDynamic(newText))
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
                        adapter!!.setData(dbRepo.update(get_id, name, TaskListItem.TYPE_ITEM_TEXT,
                                text))
                    } else {
                        adapter!!.setData(dbRepo.add(name, TaskListItem.TYPE_ITEM_TEXT, text))
                    }
                    //adapter!!.retrieveAdapter()
                    //initRecyclerView()
                }
            } else {
                toastToActivity(R.string.save_canceled)
            }
            CAMERA_RESULT -> {
                //TODO: сохранение через внешнее хранилище
                try {
                    //TODO получать файлик
                    adapter!!.setData(dbRepo.add("", TaskListItem.TYPE_ITEM_IMAGE,
                            photoFile.canonicalPath))

                }
                catch (ex : Exception){
                    toastToActivity(R.string.no_external)
                }
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
                        adapter!!.setData(dbRepo.update(get_id, "",
                                TaskListItem.TYPE_ITEM_LIST, text))
                    } else {
                        adapter!!.setData(dbRepo.add("", TaskListItem.TYPE_ITEM_LIST, text))
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
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))

        activity.startPhotoActivity(cameraIntent)
    }

    fun attachAdapter(itemsAdapter: ItemsAdapterInterface) {
        adapter = itemsAdapter
    }

    fun adapterStartFill() {
        adapter!!.setData(dbRepo.getAllTasks())
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