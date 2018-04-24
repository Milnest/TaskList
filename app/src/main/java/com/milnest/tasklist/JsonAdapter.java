package com.milnest.tasklist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by t-yar on 24.04.2018.
 */

public class JsonAdapter {
    public static String toJson(ListOfCheckboxesTaskListItem listItem) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(listItem);
    }
}
