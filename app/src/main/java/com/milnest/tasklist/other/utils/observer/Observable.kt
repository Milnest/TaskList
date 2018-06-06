package com.milnest.tasklist.other.utils.observer

import com.milnest.tasklist.entities.Task

interface Observable {
    fun registerObserver(o: Observer)
    fun removeObserver()
    fun notifyObservers(translatedText: Task)
}