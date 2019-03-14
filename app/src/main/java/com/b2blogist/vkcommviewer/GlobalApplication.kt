package com.b2blogist.vkcommviewer

import android.app.Application
import com.squareup.picasso.Picasso

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val builder = Picasso.Builder(this)
        val built = builder.build()
        Picasso.setSingletonInstance(built)
    }
}