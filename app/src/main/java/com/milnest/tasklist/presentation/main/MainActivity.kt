package com.milnest.tasklist.presentation.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.milnest.tasklist.*
import com.milnest.tasklist.entities.ResultOfActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : AppCompatActivity(), MainView {
    override var mActionMode: android.support.v7.view.ActionMode? = null
    //private val mGridManager = GridLayoutManager(this, 2)
    override val mLinearLayoutManager = LinearLayoutManager(this)
    override val mGridLayoutManager = GridLayoutManager(this, 2)
    private lateinit var searchView: SearchView
    private val presenter = MainPresenter()
    private lateinit var dialog: AlertDialog
    private var splitMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
    }

    private fun bindViews() {
        setSupportActionBar(toolbar)
        presenter.attachView(this)
        presenter.setAdapter(recyclerView)
        recyclerView.layoutManager = mLinearLayoutManager

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(getString(R.string.add_photo))
                .setMessage(getString(R.string.choose_source))
                .setPositiveButton(getString(R.string.new_cam_photo)) { _, _ ->
                    presenter.photoClicked()
                }
                .setNegativeButton(getString(R.string.gallery_photo)) { _, _ ->
                    openGallery()
                }
        dialog = dialogBuilder.create()
        add_task_text.setOnClickListener(presenter.addTextTask())
        add_task_list.setOnClickListener(presenter.addListTask())
        add_task_photo.setOnClickListener(presenter.addImgTask())
    }

    //TODO: в презентер
    fun openGallery() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        createTaskActivity(photoPickerIntent, GALLERY_RESULT)
    }

    override fun showDialog() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    override fun finishActionMode() {
        mActionMode!!.finish()
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
            R.id.action_split -> /*if (recyclerView.layoutManager === mLinearLayoutManager) {
                //recyclerView.layoutManager = mGridManager
                recyclerView.adapter
                item.setIcon(R.drawable.ic_linear_split)
            } else {
                //recyclerView.layoutManager = mLinearLayoutManager
                item.setIcon(R.drawable.ic_tasks_column_split)
            }*/
                //recyclerView.layoutManager = mGridManager
            {
                splitMenuItem = item
                presenter.setAdapter(recyclerView)
            }

        }
        return true
    }

    override fun startPhotoActivity(cameraIntent: Intent) {
        startActivityForResult(cameraIntent, CAMERA_RESULT)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.processViewRes(ResultOfActivity(requestCode, resultCode, data))
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
        intentChange.putExtra(ID, itemId)
        startActivityForResult(intentChange, actResType)
    }

    override fun createTaskActivity(createTaskIntent: Intent, taskType: Int) {
        startActivityForResult(createTaskIntent, taskType)
    }

    override fun createTaskActivity(taskType: Int, taskClass: Class<*>) {
        val textIntent = Intent(this, taskClass)
        startActivity(textIntent)
    }

    override fun setSplitIcon(iconResource: Int) {
        splitMenuItem?.setIcon(iconResource)
    }
}
