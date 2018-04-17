package com.milnest.tasklist;

/**
 * Created by t-yar on 17.04.2018.
 */

public class ImgTaskListItem extends TaskListItem {
    private int mImage;

    public ImgTaskListItem(String name, int image) {
        super(name, TaskListItem.TYPE_ITEM_IMAGE);
        mImage = image;
    }

    public int getImage() {
        return mImage;
    }

    public void setImage(int image) {
        mImage = image;
    }
}
