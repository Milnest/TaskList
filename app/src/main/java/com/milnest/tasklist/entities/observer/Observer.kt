package com.milnest.tasklist.entities.observer

import com.milnest.tasklist.entities.TextActData

interface Observer {
    fun update(translatedText: TextActData?)
}