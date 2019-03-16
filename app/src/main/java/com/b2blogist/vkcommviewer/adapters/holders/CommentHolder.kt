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
import com.b2blogist.vkcommviewer.adapters.holders.utils.Utils
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
        view.comment_text.text = Utils.linkifyHtml(comment.text ?: "No comments", Linkify.ALL)

        //set comment date
        comment.date?.let {
            view.comment_date.apply {
                visibility = View.VISIBLE
                text = Utils.convertLongToTime(it)
            }
        }

        user?.let {
            view.author_name.text = user.first_name.plus(" ").plus((user.last_name))
            Picasso.get().load(user.photo_200 ?: user.photo_100 ?: user.photo_50).fit().centerInside().into(view.group_user_avatar_list)
        }
        group?.let {
            view.author_name.text = group.name
            Picasso.get().load(group.photo_big ?: group.photo_medium ?: group.photo).fit().centerInside().into(view.group_user_avatar_list)
        }
    }
}