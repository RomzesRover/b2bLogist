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
import android.support.v7.widget.LinearLayoutManager

class GroupPageAdapter(private val context: Context, private var group: Group, private var wallMessages: ArrayList<WallMessage>, private val quantityOfWallPostToEachLoad: Int): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val TYPE_HEADER = 0
    private val visibleThreshold = 5
    private var lastVisibleItem: Int? = null
    private var totalItemCount: Int? = null
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var isLoading: Boolean = true
    private var isEndOfListReached: Boolean = true

    fun setOnLoadMoreListener(recyclerView: RecyclerView, mOnLoadMoreListener: OnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isEndOfListReached) {
                    totalItemCount = linearLayoutManager.itemCount
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                    if (!isLoading && totalItemCount!! <= lastVisibleItem!! + visibleThreshold) {
                        onLoadMoreListener?.onLoadMore()
                        isLoading = true
                    }
                }
            }
        })
    }

    fun addWallMessages(wallMessages: ArrayList<WallMessage>){
        this.wallMessages.addAll(wallMessages)
        notifyDataSetChanged()
        isLoading = false
        isEndOfListReached = wallMessages.size < quantityOfWallPostToEachLoad
    }

    fun setNewWallMessages(wallMessages: ArrayList<WallMessage>){
        this.wallMessages = wallMessages
        notifyDataSetChanged()
        isLoading = false
        isEndOfListReached = wallMessages.size < quantityOfWallPostToEachLoad
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

    interface OnLoadMoreListener{
        fun onLoadMore()
    }
}