package com.b2blogist.vkcommviewer.adapters.holders.utils

import android.content.Intent
import android.net.Uri
import android.view.View
import com.b2blogist.vkcommviewer.targets.Targets
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.simple_link.view.*
import kotlinx.android.synthetic.main.simple_photo.view.*
import kotlinx.android.synthetic.main.simple_video.view.*
import vk.api.Link
import vk.api.Photo
import vk.api.Video

object AttachmentViewJob {
    fun setUpVideoAttachment(videoView: View, video: Video): View{
        Picasso.get().load(video.image_big).fit().centerCrop().into(videoView.video_image)
        videoView.video_title.text = video.title ?: "No video name"
        videoView.video_views.text = (video.views?.toString() ?: "No videos").plus(" views")
        //set video click
        videoView.video.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.vk.com/video${video.owner_id}_${video.vid}"))
            videoView.context.startActivity(browserIntent)
        }
        return videoView
    }

    fun setUpLinkAttachment(linkView: View, link: Link): View{
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
            Picasso.get().load(src).fit().centerCrop().into(linkView.link_image)
        }
        linkView.link_title.text = link.title ?: "No link title"
        linkView.link_url.text = link.url ?: "No link url"
        //set link click
        linkView.link.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
            linkView.context.startActivity(browserIntent)
        }
        return linkView
    }

    fun setUpPhotoAttachment(photoView: View, photo: Photo): View{
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
            Picasso.get().load(src).fit().centerCrop().into(photoView.photo_image)
        }
        //set photo click
        photoView.photo.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.vk.com/photo${photo.owner_id}_${photo.pid}"))
            photoView.context.startActivity(browserIntent)
        }
        return photoView
    }
}