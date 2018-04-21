package com.milnest.tasklist;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static List<TaskListItem> mTaskListItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private Toolbar mToolbar;
    private GridLayoutManager mGridManager = new GridLayoutManager(this, 2);
    private LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
    public android.support.v7.view.ActionMode mActionMode;
    public android.support.v7.view.ActionMode.Callback mActionModeCallback;
    private ItemsAdapter adapter;
    private AlertDialog.Builder builder;
    private final int TEXT_RESULT = 1;
    public static final String NAME = "NAME";
    public static final String TEXT = "TEXT";
    private static final int CAMERA_RESULT = 2;
    private static final int GALLERY_RESULT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setInitialData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }
        return true;
    }


    /*@Override
    public boolean onContextItemSelected(MenuItem item)
    {
        CharSequence message;
        switch (item.getItemId())
        {
            case R.id.new_photo:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_RESULT);
                break;
            case R.id.open_gallery:

                break;
            default:
                return super.onContextItemSelected(item);
        }
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return true;
    }*/


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
        //Recycler data
        mTaskListItems.add(new TextTaskListItem("name1", "description1"));
        mTaskListItems.add(new TextTaskListItem("name2", "description2"));
        mTaskListItems.add(new TextTaskListItem("name3", "description3"));
        //mTaskListItems.add(new TextTaskListItem("name4", "description4"));
        mTaskListItems.add(new ImgTaskListItem("img1", BitmapFactory.decodeResource(
                getResources(), android.R.mipmap.sym_def_app_icon)));
        //View init
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        initRecyclerView();
        adapter.initActionMode();
        //Layout Manager init
        recyclerView.setLayoutManager(mLinearLayoutManager);

        initPhotoDialog();

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
                        //Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK:
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        //Тип получаемых объектов - image:
                        photoPickerIntent.setType("image/*");
                        //Запускаем переход с ожиданием обратного результата в виде информации об изображении:
                        startActivityForResult(photoPickerIntent, GALLERY_RESULT);

                    }
                });
        builder.create();
    }

    public void OnClick(View view){
        switch (view.getId()){
            case R.id.add_task_text:
                Intent textIntent = new Intent(this, TextTaskActivity.class);
                startActivityForResult(textIntent, TEXT_RESULT);
                break;
            case R.id.add_task_photo:
                builder.show();
                /*Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_RESULT);*/
                break;
            case R.id.add_task_list:
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        /*if(requestCode== TEXT_RESULT){
            if(resultCode==RESULT_OK){
                Bundle extras = data.getExtras();
                if(extras != null){
                    String name = data.getStringExtra(NAME);
                    String text = data.getStringExtra(TEXT);
                    adapter.notifyDataSetChanged();
                    mTaskListItems.add(new TextTaskListItem(name, text));
                }
            }
            else{
                Toast.makeText(this, R.string.save_canceled, Toast.LENGTH_SHORT).show();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }*/
        switch (requestCode){
            case TEXT_RESULT:
                if(resultCode==RESULT_OK){
                    Bundle extras = data.getExtras();
                    if(extras != null){
                        String name = data.getStringExtra(NAME);
                        String text = data.getStringExtra(TEXT);
                        adapter.notifyDataSetChanged();
                        mTaskListItems.add(new TextTaskListItem(name, text));
                    }
                }
                else{
                    Toast.makeText(this, R.string.save_canceled, Toast.LENGTH_SHORT).show();
                }
                break;
            case CAMERA_RESULT:
                try {
                    /*Uri imgUri = data.getData();
                    BitmapFactory.decodeFile(imgUri.getPath()); */
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    adapter.notifyDataSetChanged();
                    mTaskListItems.add(new ImgTaskListItem("image", thumbnail));
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
                    adapter.notifyDataSetChanged();
                    mTaskListItems.add(new ImgTaskListItem("image", selectedImage));
                }
                catch (FileNotFoundException  e) {
                    Toast.makeText(this, "Изображение не получено",
                            Toast.LENGTH_SHORT).show();
                }
                catch (NullPointerException ex){
                    Toast.makeText(this, "Изображение не получено",
                            Toast.LENGTH_SHORT).show();
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**Работвет с БД
     * */
    private void retrieve()
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

            //CREATE PLAYER
            TaskListItem taskListItem = new TextTaskListItem(name, content);
            //Player p=new Player(name,pos,id);

            //ADD TO PLAYERS
            adapter.notifyDataSetChanged();
            mTaskListItems.add(taskListItem);
        }

        //SET ADAPTER TO RV
        /*if(!(mTaskListItems.size()<1))
        {
            rv.setAdapter(adapter);
        }*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieve();
    }
}
