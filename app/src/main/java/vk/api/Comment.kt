package vk.api

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import vk.api.utils.Utils

class Comment() : Parcelable {
    var cid: Long? = null
    var from_id: Long? = null
    var date: Long? = null
    var text: String? = null
    var reply_to_user: Long? = null
    var reply_to_comment: Long? = null
    var attachments: ArrayList<Attachment>? = null

    constructor(parcel: Parcel) : this() {
        cid = parcel.readValue(Long::class.java.classLoader) as? Long
        from_id = parcel.readValue(Long::class.java.classLoader) as? Long
        date = parcel.readValue(Long::class.java.classLoader) as? Long
        text = parcel.readString()
        reply_to_user = parcel.readValue(Long::class.java.classLoader) as? Long
        reply_to_comment = parcel.readValue(Long::class.java.classLoader) as? Long
        attachments = arrayListOf<Attachment>().apply {
            parcel.readList(this, Attachment::class.java.classLoader)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(cid)
        parcel.writeValue(from_id)
        parcel.writeValue(date)
        parcel.writeString(text)
        parcel.writeValue(reply_to_user)
        parcel.writeValue(reply_to_comment)
        parcel.writeList(attachments)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }

        @Throws(JSONException::class)
        fun parse (o: JSONObject): Comment{
            val c = Comment()
            c.cid = o.getLong("id")
            c.from_id = o.getLong("from_id")
            c.date = o.getLong("date")
            c.text = Utils.unescape(o.getString("text"))
            c.reply_to_user = o.optLong("reply_to_user")
            c.reply_to_comment = o.optLong("reply_to_comment")
            c.attachments = Attachment.parseAttachments(o.optJSONArray("attachments"))
            return c
        }

        @Throws(JSONException::class)
        fun parseComments(o: JSONArray): ArrayList<Comment>{
            val comments = ArrayList<Comment>()
            for (i in 0 until o.length()) {
                val jcomment = o.get(i) as JSONObject
                val comment = Comment.parse(jcomment)
                comments.add(comment)
            }
            return comments
        }
    }
}