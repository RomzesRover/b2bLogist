package vk.api

import org.json.JSONException
import org.json.JSONObject



class Contact {
    var user_id: Long? = null
    var desc: String? = null
    var email: String? = null
    var phone: String? = null

    companion object {
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