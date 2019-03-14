package com.b2blogist.vkcommviewer.adapters.holders

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.group_page_header.view.*
import vk.api.Group

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