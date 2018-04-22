package com.milnest.tasklist;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
    public static final int TEXT_RESULT = 1;
    public static final String NAME = "NAME";
    public static final String TEXT = "TEXT";
    public static final String ID = "ID";
    private static final int CAMERA_RESULT = 2;
    private static final int GALLERY_RESULT = 3;
    private MenuItem searchItem;
    private SearchView searchView;
    private AlertDialog dialog;
    //private int edit_id = -1;

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
                if (recyclerView.getLayoutManager() == mLinearLayoutManager)
                recyclerView.setLayoutManager(mGridManager);
                else {
                    recyclerView.setLayoutManager(mLinearLayoutManager);
                }
                break;
            case R.id.action_search:
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
        /*searchItem = findViewById(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();*/
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

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
                dialog = builder.show();
                Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setTextColor(getResources().getColor(R.color.lum_red));
                Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setTextColor(getResources().getColor(R.color.lum_red));
                /*Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_RESULT);*/
                break;
            case R.id.add_task_list:
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
                    save("1", ItemsAdapter.TYPE_ITEM_IMAGE, imgString);
                }
                catch (FileNotFoundException  e) {
                    Toast.makeText(this, "Изображение не получено",
                            Toast.LENGTH_SHORT).show();
                }
                catch (NullPointerException ex){
                    Toast.makeText(this, "Изображение не получено",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private String bitmapToBase64(Bitmap selectedImage) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        String imgString = Base64.encodeToString(stream.toByteArray(),Base64.DEFAULT);
        return  imgString;
    }

    /**Работвет с БД
     * */
    public void retrieve()
    {
        DBAdapter db=new DBAdapter(this);

        //OPEN
        db.openDB();

        mTaskListItems.clear();

        //SELECT
        Cursor c=db.getAllTasks();

        //LOOP THRU THE DATA ADDING TO ARRAYLIST
        while (c.moveToNext())
        {
            int id = c.getInt(0);
            String name = c.getString(1);
            int type = c.getInt(2);
            String content = c.getString(3);

            //TaskListItem taskListItem;
            //CREATE TASK
            adapter.notifyDataSetChanged();
            switch (type){
                case ItemsAdapter.TYPE_ITEM_TEXT:
                    mTaskListItems.add(new TextTaskListItem(id, name, content));
                    break;
                case ItemsAdapter.TYPE_ITEM_IMAGE:
                    try {
                        byte[] bytes = Base64.decode(content, Base64.DEFAULT);
                        mTaskListItems.add(new ImgTaskListItem(id, name, BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));
                                /*BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(content)))));*/
                    }
                    catch(Exception ex){
                        Toast.makeText(this, "Не сохранено", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }

        }

    }

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
        }else
        {
            Toast.makeText(this,"Ошибка изменения!", Toast.LENGTH_SHORT).show();
        }

        //CLOSE
        db.close();

        //refresh
        retrieve();

    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieve();
    }

}
