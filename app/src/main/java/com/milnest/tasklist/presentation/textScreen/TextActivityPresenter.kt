package com.milnest.tasklist.presentation.textScreen

import android.content.Intent
import android.os.Bundle
import com.milnest.tasklist.IntentData
import com.milnest.tasklist.R
import com.milnest.tasklist.data.repository.DBRepository
import com.milnest.tasklist.entities.Task
import com.milnest.tasklist.interactor.Translate
import com.milnest.tasklist.other.utils.observer.Observer
import java.lang.ref.WeakReference

class TextActivityPresenter : Observer {
    private var task = Task(Task.TYPE_ITEM_TEXT)
    private lateinit var view: WeakReference<TextActInterface>

    fun setStartText(extras: Bundle?) {
        if (extras != null){
            val taskId = extras.getInt(IntentData.ID)
            task = DBRepository.getTaskById(taskId) as Task
            view.get()?.setText(task.title, task.data)
        }
    }

    fun saveClicked(title : String, text:String) {
        task.title = title
        task.data = text
        DBRepository.saveTask(task)
        closeView()
    }

    private fun closeView() {
        //view.get()?.saveText(Intent())
        view.get()?.finish()
    }
    fun shareClicked(title : String, text:String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        view.get()?.startShareAct(shareIntent)
    }

    fun translationClicked(title : String, text:String) {
        val translate = Translate.getTranslateObj()
        if (translate!!.observer == null) {
            translate.registerObserver(this)
        }
        translate.execute(title, text)
    }

    override fun update(title: String, text: String) {
        if (task.title == title && task.data == text) {
            view.get()?.showToast(R.string.translate_fail)
        }
        else {
            view.get()?.setText(title, text)
            view.get()?.showToast(R.string.translate_completed)
        }

        Translate.getTranslateObj()!!.removeObserver()
        Translate.delTranslateObj()
    }

    fun attachView(view: TextActInterface) {
        this.view = WeakReference(view)
    }


}