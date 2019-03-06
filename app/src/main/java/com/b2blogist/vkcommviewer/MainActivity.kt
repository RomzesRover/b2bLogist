package com.b2blogist.vkcommviewer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import vk.api.Api


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread(Runnable {
            var wms = Api.getWallMessages(-90405472L, 3, 0, "all")
            var group = Api.getGroups(arrayListOf(90405472L), null, "cover")!![0]

            Log.v("Name", group.name)

            Log.v("CoverImgSrc", group.covers?.get(4)?.src ?: "no image")

            for (one in wms) {
                Log.v("HERE", one.text)
            }
        }).start()
    }
}
