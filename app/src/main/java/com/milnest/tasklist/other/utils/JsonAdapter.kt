package com.milnest.tasklist.other.utils

import com.google.gson.GsonBuilder
import com.milnest.tasklist.entities.ListOfCheckboxesTaskListItem

/**
 * Created by t-yar on 24.04.2018.
 * Работает с json (парсит в список и обратно)
 */

object JsonAdapter {
    private val gson = GsonBuilder().create()

    fun toJson(listItem: ListOfCheckboxesTaskListItem): String {
        return gson.toJson(listItem)
    }


    fun fromJson(jsonString: String): ListOfCheckboxesTaskListItem {
        return gson.fromJson(jsonString, ListOfCheckboxesTaskListItem::class.java)
    }
}
