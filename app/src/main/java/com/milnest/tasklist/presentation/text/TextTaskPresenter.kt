package com.milnest.tasklist.presentation.text

import android.content.Intent
import android.os.Bundle
import com.milnest.tasklist.ID
import com.milnest.tasklist.R
import com.milnest.tasklist.data.repository.DBRepository
import com.milnest.tasklist.entities.Task
import com.milnest.tasklist.interactor.TranslateInteractor
import com.milnest.tasklist.other.utils.observer.Observer
import java.lang.ref.WeakReference

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
        translateInteractor = TranslateInteractor()
        translateInteractor?.run(title, text, "ru-en") { value, error ->
            if (error != null) {
                value.text?.get(0)
                value.text?.get(1)
            }
            else{
                taskView.get()?.showToast(R.string.translate_fail)
            }
        }
    }

    override fun update(title: String, text: String) {
        if (title == translateInteractor!!.TRANSLATE_FAIL && text == translateInteractor!!.TRANSLATE_FAIL) {
            taskView.get()?.showToast(R.string.translate_fail)
        }
        else {
            taskView.get()?.setText(title, text)
            taskView.get()?.showToast(R.string.translate_completed)
        }
    }

    fun attachView(taskView: TextTaskView) {
        this.taskView = WeakReference(taskView)
    }


}