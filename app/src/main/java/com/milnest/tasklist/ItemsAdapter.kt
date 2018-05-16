package com.milnest.tasklist

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.milnest.tasklist.use_cases.DBMethodsAdapter

import java.util.ArrayList

/**
 * Created by t-yar on 17.04.2018.
 */

class ItemsAdapter
//For activity

(private val mItems: List<TaskListItem>?, context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater: LayoutInflater
    private var tempViewHolder: RecyclerView.ViewHolder? = null
    private val mViewHolderList: MutableList<RecyclerView.ViewHolder>
    private val dbMethodsAdapter : DBMethodsAdapter?
    private var tempViewHolderPosition: Int = 0

    init {
        mViewHolderList = ArrayList()
        mInflater = LayoutInflater.from(context)
        dbMethodsAdapter = DBMethodsAdapter.getDBMethodsAdapter()
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
                mViewHolderList.add(tempTextHolder)
                v.setOnLongClickListener(LongElementClickListener(tempTextHolder))
                v.setOnClickListener(ElementClickListener(tempTextHolder))
                return tempViewHolder
            }
            TaskListItem.TYPE_ITEM_IMAGE -> {
                v = LayoutInflater.from(parent.context).inflate(R.layout.img_task_list_item,
                        parent, false)
                tempViewHolder = ImgItemHolder(v)
                val tempImgHolder: ImgItemHolder = tempViewHolder as ImgItemHolder;
                mViewHolderList.add(tempImgHolder)
                v.setOnLongClickListener(LongElementClickListener(tempImgHolder))
                //v.setOnClickListener(new ElementClickListener(tempViewHolder));
                return tempViewHolder
            }
            TaskListItem.TYPE_ITEM_LIST -> {
                v = LayoutInflater.from(parent.context).inflate(
                        R.layout.list_of_checkboxes_task_list_item, parent, false)
                tempViewHolder = CheckboxListItemHolder(v)
                val tempListHolder: CheckboxListItemHolder =
                        tempViewHolder as CheckboxListItemHolder
                mViewHolderList.add(tempListHolder)
                v.setOnLongClickListener(LongElementClickListener(tempListHolder))
                v.setOnClickListener(ElementClickListener(tempListHolder))
                return tempViewHolder
            }
            else -> return null
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Получаем тип айтема в данной позиции для заполнения его данными
        tempViewHolderPosition = holder.adapterPosition
        val taskListItem = mItems!![position]
        //Снимаем выделение
        if (taskListItem.isSelected)
            holder.itemView.setBackgroundResource(R.color.black)
        else
            holder.itemView.setBackgroundResource(R.color.colorAccent)
        val type = taskListItem.type
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
                    val cb = CheckBox(layout.context)
                    cb.isChecked = item.isCbState
                    cb.isClickable = false
                    cb.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    val cbText = TextView(layout.context)
                    cbText.setPadding(0, 0, 0, 10)
                    cbText.text = item.cbText
                    cbText.setTextColor(mInflater.context.resources
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

    //Удаляет задачу по позиции
    internal fun removeItem(position: Int) {
        val activity = mInflater.context as MainActivity
        dbMethodsAdapter!!.delete(position)
        //TODO : ТУТ И ВО ВСЕХ ПОДОБНЫХ МЕСТАХ ПЕРЕВЕСТИ ЛОГИКУ С АКТИВИТИ НА ПРЕЗЕНТЕР
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

    inner class LongElementClickListener(internal var mViewHolder: RecyclerView.ViewHolder) : View.OnLongClickListener {

        override fun onLongClick(v: View): Boolean {
            val activity = mInflater.context as MainActivity
            if (activity.mActionMode == null) {
                activity.mActionMode = activity.startSupportActionMode(
                        activity.mActionModeCallback!!)
                activity.mActionMode!!.title = "Action Mode"
                tempViewHolderPosition = mViewHolder.adapterPosition
                //Добавление выделения при выборе
                addSelection(mViewHolder)
            } else {
                activity.mActionMode!!.finish()
                //Сброс выделения
                removeSelection()
            }
            return true
        }


    }

    inner class ElementClickListener(internal var mViewHolder: RecyclerView.ViewHolder) : View.OnClickListener {

        override fun onClick(v: View) {
            val activity = mInflater.context as MainActivity
            tempViewHolderPosition = mViewHolder.adapterPosition
            when (mViewHolder.itemViewType) {
                TYPE_ITEM_TEXT -> {
                    val textIntentChange = Intent(activity, TextTaskActivity::class.java)
                    textIntentChange.putExtra("data", dbMethodsAdapter!!.getById(
                            mItems!![tempViewHolderPosition].id))
                    textIntentChange.putExtra("id", mItems[tempViewHolderPosition].id)
                    activity.startActivityForResult(textIntentChange, MainActivity.TEXT_RESULT)
                }
                TYPE_ITEM_LIST -> {
                    val listIntentChange = Intent(activity, ListTaskActivity::class.java)
                    listIntentChange.putExtra("data", dbMethodsAdapter!!.getById(
                            mItems!![tempViewHolderPosition].id))
                    listIntentChange.putExtra("id", mItems!![tempViewHolderPosition].id)
                    activity.startActivityForResult(listIntentChange, MainActivity.LIST_RESULT)
                }
            }
        }
    }

    //Выделяет задачу
    private fun addSelection(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.setBackgroundResource(R.color.black)
        mItems!![tempViewHolderPosition].isSelected = true
    }

    //Снимает выделение задачи
    private fun removeSelection() {
        for (item in mItems!!) {
            item.isSelected = false
        }
        for (viewHolder in mViewHolderList) {
            viewHolder.itemView.setBackgroundResource(R.color.colorAccent)
        }
    }

    fun initActionMode() {
        val activity = mInflater.context as MainActivity
        activity.mActionModeCallback = object : android.support.v7.view.ActionMode.Callback {
            override fun onCreateActionMode(mode: android.support.v7.view.ActionMode, menu: Menu): Boolean {
                val inflater = mode.menuInflater
                inflater.inflate(R.menu.menu_context_task, menu)
                return true
            }

            override fun onPrepareActionMode(mode: android.support.v7.view.ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: android.support.v7.view.ActionMode,
                                             item: MenuItem): Boolean {
                removeItem(mItems!![tempViewHolderPosition].id)
                activity.mActionMode!!.finish()
                return false
            }

            override fun onDestroyActionMode(mode: android.support.v7.view.ActionMode) {
                removeSelection()
                activity.mActionMode = null
            }

        }
    }

    companion object {

        val TYPE_ITEM_TEXT = 0
        val TYPE_ITEM_IMAGE = 1
        val TYPE_ITEM_LIST = 2
    }
}