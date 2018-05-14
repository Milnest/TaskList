package com.milnest.tasklist.presenter

import android.support.v7.app.AppCompatActivity
import android.widget.Toast

class Presenter(val activity: PresenterInterface) {

    fun toastToActivity(toShow : String) {
        activity.showToast(toShow);
    }
}