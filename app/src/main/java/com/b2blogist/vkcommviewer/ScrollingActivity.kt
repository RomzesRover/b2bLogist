package com.b2blogist.vkcommviewer

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.b2blogist.vkcommviewer.adapters.GroupPageAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.content_scrolling.*
import vk.api.Api
import vk.api.Group


class ScrollingActivity : AppCompatActivity() {
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        swiperefresh.setOnRefreshListener {
            updateOperation()
        }

        //init recycle view
        viewAdapter = GroupPageAdapter(this, Group(), ArrayList())
        viewManager = LinearLayoutManager(this)
        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //start update on activity open
        swiperefresh.isRefreshing = true
        updateOperation()
    }

    private fun updateOperation() = Thread(Runnable {
        //get group
        var group = Api.getGroups(arrayListOf(90405472L), null, "cover,contacts,status,members_count,description,site")!![0]
        //get wall messages
        var wms = Api.getWallMessages(-90405472L, 3, 0, "all")

        runOnUiThread {
            //set group name as title
            toolbar_layout.title = group.name
            //set group covers as background for toolbar
            Picasso.get().load(group.covers?.get(4)?.src).resize(toolbar_layout.width, toolbar_layout.height).centerCrop().into(object  : com.squareup.picasso.Target{
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) { }
                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) { }
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    toolbar_layout.background = BitmapDrawable(applicationContext.resources, bitmap)
                }
            })
            //update adapter for recycler view
            (recycler_view.adapter as GroupPageAdapter).setNewGroupInfo(group)
            (recycler_view.adapter as GroupPageAdapter).addWallMessages(wms)
            //stop refresh animation
            swiperefresh.isRefreshing = false
        }
    }).start()
}
