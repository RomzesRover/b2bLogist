package com.b2blogist.vkcommviewer.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.b2blogist.vkcommviewer.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.group_page_header.view.*
import vk.api.Group

class HeaderHolder(private val view: View) : RecyclerView.ViewHolder(view){
    fun bindHeader(group: Group){
        //base info
        group.name?.takeIf { it.isNotBlank() }?.let {
            view.group_name.apply {
                text = it
            }
        }
        group.status?.takeIf { it.isNotBlank() }?.let {
            view.group_status.apply {
                visibility = View.VISIBLE
                text = it
            }
        }
        //set avatar
        (group.photo_big ?: group.photo_medium ?: group.photo)?.takeIf { it.isNotBlank() }?.let {
            view.group_avatar.also { group_avatar ->
                Picasso.get().load(it).fit().centerInside().into(group_avatar)
            }
        }
        //additional info
        group.description?.takeIf { it.isNotBlank() }?.let {
            view.description.apply {
                visibility = View.VISIBLE
                group_description.text = it
            }
        }
        group.site?.takeIf { it.isNotBlank() }?.let {
            view.web_address.apply {
                visibility = View.VISIBLE
                group_web_address.text = it
            }
        }
        group.members_count?.let {
            view.followers.apply {
                visibility = View.VISIBLE
                group_followers.text = view.context.getString(R.string.followers).format(it)
            }
        }
        group.contacts?.first()?.phone?.takeIf { it.isNotBlank() }?.let {
            view.telephone.apply {
                visibility = View.VISIBLE
                group_telephone.text = it
            }
        }
        //additional info: address city + address
        group.city_name?.takeIf { it.isNotBlank() }?.let {
            view.location.apply {
                visibility = View.VISIBLE
                group_location_address.text = it
            }
        }
        group.addresses?.get(0)?.address?.takeIf { it.isNotBlank() }?.let {
            view.location.apply {
                if (visibility == View.VISIBLE)
                    group_location_address.text = group_location_address.text.toString().plus(", $it")
                else {
                    visibility = View.VISIBLE
                    group_location_address.text = it
                }
            }
        }
    }
}