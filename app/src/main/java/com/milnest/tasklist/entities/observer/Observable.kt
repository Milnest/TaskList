package com.milnest.tasklist.entities.observer

import com.milnest.tasklist.entities.TextActData

interface Observable {
    fun registerObserver(o: Observer)
    fun removeObserver()
    fun notifyObservers(translatedText: TextActData?)
}