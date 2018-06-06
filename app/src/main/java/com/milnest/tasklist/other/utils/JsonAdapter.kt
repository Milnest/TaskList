package com.milnest.tasklist.other.utils

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.milnest.tasklist.entities.CheckboxTaskListItem

/**
 * Created by t-yar on 24.04.2018.
 * Работает с json (парсит в список и обратно)
 */

object JsonAdapter {
    private val gson = GsonBuilder().create()

    fun toJson(list: List<CheckboxTaskListItem>): String {
        return gson.toJson(list)
    }

    fun fromJson(jsonString: String): List<CheckboxTaskListItem> {
        return gson.fromJson(jsonString, object : TypeToken<List<CheckboxTaskListItem>>(){}.type)
    }
}
