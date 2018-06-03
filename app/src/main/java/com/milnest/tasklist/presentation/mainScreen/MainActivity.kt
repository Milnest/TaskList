package com.milnest.tasklist.presentation.mainScreen

import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.ResultOfActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), PresenterInterface {
    override var mActionMode: android.support.v7.view.ActionMode? = null
    private val mGridManager = GridLayoutManager(this, 2)
    private val mLinearLayoutManager = LinearLayoutManager(this)
    private lateinit var searchView: SearchView
    private val presenter = Presenter(this)
    private var resAct: ResultOfActivity? = null
    private lateinit var dialogBuilder : AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
    }

    private fun bindViews() {
        val mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)
        presenter.setAdapter(recyclerView)
        recyclerView.layoutManager = mLinearLayoutManager

        dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Добавление фото")
                .setMessage("Выберите источник")
                .setPositiveButton("Сделать новое") { _, _ ->
                    presenter.photoClicked()
                }
                .setNegativeButton("Взять из галереи") { _, _ ->
                    //presenter.takeFromGallery()
                    //presenter.saveImageToFile()
                    openGallery()
                }
        dialogBuilder.create()

        add_task_text.setOnClickListener(presenter.addTextTask())
        add_task_list.setOnClickListener(presenter.addListTask())
        add_task_photo.setOnClickListener(presenter.addImgTask())
    }

    override fun showDialog() {
        val dialog = dialogBuilder.show()
        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        positiveButton.setTextColor(resources.getColor(R.color.lum_red))
        negativeButton.setTextColor(resources.getColor(R.color.lum_red))
    }

    fun openGallery() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        createTaskActivity(photoPickerIntent, GALLERY_RESULT)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(presenter.searchListener)
        searchView.setOnQueryTextFocusChangeListener(presenter.searchChangeFocus())
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_split -> if (recyclerView.layoutManager === mLinearLayoutManager) {
                recyclerView.layoutManager = mGridManager
                item.setIcon(R.drawable.ic_linear_split)
            } else {
                recyclerView.layoutManager = mLinearLayoutManager
                item.setIcon(R.drawable.ic_tasks_column_split)
            }
        }
        return true
    }

    override fun startPhotoActivity(cameraIntent: Intent) {
        startActivityForResult(cameraIntent, CAMERA_RESULT)
    }

    /*override fun startImageActivity(galleryIntent: Intent) {
        startActivityForResult(galleryIntent, GALLERY_RESULT)
    }*/

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        resAct = ResultOfActivity(requestCode, resultCode, data)
        presenter.resultActivityRecieved()
    }

    override fun getResult() : ResultOfActivity?{
        return resAct
    }

    override fun showNotif(toShow: Int) {
        Toast.makeText(this, getString(toShow), Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        presenter.updateList()
    }

    override fun showActionBar(title: Int) {
        mActionMode = startSupportActionMode(presenter.onActionModeListener)
        mActionMode!!.title = getString(title)
    }

    override fun closeActionBar() {
        mActionMode!!.finish()
    }

    override fun startTaskActivity(activityClass: Class<*>?, itemId: Int, actResType: Int) {
        val intentChange = Intent(this, activityClass)
        intentChange.putExtra("id", itemId)
        startActivityForResult(intentChange, actResType)
    }

    override fun createTaskActivity(createTaskIntent: Intent, taskType: Int) {
        startActivityForResult(createTaskIntent, taskType)
    }

    override fun createTaskActivity(taskType: Int, taskClass: Class<*>) {
        val textIntent = Intent(this, taskClass)
        startActivityForResult(textIntent, taskType)
    }

    companion object {
        val NAME = "NAME"
        val TEXT = "TEXT"
        val ID = "ID"
        val LIST = "LIST"
        val TEXT_RESULT = 1
        private val CAMERA_RESULT = 2
        private val GALLERY_RESULT = 3
        val LIST_RESULT = 4
    }

}
