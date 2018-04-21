package com.milnest.tasklist;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;

public class TextTaskActivity extends AppCompatActivity {

    EditText taskTitle;
    EditText taskText;
    private String title;
    private String text;
    Toolbar mToolbar;
    private final String TAG = "TextTaskActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_task);
        setInitialData();
    }

    private void setInitialData() {
        taskTitle = (EditText) findViewById(R.id.taskTitle);
        taskText = (EditText) findViewById(R.id.taskText);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_text, menu);
        /*Log.d(TAG, "onCreateOptionsMenu: Вызов инфлейтера");*/
        /*taskTitle.setText("called");*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_task_text_save:
                saveText();
                finish();
                break;
            case R.id.action_task_text_share:
                saveText();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                //finish();
                break;
            case R.id.action_task_text_translate:
                translation();
                break;
        }
        return true;
    }

    private void translation() {
        getText();
        new AsyncRequest().execute();
        /*try {
            taskTitle.setText(YandexTranslate.translate("ru-en", title));
            taskText.setText(YandexTranslate.translate("ru-en", text));
            Toast.makeText(this, "Перевод завершён!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Ошибка перевода!", Toast.LENGTH_SHORT).show();
        }*/
    }

    /*private void getTaskData() {
        title = taskTitle.getText().toString();
        text = taskText.getText().toString();
    }*/

    private void saveText() {
        Intent data = new Intent();
        getText();
        data.putExtra(MainActivity.NAME, title);
        data.putExtra(MainActivity.TEXT, text);
        setResult(RESULT_OK, data);
    }

    private void getText() {
        title = taskTitle.getText().toString();
        text = taskText.getText().toString();
    }

    class AsyncRequest extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... voids){
            try {
                String translatedTitle = YandexTranslate.translate("ru-en", title);
                String translatedText =YandexTranslate.translate("ru-en", text);
                return new String[]{translatedTitle, translatedText};
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if (strings != null) {
                taskTitle.setText(strings[0]);
                taskText.setText(strings[1]);
                Toast.makeText(TextTaskActivity.this, "Перевод завершён!",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(TextTaskActivity.this, "Ошибка перевода!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


}
