package com.milnest.tasklist.entities.ObserverInterfaces

interface Observable {
    fun registerObserver(o: Observer)
    fun removeObserver(o: Observer)
    fun notifyObservers(notifObject : Any?)
    //fun notifyObservers(isRetrieve: Boolean?)
}