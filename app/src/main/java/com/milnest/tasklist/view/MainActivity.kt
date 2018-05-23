package com.milnest.tasklist.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.SQLException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.ResultOfActivity
import com.milnest.tasklist.entities.TaskListItem
import com.milnest.tasklist.presenter.Presenter
import com.milnest.tasklist.presenter.PresenterInterface
import com.milnest.tasklist.repository.DBAdapter
import com.milnest.tasklist.interactor.DBMethodsAdapter
import com.milnest.tasklist.presenter.ActModeInterface
import com.milnest.tasklist.presenter.RecyclerHolderPresenter

import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.ArrayList

class MainActivity : AppCompatActivity(), PresenterInterface, ActModeInterface {

    private var recyclerView: RecyclerView? = null
    private var mToolbar: Toolbar? = null
    private val mGridManager = GridLayoutManager(this, 2)
    private val mLinearLayoutManager = LinearLayoutManager(this)
    override var mActionMode: android.support.v7.view.ActionMode? = null
    var mActionModeCallback: android.support.v7.view.ActionMode.Callback? = null
    lateinit var adapter: ItemsAdapter
    private var builder: AlertDialog.Builder? = null
    private var searchView: SearchView? = null
    private var dialog: AlertDialog? = null
    override var dbMethodsAdapter : DBMethodsAdapter? = null
    private val mainPresenter = Presenter(this)

    //Для результата в презентер
    private var resAct: ResultOfActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity = this
        setInitialData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView!!.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        initSearch()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_split -> if (recyclerView!!.layoutManager === mLinearLayoutManager) {
                recyclerView!!.layoutManager = mGridManager
                item.setIcon(R.drawable.ic_linear_split)
            } else {
                recyclerView!!.layoutManager = mLinearLayoutManager
                item.setIcon(R.drawable.ic_tasks_column_split)
            }
        }/*case R.id.action_search:*/
        return true
    }


    /**Инициализирует Recycler */
    private fun initRecyclerView() {
        recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        // создаем адаптер
        adapter = ItemsAdapter(mTaskListItems, this)
        // устанавливаем для списка адаптер
        recyclerView!!.adapter = adapter
        RecyclerHolderPresenter.attachAdapter(adapter)
    }


    /**Заполняет Recycler тестовыми данными и инициализирует View */
    private fun setInitialData() {
        //View init
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)

        initPresenter()

        RecyclerHolderPresenter.attachView(this)
        //Recycler data
        initRecyclerView()
        adapter.initActionMode()
        //Layout Manager init
        recyclerView!!.layoutManager = mLinearLayoutManager
        initPhotoDialog()

        dbMethodsAdapter!!.open()

        //initSearch()
    }

    private fun initPresenter() {
        //Init presenter
        //val mainPresenter = Presenter(this)
        //DBAdapter.setDBAdapter(this)
        //val db = DBAdapter.getDBAdapter()
        //dbMethodsAdapter = DBMethodsAdapter(mTaskListItems, db!!, adapter, this)
        DBMethodsAdapter.setDBMethodsAdapter(this)
        dbMethodsAdapter = DBMethodsAdapter.getDBMethodsAdapter()
    }

    private fun initSearch() {
        searchView!!.setOnQueryTextListener(mainPresenter)
        searchView!!.setOnQueryTextFocusChangeListener(mainPresenter)
    }

    /**Отображает диалог выбора места получения изображеемя
     */
    private fun initPhotoDialog() {
        builder = AlertDialog.Builder(this)
        builder!!.setTitle("Добавление фото")
                .setMessage("Выберите источник")
                .setPositiveButton("Сделать новое") { dialog, id ->
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAMERA_RESULT)
                }
                .setNegativeButton("Взять из галереи") { dialog, id ->
                    //Вызываем стандартную галерею для выбора изображения с помощью
                    // Intent.ACTION_PICK:
                    val photoPickerIntent = Intent(Intent.ACTION_PICK)
                    //Тип получаемых объектов - image:
                    photoPickerIntent.type = "image/*"
                    //Запускаем переход с ожиданием обратного результата в виде информации
                    // об изображении:
                    startActivityForResult(photoPickerIntent, GALLERY_RESULT)
                }
        dialog = builder!!.create()

    }

    fun OnClick(view: View) {
        //TODO : в презентер
        when (view.id) {
            R.id.add_task_text -> {
                val textIntent = Intent(this, TextTaskActivity::class.java)
                startActivityForResult(textIntent, TEXT_RESULT)
            }
            R.id.add_task_photo -> {
                //Применяем стили для кнопок диалога
                dialog = builder!!.show()
                val positiveButton = dialog!!.getButton(DialogInterface.BUTTON_POSITIVE)
                positiveButton.setTextColor(resources.getColor(R.color.lum_red))
                val negativeButton = dialog!!.getButton(DialogInterface.BUTTON_NEGATIVE)
                negativeButton.setTextColor(resources.getColor(R.color.lum_red))
            }
            R.id.add_task_list -> {
                val listIntent = Intent(this, ListTaskActivity::class.java)
                startActivityForResult(listIntent, LIST_RESULT)
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        saveResult(ResultOfActivity(requestCode, resultCode, data))
        mainPresenter.resultActivityRecieved()
    }

    private fun saveResult(res : ResultOfActivity) {
        resAct = res
    }

    override fun getResult() : ResultOfActivity?{
        return resAct
    }

    //Преобразует картинку в Base64 для хранения в БД
    private fun bitmapToBase64(selectedImage: Bitmap): String {
        val stream = ByteArrayOutputStream()
        selectedImage.compress(Bitmap.CompressFormat.PNG, 1, stream)
        val imgString = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
        return imgString
    }

    override fun showToast(toShow: Int) {
        Toast.makeText(this, getString(toShow), Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        dbMethodsAdapter!!.retrieve();
    }

    override fun onDestroy() {
        super.onDestroy()
        dbMethodsAdapter!!.close()
    }

    override fun showActionBar(title: Int) {
        mActionMode = startSupportActionMode(mActionModeCallback!!)
        mActionMode!!.title = getString(title)
    }

    override fun closeActionBar() {
        mActionMode!!.finish()
    }

    override fun startTaskActivity(activityClass: Class<*>?, itemId: Int, actResType: Int) {
        val intentChange = Intent(this, activityClass)
        intentChange.putExtra("data", dbMethodsAdapter!!.getById(itemId))
        intentChange.putExtra("id", itemId)
        startActivityForResult(intentChange, actResType)
    }

    companion object {

        var mTaskListItems: MutableList<TaskListItem> = ArrayList()
        val NAME = "NAME"
        val TEXT = "TEXT"
        val ID = "ID"
        val LIST = "LIST"
        val TEXT_RESULT = 1
        private val CAMERA_RESULT = 2
        private val GALLERY_RESULT = 3
        val LIST_RESULT = 4

        @SuppressLint("StaticFieldLeak")
        lateinit var activity: MainActivity
    }

}
