package com.b2blogist.vkcommviewer.adapters.holders.utils

import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.URLSpan
import android.text.util.Linkify
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun convertLongToTime(time: Long): String {
        val date = Date(time * 1000L)
        val format = SimpleDateFormat("dd MMM yyyy 'at' HH:mm")
        return format.format(date)
    }

    fun linkifyHtml(html: String, linkifyMask: Int): Spannable {
        val text = Html.fromHtml(html)
        val currentSpans = text.getSpans(0, text.length, URLSpan::class.java)

        val buffer = SpannableString(text)
        Linkify.addLinks(buffer, linkifyMask)

        for (span in currentSpans) {
            val end = text.getSpanEnd(span)
            val start = text.getSpanStart(span)
            buffer.setSpan(span, start, end, 0)
        }
        return buffer
    }
}