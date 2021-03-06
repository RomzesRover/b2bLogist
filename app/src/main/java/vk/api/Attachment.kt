package vk.api

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class Attachment() : Parcelable {
    var type: String? = null //photo,posted_photo,video,audio,link,note,app,poll,doc,geo,message,page,album
    var photo: Photo? = null //public Photo posted_photo;
    var video: Video? = null
    var link: Link? = null
    var wallMessage: WallMessage? = null

    constructor(parcel: Parcel) : this() {
        type = parcel.readString()
        photo = parcel.readParcelable(Photo::class.java.classLoader)
        video = parcel.readParcelable(Video::class.java.classLoader)
        link = parcel.readParcelable(Link::class.java.classLoader)
        wallMessage = parcel.readParcelable(WallMessage::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeParcelable(photo, flags)
        parcel.writeParcelable(video, flags)
        parcel.writeParcelable(link, flags)
        parcel.writeParcelable(wallMessage, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Attachment> {
        override fun createFromParcel(parcel: Parcel): Attachment {
            return Attachment(parcel)
        }

        override fun newArray(size: Int): Array<Attachment?> {
            return arrayOfNulls(size)
        }

        @Throws(JSONException::class)
        fun parseAttachments(attachments: JSONArray?): ArrayList<Attachment> {
            val attachments_arr = ArrayList<Attachment>()
            if (attachments != null) {
                val size = attachments.length()
                for (j in 0 until size) {
                    val att = attachments.get(j)
                    if (!(att is JSONObject))
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
}