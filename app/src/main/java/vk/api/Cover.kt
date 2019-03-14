package vk.api

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONException
import org.json.JSONObject

class Cover() : Parcelable {
    var src: String? = null
    var width: Int = 0//0 means value is unknown
    var height: Int = 0//0 means value is unknown

    constructor(parcel: Parcel) : this() {
        src = parcel.readString()
        width = parcel.readInt()
        height = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(src)
        parcel.writeInt(width)
        parcel.writeInt(height)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Cover> {
        override fun createFromParcel(parcel: Parcel): Cover {
            return Cover(parcel)
        }

        override fun newArray(size: Int): Array<Cover?> {
            return arrayOfNulls(size)
        }

        @Throws(NumberFormatException::class, JSONException::class)
        fun parse(o: JSONObject): Cover {
            val c = Cover()
            c.src = o.getString("url")
            c.width = o.getInt("width")
            c.height = o.getInt("height")
            return c;
        }
    }

}