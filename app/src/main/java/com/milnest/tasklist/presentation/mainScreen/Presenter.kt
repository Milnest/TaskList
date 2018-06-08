package com.milnest.tasklist.presentation.mainScreen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.support.v7.view.ActionMode
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.milnest.tasklist.R
import com.milnest.tasklist.IntentData
import com.milnest.tasklist.app
import com.milnest.tasklist.entities.ResultOfActivity
import com.milnest.tasklist.entities.Task
import com.milnest.tasklist.other.utils.PhotoInteractor
import com.milnest.tasklist.presentation.element.ItemsAdapter
import com.milnest.tasklist.presentation.listScreen.ListTaskActivity
import com.milnest.tasklist.presentation.textScreen.TextTaskActivity
import com.milnest.tasklist.data.repository.DBRepository
import java.io.File
import java.lang.ref.WeakReference


class Presenter {
    val adapter = ItemsAdapter(onItemClickListener)
    lateinit var photoFile: File
    var curPosDelete = -1
    lateinit var view: WeakReference<PresenterInterface>

    fun attachView(view: PresenterInterface) {
        this.view = WeakReference(view)
    }

    fun setAdapter(itemsView: RecyclerView) {
        itemsView.adapter = adapter
    }

    fun notifToActivity(toShow: Int) {
        view.get()?.showNotif(toShow);
    }

    fun processViewRes(result: ResultOfActivity?) {
        if (result!!.resultCode == Activity.RESULT_OK) {
            when (result.requestCode) {
                IntentData.CAMERA_RESULT -> {
                    try {
                        DBRepository.addTask("", Task.TYPE_ITEM_IMAGE, photoFile.canonicalPath)
                        adapter.setData(DBRepository.getAllTasks())
                    } catch (ex: Exception) {
                        notifToActivity(R.string.no_external)
                    }
                }

                IntentData.GALLERY_RESULT -> {
                    try {
                        val img = MediaStore.Images.Media.getBitmap(app.context.contentResolver,
                                result.data!!.data)
                        //Костыль
                        val file = PhotoInteractor.saveImageToFile(img)
                        MediaStore.Images.Media.insertImage(app.context.contentResolver,
                                file.canonicalPath, file.name, file.name)
                        DBRepository.addTask("", Task.TYPE_ITEM_IMAGE, file.canonicalPath)
                        adapter.setData(DBRepository.getAllTasks())
                    } catch (ex: Exception) {
                        notifToActivity(R.string.no_external)
                    }
                }
            }
        }
        else notifToActivity(R.string.save_canceled)
    }

    fun photoClicked() {
        savePhoto()
    }

    private fun savePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        photoFile = PhotoInteractor.createFilePath()
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))

        view.get()?.startPhotoActivity(cameraIntent)
    }

    fun updateList() {
        adapter.setData(DBRepository.getAllTasks())
    }

    fun addTextTask() = View.OnClickListener {
        view.get()?.createTaskActivity(IntentData.TEXT_RESULT, TextTaskActivity::class.java)
    }

    fun addListTask() = View.OnClickListener {
        view.get()?.createTaskActivity(IntentData.LIST_RESULT, ListTaskActivity::class.java)
    }

    fun addImgTask() = View.OnClickListener {
        view.get()?.showDialog()
    }

    fun searchChangeFocus() = View.OnFocusChangeListener { view: View, b: Boolean ->
        adapter.setData(DBRepository.getAllTasks())
    }

    val searchListener: SearchView.OnQueryTextListener
        get() = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isEmpty()) adapter.setData(DBRepository.getAllTasks())
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.setData(DBRepository.searchDynamicTask(newText))
                return true
            }
        }

    private val onItemClickListener: ItemsAdapter.IClickListener
        get() = object : ItemsAdapter.IClickListener {
            override fun onItemClick(position: Int) {
                val id = adapter.getItemId(position).toInt()
                val type = adapter.getItemViewType(position)

                when (type) {
                    Task.TYPE_ITEM_TEXT -> {
                        view.get()?.startTaskActivity(TextTaskActivity::class.java as? Class<*>,
                                id, IntentData.TEXT_RESULT)
                    }
                    Task.TYPE_ITEM_LIST -> {
                        view.get()?.startTaskActivity(ListTaskActivity::class.java,
                                id, IntentData.LIST_RESULT)
                    }
                }
            }

            override fun onItemLongClick(position: Int): Boolean {
                if (view.get()?.mActionMode == null) {
                    curPosDelete = position
                    view.get()?.showActionBar(R.string.action_mode)
                    //Добавление выделения при выборе
                    adapter.addSelection(position)
                } else {
                    curPosDelete = -1
                    view.get()?.closeActionBar()
                    //Сброс выделения
                    adapter.removeSelection()
                }
                return true
            }
        }

    val onActionModeListener: ActionMode.Callback
        get() = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                val inflater = mode.menuInflater
                inflater.inflate(R.menu.menu_context_task, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                if (curPosDelete != -1) {
                    DBRepository.deleteTask(adapter.getItemId(curPosDelete))
                    adapter.setData(DBRepository.getAllTasks())
                    view.get()?.finishActionMode()
                }
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                adapter.removeSelection()
                view.get()?.mActionMode = null
            }
        }
}