package com.milnest.tasklist;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static List<TaskListItem> mTaskListItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private Toolbar mToolbar;
    private GridLayoutManager mGridManager = new GridLayoutManager(this, 2);
    private LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
    public android.support.v7.view.ActionMode mActionMode;
    public android.support.v7.view.ActionMode.Callback mActionModeCallback;
    public ItemsAdapter adapter;
    private AlertDialog.Builder builder;
    public static final String NAME = "NAME";
    public static final String TEXT = "TEXT";
    public static final String ID = "ID";
    public static final String LIST = "LIST";
    public static final int TEXT_RESULT = 1;
    private static final int CAMERA_RESULT = 2;
    private static final int GALLERY_RESULT = 3;
    public static final int LIST_RESULT = 4;
    private SearchView searchView;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setInitialData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        initSearch();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_split:
                if (recyclerView.getLayoutManager() == mLinearLayoutManager) {
                    recyclerView.setLayoutManager(mGridManager);
                    item.setIcon(R.drawable.ic_linear_split);
                }
                else {
                    recyclerView.setLayoutManager(mLinearLayoutManager);
                    item.setIcon(R.drawable.ic_tasks_column_split);
                }
                break;
            /*case R.id.action_search:*/
        }
        return true;
    }


    /**Инициализирует Recycler*/
    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // создаем адаптер
        adapter = new ItemsAdapter(mTaskListItems, this);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
    }


    /**Заполняет Recycler тестовыми данными и инициализирует View*/
    private void setInitialData() {
        //View init
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //Recycler data
        initRecyclerView();
        adapter.initActionMode();
        //Layout Manager init
        recyclerView.setLayoutManager(mLinearLayoutManager);
        initPhotoDialog();
    }

    private void initSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null) {
                    search(query);
                    return true;
                }
                else{
                    retrieve();
                    return true;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchDynamic(newText);
                return true;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                retrieve();
            }
        });

    }

    /**Отображает диалог выбора места получения изображеемя
     * */
    private void initPhotoDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавление фото")
                .setMessage("Выберите источник")
                .setPositiveButton("Сделать новое", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_RESULT);
                    }
                })
                .setNegativeButton("Взять из галереи", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Вызываем стандартную галерею для выбора изображения с помощью
                        // Intent.ACTION_PICK:
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        //Тип получаемых объектов - image:
                        photoPickerIntent.setType("image/*");
                        //Запускаем переход с ожиданием обратного результата в виде информации
                        // об изображении:
                        startActivityForResult(photoPickerIntent, GALLERY_RESULT);

                    }
                });
        dialog = builder.create();

    }

    public void OnClick(View view){
        switch (view.getId()){
            case R.id.add_task_text:
                Intent textIntent = new Intent(this, TextTaskActivity.class);
                startActivityForResult(textIntent, TEXT_RESULT);
                break;
            case R.id.add_task_photo:
                //Применяем стили для кнопок диалога
                dialog = builder.show();
                Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setTextColor(getResources().getColor(R.color.lum_red));
                Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setTextColor(getResources().getColor(R.color.lum_red));
                break;
            case R.id.add_task_list:
                Intent listIntent = new Intent(this, ListTaskActivity.class);
                startActivityForResult(listIntent, LIST_RESULT);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TEXT_RESULT:
                if(resultCode==RESULT_OK){
                    Bundle extras = data.getExtras();
                    if(extras != null){
                        String name = data.getStringExtra(NAME);
                        String text = data.getStringExtra(TEXT);
                        int get_id = data.getIntExtra(ID, -1);
                        if(get_id != -1){
                            edit(get_id, name, ItemsAdapter.TYPE_ITEM_TEXT, text);
                        }
                        else {
                        save(name, ItemsAdapter.TYPE_ITEM_TEXT, text);
                        }
                    }
                }
                else{
                    Toast.makeText(this, R.string.save_canceled, Toast.LENGTH_SHORT).show();
                }
                break;
            case CAMERA_RESULT:
                try {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    String imageString = bitmapToBase64(thumbnail);
                    save("1", ItemsAdapter.TYPE_ITEM_IMAGE, imageString);
                }
                catch (NullPointerException ex){
                    Toast.makeText(this, "Фото не сделано", Toast.LENGTH_SHORT).show();
                }
                break;
            case GALLERY_RESULT:
                Uri imageUri;
                InputStream imageStream;
                try {
                    imageUri = data.getData();
                    imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    String imgString = bitmapToBase64(selectedImage);
                    save("", ItemsAdapter.TYPE_ITEM_IMAGE, imgString);
                }
                catch (FileNotFoundException  e) {
                    Toast.makeText(this, "Изображение не получено",
                            Toast.LENGTH_SHORT).show();
                }
                catch (NullPointerException ex){
                    Toast.makeText(this, "Изображение не получено",
                            Toast.LENGTH_SHORT).show();
                }
                catch (SQLException exc){
                    Toast.makeText(this, "Некорректное изображение",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case LIST_RESULT:
                if (resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    if(extras != null){
                        String text = data.getStringExtra(LIST);
                        int get_id = data.getIntExtra(ID, -1);
                        if(get_id != -1){
                            edit(get_id, "", ItemsAdapter.TYPE_ITEM_LIST, text);
                        }
                        else {
                            save("", ItemsAdapter.TYPE_ITEM_LIST, text);
                        }

                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    //Преобразует картинку в Base64 для хранения в БД
    private String bitmapToBase64(Bitmap selectedImage) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 1, stream);
        String imgString = Base64.encodeToString(stream.toByteArray(),Base64.DEFAULT);
        return  imgString;
    }

    /**Обновляет список задач
     * */
    public void retrieve()
    {
        DBAdapter db=new DBAdapter(this);

        //OPEN
        db.openDB();

        mTaskListItems.clear();

        //SELECT
        Cursor c=db.getAllTasks();

        //DATA ADDING TO ARRAYLIST
        showItems(c);

        db.close();

    }

    /**Отображает список задач, согласно текущим условиям(в том числе и для поиска)
     * */
    private void showItems(Cursor c) {
        adapter.notifyDataSetChanged();
            while (c.moveToNext()) {
                int id = c.getInt(0);
                String name = c.getString(1);
                int type = c.getInt(2);
                String content = c.getString(3);

                switch (type) {
                    case ItemsAdapter.TYPE_ITEM_TEXT:
                        mTaskListItems.add(new TextTaskListItem(id, name, content));
                        break;
                    case ItemsAdapter.TYPE_ITEM_IMAGE:
                        byte[] bytes = Base64.decode(content, Base64.DEFAULT);
                        mTaskListItems.add(new ImgTaskListItem(id, name,
                                BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));
                        break;
                    case ItemsAdapter.TYPE_ITEM_LIST:
                        //Вместо этого парс
                        /*List<CheckboxTaskListItem> cbList = new ArrayList<>();
                        cbList.add(new CheckboxTaskListItem("текст", true));
                        cbList.add(new CheckboxTaskListItem("текст", false));
                        mTaskListItems.add(new ListOfCheckboxesTaskListItem(id, name, type, cbList));*/

                        /*GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();*/

                        //gson.fromJson(new String(content), ListOfCheckboxesTaskListItem.class);

                        /*mTaskListItems.add(gson.fromJson(new String(content), ListOfCheckboxesTaskListItem.class));*/
                        ListOfCheckboxesTaskListItem cbList = JsonAdapter.fromJson(content);
                        cbList.setId(id);
                        mTaskListItems.add(cbList);
                        break;
                }
            }
    }


    /**Сохраняет новую задачу
     * */
    public void save(String name, int type, String content)
    {
        DBAdapter db=new DBAdapter(this);

        //OPEN
        db.openDB();

        //INSERT
        long result=db.add(name, type, content);

        if(result>0)
        {
            Toast.makeText(this, "Задача добавлена!", Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(this, "Ошибка добавления!", Toast.LENGTH_SHORT).show();
        }

        //CLOSE
        db.close();

        //refresh
        retrieve();

    }

    /**Удаляет значение по id
     * */
    public void delete(int id)
    {
        DBAdapter db=new DBAdapter(this);

        //OPEN
        db.openDB();

        //INSERT
        long result=db.Delete(id);

        if(result>0)
        {
            Toast.makeText(this, "Задача успешно удалена!", Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(this, "Ошибка удаления!", Toast.LENGTH_SHORT).show();
        }

        //CLOSE
        db.close();

        //refresh
        retrieve();

    }

    /**Выполняет получение значения по id
     * */
    public String[] getById(int id)
    {

        DBAdapter db = new DBAdapter(this);
        db.openDB();

        //SELECT
        Cursor c=db.getAllTasks();
        String name = "";
        String content = "";

        //LOOP THRU THE DATA ADDING TO ARRAYLIST
        while (c.moveToNext()) {
            int get_id = c.getInt(0);
            if(get_id == id) {
            name = c.getString(1);
            //int type = c.getInt(2);
            content = c.getString(3);
            }
        }
        db.close();

        return new String[]{name, content};
    }

    /**Осуществляет редактирование значения по id
     * */
    public void edit(int id, String name, int type, String content)
    {
        DBAdapter db=new DBAdapter(this);

        //OPEN
        db.openDB();

        //INSERT
        long result = db.UPDATE(id, name, type, content);

        if(result>0)
        {
            Toast.makeText(this,"Задача изменена!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this,"Ошибка изменения!", Toast.LENGTH_SHORT).show();
        }

        //CLOSE
        db.close();

        //refresh
        retrieve();

    }

    /**Осуществляет простой поиск из строки прямым сравнением значений
     * */
    public void search(String textToSearch){
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        mTaskListItems.clear();
        Cursor c = db.Search(textToSearch);
        showItems(c);
        db.close();
    }

    /**Осуществляет динамический поиск из строки
     * */
    public void searchDynamic(String textToSearch){
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        mTaskListItems.clear();
        Cursor c = db.Search(textToSearch);
        showItems(c);
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieve();
    }

}
