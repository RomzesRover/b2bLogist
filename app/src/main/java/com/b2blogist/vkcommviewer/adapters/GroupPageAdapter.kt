package com.b2blogist.vkcommviewer.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.b2blogist.vkcommviewer.R
import kotlinx.android.synthetic.main.group_page_header.view.*
import kotlinx.android.synthetic.main.group_page_row.view.*
import vk.api.Group
import vk.api.WallMessage




class GroupPageAdapter(private val context: Context,private var group: Group, private val wallMessages: ArrayList<WallMessage>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val TYPE_HEADER = 0

    fun addWallMessages(wallMessages: ArrayList<WallMessage>){
        this.wallMessages.addAll(wallMessages)
        notifyDataSetChanged()
    }

    fun setNewGroupInfo(group: Group){
        this.group = group
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_HEADER -> HeaderHolder(LayoutInflater.from(context).inflate(R.layout.group_page_header, parent, false))
            else -> WallMessageHolder(LayoutInflater.from(context).inflate(R.layout.group_page_row, parent, false))
        }
    }

    override fun getItemCount() = wallMessages.size+1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderHolder) {
            holder.bindHeader(group)
        } else {
            if (holder is WallMessageHolder) {
                holder.bindWallMessage(wallMessages[position-1])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class HeaderHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{
        private var group: Group? = null

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Log.d("RecyclerView", "CLICK!")
        }

        fun bindHeader(group: Group){
            this.group = group
            view.group_name.text = group.name

        }
    }

    class WallMessageHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{
        private var wallMessage: WallMessage? = null

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Log.d("RecyclerView", "CLICK!")
        }

        fun bindWallMessage(wallMessage: WallMessage){
            this.wallMessage = wallMessage
            view.tvAnimalName.text = wallMessage.text

        }
    }
}