package vk.api

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONException
import org.json.JSONObject
import vk.api.utils.Utils


class Link() : Parcelable {
    var url: String? = null
    var title: String? = null
    var description: String? = null
    var image_src: String? = null
    var photo: Photo? = null

    constructor(parcel: Parcel) : this() {
        url = parcel.readString()
        title = parcel.readString()
        description = parcel.readString()
        image_src = parcel.readString()
        photo = parcel.readParcelable(Photo::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(image_src)
        parcel.writeParcelable(photo, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Link> {
        override fun createFromParcel(parcel: Parcel): Link {
            return Link(parcel)
        }

        override fun newArray(size: Int): Array<Link?> {
            return arrayOfNulls(size)
        }

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