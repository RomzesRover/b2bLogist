package com.b2blogist.vkcommviewer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import vk.api.WallMessage

class PostCommentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_comments)


        var wallMessage = intent.getParcelableExtra<WallMessage>("wallMessage")

        Toast.makeText(this, wallMessage.text, Toast.LENGTH_LONG).show()

    }
}
