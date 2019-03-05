package vk.api

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class Attachment {
    lateinit var type: String //photo,posted_photo,video,audio,link,note,app,poll,doc,geo,message,page,album
    lateinit var photo: Photo //public Photo posted_photo;
    lateinit var video: Video
    lateinit var link: Link
    lateinit var wallMessage: WallMessage

    @Throws(JSONException::class)
    fun parseAttachments(attachments: JSONArray?, from_id: Long, copy_owner_id: Long, geo_json: JSONObject?): ArrayList<Attachment> {
        val attachments_arr = ArrayList<Attachment>()
        if (attachments != null) {
            val size = attachments.length()
            for (j in 0 until size) {
                val att = attachments.get(j)
                if (att is JSONObject == false)
                    continue
                val attachment = Attachment()
                attachment.type = att.getString("type")

                when (attachment.type){
                    "photo", "posted_photo" ->{
                        val x = att.optJSONObject("photo")
                        if (x != null)
                            attachment.photo = Photo.parse(x)
                    }
                    "link" -> attachment.link = Link.parse(att.getJSONObject("link"))
                    "video" -> attachment.video = Video.parseForAttachments(att.getJSONObject("video"))
                    "wall" -> attachment.wallMessage = WallMessage.parse(att.getJSONObject("wall"))
                }
                attachments_arr.add(attachment)
            }
        }
        return attachments_arr
    }
}