package com.b2blogist.vkcommviewer.adapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.b2blogist.vkcommviewer.R
import com.b2blogist.vkcommviewer.adapters.holders.HeaderHolder
import com.b2blogist.vkcommviewer.adapters.holders.WallMessageHolder
import vk.api.Group
import vk.api.WallMessage
import java.util.*


class GroupPageAdapter(private val context: Context, private var group: Group, private var wallMessages: ArrayList<WallMessage>, private val quantityOfWallPostToEachLoad: Int, private val targetWidth: Int): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val TYPE_HEADER = 0
    private val visibleThreshold = 5
    private var lastVisibleItem: Int? = null
    private var totalItemCount: Int? = null
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var isLoading: Boolean = false
    private var isEndOfListReached: Boolean = false
    private lateinit var layoutInflater: LayoutInflater

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
                        isLoading = true
                        onLoadMoreListener?.onLoadMore()
                    }
                }
            }
        })
    }

    fun loadInProgress(){
        isLoading = true
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
        layoutInflater = LayoutInflater.from(context)
        return when(viewType){
            TYPE_HEADER -> HeaderHolder(
                layoutInflater.inflate(
                    R.layout.group_page_header,
                    parent,
                    false
                )
            )
            else -> WallMessageHolder(
                layoutInflater.inflate(
                    R.layout.group_page_row,
                    parent,
                    false
                ), false
            )
        }
    }

    override fun getItemCount() = wallMessages.size+1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderHolder) {
            holder.bindHeader(group)
        } else {
            if (holder is WallMessageHolder) {
                holder.bindWallMessage(group, wallMessages[position-1], layoutInflater, targetWidth)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface OnLoadMoreListener{
        fun onLoadMore()
    }
}