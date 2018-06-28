package com.milnest.tasklist.presentation.list

import android.support.v7.widget.RecyclerView
import android.text.Editable
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

class ListTaskAdapter(val cbClickListener: CbClickListener):
        RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    val CHECKBOX_ITEM_TYPE = 0
    val ADD_ITEM_TYPE = 1
    private var onBind : Boolean = true

    private lateinit var itemsList: MutableList<*/*CheckboxTaskListItem*/>
    private val createCb = App.context.resources.getString(R.string.new_cb)
    private var curRecyclerView:RecyclerView? = null

    fun setData(data : MutableList<CheckboxTaskListItem>){
        itemsList = data
        /*onCreateViewHolder(curRecyclerView as ViewGroup, 1)*/
        //onCreateViewHolder(,1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType){
            CHECKBOX_ITEM_TYPE -> {
                CbHolder(LayoutInflater.from(parent.context).inflate(R.layout.checkbox_item,
                        parent, false))
            }
            else /*ADD_ITEM_TYPE*/ -> {
                AddHolder(LayoutInflater.from(parent.context).inflate(R.layout.add_new_cb,
                        parent, false))
            }
        }

        /*val v: View = LayoutInflater.from(parent.context).inflate(R.layout.checkbox_item,
                parent, false)
        return CbHolder(v)*/
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBind = true
        if (holder is CbHolder) {
            val cbItem = itemsList[position]
            val cbHolder = holder
            cbHolder.state.isChecked = /*cbItem.isCbState*/ (cbItem as CheckboxTaskListItem).isCbState
            cbHolder.text.setText(cbItem.cbText)
        }
        else{
           val newHolder = holder as AddHolder
           newHolder.newCb.text = createCb
        }
        onBind = false
    }

    override fun getItemCount(): Int {
        return itemsList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == itemsList.size) ADD_ITEM_TYPE else CHECKBOX_ITEM_TYPE
    }

    fun setRecycler(recycler_view_cb: RecyclerView) {
        curRecyclerView = recycler_view_cb
    }

    interface CbClickListener {
        fun onRemoveItem(position: Int)
        fun onAddItem()
        fun onStateChanged(layoutPosition: Int, state: Boolean)
        fun onTextChanged(layoutPosition: Int, text: String)
    }

    inner class AddHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        internal var newCb: TextView = itemView.findViewById(R.id.new_cb)
        init {
            newCb.setOnClickListener { cbClickListener.onAddItem() }
        }
    }

    inner class CbHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        internal var state: CheckBox = itemView.findViewById(R.id.addedCb)
        internal var text: EditText = itemView.findViewById(R.id.addedCbText)
        internal var deleteTextView: TextView = itemView.findViewById(R.id.delTextView)
        init {
            deleteTextView.text = "X"
            deleteTextView.setOnClickListener {cbClickListener.onRemoveItem(layoutPosition)}
            state.setOnCheckedChangeListener { _, isChecked -> if(!onBind) cbClickListener.onStateChanged(layoutPosition, isChecked)}
            text.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!onBind) cbClickListener.onTextChanged(layoutPosition, text.text.toString())
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })
        }
    }

}
