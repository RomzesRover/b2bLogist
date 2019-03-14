package vk.api

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONException
import org.json.JSONObject
import vk.api.utils.Utils


class WallMessage() : Parcelable{
    var from_id: Long? = null
    var to_id: Long? = null
    var date: Long? = null
    var post_type: Int = -1 //where -1 - undefined 0 - post, 1 - copy, 2 - postpone, 3 - suggests
    var text: String? = null
    var isPinned: Boolean = false
    var id: Long? = null
    var attachments: ArrayList<Attachment>? = null
    var comment_count: Long? = null
    var comment_can_post: Boolean = false

    //likes
    var like_count: Int? = null
    var user_like: Boolean = false
    var can_like: Boolean = false
    var like_can_publish: Boolean = false

    //reposts
    var reposts_count: Int? = null
    var user_reposted: Boolean = false

    //views
    var views_count: Int? = null

    var copy_history: ArrayList<WallMessage>? = null

    var signer_id: Long? = null

    constructor(parcel: Parcel) : this() {
        from_id = parcel.readValue(Long::class.java.classLoader) as? Long
        to_id = parcel.readValue(Long::class.java.classLoader) as? Long
        date = parcel.readValue(Long::class.java.classLoader) as? Long
        post_type = parcel.readInt()
        text = parcel.readString()
        isPinned = parcel.readByte() != 0.toByte()
        id = parcel.readValue(Long::class.java.classLoader) as? Long
        attachments = arrayListOf<Attachment>().apply {
            parcel.readList(this, Attachment::class.java.classLoader)
        }
        comment_count = parcel.readValue(Long::class.java.classLoader) as? Long
        comment_can_post = parcel.readByte() != 0.toByte()
        like_count = parcel.readValue(Int::class.java.classLoader) as? Int
        user_like = parcel.readByte() != 0.toByte()
        can_like = parcel.readByte() != 0.toByte()
        like_can_publish = parcel.readByte() != 0.toByte()
        reposts_count = parcel.readValue(Int::class.java.classLoader) as? Int
        user_reposted = parcel.readByte() != 0.toByte()
        views_count = parcel.readValue(Int::class.java.classLoader) as? Int
        copy_history = arrayListOf<WallMessage>().apply {
            parcel.readList(this, WallMessage::class.java.classLoader)
        }
        signer_id = parcel.readValue(Long::class.java.classLoader) as? Long
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(from_id)
        parcel.writeValue(to_id)
        parcel.writeValue(date)
        parcel.writeInt(post_type)
        parcel.writeString(text)
        parcel.writeByte(if (isPinned) 1 else 0)
        parcel.writeValue(id)
        parcel.writeList(attachments)
        parcel.writeValue(comment_count)
        parcel.writeByte(if (comment_can_post) 1 else 0)
        parcel.writeValue(like_count)
        parcel.writeByte(if (user_like) 1 else 0)
        parcel.writeByte(if (can_like) 1 else 0)
        parcel.writeByte(if (like_can_publish) 1 else 0)
        parcel.writeValue(reposts_count)
        parcel.writeByte(if (user_reposted) 1 else 0)
        parcel.writeValue(views_count)
        parcel.writeList(copy_history)
        parcel.writeValue(signer_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WallMessage> {
        override fun createFromParcel(parcel: Parcel): WallMessage {
            return WallMessage(parcel)
        }

        override fun newArray(size: Int): Array<WallMessage?> {
            return arrayOfNulls(size)
        }

        @Throws(JSONException::class)
        fun parse(o: JSONObject): WallMessage {
            val wm = WallMessage()
            wm.id = o.getLong("id")
            wm.from_id = o.getLong("from_id")
            if (o.has("to_id"))
                wm.to_id = o.getLong("to_id")
            else
            //in copy_history owner_id is used
                wm.to_id = o.getLong("owner_id")
            wm.date = o.optLong("date")
            wm.post_type = getPostType(o)
            wm.text = Utils.unescape(o.optString("text"))
            wm.isPinned = o.has("is_pinned") && o.optLong("is_pinned") == 1L
            if (o.has("likes")) {
                val jlikes = o.getJSONObject("likes")
                wm.like_count = jlikes.optInt("count")
                wm.user_like = jlikes.optInt("user_likes") == 1
                wm.can_like = jlikes.optInt("can_like") == 1
                wm.like_can_publish = jlikes.optInt("can_publish") == 1
            }
            if (o.has("views")) {
                val jviews = o.getJSONObject("views")
                wm.views_count = jviews.optInt("count")
            }
            val copy_history_json = o.optJSONArray("copy_history")
            if (copy_history_json != null) {
                wm.copy_history = ArrayList()
                for (i in 0 until copy_history_json.length()) {
                    val history_item = copy_history_json.getJSONObject(i)

                    //empty items happen sometimes, seems to be bug in API
                    if (history_item.isNull("id"))
                        continue

                    wm.copy_history!!.add(parse(history_item))
                }
            }
            val attachments = o.optJSONArray("attachments")
            val geo_json = o.optJSONObject("geo")
            //владельцем опроса является to_id. Даже если добавить опрос в группу от своего имени, то from_id буду я, но опрос всё-равно будет принадлежать группе.
            wm.attachments = Attachment.parseAttachments(attachments, wm.to_id!!, geo_json)
            if (o.has("comments")) {
                val jcomments = o.getJSONObject("comments")
                wm.comment_count = jcomments.optInt("count").toLong()
                wm.comment_can_post = jcomments.optInt("can_post") == 1
            }
            wm.signer_id = o.optLong("signer_id")
            if (o.has("reposts")) {
                val jlikes = o.getJSONObject("reposts")
                wm.reposts_count = jlikes.optInt("count")
                wm.user_reposted = jlikes.optInt("user_reposted") == 1
            }
            return wm
        }

        fun getPostType(o: JSONObject): Int {
            if (o.has("post_type")) {
                return when (o.optString("post_type")){
                    "post" -> 0
                    "copy" -> 1
                    "postpone" -> 2
                    "suggest" -> 3
                    else -> -1
                }
            }
            return -1
        }
    }
}