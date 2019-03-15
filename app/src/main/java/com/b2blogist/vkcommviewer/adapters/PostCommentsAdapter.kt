package com.b2blogist.vkcommviewer.adapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.b2blogist.vkcommviewer.R
import com.b2blogist.vkcommviewer.adapters.holders.CommentHolder
import com.b2blogist.vkcommviewer.adapters.holders.HeaderHolder
import com.b2blogist.vkcommviewer.adapters.holders.WallMessageHolder
import vk.api.*
import java.util.ArrayList

class PostCommentsAdapter(private val context: Context, private var group: Group,  private var wallMessage: WallMessage, private var comments: Comments, private val quantityOfWallPostToEachLoad: Int, private val targetWidth: Int): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
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

    fun loadIsFailed(){
        isLoading = false
    }

    fun addComments(comments: Comments){
        this.comments.comments?.addAll(comments.comments as ArrayList<Comment>)
        this.comments.users?.addAll(comments.users as ArrayList<User>)
        this.comments.groups?.addAll(comments.groups as ArrayList<Group>)
        notifyDataSetChanged()
        isLoading = false
        isEndOfListReached = comments.comments!!.size < quantityOfWallPostToEachLoad
    }

    fun setNewWallComments(comments: Comments){
        this.comments = comments
        notifyDataSetChanged()
        isLoading = false
        isEndOfListReached = comments.comments!!.size < quantityOfWallPostToEachLoad
    }

    fun setNewPostInfo(wallMessage: WallMessage){
        this.wallMessage = wallMessage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        layoutInflater = LayoutInflater.from(context)
        return when(viewType){
            TYPE_HEADER -> WallMessageHolder(
                layoutInflater.inflate(
                    R.layout.group_page_row,
                    parent,
                    false
                ), true
            )
            else -> CommentHolder(
                layoutInflater.inflate(
                    R.layout.comment_row,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount() = comments.comments!!.size+1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is WallMessageHolder) {
            holder.bindWallMessage(group, wallMessage, layoutInflater, targetWidth)
        } else {
            if (holder is CommentHolder) {
                val comment = comments.comments!![position-1]
                holder.bindHeader(comment, comments.users?.find { it.uid != null && it.uid == comment.from_id }, comments.groups?.find { it.gid != null && -it.gid!! == comment.from_id })
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