package vk.api

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import vk.api.utils.Utils

class GroupAddress() : Parcelable {
    var address: String?= null

    constructor(parcel: Parcel) : this() {
        address = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GroupAddress> {
        override fun createFromParcel(parcel: Parcel): GroupAddress {
            return GroupAddress(parcel)
        }

        override fun newArray(size: Int): Array<GroupAddress?> {
            return arrayOfNulls(size)
        }

        @Throws(JSONException::class)
        fun parse(o: JSONObject): GroupAddress{
            val ga = GroupAddress()
            ga.address = Utils.unescape(if (o.has("address")) o.getString("address") else null)
            return ga
        }

        @Throws(JSONException::class)
        fun parseGroupAddresses(jgroupaddresses: JSONArray): ArrayList<GroupAddress>{
            val groupAddresses = ArrayList<GroupAddress>()

            for (i in 0 until jgroupaddresses.length()) {
                val jgroupaddress = jgroupaddresses.get(i) as JSONObject
                val groupAddress = GroupAddress.parse(jgroupaddress)
                groupAddresses.add(groupAddress)
            }

            return  groupAddresses
        }
    }
}