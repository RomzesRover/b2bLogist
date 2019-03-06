package vk.api

import org.json.JSONException
import org.json.JSONObject
import vk.api.utils.Utils


class WallMessage{
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

    var copy_history: ArrayList<WallMessage>? = null

    var signer_id: Long? = null

    companion object {
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