package vk.api

import org.json.JSONException
import org.json.JSONObject
import vk.api.utils.Utils


class Link {
    var url: String? = null
    var title: String? = null
    var description: String? = null
    var image_src: String? = null
    var photo: Photo? = null

    companion object {
        @Throws(NumberFormatException::class, JSONException::class)
        fun parse(o: JSONObject): Link {
            val link = Link()
            link.url = o.optString("url")
            link.title = Utils.unescape(o.optString("title"))
            link.description = Utils.unescape(o.optString("description"))
            if (o.has("photo"))
                link.photo = Photo.parse(o.optJSONObject("photo"))
            return link
        }

        @Throws(NumberFormatException::class, JSONException::class)
        fun parseFromGroup(o: JSONObject): Link {
            val link = Link()
            link.url = o.optString("url")
            link.title = Utils.unescape(o.optString("name"))
            link.description = Utils.unescape(o.optString("desc"))
            link.image_src = o.optString("photo_100")
            return link
        }
    }
}