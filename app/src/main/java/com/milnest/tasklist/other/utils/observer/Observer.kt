package com.milnest.tasklist.other.utils.observer

interface Observer {
    fun update(title: String, text: String)
}