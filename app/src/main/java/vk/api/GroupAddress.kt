package vk.api

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import vk.api.utils.Utils

class GroupAddress {
    var address: String?= null

    companion object {
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