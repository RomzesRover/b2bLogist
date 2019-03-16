package com.b2blogist.vkcommviewer

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.b2blogist.vkcommviewer.adapters.GroupPageAdapter
import com.b2blogist.vkcommviewer.targets.Targets
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.content_scrolling.*
import vk.api.Api
import vk.api.Group
import vk.api.WallMessage
import java.util.regex.Pattern


class ScrollingActivity : AppCompatActivity() {
    private lateinit var viewAdapter: GroupPageAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        swiperefresh.setOnRefreshListener {
            updateOperation()
        }

        //init images size targeted width
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        Targets.targetWidth = size.x / 2

        //start update on activity open
        swiperefresh.isRefreshing = true
        updateOperation()
    }

    private fun updateOperation() = Thread(Runnable {
        try {
            //initial load of page
            if (::viewAdapter.isInitialized) viewAdapter.loadInProgress()
            //get group
            var group = Api.getGroups(
                arrayListOf(Targets.targetGroupID),
                null,
                "cover,contacts,status,members_count,description,site,city"
            )!![0]
            //get wall messages
            var wallMessages =
                Api.getWallMessages(-Targets.targetGroupID, Targets.quantityOfWallPostToEachLoad, 0, "all")

            //fix user links n lines
            convertTextLinksFromVkStyleToWebStyleSortPhotoLinks(wallMessages)

            runOnUiThread {
                //set group name as title
                toolbar_layout.title = group.name
                //set group covers as background for toolbar
                var src: String? = ""
                var width = -1
                run breaker@{
                    group.covers?.forEach { cover ->
                        if (cover.width > width) {
                            width = cover.width
                            src = cover.src
                            if (width >= Targets.targetWidth)
                                return@breaker
                        }
                    }
                }
                Picasso.get().load(src).resize(toolbar_layout.width, toolbar_layout.height)
                    .centerCrop().into(object : com.squareup.picasso.Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        toolbar_layout.background =
                            BitmapDrawable(applicationContext.resources, bitmap)
                    }
                })
                //update adapter for recycler view
                initRecycleView(group, wallMessages)
                //stop refresh animation
                swiperefresh.isRefreshing = false
            }
        } catch (e: java.lang.Exception){
            runOnUiThread {
                //stop refresh animation
                swiperefresh.isRefreshing = false
                Toast.makeText(applicationContext, R.string.error_on_load, Toast.LENGTH_LONG).show()
                viewAdapter.loadIsFailed()
            }
            e.printStackTrace()
        }
    }).start()

    private fun initRecycleView(group: Group, wallMessages: ArrayList<WallMessage>){
        if (::viewAdapter.isInitialized){
            viewAdapter.setNewGroupInfo(group)
            viewAdapter.setNewWallMessages(wallMessages)
        } else {
            //init recycle view
            viewAdapter = GroupPageAdapter(this, group, wallMessages)
            viewManager = LinearLayoutManager(this)
            val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(this.getDrawable(R.drawable.divider)!!)
            recycler_view.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
                addItemDecoration(dividerItemDecoration)
            }
            //set on load more listener (load and apply new posts)
            viewAdapter.setOnLoadMoreListener(recycler_view, object : GroupPageAdapter.OnLoadMoreListener {
                override fun onLoadMore() = Thread(Runnable {
                    try {
                        var wms = Api.getWallMessages(-Targets.targetGroupID, Targets.quantityOfWallPostToEachLoad, viewAdapter.itemCount - 1, "all")
                        //fix user links n lines
                        convertTextLinksFromVkStyleToWebStyleSortPhotoLinks(wms)
                        runOnUiThread {
                            viewAdapter.addWallMessages(wms)
                        }
                    } catch (e: java.lang.Exception){
                        runOnUiThread {
                            Toast.makeText(applicationContext, R.string.error_on_load, Toast.LENGTH_LONG).show()
                            viewAdapter.loadIsFailed()
                        }
                        e.printStackTrace()
                    }
                    }).start()
            })
        }
    }

    private fun convertTextLinksFromVkStyleToWebStyleSortPhotoLinks(wms: ArrayList<WallMessage>){
        wms.forEach {
            var tempResult: String? = null
            it.text?.let { it1 ->
                val p = Pattern.compile("\\[.*?\\d+\\|.*?\\]")
                val m = p.matcher(it1)
                val sb = StringBuffer()
                while (m.find()) {
                    m.appendReplacement(sb, m.group().replace("[", "<a href=\"https://vk.com/").replace("|", "\">").replace("]", "</a>"))
                }
                m.appendTail(sb)
                tempResult = sb.toString()
            }
            it.text = tempResult?.replace("\n", "<br />")
            it.attachments?.forEach {attachment ->
                attachment.link?.let {link ->
                    link.photo?.photo_sizes?.sortBy { photo_size -> photo_size.width }
                }
                attachment.photo?.let {photo ->
                    photo?.photo_sizes?.sortBy { photo_size -> photo_size.width }
                }
            }
        }
    }
}
