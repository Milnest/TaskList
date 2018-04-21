package com.milnest.tasklist;

/**
 * Created by t-yar on 17.04.2018.
 */

public class TextTaskListItem extends TaskListItem {

    private String mText;

    public TextTaskListItem(int id, String name, String text) {
        super(id, name, TaskListItem.TYPE_ITEM_TEXT);
        mText = text;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }
}
