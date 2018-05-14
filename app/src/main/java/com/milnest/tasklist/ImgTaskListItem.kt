package com.milnest.tasklist

import android.graphics.Bitmap

/**
 * Created by t-yar on 17.04.2018.
 */
/**Класс задачи-картинки
 */
class ImgTaskListItem(id: Int, name: String, //private int mImage;
                      var image: Bitmap?) : TaskListItem(id, name, TaskListItem.TYPE_ITEM_IMAGE)
