package com.b2blogist.vkcommviewer.adapters.holders.utils

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.b2blogist.vkcommviewer.R
import com.b2blogist.vkcommviewer.targets.Targets
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.simple_link.view.*
import kotlinx.android.synthetic.main.simple_photo.view.*
import kotlinx.android.synthetic.main.simple_video.view.*
import vk.api.Attachment
import vk.api.Link
import vk.api.Photo
import vk.api.Video

object AttachmentViewJob {
    fun doAttachmentsJob(attachments: ArrayList<Attachment>?, parent: View, layoutInflater: LayoutInflater){
        (parent as ViewGroup).removeAllViews()
        attachments?.forEach {
            it.video?.let { video ->
                val videoView = layoutInflater.inflate(R.layout.simple_video, parent, false)
                //add to list
                parent.addView(setUpVideoAttachment(videoView, video))
            }
            it.photo?.let {photo ->
                val photoView = layoutInflater.inflate(R.layout.simple_photo, parent, false)
                //add to list
                parent.addView(setUpPhotoAttachment(photoView, photo))
            }
            it.link?.let {link ->
                //in attachments link found show link block
                val linkView = layoutInflater.inflate(R.layout.simple_link, parent, false)
                //add to list
                parent.addView(setUpLinkAttachment(linkView, link))
            }
        }
    }

    private fun setUpVideoAttachment(videoView: View, video: Video): View{
        Picasso.get().load(video.image_big).into(videoView.video_image)
        videoView.video_title.text = video.title ?: ""
        videoView.video_views.text = videoView.context.getString(R.string.views).format(video.views?.toString() ?: videoView.context.getString(R.string.no))
        //set video click
        videoView.video.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.vk.com/video${video.owner_id}_${video.vid}"))
            videoView.context.startActivity(browserIntent)
        }
        return videoView
    }

    private fun setUpLinkAttachment(linkView: View, link: Link): View{
        linkView.link_image.visibility = View.GONE
        link.photo?.photo_sizes?.let { photo_sizes ->
            var src: String? = ""
            var width = -1
            run breaker@{
                photo_sizes.forEach {photo_size ->
                    if (photo_size.width > width) {
                        width = photo_size.width
                        src = photo_size.src
                        if (width >= Targets.targetWidth)
                            return@breaker
                    }
                }
            }
            linkView.link_image.visibility = View.VISIBLE
            Picasso.get().load(src).into(linkView.link_image)
        }
        linkView.link_title.text = link.title ?: ""
        linkView.link_url.text = link.url ?: ""
        //set link click
        linkView.link.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
            linkView.context.startActivity(browserIntent)
        }
        return linkView
    }

    private fun setUpPhotoAttachment(photoView: View, photo: Photo): View{
        photoView.photo_image.visibility = View.GONE
        photo?.photo_sizes?.let { photo_sizes ->
            var src: String? = ""
            var width = -1
            run breaker@{
                photo_sizes.forEach {photo_size ->
                    if (photo_size.width > width) {
                        width = photo_size.width
                        src = photo_size.src
                        if (width >= Targets.targetWidth)
                            return@breaker
                    }
                }
            }
            photoView.photo_image.visibility = View.VISIBLE
            Picasso.get().load(src).into(photoView.photo_image)
        }
        //set photo click
        photoView.photo.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.vk.com/photo${photo.owner_id}_${photo.pid}"))
            photoView.context.startActivity(browserIntent)
        }
        return photoView
    }
}