package vk.api

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONException
import org.json.JSONObject



class Contact() :Parcelable {
    var user_id: Long? = null
    var desc: String? = null
    var email: String? = null
    var phone: String? = null

    constructor(parcel: Parcel) : this() {
        user_id = parcel.readValue(Long::class.java.classLoader) as? Long
        desc = parcel.readString()
        email = parcel.readString()
        phone = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(user_id)
        parcel.writeString(desc)
        parcel.writeString(email)
        parcel.writeString(phone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }

        @Throws(JSONException::class)
        fun parse(o: JSONObject): Contact {
            val c = Contact()
            if (o.has("user_id"))
                c.user_id = o.optLong("user_id")
            c.desc = o.optString("desc")
            c.email = o.optString("email")
            c.phone = o.optString("phone")
            return c
        }
    }

}