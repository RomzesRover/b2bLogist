package vk.api

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONException
import org.json.JSONObject
import vk.api.utils.Utils


class Video() : Parcelable {
    var vid: Long? = null
    var owner_id: Long? = null
    var title: String? = null
    var description: String? = null
    var duration: Long? = null
    var image: String? = null//130*97
    var image_big: String? = null//320*240
    var date: Long? = null
    var player: String? = null
    //files
    var external: String? = null
    var mp4_240: String? = null
    var mp4_360: String? = null
    var mp4_480: String? = null
    var mp4_720: String? = null
    var flv_320: String? = null
    var access_key: String? = null//used when private video attached to message
    var views: Int? = null

    constructor(parcel: Parcel) : this() {
        vid = parcel.readValue(Long::class.java.classLoader) as? Long
        owner_id = parcel.readValue(Long::class.java.classLoader) as? Long
        title = parcel.readString()
        description = parcel.readString()
        duration = parcel.readValue(Long::class.java.classLoader) as? Long
        image = parcel.readString()
        image_big = parcel.readString()
        date = parcel.readValue(Long::class.java.classLoader) as? Long
        player = parcel.readString()
        external = parcel.readString()
        mp4_240 = parcel.readString()
        mp4_360 = parcel.readString()
        mp4_480 = parcel.readString()
        mp4_720 = parcel.readString()
        flv_320 = parcel.readString()
        access_key = parcel.readString()
        views = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(vid)
        parcel.writeValue(owner_id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeValue(duration)
        parcel.writeString(image)
        parcel.writeString(image_big)
        parcel.writeValue(date)
        parcel.writeString(player)
        parcel.writeString(external)
        parcel.writeString(mp4_240)
        parcel.writeString(mp4_360)
        parcel.writeString(mp4_480)
        parcel.writeString(mp4_720)
        parcel.writeString(flv_320)
        parcel.writeString(access_key)
        parcel.writeValue(views)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Video> {
        override fun createFromParcel(parcel: Parcel): Video {
            return Video(parcel)
        }

        override fun newArray(size: Int): Array<Video?> {
            return arrayOfNulls(size)
        }

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
            v.views = o.getInt("views")
            v.date = o.optLong("date")
            v.player = o.optString("player")
            v.access_key = o.optString("access_key")
            return v
        }
    }
}