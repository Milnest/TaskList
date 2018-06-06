package com.milnest.tasklist.other.utils.observer

import com.milnest.tasklist.entities.Task

interface Observer {
    fun update(translatedText: Task)
}