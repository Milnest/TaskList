package com.milnest.tasklist

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * Created by t-yar on 24.04.2018.
 * Работает с json (парсит в список и обратно)
 */

object JsonAdapter {
    private val builder = GsonBuilder()
    private val gson = builder.create()

    fun toJson(listItem: ListOfCheckboxesTaskListItem): String {
        return gson.toJson(listItem)
    }


    fun fromJson(jsonString: String): ListOfCheckboxesTaskListItem {
        return gson.fromJson(jsonString, ListOfCheckboxesTaskListItem::class.java!!)
    }
}
