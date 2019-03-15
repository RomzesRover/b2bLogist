package com.b2blogist.vkcommviewer

import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.b2blogist.vkcommviewer.adapters.PostCommentsAdapter
import kotlinx.android.synthetic.main.activity_post_comments.*
import kotlinx.android.synthetic.main.content_post_comments.*
import vk.api.*
import java.util.regex.Pattern

class PostCommentsActivity : AppCompatActivity() {
    private lateinit var viewAdapter: PostCommentsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val quantityOfCommentsToEachLoad = 15
    private var targetWidth: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_comments)

        swiperefresh.setOnRefreshListener {
            updateOperation()
        }

        //init images size targeted width
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        targetWidth = size.x / 2

        //start update on activity open
        swiperefresh.isRefreshing = true
        updateOperation()
    }

    private fun updateOperation() = Thread(Runnable {
        try {
            //initial load of page
            if (::viewAdapter.isInitialized) viewAdapter.loadInProgress()
            //get post n group
            var wms = intent.getParcelableExtra<WallMessage>("wallMessage")
            var group = intent.getParcelableExtra<Group>("group")
            //get comments
            var comments = Api.getCommentsOldApi(wms.from_id!!, wms.id!!, quantityOfCommentsToEachLoad, 0, true)

            //fix user links
            convertTextLinksFromVkStyleToWebStyle(comments.comments!!)

            runOnUiThread {
                //update adapter for recycler view
                initRecycleView(wms, group, comments)
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

    private fun initRecycleView(wms: WallMessage, group: Group, comments: Comments) {
        if (::viewAdapter.isInitialized){
            viewAdapter.setNewPostInfo(wms)
            viewAdapter.setNewWallComments(comments)
        } else {
            //init recycle view
            viewAdapter = PostCommentsAdapter(this, group, wms, comments, quantityOfCommentsToEachLoad, targetWidth)
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
            viewAdapter.setOnLoadMoreListener(recycler_view, object : PostCommentsAdapter.OnLoadMoreListener {
                override fun onLoadMore() = Thread(Runnable {
                    try {
                        var comments = Api.getCommentsOldApi(wms.from_id!!, wms.id!!, quantityOfCommentsToEachLoad, viewAdapter.itemCount-1, true)
                        //fix user links n lines
                        convertTextLinksFromVkStyleToWebStyle(comments.comments!!)
                        runOnUiThread {
                            viewAdapter.addComments(comments)
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

    private fun convertTextLinksFromVkStyleToWebStyle(comments: ArrayList<Comment>){
        comments.forEach {
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
        }
    }
}
