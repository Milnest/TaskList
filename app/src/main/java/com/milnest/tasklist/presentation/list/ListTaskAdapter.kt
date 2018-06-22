package com.milnest.tasklist.presentation.list

import android.support.v7.widget.RecyclerView
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.milnest.tasklist.App
import com.milnest.tasklist.R
import com.milnest.tasklist.entities.CheckboxTaskListItem

class ListTaskAdapter(private val cbClickListener: CbClickListener):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //private val itemsList: MutableList<CheckboxTaskListItem> = ArrayList()
//    private val cbList: MutableList<Pair<*, *>> = ArrayList()
    private val cbHolderList: MutableList<CbHolder> = ArrayList()
    private val cbList: MutableList<CheckBox> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.checkbox_item,
                parent, false)
        val tempHolder = CbHolder(v)
        cbHolderList.add(tempHolder)
        return tempHolder
    }

    fun setData(data : List<CheckboxTaskListItem>){
        for (item in data){
            val cb = CheckBox(App.context)
            cb.isChecked = item.isCbState
            val cbText  = EditText(App.context)
            cbText.setText(item.cbText)
//            cbList.add(Pair(cb, cbText))
            cbList.add(cb)
        }
        notifyDataSetChanged()
    }

    inner class CbHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        internal var state: CheckBox = itemView.findViewById(R.id.addedCb)
        internal var text: EditText = itemView.findViewById(R.id.addedCbText)
        internal var deleteTextView: TextView = itemView.findViewById(R.id.delTextView)
        init {
            deleteTextView.text = "X"
            deleteTextView.setOnClickListener {cbClickListener.onItemClick(layoutPosition)}
            state.setOnClickListener { cbClickListener.onStateChanged(layoutPosition, state.isChecked)}
//            text.addTextChangedListener( cbClickListener.onTextChanged(layoutPosition, text.text.toString()) )
        }
        //TODO: СДЕЛАТЬ ЛИСТЕНЕР КЛИКА
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        //val cbItem = itemsList[position]
        val cbItem = cbList[position]
        val cbHolder = holder as CbHolder
        //cbHolderList.add(cbHolder)
        cbHolder.state = cbItem
//        cbHolder.state = cbItem.first as CheckBox
//        cbHolder.text =  cbItem.second as EditText
//        cbList.add(Pair(cbHolder.state, cbHolder.text))
    }

    override fun getItemCount(): Int {
        return cbList.size ?: 0 //TODO: meh
    }

    fun addCb(/*item : CheckboxTaskListItem*/cb: CheckBox){
       // itemsList.add(item)
        /*val cb = CheckBox(App.context)
        cb.isChecked = item.isCbState*/
        /*val cbText  = EditText(App.context)
        cbText.setText(item.cbText*//*"1000"*//*) //TODO: значение не приходит, с 1000 работает*/
        //cbList.add(Pair(cb, cbText))
        cbList.add(cb)
        notifyDataSetChanged()
    }

    fun deleteCb(position: Int){
        cbList.removeAt(position)
        notifyDataSetChanged()
    }

    fun getList(): MutableList<CheckboxTaskListItem> {
        val resultList: MutableList<CheckboxTaskListItem> = ArrayList()
        for (item in cbList){
            resultList.add(CheckboxTaskListItem("10", item.isChecked))
        }
        /*for (item in cbHolderList){
            resultList.add(CheckboxTaskListItem(item.text.text.toString(), item.state.isChecked))
        }*/
        return resultList
    }

    fun changeState(layoutPosition: Int, state: Boolean) {
        val cb = CheckBox(App.context)
        cb.isChecked = state
        cbList.set(layoutPosition, cb)
    }

    /*fun changeState(layoutPosition: Int, state: Boolean) {
        itemsList[layoutPosition].isCbState = state
    }

    fun changeText(layoutPosition: Int, text: String) {
        itemsList[layoutPosition].cbText = text
    }*/

    interface CbClickListener {
        fun onItemClick(position: Int)
        fun onStateChanged(layoutPosition: Int, state: Boolean)
//        fun onTextChanged(layoutPosition: Int, text: String) : TextWatcher
    }
}
