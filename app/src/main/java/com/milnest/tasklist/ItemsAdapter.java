package com.milnest.tasklist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Selection;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by t-yar on 17.04.2018.
 */

public class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_ITEM_TEXT = 0;
    private final int TYPE_ITEM_IMAGE = 1;
    private List<TaskListItem> mItems;
    private LayoutInflater mInflater;
    private RecyclerView.ViewHolder tempViewHolder;
    private List<RecyclerView.ViewHolder> mViewHolderList;
    private int tempViewHolderPosition;
    //private boolean[] selects;

    //For activity
    /*private ActionMode mActionMode;
    private ActionMode.Callback mActionModeCallback;*/

    public ItemsAdapter(List<TaskListItem> items, Context context) {
        mItems = items;
        //selects = new boolean[items.size()];
        mViewHolderList = new ArrayList<RecyclerView.ViewHolder>();
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        switch (viewType) {
            // инфлейтим нужную разметку в зависимости от того,
            // какой тип айтема нужен в данной позиции

            case TaskListItem.TYPE_ITEM_TEXT:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_task_list_item,
                        parent, false);
                tempViewHolder = new TextItemHolder(v);
                mViewHolderList.add(tempViewHolder);
                v.setOnLongClickListener(new LongElementClickListener(tempViewHolder));
                /*v.setOnClickListener(new ElementClickListener(tempViewHolder));*/
                return tempViewHolder;
            case TaskListItem.TYPE_ITEM_IMAGE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_task_list_item,
                        parent, false);
                tempViewHolder = new ImgItemHolder(v);
                mViewHolderList.add(tempViewHolder);
                v.setOnLongClickListener(new LongElementClickListener(tempViewHolder));
                v.setOnClickListener(new ElementClickListener(tempViewHolder));
                return tempViewHolder;
            default:
                return null;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Получаем тип айтема в данной позиции для заполнения его данными
        /*if(selects[position]) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        }
        else{
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }*/

        TaskListItem taskListItem = mItems.get(position);
        if(taskListItem.isSelected())
            holder.itemView.setBackgroundResource(R.color.black);
        else
            holder.itemView.setBackgroundResource(R.color.colorAccent);
        int type = taskListItem.getType();
        switch (type) {
            case TaskListItem.TYPE_ITEM_TEXT:
                //Выполняется приведение типа для вызова отличных методов
                TextTaskListItem textTaskListItem = (TextTaskListItem) taskListItem;
                TextItemHolder textItemHolder = (TextItemHolder) holder;
                textItemHolder.mName.setText(textTaskListItem.getName());
                textItemHolder.mText.setText(textTaskListItem.getText());
                /*if(selects[position]) {
                    textItemHolder.mName.setBackgroundColor(Color.BLACK);
                    textItemHolder.mText.setBackgroundColor(Color.BLACK);
                }
                else{
                    textItemHolder.mName.setBackgroundColor(Color.GREEN);
                    textItemHolder.mText.setBackgroundColor(Color.GREEN);
                }*/
                break;
            case TaskListItem.TYPE_ITEM_IMAGE:
                ImgTaskListItem imgTaskListItem = (ImgTaskListItem) taskListItem;
                ImgItemHolder imgItemHolder = (ImgItemHolder) holder;
                imgItemHolder.mImgName.setText(imgTaskListItem.getName());
                //imgItemHolder.mImage.setImageResource(imgTaskListItem.getImage());
                imgItemHolder.mImage.setImageBitmap(imgTaskListItem.getImage());
                /*if(selects[position]) {
                    holder.itemView.setBackgroundColor(Color.LTGRAY);
                }
                else{
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                }*/
                break;
        }
    }


    @Override
    public int getItemCount() {
        if (mItems != null) {
            return mItems.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems != null) {
            TaskListItem taskListItem = mItems.get(position);
            if (taskListItem != null) {
                return taskListItem.getType();
            }
        }
        return 0;
    }

    void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }


    public static class TextItemHolder extends RecyclerView.ViewHolder {
        //Текстовые поля
        TextView mName;
        TextView mText;

        public TextItemHolder(View itemView) {
            super(itemView);
            //Текстовые поля
            mName = itemView.findViewById(R.id.name);
            mText = itemView.findViewById(R.id.text);
        }
    }

    public static class ImgItemHolder extends RecyclerView.ViewHolder {
        //Поля картиники
        TextView mImgName;
        ImageView mImage;

        public ImgItemHolder(View itemView) {
            super(itemView);
            //Поля картинки
            mImgName = itemView.findViewById(R.id.imgName);
            mImage = itemView.findViewById(R.id.img);
        }
    }

    public class LongElementClickListener implements View.OnLongClickListener {

        RecyclerView.ViewHolder mViewHolder;

        public LongElementClickListener(RecyclerView.ViewHolder viewHolder) {
            mViewHolder = viewHolder;
        }

        @Override
        public boolean onLongClick(View v) {
            MainActivity activity = (MainActivity) mInflater.getContext();
            if (activity.mActionMode == null) {
                activity.mActionMode = activity.startSupportActionMode(activity.mActionModeCallback);
                activity.mActionMode.setTitle("Action Mode");
                tempViewHolderPosition = mViewHolder.getAdapterPosition();
                //Добавление выделения при выборе
                addSelection(mViewHolder);
            } else {
                activity.mActionMode.finish();
                //Сброс выделения
                removeSelection();
            }
            return true;
            /*removeItem(mViewHolder.getAdapterPosition());
            return true;*/
        }


    }

    public class ElementClickListener implements View.OnClickListener{
        RecyclerView.ViewHolder mViewHolder;

        public ElementClickListener(RecyclerView.ViewHolder viewHolder) {
            mViewHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            /*MainActivity activity = (MainActivity) mInflater.getContext();
            ImgItemHolder imgItemHolder = (ImgItemHolder) mViewHolder;
            ImgTaskListItem item = (ImgTaskListItem) mItems.get(imgItemHolder.getAdapterPosition());
            item.getImage();*/
        }
    }

    private void addSelection(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackgroundResource(R.color.black);
        //selects[tempViewHolderPosition] = true;
        mItems.get(tempViewHolderPosition).setSelected(true);
    }

    private void removeSelection() {
        /*for (boolean select:selects
                ) {
            select = false;
        }*/
        for (TaskListItem item:mItems
             ) {
            item.setSelected(false);
        }
        for (RecyclerView.ViewHolder viewHolder: mViewHolderList
                ) {
            viewHolder.itemView.setBackgroundResource(R.color.colorAccent);
        }
    }

    public void initActionMode() {
        final MainActivity activity = (MainActivity) mInflater.getContext();
        activity.mActionModeCallback = new android.support.v7.view.ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_context_task, menu);
               /* menuInflater.inflate(R.menu.menu_context_task, menu);*/
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item) {
                removeItem(tempViewHolderPosition);
                activity.mActionMode.finish();
                return false;
            }

            @Override
            public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {
                removeSelection();
                activity.mActionMode = null;
            }

        };
    }
}