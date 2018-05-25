package com.milnest.tasklist.presentation.element

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.milnest.tasklist.R
import com.milnest.tasklist.application.app
import com.milnest.tasklist.entities.ImgTaskListItem
import com.milnest.tasklist.entities.ListOfCheckboxesTaskListItem
import com.milnest.tasklist.entities.TaskListItem
import com.milnest.tasklist.entities.TextTaskListItem
import com.milnest.tasklist.interactor.ChangeCbColor
import com.milnest.tasklist.interactor.TaskDataInteractor
import com.milnest.tasklist.presentation.main.MainActivity

import java.util.ArrayList

/**
 * Created by t-yar on 17.04.2018.
 */

class ItemsAdapter
//For activity

(private val mItems: List<TaskListItem>?) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemsAdapterInterface {
    private var tempViewHolder: RecyclerView.ViewHolder? = null
    private val mViewHolderList: MutableList<RecyclerView.ViewHolder>
    private val taskDataInteractor : TaskDataInteractor

    init {
        mViewHolderList = ArrayList()
        taskDataInteractor = TaskDataInteractor.getDBMethodsAdapter()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val v: View

        when (viewType) {
        // инфлейтим нужную разметку в зависимости от того,
        // какой тип айтема нужен в данной позиции
            TaskListItem.TYPE_ITEM_TEXT -> {
                v = LayoutInflater.from(parent.context).inflate(R.layout.text_task_list_item,
                        parent, false)
                //val x: String? = y as String?
                tempViewHolder = TextItemHolder(v)
                val tempTextHolder: TextItemHolder = tempViewHolder as TextItemHolder;
                //mViewHolderList.add(tempTextHolder)
                return tempViewHolder
            }
            TaskListItem.TYPE_ITEM_IMAGE -> {
                v = LayoutInflater.from(parent.context).inflate(R.layout.img_task_list_item,
                        parent, false)
                tempViewHolder = ImgItemHolder(v)
                val tempImgHolder: ImgItemHolder = tempViewHolder as ImgItemHolder;
                //mViewHolderList.add(tempImgHolder)
                return tempViewHolder
            }
            TaskListItem.TYPE_ITEM_LIST -> {
                v = LayoutInflater.from(parent.context).inflate(
                        R.layout.list_of_checkboxes_task_list_item, parent, false)
                tempViewHolder = CheckboxListItemHolder(v)
                val tempListHolder: CheckboxListItemHolder =
                        tempViewHolder as CheckboxListItemHolder
                //mViewHolderList.add(tempListHolder)
                return tempViewHolder
            }
            else -> return null
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Получаем тип айтема в данной позиции для заполнения его данными
        tempViewHolderPosition = holder.adapterPosition
        mViewHolderList.add(tempViewHolderPosition, holder)
        val taskListItem = mItems!![position]
        val type = taskListItem.type
        holder.itemView.setOnLongClickListener(RecyclerHolderPresenter(tempViewHolderPosition, type))
        holder.itemView.setOnClickListener(RecyclerHolderPresenter(tempViewHolderPosition, type))
        //Снимаем выделение
        if (taskListItem.isSelected)
            holder.itemView.setBackgroundResource(R.color.black)
        else
            holder.itemView.setBackgroundResource(R.color.colorAccent)
        when (type) {
            TaskListItem.TYPE_ITEM_TEXT -> {
                //Выполняется приведение типа для вызова отличных методов
                val textTaskListItem = taskListItem as TextTaskListItem
                val textItemHolder = holder as TextItemHolder
                textItemHolder.mName.text = textTaskListItem.name
                textItemHolder.mText.text = textTaskListItem.text
            }
            TaskListItem.TYPE_ITEM_IMAGE -> {
                val imgTaskListItem = taskListItem as ImgTaskListItem
                val imgItemHolder = holder as ImgItemHolder
                imgItemHolder.mImgName.text = imgTaskListItem.name
                imgItemHolder.mImage.setImageBitmap(imgTaskListItem.image)
            }
            TaskListItem.TYPE_ITEM_LIST -> {
                val listOfCbTaskListItem = taskListItem as ListOfCheckboxesTaskListItem
                val cbListItemHolder = holder as CheckboxListItemHolder
                val layout = cbListItemHolder.itemView.findViewById<View>(R.id.layout_to_add) as LinearLayout
                //Очистить view holder.
                layout.removeAllViews()
                //Заполнить ViewHolder новыми элементами.
                for (item in listOfCbTaskListItem.cbList!!) {
                    val cb = CheckBox(layout.context).apply {
                        setOnTouchListener { _, _ -> true }
                    }
                    cb.isChecked = item.isCbState
                    cb.isClickable = false
                    cb.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    val cbText = TextView(layout.context)
                    cbText.setPadding(0, 0, 0, 10)
                    cbText.text = item.cbText
                    cbText.setTextColor(app.context.resources
                            .getColor(R.color.lum_red))
                    cbText.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    cbText.isClickable = false
                    val innerLayout = LinearLayout(layout.context)
                    innerLayout.addView(cb)
                    innerLayout.addView(cbText)
                    layout.addView(innerLayout)
                    ChangeCbColor.change(cb)
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return mItems?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        if (mItems != null) {
            val taskListItem = mItems[position]
            //if (taskListItem != null) {
                return taskListItem.type
            //}
        }
        return 0
    }


    class TextItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Текстовые поля
        internal var mName: TextView
        internal var mText: TextView

        init {
            //Текстовые поля
            mName = itemView.findViewById(R.id.name)
            mText = itemView.findViewById(R.id.text)
        }
    }

    class ImgItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Поля картиники
        internal var mImgName: TextView
        internal var mImage: ImageView

        init {
            //Поля картинки
            mImgName = itemView.findViewById(R.id.imgName)
            mImage = itemView.findViewById(R.id.img)
        }
    }

    inner class CheckboxListItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Поля картиники
        internal var cbListLayout: LinearLayout

        init {
            cbListLayout = itemView.findViewById<View>(R.id.layout_to_add) as LinearLayout
        }
    }

    //Выделяет задачу
    /*override fun addSelection(viewHolder: RecyclerView.ViewHolder) {
        //mViewHolderList.get(tempViewHolderPosition);
        viewHolder.itemView.setBackgroundResource(R.color.black)
        tempViewHolderPosition = viewHolder.adapterPosition
        mItems!![tempViewHolderPosition].isSelected = true
    }*/

    override fun addSelection(position: Int) {
        tempViewHolderPosition = position
        var viewHolder = mViewHolderList.get(tempViewHolderPosition);
        viewHolder.itemView.setBackgroundResource(R.color.black)
        //tempViewHolderPosition = viewHolder.adapterPosition
        mItems!![tempViewHolderPosition].isSelected = true
    }

    //Снимает выделение задачи
    override fun removeSelection() {
        for (item in mItems!!) {
            item.isSelected = false
        }
        for (viewHolder in mViewHolderList) {
            viewHolder.itemView.setBackgroundResource(R.color.colorAccent)
        }
    }

    override fun retrieveAdapter(){
        notifyDataSetChanged()
    }

    companion object {
        val TYPE_ITEM_TEXT = 0
        val TYPE_ITEM_IMAGE = 1
        val TYPE_ITEM_LIST = 2
        var tempViewHolderPosition: Int = 0
    }
}