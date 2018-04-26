package com.milnest.tasklist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by t-yar on 17.04.2018.
 */

public class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ITEM_TEXT = 0;
    public static final int TYPE_ITEM_IMAGE = 1;
    public static final int TYPE_ITEM_LIST = 2;
    private List<TaskListItem> mItems;
    private LayoutInflater mInflater;
    private RecyclerView.ViewHolder tempViewHolder;
    private List<RecyclerView.ViewHolder> mViewHolderList;
    private int tempViewHolderPosition;

    //For activity

    public ItemsAdapter(List<TaskListItem> items, Context context) {
        mItems = items;
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
                v.setOnClickListener(new ElementClickListener(tempViewHolder));
                return tempViewHolder;
            case TaskListItem.TYPE_ITEM_IMAGE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_task_list_item,
                        parent, false);
                tempViewHolder = new ImgItemHolder(v);
                mViewHolderList.add(tempViewHolder);
                v.setOnLongClickListener(new LongElementClickListener(tempViewHolder));
                //v.setOnClickListener(new ElementClickListener(tempViewHolder));
                return tempViewHolder;
            case TaskListItem.TYPE_ITEM_LIST:
                v = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.list_of_checkboxes_task_list_item, parent, false);
                tempViewHolder = new CheckboxListItemHolder(v);
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
        tempViewHolderPosition = holder.getAdapterPosition();
        TaskListItem taskListItem = mItems.get(position);
        //Снимаем выделение
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
                break;
            case TaskListItem.TYPE_ITEM_IMAGE:
                ImgTaskListItem imgTaskListItem = (ImgTaskListItem) taskListItem;
                ImgItemHolder imgItemHolder = (ImgItemHolder) holder;
                imgItemHolder.mImgName.setText(imgTaskListItem.getName());
                imgItemHolder.mImage.setImageBitmap(imgTaskListItem.getImage());
                break;
            case TaskListItem.TYPE_ITEM_LIST:
                ListOfCheckboxesTaskListItem listOfCbTaskListItem =
                        (ListOfCheckboxesTaskListItem) taskListItem;
                CheckboxListItemHolder cbListItemHolder = (CheckboxListItemHolder) holder;
                LinearLayout layout = (LinearLayout)cbListItemHolder.itemView.findViewById(R.id.layout_to_add);
                //Очистить view holder.
                layout.removeAllViews();
                //Заполнить ViewHolder новыми элементами.
                for (CheckboxTaskListItem item: listOfCbTaskListItem.getCbList()
                        ) {
                    CheckBox cb = new CheckBox(layout.getContext());
                    cb.setChecked(item.isCbState());
                    cb.setClickable(false);
                    cb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.
                            LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    TextView cbText = new TextView(layout.getContext());
                    cbText.setPadding(0,0,0,10);
                    cbText.setText(item.getCbText());
                    cbText.setTextColor(mInflater.getContext().getResources()
                            .getColor(R.color.lum_red));
                    cbText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.
                            LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    cbText.setClickable(false);
                    LinearLayout innerLayout = new LinearLayout(layout.getContext());
                    innerLayout.addView(cb);
                    innerLayout.addView(cbText);
                    layout.addView(innerLayout);
                    ChangeCbColor.change(cb);
                }
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

    //Удаляет задачу по позиции
    void removeItem(int position) {
        MainActivity activity = (MainActivity) mInflater.getContext();
        activity.delete(position);
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

    public class CheckboxListItemHolder extends RecyclerView.ViewHolder {
        //Поля картиники
        LinearLayout cbListLayout;
        public CheckboxListItemHolder(View itemView) {
            super(itemView);
            cbListLayout = (LinearLayout)itemView.findViewById(R.id.layout_to_add);
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
                activity.mActionMode = activity.startSupportActionMode(
                        activity.mActionModeCallback);
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
        }


    }

    public class ElementClickListener implements View.OnClickListener{
        RecyclerView.ViewHolder mViewHolder;

        public ElementClickListener(RecyclerView.ViewHolder viewHolder) {
            mViewHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            MainActivity activity = (MainActivity) mInflater.getContext();
            tempViewHolderPosition = mViewHolder.getAdapterPosition();
            switch (mViewHolder.getItemViewType()) {
                case TYPE_ITEM_TEXT:
                    Intent textIntentChange = new Intent(activity, TextTaskActivity.class);
                    textIntentChange.putExtra("data", activity.getById(
                            mItems.get(tempViewHolderPosition).getId()));
                    textIntentChange.putExtra("id", mItems.get(
                            tempViewHolderPosition).getId());
                    activity.startActivityForResult(textIntentChange, MainActivity.TEXT_RESULT);
                    break;
                case TYPE_ITEM_LIST:
                    Intent listIntentChange = new Intent(activity, ListTaskActivity.class);
                    listIntentChange.putExtra("data", activity.getById(
                            mItems.get(tempViewHolderPosition).getId()));
                    listIntentChange.putExtra("id", mItems.get(
                            tempViewHolderPosition).getId());
                    activity.startActivityForResult(listIntentChange, MainActivity.LIST_RESULT);
            }
        }
    }

    //Выделяет задачу
    private void addSelection(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackgroundResource(R.color.black);
        mItems.get(tempViewHolderPosition).setSelected(true);
    }

    //Снимает выделение задачи
    private void removeSelection() {
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
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.support.v7.view.ActionMode mode,
                                               MenuItem item) {
                removeItem(mItems.get(tempViewHolderPosition).getId());
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