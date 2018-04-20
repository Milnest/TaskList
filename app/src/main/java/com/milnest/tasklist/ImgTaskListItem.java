package com.milnest.tasklist;

import android.graphics.Bitmap;

/**
 * Created by t-yar on 17.04.2018.
 */

public class ImgTaskListItem extends TaskListItem {
    //private int mImage;
    private Bitmap mImage;

    public ImgTaskListItem(String name, Bitmap image) {
        super(name, TaskListItem.TYPE_ITEM_IMAGE);
        mImage = image;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap image) {
        mImage = image;
    }
}
