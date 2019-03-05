package vk.api

import org.json.JSONException
import org.json.JSONObject
import vk.api.utils.Utils


class Link {
    lateinit var url: String
    lateinit var title: String
    lateinit var description: String
    lateinit var image_src: String

    companion object {
        @Throws(NumberFormatException::class, JSONException::class)
        fun parse(o: JSONObject): Link {
            val link = Link()
            link.url = o.optString("url")
            link.title = Utils.unescape(o.optString("title"))
            link.description = Utils.unescape(o.optString("description"))
            link.image_src = o.optString("image_src")
            return link
        }
    }
}