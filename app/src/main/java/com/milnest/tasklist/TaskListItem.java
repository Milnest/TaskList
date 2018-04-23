package com.milnest.tasklist;

/**
 * Created by t-yar on 17.04.2018.
 */

/**Общий класс задач для списка
 * */
public class TaskListItem {
    public static final int TYPE_ITEM_TEXT = 0;
    public static final int TYPE_ITEM_IMAGE = 1;
    public static final int TYPE_ITEM_LIST = 2;
    private int mId;
    private String mName;
    private int mType;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    /*public TaskListItem(String name, int type) {
        mName = name;
        mType = type;
    }*/
    public TaskListItem(int id, String name, int type) {
        mName = name;
        mType = type;
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
