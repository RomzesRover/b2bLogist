package com.b2blogist.vkcommviewer.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
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
import kotlinx.android.synthetic.main.group_page_header.view.*
import kotlinx.android.synthetic.main.group_page_row.view.*
import kotlinx.android.synthetic.main.simple_link.view.*
import kotlinx.android.synthetic.main.simple_photo.view.*
import kotlinx.android.synthetic.main.simple_video.view.*
import vk.api.Group
import vk.api.WallMessage
import java.text.SimpleDateFormat
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
            TYPE_HEADER -> HeaderHolder(layoutInflater.inflate(R.layout.group_page_header, parent, false))
            else -> WallMessageHolder(layoutInflater.inflate(R.layout.group_page_row, parent, false))
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

            view.group_name.text = group.name ?: "No group name"
            view.group_status.text = group.status ?: "No group status"
            Picasso.get().load(group.photo_big ?: group.photo_medium ?: group.photo).fit().centerInside().into(view.group_avatar)
            view.group_description.text = group.description ?: "No group desc"
            view.group_web_address.text = group.site ?: "No group site"
            view.group_location_address.text = (group.city_name ?: "No city").plus( ", ").plus(group.addresses?.get(0)?.address ?: "No group address")
            view.group_telephone.text = group.contacts?.first()?.phone ?: "No phone"
        }
    }

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
                        var width = 0
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
                        var width = 0
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

    interface OnLoadMoreListener{
        fun onLoadMore()
    }
}