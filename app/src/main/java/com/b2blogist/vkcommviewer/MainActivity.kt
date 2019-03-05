package com.b2blogist.vkcommviewer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import vk.api.Api
import vk.api.WallMessage
import vk.api.utils.Utils


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val api = Api("476d1af3476d1af3476d1af3c5470402c64476d476d1af31b13955079b57e24d50dcd87", "6887477")

        Thread(Runnable {
            var wms = api.getWallMessages(-90405472L, 3, 0, "all")

            for (one in wms) {
                Log.v("HERE", one.text)
            }
        }).start()
    }
}
