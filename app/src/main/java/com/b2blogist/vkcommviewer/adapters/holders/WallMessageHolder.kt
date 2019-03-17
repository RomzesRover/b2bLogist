package com.b2blogist.vkcommviewer.adapters.holders

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import com.b2blogist.vkcommviewer.PostCommentsActivity
import com.b2blogist.vkcommviewer.adapters.holders.utils.AttachmentViewJob
import com.b2blogist.vkcommviewer.adapters.holders.utils.Utils
import com.b2blogist.vkcommviewer.customViews.LayoutedTextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.group_page_row.view.*
import vk.api.Group
import vk.api.WallMessage

class WallMessageHolder(private val view: View, private val isOnTop: Boolean) : RecyclerView.ViewHolder(view), View.OnClickListener{
    private var wallMessage: WallMessage? = null
    private var group: Group? = null

    init {
        view.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (!isOnTop) {
            var postCommentsIntent = Intent(v.context, PostCommentsActivity::class.java)
            postCommentsIntent.putExtra("wallMessage", wallMessage)
            postCommentsIntent.putExtra("group", group)
            v.context.startActivity(postCommentsIntent)
        }
    }

    fun bindWallMessage(group: Group, wallMessage: WallMessage, layoutInflater: LayoutInflater){
        this.group = group
        this.wallMessage = wallMessage

        //set group name
        group.name?.takeIf { it.isNotBlank() }?.let {
            view.group_name_list.apply {
                text = it
            }
        }
        //set avatar
        (group.photo_big ?: group.photo_medium ?: group.photo)?.takeIf { it.isNotBlank() }?.let {
            view.group_avatar_list.also { group_avatar_list ->
                Picasso.get().load(it).fit().centerInside().into(group_avatar_list)
            }
        }
        //set post date
        wallMessage.date?.let {
            view.post_date.apply {
                visibility = View.VISIBLE
                text = Utils.convertLongToTime(it)
            }
        }
        //set post text
        wallMessage.text?.takeIf { it.isNotBlank() }?.let {
            view.post_text.setOnClickListener(this)
            view.post_text.apply {
                visibility = View.VISIBLE
                movementMethod = LinkMovementMethod.getInstance()
                text = Utils.linkifyHtml(it)
            }
            if (!isOnTop)
                view.post_text.setOnLayoutListener(object : LayoutedTextView.OnLayoutListener{
                    override fun onLayouted(textView: LayoutedTextView) {
                        textView.maxLines = 12
                        if (textView.lineCount > textView.maxLines){
                            view.read_more.visibility = View.VISIBLE
                        }
                    }
                })
        }

        //set numerated post data
        view.likes.text = wallMessage.like_count?.toString() ?: "0"
        view.comments.text = wallMessage.comment_count?.toString() ?: "0"
        view.shares.text = wallMessage.reposts_count?.toString() ?: "0"
        view.views.text = wallMessage.views_count?.toString() ?: "0"

        //attachments job
        AttachmentViewJob.doAttachmentsJob(wallMessage.attachments, view.attachments, layoutInflater)
    }
}