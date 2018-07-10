package com.milnest.tasklist.presentation.main

import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.Task
import com.milnest.tasklist.other.utils.JsonAdapter
import com.squareup.picasso.Picasso
import java.io.File

/**
 * Created by t-yar on 17.04.2018.
 */

open class ItemsAdapter(private val iClickListener: IClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var tempViewHolder: RecyclerView.ViewHolder? = null
    private val mViewHolderList: MutableList<RecyclerView.ViewHolder> = java.util.ArrayList()
    private var mItems: MutableList<taskWithState>? = ArrayList()
    //override val ADAPTER_TYPE = ADAPTER_TYPE_LINEAR
    private val LIST_SIZE = 5

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View

        when (viewType) {
            Task.TYPE_ITEM_TEXT -> {
                v = LayoutInflater.from(parent.context).inflate(R.layout.item_main_text,
                        parent, false)
                tempViewHolder = TextItemHolder(v)
                return tempViewHolder as TextItemHolder
            }
            Task.TYPE_ITEM_IMAGE -> {
                v = LayoutInflater.from(parent.context).inflate(R.layout.item_main_img,
                        parent, false)
                tempViewHolder = ImgItemHolder(v)
                return tempViewHolder as ImgItemHolder
            }
            /*Task.TYPE_ITEM_LIST*/ else -> {
                v = LayoutInflater.from(parent.context).inflate(
                        R.layout.item_main_cblist, parent, false)
                tempViewHolder = CheckboxListItemHolder(v)
                return tempViewHolder as CheckboxListItemHolder
            }
            //else ->
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        mViewHolderList.add(holder.adapterPosition, holder)
        val taskListItem = mItems!![position]
        val type = taskListItem.task.type
        if (mItems!![position].state)
            holder.itemView.setBackgroundResource(R.color.black)
        else
            holder.itemView.setBackgroundResource(R.color.colorWhite)
        when (type) {
            Task.TYPE_ITEM_TEXT -> {
                val textItemHolder = holder as TextItemHolder
                textItemHolder.mName.text = taskListItem.task.title
                textItemHolder.mText.text = taskListItem.task.data
            }
            Task.TYPE_ITEM_IMAGE -> {
                val imgItemHolder = holder as ImgItemHolder
                imgItemHolder.mImgName.text = taskListItem.task.title
                Picasso.get().load(File(taskListItem.task.data)).resize(250, 250).into(imgItemHolder.mImage)
            }
            Task.TYPE_ITEM_LIST -> {
                val cbListItemHolder = holder as CheckboxListItemHolder
                //val listView = cbListItemHolder.cbListView
                val recView = cbListItemHolder.cbRecyclerView
                recView.layoutManager = LinearLayoutManager(holder.itemView.context)
                val listOfCb = JsonAdapter.fromJson(taskListItem.task.data)
                if (listOfCb.size > LIST_SIZE){
//                    listView.adapter = CbAdapterMain(listOfCb.subList(0, 4).toMutableList())
                    recView.adapter = CbAdapterMainRecycler(listOfCb.subList(0, 4))
                }else{
                    recView.adapter = CbAdapterMainRecycler(listOfCb)
//                    listView.adapter = CbAdapterMain(listOfCb.toMutableList())
                }

                /*val cbListItemHolder = holder as CheckboxListItemHolder
                val layout = cbListItemHolder.cbListLayout
                layout.removeAllViews()
                val listOfCb = JsonAdapter.fromJson(taskListItem.task.data)
                for (item in listOfCb) {
                    val cb = CheckBox(layout.context).apply {
                        setOnTouchListener { _, _ -> true }
                    }
                    cb.isChecked = item.isCbState
                    cb.isClickable = false
                    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams.setMargins(3, 0, 6, 0)
                    layoutParams.gravity = Gravity.CENTER
                    cb.layoutParams = layoutParams
                    val cbText = TextView(layout.context)
                    cbText.text = item.cbText
                    cbText.setTextColor(App.context.resources
                            .getColor(R.color.dark_gray))
                    val textParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    textParams.gravity = Gravity.CENTER
                    cbText.layoutParams = textParams
                    cbText.isClickable = false
                    val innerLayout = LinearLayout(layout.context)
                    innerLayout.addView(cb)
                    innerLayout.addView(cbText)
                    layout.addView(innerLayout)
                }*/
            }
        }
    }


    override fun getItemCount(): Int {
        return mItems?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return mItems!!.get(position).task.type
    }


    open inner class TaskItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        init {
            val cardView = itemView.findViewById<CardView>(R.id.card_view)
            cardView.setOnClickListener { iClickListener.onItemClick(layoutPosition) }
            cardView.setOnLongClickListener { iClickListener.onItemLongClick(layoutPosition) }
        }
    }

    inner class TextItemHolder(itemView: View) : TaskItemHolder(itemView) {
        internal var mName: TextView = itemView.findViewById(R.id.name)
        internal var mText: TextView = itemView.findViewById(R.id.text)
    }

    inner class ImgItemHolder(itemView: View) : TaskItemHolder(itemView) {
        internal var mImgName: TextView = itemView.findViewById(R.id.imgName)
        internal var mImage: ImageView = itemView.findViewById(R.id.img)
    }

    inner class CheckboxListItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /*internal var cbListLayout: LinearLayout = itemView.findViewById<View>(R.id.layout_to_add)
                as LinearLayout*/
        /*internal var cbListView : ListView = itemView.findViewById(R.id.cb_list_main)*/
        internal var  cbRecyclerView : RecyclerView = itemView.findViewById(R.id.recycler_view_cb)
        init {
            cbRecyclerView.setOnTouchListener { _, _ -> false }
            itemView.findViewById<View>(R.id.clickView).setOnClickListener { iClickListener.onItemClick(layoutPosition) }
            itemView.findViewById<View>(R.id.clickView).setOnLongClickListener { iClickListener.onItemLongClick(layoutPosition) }
        }
    }

    //TODO:?????
    open fun addSelection(position: Int) {
        var holderToSelect: RecyclerView.ViewHolder? = null
        for (viewHolder in mViewHolderList){
            if (viewHolder.adapterPosition == position) holderToSelect=viewHolder
        }
        holderToSelect?.itemView?.setBackgroundResource(R.color.black)
        mItems!!.get(position).state = true
    }

    open fun removeSelection() {
        for (item in mItems!!){
            item.state = false
        }
        for (viewHolder in mViewHolderList) {
            viewHolder.itemView.setBackgroundResource(R.color.colorWhite)
        }
    }

    inner class taskWithState(var task: Task, var state: Boolean)

    open fun setData(data : List<Task>){
        mItems!!.clear()
        for (item in data){
            mItems!!.add(taskWithState(item, false))
        }

        notifyDataSetChanged()
    }


    override fun getItemId(position: Int): Long {
        return mItems!![position].task.id.toLong()
    }

    /*interface IClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int) : Boolean
    }*/
}