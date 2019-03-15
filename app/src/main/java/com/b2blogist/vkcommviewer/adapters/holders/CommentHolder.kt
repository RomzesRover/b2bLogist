package com.b2blogist.vkcommviewer.adapters.holders

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.text.util.Linkify
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.comment_row.view.*
import vk.api.Comment
import vk.api.Group
import vk.api.User
import java.text.SimpleDateFormat
import java.util.*

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

    fun bindHeader(comment: Comment, user: User?, group: Group?){
        this.comment = comment
        this.group = group
        this.user = user

        view.comment_text.setOnClickListener(this)
        view.comment_text.movementMethod = LinkMovementMethod.getInstance()
        view.comment_text.text = linkifyHtml(comment.text ?: "No comments", Linkify.ALL)
        view.comment_date.text = convertLongToTime(comment.date)
        user?.let {
            view.author_name.text = user.first_name.plus(" ").plus((user.last_name))
            Picasso.get().load(user.photo_200 ?: user.photo_100 ?: user.photo_50).fit().centerInside().into(view.group_user_avatar_list)
        }
        group?.let {
            view.author_name.text = group.name
            Picasso.get().load(group.photo_big ?: group.photo_medium ?: group.photo).fit().centerInside().into(view.group_user_avatar_list)
        }
    }

    private fun convertLongToTime(time: Long?): String {
        time?.let {
            val date = Date(it * 1000L)
            val format = SimpleDateFormat("dd MMM yyyy 'at' HH:mm")
            return format.format(date)
        }
        return "No post date"
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