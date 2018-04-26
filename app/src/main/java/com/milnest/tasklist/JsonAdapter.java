package com.milnest.tasklist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by t-yar on 24.04.2018.
 * Работает с json (парсит в список и обратно)
 */

public class JsonAdapter {
    private static GsonBuilder builder = new GsonBuilder();
    private static Gson gson = builder.create();

    public static String toJson(ListOfCheckboxesTaskListItem listItem) {
        return gson.toJson(listItem);
    }


    public static ListOfCheckboxesTaskListItem  fromJson(String jsonString){
        return gson.fromJson(jsonString, ListOfCheckboxesTaskListItem.class);
    }
}
