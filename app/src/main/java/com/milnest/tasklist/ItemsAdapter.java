package com.milnest.tasklist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by t-yar on 17.04.2018.
 */

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemHolder> {

    private final int TYPE_ITEM_TEXT = 0;
    private final int TYPE_ITEM_IMAGE = 1;
    private List<TaskListItem> mItems;
    private LayoutInflater mInflater;

    public ItemsAdapter(List<TaskListItem> items, Context context) {
        mItems = items;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ItemsAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        switch (viewType) {
            // инфлейтим нужную разметку в зависимости от того,
            // какой тип айтема нужен в данной позиции

            case TYPE_ITEM_TEXT:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_task_list_item, parent, false);
                break;
            case TYPE_ITEM_IMAGE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_task_list_item, parent, false);
                break;
            default:
                v = null;
                break;
        }
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemsAdapter.ItemHolder holder, int position) {
        // Получаем тип айтема в данной позиции для заполнения его данными
        TaskListItem taskListItem = mItems.get(position);
        int type = taskListItem.getType();
        switch (type) {
            case TYPE_ITEM_TEXT:
                //Выполняется приведение типа для вызова отличных методов
                TextTaskListItem textTaskListItem = (TextTaskListItem)taskListItem;
                holder.mName.setText(textTaskListItem.getName());
                holder.mText.setText(textTaskListItem.getText());
                break;
            case TYPE_ITEM_IMAGE:
                ImgTaskListItem imgTaskListItem = (ImgTaskListItem) taskListItem;
                holder.mImgName.setText(imgTaskListItem.getName());
                holder.mImage.setImageResource(imgTaskListItem.getImage());
                break;
        }
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }


    class ItemHolder extends RecyclerView.ViewHolder{
        //Текстовые поля
        TextView mName;
        TextView mText;
        //Поля картиник
        TextView mImgName;
        ImageView mImage;
        //Поля списка

        public ItemHolder(View itemView) {
            super(itemView);
            //Текстовые поля
            mName = itemView.findViewById(R.id.name);
            mText = itemView.findViewById(R.id.text);
            //Поля картинки
            mImgName = itemView.findViewById(R.id.imgName);
            mImage = itemView.findViewById(R.id.img);
            //Поля списка
        }
    }
}
