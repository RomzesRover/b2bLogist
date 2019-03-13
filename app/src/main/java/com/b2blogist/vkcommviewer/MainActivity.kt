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
            var group = Api.getGroups(arrayListOf(90405472L), null, "cover,contacts,status,members_count,description,site,city")!![0]

            Log.v("Name", group.name)

            Log.v("CoverImgSrc", group.covers?.get(4)?.src ?: "no image")
            Log.v("Contact1", group.contacts?.get(0)?.phone ?: "no contact phone")
            Log.v("status", group.status ?: "there is no status")
            Log.v("ava", group.photo_big ?: "there is no photo image")
            Log.v("members_count", if (group.members_count == null) "there is no members data" else group.members_count.toString())
            Log.v("description", group.description ?: "there is no description")
            Log.v("site", group.site ?: "there is no site")
            Log.v("address", group.addresses?.get(0)?.address ?: "there is no address")
            Log.v("city", group.city_name ?: "no city name")

            for (one in wms) {
                Log.v("HERE", one.text)
            }
        }).start()
    }
}
