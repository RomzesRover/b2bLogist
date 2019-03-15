package vk.api

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import vk.api.utils.Utils

class User() : Parcelable {
    var uid: Long? = null
    var first_name: String? = null
    var last_name: String? = null
    var sex: Int? = null
    var screen_name: String? = null
    var photo_50: String? = null
    var photo_100: String? = null
    var photo_200: String? = null
    var online: Int? = null

    constructor(parcel: Parcel) : this() {
        uid = parcel.readValue(Long::class.java.classLoader) as? Long
        first_name = parcel.readString()
        last_name = parcel.readString()
        sex = parcel.readValue(Int::class.java.classLoader) as? Int
        screen_name = parcel.readString()
        photo_50 = parcel.readString()
        photo_100 = parcel.readString()
        photo_200 = parcel.readString()
        online = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(uid)
        parcel.writeString(first_name)
        parcel.writeString(last_name)
        parcel.writeValue(sex)
        parcel.writeString(screen_name)
        parcel.writeString(photo_50)
        parcel.writeString(photo_100)
        parcel.writeString(photo_200)
        parcel.writeValue(online)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }

        @Throws(JSONException::class)
        fun parse(o: JSONObject): User{
            val u = User()
            u.uid = o.getLong("id")
            u.first_name = Utils.unescape(o.getString("first_name"))
            u.last_name = Utils.unescape(o.getString("last_name"))
            u.sex = o.getInt("sex")
            u.screen_name = Utils.unescape(o.getString("screen_name"))
            u.photo_50 = o.optString("photo_50")
            u.photo_100 = o.optString("photo_100")
            u.photo_200 = o.optString("photo_200")
            u.online = o.getInt("online")
            return u
        }

        @Throws(JSONException::class)
        fun parseUsers(o: JSONArray): ArrayList<User>{
            val users = ArrayList<User>()
            for (i in 0 until o.length()) {
                val juser = o.get(i) as JSONObject
                val user = User.parse(juser)
                users.add(user)
            }
            return users
        }
    }
}