package vk.api

import org.json.JSONException
import org.json.JSONObject



class Contact {
    var user_id: Long? = null
    lateinit var desc: String
    lateinit var email: String
    lateinit var phone: String

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