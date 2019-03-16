package com.b2blogist.vkcommviewer.adapters.holders

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.b2blogist.vkcommviewer.PostCommentsActivity
import com.b2blogist.vkcommviewer.R
import com.b2blogist.vkcommviewer.adapters.holders.utils.AttachmentViewJob
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.group_page_row.view.*
import vk.api.Group
import vk.api.WallMessage
import java.text.SimpleDateFormat
import java.util.*

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

    fun bindWallMessage(group: Group, wallMessage: WallMessage, layoutInflater: LayoutInflater, targetWidth: Int){
        this.group = group
        this.wallMessage = wallMessage

        //set group info
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
                text = convertLongToTime(it)
            }
        }
        //set post text
        wallMessage.text?.takeIf { it.isNotBlank() }?.let {
            view.post_text.setOnClickListener(this)
            view.post_text.apply {
                visibility = View.VISIBLE
                movementMethod = LinkMovementMethod.getInstance()
                text = linkifyHtml(it, Linkify.ALL)
            }
        }

        //set numerated post data
        view.likes.text = wallMessage.like_count?.toString() ?: "0"
        view.comments.text = wallMessage.comment_count?.toString() ?: "0"
        view.shares.text = wallMessage.reposts_count?.toString() ?: "0"
        view.views.text = wallMessage.views_count?.toString() ?: "0"

        //attachments job
        view.attachments.removeAllViews()

        wallMessage.attachments?.forEach {
            it.video?.let { video ->
                val videoView = layoutInflater.inflate(R.layout.simple_video, view.attachments as ViewGroup, false)
                //add to list
                view.attachments.addView(AttachmentViewJob.setUpVideoAttachment(videoView, video))
            }
            it.photo?.let {photo ->
                val photoView = layoutInflater.inflate(R.layout.simple_photo, view.attachments as ViewGroup, false)
                //add to list
                view.attachments.addView(AttachmentViewJob.setUpPhotoAttachment(photoView, photo, targetWidth))
            }
            it.link?.let {link ->
                //in attachments link found show link block
                val linkView = layoutInflater.inflate(R.layout.simple_link, view.attachments as ViewGroup, false)
                //add to list
                view.attachments.addView(AttachmentViewJob.setUpLinkAttachment(linkView, link, targetWidth))
            }
        }
    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time * 1000L)
        val format = SimpleDateFormat("dd MMM yyyy 'at' HH:mm")
        return format.format(date)
    }

    private fun linkifyHtml(html: String, linkifyMask: Int): Spannable {
        val text = Html.fromHtml(html)
        val currentSpans = text.getSpans(0, text.length, URLSpan::class.java)

        val buffer = SpannableString(text)
        Linkify.addLinks(buffer, linkifyMask)

        for (span in currentSpans) {
            val end = text.getSpanEnd(span)
            val start = text.getSpanStart(span)
            buffer.setSpan(span, start, end, 0)
        }
        return buffer
    }
}