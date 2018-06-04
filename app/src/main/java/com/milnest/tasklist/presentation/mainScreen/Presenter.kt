package com.milnest.tasklist.presentation.mainScreen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.milnest.tasklist.R
import com.milnest.tasklist.application.app
import com.milnest.tasklist.entities.ResultOfActivity
import com.milnest.tasklist.entities.TaskListItem
import com.milnest.tasklist.other.utils.PhotoInteractor
import com.milnest.tasklist.presentation.element.ItemsAdapter
import com.milnest.tasklist.presentation.listScreen.ListTaskActivity
import com.milnest.tasklist.presentation.textScreen.TextTaskActivity
import com.milnest.tasklist.repository.DBRepository
import java.io.File


class Presenter(val view: PresenterInterface) {
    var adapter: ItemsAdapter? = null
    lateinit var photoFile: File
    var curPosDelete : Int? = null

    fun setAdapter(itemsView: RecyclerView){
        adapter = ItemsAdapter(onItemClickListener)
        itemsView.adapter = adapter
    }

    fun notifToActivity(toShow: Int) {
        view.showNotif(toShow);
    }

    fun resultActivityRecieved() {
        processRes(view.getResult())
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
                        DBRepository.updateTask(get_id, name, TaskListItem.TYPE_ITEM_TEXT, text)
                        adapter!!.setData(DBRepository.getAllTasks())
                    } else {
                        DBRepository.addTask(name, TaskListItem.TYPE_ITEM_TEXT, text)
                        adapter!!.setData(DBRepository.getAllTasks())
                    }
                }
            } else {
                notifToActivity(R.string.save_canceled)
            }
            CAMERA_RESULT -> {
                try {
                    DBRepository.addTask("", TaskListItem.TYPE_ITEM_IMAGE, photoFile.canonicalPath)
                    adapter!!.setData(DBRepository.getAllTasks())
                } catch (ex: Exception) {
                    notifToActivity(R.string.no_external)
                }
            }

            GALLERY_RESULT -> {
                try {
                    val img = MediaStore.Images.Media.getBitmap(app.context.contentResolver,
                            result.data!!.data)
                    val file = PhotoInteractor.saveImageToFile(img)
                    MediaStore.Images.Media.insertImage(app.context.contentResolver,
                            file.canonicalPath, file.name, file.name)
                    DBRepository.addTask("", TaskListItem.TYPE_ITEM_IMAGE, file.canonicalPath)
                    adapter!!.setData(DBRepository.getAllTasks())
                } catch (ex: Exception) {
                    notifToActivity(R.string.no_external)
                }
            }
            LIST_RESULT -> if (result.resultCode == Activity.RESULT_OK) {
                val extras = result.data!!.extras
                if (extras != null) {
                    val text = result.data.getStringExtra(LIST)
                    val get_id = result.data.getIntExtra(ID, -1)
                    if (get_id != -1) {
                        DBRepository.updateTask(get_id, "",
                                TaskListItem.TYPE_ITEM_LIST, text)
                        adapter!!.setData(DBRepository.getAllTasks())
                    } else {
                        DBRepository.addTask("", TaskListItem.TYPE_ITEM_LIST, text)
                        adapter!!.setData(DBRepository.getAllTasks())
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

        view.startPhotoActivity(cameraIntent)
    }

    fun updateList() {
        adapter!!.setData(DBRepository.getAllTasks())
    }

    fun addTextTask() = View.OnClickListener {
        view.createTaskActivity(TEXT_RESULT, TextTaskActivity::class.java)
    }

    fun addListTask() = View.OnClickListener {
        view.createTaskActivity(LIST_RESULT, ListTaskActivity::class.java)
    }

    fun addImgTask() = View.OnClickListener {
        view.showDialog()
    }

    fun searchChangeFocus() = View.OnFocusChangeListener { view: View, b: Boolean ->
        adapter!!.setData(DBRepository.getAllTasks())
    }

    val searchListener: SearchView.OnQueryTextListener
        get() = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query == null) adapter!!.setData(DBRepository.getAllTasks())
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter!!.setData(DBRepository.searchDynamicTask(newText))
                return true
            }
        }

    val onItemClickListener : ItemsAdapter.IClickListener
        get() = object : ItemsAdapter.IClickListener {
            override fun onItemClick(position: Int) {
                val id = adapter!!.getItemId(position).toInt()
                val type = adapter!!.getItemViewType(position)

                when (type) {
                    TaskListItem.TYPE_ITEM_TEXT -> {
                        view.startTaskActivity(TextTaskActivity::class.java as? Class<*>,
                                id, MainActivity.TEXT_RESULT/*, arrayOf((item as TextTaskListItem).name, item.text)*/)
                    }
                    TaskListItem.TYPE_ITEM_LIST -> {
                        view.startTaskActivity(ListTaskActivity::class.java,
                                id, MainActivity.LIST_RESULT/*, taskRepo.getTaskById(id)*/)
                    }
                }
            }

            override fun onItemLongClick(position: Int): Boolean {
                if (view.mActionMode == null) {
                    curPosDelete = position
                    view.showActionBar(R.string.action_mode)
                    //Добавление выделения при выборе
                    adapter!!.addSelection(position)
                } else {
                    curPosDelete = null
                    view.closeActionBar()
                    //Сброс выделения
                    adapter!!.removeSelection()
                }
                return true
            }
        }

    val onActionModeListener : android.support.v7.view.ActionMode.Callback
        get() = object : android.support.v7.view.ActionMode.Callback {
            override fun onCreateActionMode(mode: android.support.v7.view.ActionMode, menu: Menu):
                    Boolean {
                val inflater = mode.menuInflater
                inflater.inflate(R.menu.menu_context_task, menu)
                return true
            }

            override fun onPrepareActionMode(mode: android.support.v7.view.ActionMode, menu: Menu):
                    Boolean {
                return false
            }

            override fun onActionItemClicked(mode: android.support.v7.view.ActionMode,
                                             item: MenuItem): Boolean {
                if (curPosDelete is Int) {
                    DBRepository.deleteTask(adapter!!.getItemId(curPosDelete!!).toInt())
                    adapter!!.setData(DBRepository.getAllTasks())
                    view.mActionMode!!.finish()
                }
                return false
            }

            override fun onDestroyActionMode(mode: android.support.v7.view.ActionMode) {
                adapter!!.removeSelection()
                view.mActionMode = null
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