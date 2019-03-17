package com.b2blogist.vkcommviewer.adapters.holders

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import com.b2blogist.vkcommviewer.adapters.holders.utils.AttachmentViewJob
import com.b2blogist.vkcommviewer.adapters.holders.utils.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.comment_row.view.*
import vk.api.Comment
import vk.api.Group
import vk.api.User

class CommentHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{
    private var comment: Comment? = null
    private var user: User? = null
    private var group: Group? = null

    init {
        view.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        user?.let {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.vk.com/id${it.uid}"))
            v.context.startActivity(browserIntent)
        }
        group?.let {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.vk.com/club${it.gid}"))
            v.context.startActivity(browserIntent)
        }
    }

    fun bindHeader(comment: Comment, user: User?, group: Group?, layoutInflater: LayoutInflater){
        this.comment = comment
        this.group = group
        this.user = user

        //set comment date
        comment.date?.let {
            view.comment_date.apply {
                visibility = View.VISIBLE
                text = Utils.convertLongToTime(it)
            }
        }

        //set comment text
        comment.text?.takeIf { it.isNotBlank() }?.let {
            view.comment_text.setOnClickListener(this)
            view.comment_text.apply {
                visibility = View.VISIBLE
                movementMethod = LinkMovementMethod.getInstance()
                text = Utils.linkifyHtml(it)
            }
        }

        user?.apply {
            //set user name
            first_name?.takeIf { it.isNotBlank() }?.let {
                view.author_name.apply {
                    text = it
                }
            }
            last_name?.takeIf { it.isNotBlank() }?.let {
                view.author_name.apply {
                    text = if (text.toString().isBlank()) it else text.toString().plus(" $it")
                }
            }

            //set avatar
            (photo_200 ?: photo_100 ?: photo_50)?.takeIf { it.isNotBlank() }?.let {
                view.group_user_avatar_list.also { group_user_avatar_list ->
                    Picasso.get().load(it).into(group_user_avatar_list)
                }
            }
        }
        group?.apply {
            //set group name
            name?.takeIf { it.isNotBlank() }?.let {
                view.author_name.apply {
                    text = it
                }
            }
            //set avatar
            (photo_big ?: photo_medium ?: photo)?.takeIf { it.isNotBlank() }?.let {
                view.group_user_avatar_list.also { group_user_avatar_list ->
                    Picasso.get().load(it).into(group_user_avatar_list)
                }
            }
        }

        //attachments job
        AttachmentViewJob.doAttachmentsJob(comment.attachments, view.attachments, layoutInflater)
    }
}