package vk.api

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONException
import org.json.JSONObject

class Comments() : Parcelable {
    var comments: ArrayList<Comment>? = null
    var users: ArrayList<User>? = null
    var groups: ArrayList<Group>? = null

    constructor(parcel: Parcel) : this() {
        comments = arrayListOf<Comment>().apply {
            parcel.readList(this, Comment::class.java.classLoader)
        }
        users = arrayListOf<User>().apply {
            parcel.readList(this, User::class.java.classLoader)
        }
        groups = arrayListOf<Group>().apply {
            parcel.readList(this, Group::class.java.classLoader)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(comments)
        parcel.writeList(users)
        parcel.writeList(groups)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comments> {
        override fun createFromParcel(parcel: Parcel): Comments {
            return Comments(parcel)
        }

        override fun newArray(size: Int): Array<Comments?> {
            return arrayOfNulls(size)
        }

        @Throws(JSONException::class)
        fun parse(o: JSONObject): Comments{
            val cs = Comments()
            cs.comments = Comment.parseComments(o.optJSONArray("items"))
            if (o.has("profiles"))
                cs.users = User.parseUsers(o.optJSONArray("profiles"))
            if (o.has("groups"))
                cs.groups = Group.parseGroups(o.optJSONArray("groups"))
            return cs
        }
    }
}