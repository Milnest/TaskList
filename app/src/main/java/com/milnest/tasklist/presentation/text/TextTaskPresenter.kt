package com.milnest.tasklist.presentation.text

import android.content.Intent
import android.os.Bundle
import com.google.gson.GsonBuilder
import com.milnest.tasklist.ID
import com.milnest.tasklist.R
import com.milnest.tasklist.data.repository.DBRepository
import com.milnest.tasklist.data.web.APIService
import com.milnest.tasklist.data.web.TranslateData
import com.milnest.tasklist.entities.Task
import com.milnest.tasklist.interactor.TranslateInteractor
import com.milnest.tasklist.other.utils.observer.Observer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference
import java.net.URLEncoder

class TextTaskPresenter : Observer {
    private var task = Task(Task.TYPE_ITEM_TEXT)
    private lateinit var taskView: WeakReference<TextTaskView>
    var translateInteractor : TranslateInteractor? = null

    fun setStartText(extras: Bundle?) {
        if (extras != null){
            val taskId = extras.getInt(ID)
            task = DBRepository.getTaskById(taskId) as Task
            taskView.get()?.setText(task.title, task.data)
        }
    }

    fun saveClicked(title : String, text:String) {
        task.title = title
        task.data = text
        DBRepository.saveTask(task)
        closeView()
    }

    private fun closeView() {
        taskView.get()?.finish()
    }
    fun shareClicked(title : String, text:String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        taskView.get()?.startShareAct(shareIntent)
    }

    fun translationClicked(title : String, text:String) {
//        method(title, "ru-en")
        translateInteractor = TranslateInteractor()
        if (translateInteractor?.observer == null) {
            translateInteractor?.registerObserver(this)
        }
        translateInteractor?.execute(title, text)
    }

    /*fun method(input: String, transDirection: String){
        val retrofit = Retrofit.Builder()
                .baseUrl("https://translate.yandex.net/api/v1.5/tr.json/")
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()

        val service = retrofit.create<APIService>(APIService::class.java)
        val operation = service.translate("trnsl.1.1.20180420T121109Z.b002d3187929b557" +
                ".b397db53cb8218077027dca1b19ad897ee594788", URLEncoder.encode(input, "UTF-8"), transDirection)
        operation.enqueue(object : Callback<TranslateData> {
            override fun onFailure(call: Call<TranslateData>?, t: Throwable?) {
                taskView.get()?.setText(t!!.message, t.message)
            }

            override fun onResponse(call: Call<TranslateData>?, response: Response<TranslateData>?) {
                taskView.get()?.setText(response!!.body()!!.text!!.get(0), response.body()!!.text!!.get(0))
            }

        })
    }*/
    override fun update(title: String, text: String) {
        if (task.title == title && task.data == text) {
            taskView.get()?.showToast(R.string.translate_fail)
        }
        else {
            taskView.get()?.setText(title, text)
            taskView.get()?.showToast(R.string.translate_completed)
        }

        translateInteractor?.removeObserver()
        translateInteractor = null
    }

    fun attachView(taskView: TextTaskView) {
        this.taskView = WeakReference(taskView)
    }


}