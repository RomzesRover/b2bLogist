package vk.api

import org.json.JSONException
import org.json.JSONObject

class Cover {
    var src: String? = null
    var width: Int = 0//0 means value is unknown
    var height: Int = 0//0 means value is unknown

    companion object {
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