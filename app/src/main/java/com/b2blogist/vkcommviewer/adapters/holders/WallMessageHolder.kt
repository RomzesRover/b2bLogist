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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.b2blogist.vkcommviewer.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.group_page_row.view.*
import kotlinx.android.synthetic.main.simple_link.view.*
import kotlinx.android.synthetic.main.simple_photo.view.*
import kotlinx.android.synthetic.main.simple_video.view.*
import vk.api.Group
import vk.api.WallMessage
import java.text.SimpleDateFormat
import java.util.*

class WallMessageHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{
    private var wallMessage: WallMessage? = null
    private var group: Group? = null

    init {
        view.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        Log.d("RecyclerView", "CLICK!")
    }

    fun bindWallMessage(group: Group, wallMessage: WallMessage, layoutInflater: LayoutInflater, targetWidth: Int){
        this.group = group
        this.wallMessage = wallMessage

        Picasso.get().load(group.photo_big ?: group.photo_medium ?: group.photo).fit().centerInside().into(view.group_avatar_list)
        view.group_name_list.text = group.name ?: "No group name"
        view.post_date.text = convertLongToTime(wallMessage.date)
        view.post_text.movementMethod = LinkMovementMethod.getInstance()
        view.post_text.text = linkifyHtml(wallMessage.text ?: "No post text", Linkify.ALL)
        view.likes.text = wallMessage.like_count?.toString() ?: "0"
        view.comments.text = wallMessage.comment_count?.toString() ?: "0"
        view.shares.text = wallMessage.reposts_count?.toString() ?: "0"
        view.views.text = wallMessage.views_count?.toString() ?: "0"

        view.attachments.removeAllViews()

        wallMessage.attachments?.forEach {
            it.video?.let { video ->
                val videoView = layoutInflater.inflate(R.layout.simple_video, view.attachments as ViewGroup, false)
                Picasso.get().load(video.image_big).fit().centerCrop().into(videoView.video_image)
                videoView.video_title.text = video.title ?: "No video name"
                videoView.video_views.text = (video.views?.toString() ?: "No videos").plus(" views")
                //set video click
                videoView.video.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.vk.com/video${video.owner_id}_${video.vid}"))
                    videoView.context.startActivity(browserIntent)
                }
                //add to list
                view.attachments.addView(videoView)
            }
            it.photo?.let {photo ->
                val photoView = layoutInflater.inflate(R.layout.simple_photo, view.attachments as ViewGroup, false)
                photoView.photo_image.visibility = View.GONE
                photo?.photo_sizes?.let { photo_sizes ->
                    var src: String? = ""
                    var width = -1
                    run breaker@{
                        photo_sizes.forEach {photo_size ->
                            if (photo_size.width > width) {
                                width = photo_size.width
                                src = photo_size.src
                                if (width >= targetWidth)
                                    return@breaker
                            }
                        }
                    }
                    photoView.photo_image.visibility = View.VISIBLE
                    Picasso.get().load(src).fit().centerCrop().into(photoView.photo_image)
                }
                //set photo click
                photoView.photo.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.vk.com/photo${photo.owner_id}_${photo.pid}"))
                    photoView.context.startActivity(browserIntent)
                }
                //add to list
                view.attachments.addView(photoView)
            }
            it.link?.let {link ->
                //in attachements link found show link block
                val linkView = layoutInflater.inflate(R.layout.simple_link, view.attachments as ViewGroup, false)
                linkView.link_image.visibility = View.GONE
                link.photo?.photo_sizes?.let { photo_sizes ->
                    var src: String? = ""
                    var width = -1
                    run breaker@{
                        photo_sizes.forEach {photo_size ->
                            if (photo_size.width > width) {
                                width = photo_size.width
                                src = photo_size.src
                                if (width >= targetWidth)
                                    return@breaker
                            }
                        }
                    }
                    linkView.link_image.visibility = View.VISIBLE
                    Picasso.get().load(src).fit().centerCrop().into(linkView.link_image)
                }
                linkView.link_title.text = link.title ?: "No link title"
                linkView.link_url.text = link.url ?: "No link url"
                //set link click
                linkView.link.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                    linkView.context.startActivity(browserIntent)
                }
                //add to list
                view.attachments.addView(linkView)
            }
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