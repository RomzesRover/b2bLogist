package vk.api

import org.json.JSONException
import org.json.JSONObject
import vk.api.utils.Utils


class Photo {
    var pid: Long = 0
    var aid: Long = 0
    var owner_id: String? = null
    var src: String? = null//photo_130
    var src_small: String? = null//photo_75
    var src_big: String? = null//photo_604
    var src_xbig: String? = null//photo_807
    var src_xxbig: String? = null//photo_1280
    var src_xxxbig: String? = null//photo_2560
    var phototext: String? = null
    var created: Long = 0
    var like_count: Int? = null
    var user_likes: Boolean? = null
    var comments_count: Int? = null
    var tags_count: Int? = null
    var can_comment: Boolean? = null
    var width: Int = 0//0 means value is unknown
    var height: Int = 0//0 means value is unknown
    var access_key: String? = null
    var user_id: String? = null //for group

    companion object {
        @Throws(NumberFormatException::class, JSONException::class)
        fun parse(o: JSONObject): Photo {
            val p = Photo()
            p.pid = o.getLong("id")
            p.aid = o.optLong("album_id")
            p.owner_id = o.getString("owner_id")
            p.src = o.optString("photo_130")
            p.src_small = o.optString("photo_75")
            p.src_big = o.optString("photo_604")
            p.src_xbig = o.optString("photo_807")
            p.src_xxbig = o.optString("photo_1280")
            p.src_xxxbig = o.optString("photo_2560")
            p.phototext = Utils.unescape(o.optString("text"))
            p.created = o.optLong("date") //date instead created for api v 5.0 and higher
            p.user_id = o.optString("user_id")

            if (o.has("likes")) {
                val jlikes = o.getJSONObject("likes")
                p.like_count = jlikes.optInt("count")
                p.user_likes = jlikes.optInt("user_likes") == 1
            }
            if (o.has("comments")) {
                val jcomments = o.getJSONObject("comments")
                p.comments_count = jcomments.optInt("count")
            }
            if (o.has("tags")) {
                val jtags = o.getJSONObject("tags")
                p.tags_count = jtags.optInt("count")
            }
            if (o.has("can_comment"))
                p.can_comment = o.optInt("can_comment") == 1
            p.width = o.optInt("width")
            p.height = o.optInt("height")
            p.access_key = o.optString("access_key")
            return p
        }

        @Throws(NumberFormatException::class, JSONException::class)
        fun parseCounts(o: JSONObject): Photo {
            val p = Photo()
            val pid_array = o.optJSONArray("pid")
            if (pid_array != null && pid_array.length() > 0) {
                p.pid = pid_array.getLong(0)
            }
            val likes_array = o.optJSONArray("likes")
            if (likes_array != null && likes_array.length() > 0) {
                val jlikes = likes_array.getJSONObject(0)
                p.like_count = jlikes.optInt("count")
                p.user_likes = jlikes.optInt("user_likes") == 1
            }
            val comments_array = o.optJSONArray("comments")
            if (comments_array != null && comments_array.length() > 0) {
                val jcomments = comments_array.getJSONObject(0)
                p.comments_count = jcomments.optInt("count")
            }
            val tags_array = o.optJSONArray("tags")
            if (tags_array != null && tags_array.length() > 0) {
                val jtags = tags_array.getJSONObject(0)
                p.tags_count = jtags.optInt("count")
            }
            val can_comment_array = o.optJSONArray("can_comment")
            if (can_comment_array != null && can_comment_array.length() > 0) {
                p.can_comment = can_comment_array.getInt(0) == 1
            }
            val user_id_array = o.optJSONArray("user_id")
            if (user_id_array != null && user_id_array.length() > 0) {
                p.user_id = user_id_array.getString(0)
            }
            return p
        }
    }
}