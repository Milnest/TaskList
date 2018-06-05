package com.milnest.tasklist.presentation.element

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
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
import com.milnest.tasklist.other.utils.ChangeCbColor
import java.util.*

/**
 * Created by t-yar on 17.04.2018.
 */

class ItemsAdapter(private val iClickListener: IClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var tempViewHolder: RecyclerView.ViewHolder? = null
    private val mViewHolderList: MutableList<RecyclerView.ViewHolder>
    private var mItems: List<TaskListItem>? = null

    init {
        mViewHolderList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val v: View

        when (viewType) {
        // инфлейтим нужную разметку в зависимости от того,
        // какой тип айтема нужен в данной позиции
            TaskListItem.TYPE_ITEM_TEXT -> {
                v = LayoutInflater.from(parent.context).inflate(R.layout.text_task_list_item,
                        parent, false)
                tempViewHolder = TextItemHolder(v)
                val tempTextHolder: TextItemHolder = tempViewHolder as TextItemHolder;
                return tempViewHolder
            }
            TaskListItem.TYPE_ITEM_IMAGE -> {
                v = LayoutInflater.from(parent.context).inflate(R.layout.img_task_list_item,
                        parent, false)
                tempViewHolder = ImgItemHolder(v)
                val tempImgHolder: ImgItemHolder = tempViewHolder as ImgItemHolder;
                return tempViewHolder
            }
            TaskListItem.TYPE_ITEM_LIST -> {
                v = LayoutInflater.from(parent.context).inflate(
                        R.layout.list_of_checkboxes_task_list_item, parent, false)
                tempViewHolder = CheckboxListItemHolder(v)
                val tempListHolder: CheckboxListItemHolder =
                        tempViewHolder as CheckboxListItemHolder
                return tempViewHolder
            }
            else -> return null
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Получаем тип айтема в данной позиции для заполнения его данными
        mViewHolderList.add(holder.adapterPosition, holder)
        val taskListItem = mItems!![position]
        val type = taskListItem.type
        //Снимаем выделение
        if (taskListItem.isSelected)
            holder.itemView.setBackgroundResource(R.color.black)
        else
            holder.itemView.setBackgroundResource(R.color.colorAccent)
        when (type) {
            TaskListItem.TYPE_ITEM_TEXT -> {
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
                val layout = cbListItemHolder.cbListLayout/*cbListItemHolder.itemView.findViewById<View>(R.id.layout_to_add) as LinearLayout*/
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
        return mItems!!.get(position).type
    }


    open inner class TaskItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener { iClickListener.onItemClick(layoutPosition) }
            itemView.setOnLongClickListener { iClickListener.onItemLongClick(layoutPosition) }
        }
    }

    inner class TextItemHolder(itemView: View) : TaskItemHolder(itemView) {
        //Текстовые поля
        internal var mName: TextView = itemView.findViewById(R.id.name)
        internal var mText: TextView = itemView.findViewById(R.id.text)
    }

    inner class ImgItemHolder(itemView: View) : TaskItemHolder(itemView) {
        //Поля картиники
        internal var mImgName: TextView = itemView.findViewById(R.id.imgName)
        internal var mImage: ImageView = itemView.findViewById(R.id.img)
    }

    inner class CheckboxListItemHolder(itemView: View) : TaskItemHolder(itemView) {
        //Лайаут списка чекбоксов для заполнения
        internal var cbListLayout: LinearLayout = itemView.findViewById<View>(R.id.layout_to_add)
                as LinearLayout
    }

    fun addSelection(position: Int) {
        val viewHolder = mViewHolderList.get(position);
        viewHolder.itemView.setBackgroundResource(R.color.black)
        mItems!![position].isSelected = true
    }

    fun removeSelection() {
        for (item in mItems!!) {
            item.isSelected = false
        }
        for (viewHolder in mViewHolderList) {
            viewHolder.itemView.setBackgroundResource(R.color.colorAccent)
        }
    }

    fun setData(data : List<TaskListItem>){
        mItems = data
        notifyDataSetChanged()
    }


    override fun getItemId(position: Int): Long {
        return mItems!!.get(position).id.toLong()
    }

    interface IClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int) : Boolean
    }
}