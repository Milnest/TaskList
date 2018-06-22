package com.milnest.tasklist.presentation.main

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
import com.milnest.tasklist.*
import com.milnest.tasklist.entities.ResultOfActivity
import com.milnest.tasklist.entities.Task
import com.milnest.tasklist.other.utils.PhotoInteractor
import com.milnest.tasklist.presentation.list.ListTaskActivity
import com.milnest.tasklist.presentation.text.TextTaskTaskActivity
import com.milnest.tasklist.data.repository.DBRepository
import java.io.File
import java.lang.ref.WeakReference


class MainPresenter {
    val adapter = ItemsAdapter(onItemClickListener)
    private lateinit var photoFile: File
    var curPosDelete = -1
    lateinit var view: WeakReference<MainView>

    fun attachView(view: MainView) {
        this.view = WeakReference(view)
    }

    fun setAdapter(itemsView: RecyclerView) {
        itemsView.adapter = adapter
    }

    private fun notifToActivity(toShow: Int) {
        view.get()?.showNotif(toShow);
    }

    fun processViewRes(result: ResultOfActivity?) {
        if (result!!.resultCode == Activity.RESULT_OK) {
            when (result.requestCode) {
                CAMERA_RESULT -> {
                    try {
                        DBRepository.addTask("", Task.TYPE_ITEM_IMAGE, photoFile.canonicalPath)
                        adapter.setData(DBRepository.getAllTasks())
                    } catch (ex: Exception) {
                        notifToActivity(R.string.no_external)
                    }
                }

                GALLERY_RESULT -> {
                    try {
                        val img = MediaStore.Images.Media.getBitmap(App.context.contentResolver,
                                result.data!!.data)
                        //Костыль
                        val file = PhotoInteractor.saveImageToFile(img)
                        MediaStore.Images.Media.insertImage(App.context.contentResolver,
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
        view.get()?.createTaskActivity(TEXT_RESULT, TextTaskTaskActivity::class.java)
    }

    fun addListTask() = View.OnClickListener {
        view.get()?.createTaskActivity(LIST_RESULT, ListTaskActivity::class.java)
    }

    fun addImgTask() = View.OnClickListener {
        view.get()?.showDialog()
    }

    fun searchChangeFocus() = View.OnFocusChangeListener { _: View, _: Boolean ->
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
                        view.get()?.startTaskActivity(TextTaskTaskActivity::class.java as? Class<*>,
                                id, TEXT_RESULT)
                    }
                    Task.TYPE_ITEM_LIST -> {
                        view.get()?.startTaskActivity(ListTaskActivity::class.java,
                                id, LIST_RESULT)
                    }
                }
            }

            override fun onItemLongClick(position: Int): Boolean {
                if (view.get()?.mActionMode == null) {
                    curPosDelete = position
                    view.get()?.showActionBar(R.string.action_mode)
                    adapter.addSelection(position)
                } else {
                    curPosDelete = -1
                    view.get()?.closeActionBar()
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