package vk.api

import org.json.JSONException
import org.json.JSONObject
import vk.api.utils.Utils


class Video {
    var vid: Long = 0
    var owner_id: Long = 0
    lateinit var title: String
    lateinit var description: String
    var duration: Long = 0
    var link1: String? = null
    lateinit var image: String//130*97
    lateinit var image_big: String//320*240
    //public String photo_640;
    var date: Long = 0
    lateinit var player: String
    //files
    lateinit var external: String
    lateinit var mp4_240: String
    lateinit var mp4_360: String
    lateinit var mp4_480: String
    lateinit var mp4_720: String
    lateinit var flv_320: String
    lateinit var access_key: String//used when private video attached to message
    var views: Int = 0


    @Throws(NumberFormatException::class, JSONException::class)
    fun parse(o: JSONObject): Video {
        val v = Video()
        v.vid = o.getLong("id")
        v.owner_id = o.getLong("owner_id")
        v.title = Utils.unescape(o.optString("title"))
        v.duration = o.optLong("duration")
        v.description = Utils.unescape(o.optString("description"))
        v.image = o.optString("photo_130")
        //notifications.get возвращает видео по-старому в типе like_video - баг в API
        if (!o.has("photo_130") && o.has("image"))
            v.image = o.optString("image")
        v.image_big = o.optString("photo_320")
        //notifications.get возвращает видео по-старому в типе like_video - баг в API
        if (!o.has("photo_320") && o.has("image_medium"))
            v.image_big = o.optString("image_medium")
        v.date = o.optLong("date")
        v.player = o.optString("player")
        if (o.has("views"))
            v.views = o.getInt("views")

        val files = o.optJSONObject("files")
        if (files != null) {
            v.external = files.optString("external")
            v.mp4_240 = files.optString("mp4_240")
            v.mp4_360 = files.optString("mp4_360")
            v.mp4_480 = files.optString("mp4_480")
            v.mp4_720 = files.optString("mp4_720")
            v.flv_320 = files.optString("flv_320")
        }
        return v
    }

    @Throws(NumberFormatException::class, JSONException::class)
    fun parseForAttachments(o: JSONObject): Video {
        val v = Video()
        v.vid = o.getLong("id")
        v.owner_id = o.getLong("owner_id")
        v.title = Utils.unescape(o.getString("title"))
        v.duration = o.getLong("duration")
        v.description = Utils.unescape(o.optString("description"))
        v.image = o.optString("photo_130")
        v.image_big = o.optString("photo_320")
        v.date = o.optLong("date")
        v.player = o.optString("player")
        v.access_key = o.optString("access_key")
        return v
    }

    fun getVideoUrl(): String {
        return getVideoUrl(owner_id, vid)
    }

    fun getVideoUrl(owner_id: Long, video_id: Long): String {
        var res: String? = null
        val base_url = "http://vk.com/"
        res = base_url + "video" + owner_id + "_" + video_id
        //sample http://vkontakte.ru/video4491835_158963813
        //http://79.gt2.vkadre.ru/assets/videos/f6b1af1e4258-24411750.vk.flv
        return res
    }
}