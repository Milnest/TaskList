package com.milnest.tasklist

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
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
import android.widget.Button
import android.widget.Toast
import com.milnest.tasklist.presenter.Presenter
import com.milnest.tasklist.presenter.PresenterInterface

import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.ArrayList

class MainActivity : AppCompatActivity(), PresenterInterface {
    private var recyclerView: RecyclerView? = null
    private var mToolbar: Toolbar? = null
    private val mGridManager = GridLayoutManager(this, 2)
    private val mLinearLayoutManager = LinearLayoutManager(this)
    var mActionMode: android.support.v7.view.ActionMode? = null
    var mActionModeCallback: android.support.v7.view.ActionMode.Callback? = null
    lateinit var adapter: ItemsAdapter
    private var builder: AlertDialog.Builder? = null
    private var searchView: SearchView? = null
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
    }


    /**Заполняет Recycler тестовыми данными и инициализирует View */
    private fun setInitialData() {
        //View init
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)
        //Recycler data
        initRecyclerView()
        adapter.initActionMode()
        //Layout Manager init
        recyclerView!!.layoutManager = mLinearLayoutManager
        initPhotoDialog()
        //initPresenter()
    }

    /*private fun initPresenter() {
        //Init presenter
        val mainPresenter = Presenter(this)
        val dbMethodsAdapter = DBMethodsAdapter(mTaskListItems, )
    }*/

    private fun initSearch() {
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    search(query)
                    return true
                } else {
                    retrieve()
                    return true
                }
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchDynamic(newText)
                return true
            }
        })

        searchView!!.setOnQueryTextFocusChangeListener { v, hasFocus -> retrieve() }

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
        when (requestCode) {
            TEXT_RESULT -> if (resultCode == Activity.RESULT_OK) {
                val extras = data!!.extras /*if (data!!.extras != null) data.extras else null*/
                if (extras != null) {
                    val name = data.getStringExtra(NAME)
                    val text = data.getStringExtra(TEXT)
                    val get_id = data.getIntExtra(ID, -1)
                    if (get_id != -1) {
                        edit(get_id, name, ItemsAdapter.TYPE_ITEM_TEXT, text)
                    } else {
                        save(name, ItemsAdapter.TYPE_ITEM_TEXT, text)
                    }
                }
            } else {
                Toast.makeText(this, R.string.save_canceled, Toast.LENGTH_SHORT).show()
            }
            CAMERA_RESULT -> try {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                val imageString = bitmapToBase64(thumbnail)
                save("1", ItemsAdapter.TYPE_ITEM_IMAGE, imageString)
            } catch (ex: NullPointerException) {
                Toast.makeText(this, "Фото не сделано", Toast.LENGTH_SHORT).show()
            }

            GALLERY_RESULT -> {
                val imageUri: Uri?
                val imageStream: InputStream?
                try {
                    imageUri = data!!.data
                    imageStream = contentResolver.openInputStream(imageUri!!)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    val imgString = bitmapToBase64(selectedImage)
                    save("", ItemsAdapter.TYPE_ITEM_IMAGE, imgString)
                } catch (e: FileNotFoundException) {
                    Toast.makeText(this, "Изображение не получено",
                            Toast.LENGTH_SHORT).show()
                } catch (ex: NullPointerException) {
                    Toast.makeText(this, "Изображение не получено",
                            Toast.LENGTH_SHORT).show()
                } catch (exc: SQLException) {
                    Toast.makeText(this, "Некорректное изображение",
                            Toast.LENGTH_SHORT).show()
                }

            }
            LIST_RESULT -> if (resultCode == Activity.RESULT_OK) {
                val extras = data!!.extras
                if (extras != null) {
                    val text = data.getStringExtra(LIST)
                    val get_id = data.getIntExtra(ID, -1)
                    if (get_id != -1) {
                        edit(get_id, "", ItemsAdapter.TYPE_ITEM_LIST, text)
                    } else {
                        save("", ItemsAdapter.TYPE_ITEM_LIST, text)
                    }

                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    //Преобразует картинку в Base64 для хранения в БД
    private fun bitmapToBase64(selectedImage: Bitmap): String {
        val stream = ByteArrayOutputStream()
        selectedImage.compress(Bitmap.CompressFormat.PNG, 1, stream)
        val imgString = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
        return imgString
    }

    /**Обновляет список задач
     */
    fun retrieve() {
        val db = DBAdapter(this)

        //OPEN
        db.openDB()

        mTaskListItems.clear()

        //SELECT
        val c = db.allTasks

        //DATA ADDING TO ARRAYLIST
        showItems(c)

        db.close()

    }

    /**Отображает список задач, согласно текущим условиям(в том числе и для поиска)
     */
    private fun showItems(c: Cursor) {
        adapter.notifyDataSetChanged()
        while (c.moveToNext()) {
            val id = c.getInt(0)
            val name = c.getString(1)
            val type = c.getInt(2)
            val content = c.getString(3)

            when (type) {
                ItemsAdapter.TYPE_ITEM_TEXT -> mTaskListItems.add(TextTaskListItem(id, name, content))
                ItemsAdapter.TYPE_ITEM_IMAGE -> {
                    val bytes = Base64.decode(content, Base64.DEFAULT)
                    mTaskListItems.add(ImgTaskListItem(id, name,
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)))
                }
                ItemsAdapter.TYPE_ITEM_LIST -> {
                    //Вместо этого парс
                    /*List<CheckboxTaskListItem> cbList = new ArrayList<>();
                        cbList.add(new CheckboxTaskListItem("текст", true));
                        cbList.add(new CheckboxTaskListItem("текст", false));
                        mTaskListItems.add(new ListOfCheckboxesTaskListItem(id, name, type, cbList));*/

                    /*GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();*/

                    //gson.fromJson(new String(content), ListOfCheckboxesTaskListItem.class);

                    /*mTaskListItems.add(gson.fromJson(new String(content), ListOfCheckboxesTaskListItem.class));*/
                    val cbList = JsonAdapter.fromJson(content)
                    cbList.id = id
                    mTaskListItems.add(cbList)
                }
            }
        }
    }


    /**Сохраняет новую задачу
     */
    fun save(name: String, type: Int, content: String) {
        val db = DBAdapter(this)

        //OPEN
        db.openDB()

        //INSERT
        val result = db.add(name, type, content)

        if (result > 0) {
            Toast.makeText(this, "Задача добавлена!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Ошибка добавления!", Toast.LENGTH_SHORT).show()
        }

        //CLOSE
        db.close()

        //refresh
        retrieve()

    }

    /**Удаляет значение по id
     */
    fun delete(id: Int) {
        val db = DBAdapter(this)

        //OPEN
        db.openDB()

        //INSERT
        val result = db.Delete(id)

        if (result > 0) {
            Toast.makeText(this, "Задача успешно удалена!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Ошибка удаления!", Toast.LENGTH_SHORT).show()
        }

        //CLOSE
        db.close()

        //refresh
        retrieve()

    }

    /**Выполняет получение значения по id
     */
    fun getById(id: Int): Array<String> {

        val db = DBAdapter(this)
        db.openDB()

        //SELECT
        val c = db.allTasks
        var name = ""
        var content = ""

        //LOOP THRU THE DATA ADDING TO ARRAYLIST
        while (c.moveToNext()) {
            val get_id = c.getInt(0)
            //TODO перекодить, забирая значения из БД напрямую!
            if (get_id == id) {
                name = c.getString(1)
                //int type = c.getInt(2);
                content = c.getString(3)
            }
        }
        db.close()

        return arrayOf(name, content)
    }

    /**Осуществляет редактирование значения по id
     */
    fun edit(id: Int, name: String, type: Int, content: String) {
        val db = DBAdapter(this)

        //OPEN
        db.openDB()

        //INSERT
        val result = db.UPDATE(id, name, type, content)

        if (result > 0) {
            Toast.makeText(this, "Задача изменена!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Ошибка изменения!", Toast.LENGTH_SHORT).show()
        }

        //CLOSE
        db.close()

        //refresh
        retrieve()

    }

    /**Осуществляет простой поиск из строки прямым сравнением значений
     */
    fun search(textToSearch: String?) {
        val db = DBAdapter(this)
        db.openDB()
        mTaskListItems.clear()
        val c = db.Search(textToSearch!!)
        showItems(c)
        db.close()
    }

    /**Осуществляет динамический поиск из строки
     */
    fun searchDynamic(textToSearch: String) {
        val db = DBAdapter(this)
        db.openDB()
        mTaskListItems.clear()
        val c = db.SearchDynamic(textToSearch)
        showItems(c)
        db.close()
    }

    override fun showToast(toShow: String) {
        Toast.makeText(this, toShow, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        retrieve()
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
    }

}
