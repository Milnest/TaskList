package com.milnest.tasklist.other.utils.observer

interface Observable {
    fun registerObserver(o: Observer)
    fun removeObserver()
    fun notifyObservers(title: String, text : String)
}