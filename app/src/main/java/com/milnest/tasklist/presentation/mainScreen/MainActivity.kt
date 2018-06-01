package com.milnest.tasklist.presentation.mainScreen

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.ResultOfActivity
import com.milnest.tasklist.presentation.element.ActModeInterface
import com.milnest.tasklist.presentation.element.ItemsAdapter
import com.milnest.tasklist.presentation.element.RecyclerListPresenter


class MainActivity : AppCompatActivity(), PresenterInterface, ActModeInterface {
    override var mActionMode: android.support.v7.view.ActionMode? = null
    private lateinit var recyclerView: RecyclerView
    private val mGridManager = GridLayoutManager(this, 2)
    private val mLinearLayoutManager = LinearLayoutManager(this)
    private lateinit var adapter: ItemsAdapter
    private lateinit var searchView: SearchView
    private lateinit var addTaskText : View
    private lateinit var addTaskList : View
    private lateinit var addTaskImg : View
    private val mainPresenter = Presenter(this)
    private val listPresenter = RecyclerListPresenter()
    //Для результата в презентер
    private var resAct: ResultOfActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setInitialData()
        lateinit var context: Context
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        initSearch()
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


    /**Инициализирует Recycler */
    private fun initRecyclerView() {
        recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        adapter = ItemsAdapter(listPresenter.onItemClickListener)
        recyclerView.adapter = adapter
        RecyclerListPresenter.attachAdapter(adapter)
        mainPresenter.attachAdapter(adapter)
        mainPresenter.adapterStartFill()
    }

    /**Заполняет Recycler тестовыми данными и инициализирует View */
    private fun setInitialData() {
        val mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)
        RecyclerListPresenter.attachView(this)
        initRecyclerView()
        //mActionModeCallback = listPresenter
        recyclerView.layoutManager = mLinearLayoutManager
        initPresenter()
    }

    private fun initPresenter() {
        mainPresenter.initPhotoDialog(this)
        addTaskText = findViewById(R.id.add_task_text)
        addTaskText.setOnClickListener(mainPresenter.addTextTask())
        addTaskList = findViewById(R.id.add_task_list)
        addTaskList.setOnClickListener(mainPresenter.addListTask())
        addTaskImg = findViewById(R.id.add_task_photo)
        addTaskImg.setOnClickListener(mainPresenter.addImgTask())
    }

    private fun initSearch() {
        searchView.setOnQueryTextListener(mainPresenter.searchListener)
        searchView.setOnQueryTextFocusChangeListener(mainPresenter.searchChangeFocus())
    }

    override fun startPhotoActivity(cameraIntent: Intent) {
        startActivityForResult(cameraIntent, CAMERA_RESULT)
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

    override fun showNotif(toShow: Int) {
        Toast.makeText(this, getString(toShow), Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        mainPresenter.adapterStartFill()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun showActionBar(title: Int) {
        mActionMode = startSupportActionMode(listPresenter.onActionModeListener)
        mActionMode!!.title = getString(title)
    }

    override fun closeActionBar() {
        mActionMode!!.finish()
    }

    override fun startTaskActivity(activityClass: Class<*>?, itemId: Int, actResType: Int
                                   /*, task: Array<String?>*/) {
        val intentChange = Intent(this, activityClass)
        /*intentChange.putExtra("data", task)*/
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

    override fun setColorButtons(positiveButton: Button, negativeButton: Button) {
        positiveButton.setTextColor(resources.getColor(R.color.lum_red))
        negativeButton.setTextColor(resources.getColor(R.color.lum_red))
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
