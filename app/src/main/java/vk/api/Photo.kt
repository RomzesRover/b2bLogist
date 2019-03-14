package vk.api

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import vk.api.utils.Utils


class Photo() : Parcelable {
    var pid: Long? = null
    var aid: Long? = null
    var owner_id: String? = null
    var photo_sizes: ArrayList<PhotoSize>? = null
    var src: String? = null//photo_130
    var src_small: String? = null//photo_75
    var src_big: String? = null//photo_604
    var src_xbig: String? = null//photo_807
    var src_xxbig: String? = null//photo_1280
    var src_xxxbig: String? = null//photo_2560
    var phototext: String? = null
    var created: Long? = null
    var like_count: Int? = null
    var user_likes: Boolean? = null
    var comments_count: Int? = null
    var tags_count: Int? = null
    var can_comment: Boolean? = null
    var width: Int = 0//0 means value is unknown
    var height: Int = 0//0 means value is unknown
    var access_key: String? = null
    var user_id: String? = null //for group

    constructor(parcel: Parcel) : this() {
        pid = parcel.readValue(Long::class.java.classLoader) as? Long
        aid = parcel.readValue(Long::class.java.classLoader) as? Long
        owner_id = parcel.readString()
        photo_sizes = arrayListOf<PhotoSize>().apply {
            parcel.readList(this, PhotoSize::class.java.classLoader)
        }
        src = parcel.readString()
        src_small = parcel.readString()
        src_big = parcel.readString()
        src_xbig = parcel.readString()
        src_xxbig = parcel.readString()
        src_xxxbig = parcel.readString()
        phototext = parcel.readString()
        created = parcel.readValue(Long::class.java.classLoader) as? Long
        like_count = parcel.readValue(Int::class.java.classLoader) as? Int
        user_likes = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        comments_count = parcel.readValue(Int::class.java.classLoader) as? Int
        tags_count = parcel.readValue(Int::class.java.classLoader) as? Int
        can_comment = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        width = parcel.readInt()
        height = parcel.readInt()
        access_key = parcel.readString()
        user_id = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(pid)
        parcel.writeValue(aid)
        parcel.writeString(owner_id)
        parcel.writeList(photo_sizes)
        parcel.writeString(src)
        parcel.writeString(src_small)
        parcel.writeString(src_big)
        parcel.writeString(src_xbig)
        parcel.writeString(src_xxbig)
        parcel.writeString(src_xxxbig)
        parcel.writeString(phototext)
        parcel.writeValue(created)
        parcel.writeValue(like_count)
        parcel.writeValue(user_likes)
        parcel.writeValue(comments_count)
        parcel.writeValue(tags_count)
        parcel.writeValue(can_comment)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeString(access_key)
        parcel.writeString(user_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Photo> {
        override fun createFromParcel(parcel: Parcel): Photo {
            return Photo(parcel)
        }

        override fun newArray(size: Int): Array<Photo?> {
            return arrayOfNulls(size)
        }

        @Throws(NumberFormatException::class, JSONException::class)
        fun parse(o: JSONObject): Photo {
            val p = Photo()
            p.pid = o.getLong("id")
            p.aid = o.optLong("album_id")
            p.owner_id = o.getString("owner_id")
            if (o.has("sizes"))
                p.photo_sizes = PhotoSize.parseArrayOfPhotoSizes(o.optJSONArray("sizes"))
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
    }

    class PhotoSize() : Parcelable{
        var type: String? = null
        var src: String? = null
        var width: Int = 0//0 means value is unknown
        var height: Int = 0//0 means value is unknown

        constructor(parcel: Parcel) : this() {
            type = parcel.readString()
            src = parcel.readString()
            width = parcel.readInt()
            height = parcel.readInt()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(type)
            parcel.writeString(src)
            parcel.writeInt(width)
            parcel.writeInt(height)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<PhotoSize> {
            override fun createFromParcel(parcel: Parcel): PhotoSize {
                return PhotoSize(parcel)
            }

            override fun newArray(size: Int): Array<PhotoSize?> {
                return arrayOfNulls(size)
            }

            @Throws(NumberFormatException::class, JSONException::class)
            fun parse (o: JSONObject): PhotoSize{
                val ps = PhotoSize()
                ps.type = o.optString("type")
                ps.src = o.optString("url")
                ps.width = o.optInt("width")
                ps.height = o.optInt("height")
                return ps
            }

            @Throws(NumberFormatException::class, JSONException::class)
            fun parseArrayOfPhotoSizes(o: JSONArray): ArrayList<PhotoSize>{
                val photoSizes = ArrayList<PhotoSize>()
                for (i in 0 until o.length()){
                    val jphotosize = o.get(i) as JSONObject
                    val photoSize = parse(jphotosize)
                    photoSizes.add(photoSize)
                }
                return photoSizes
            }
        }
    }
}