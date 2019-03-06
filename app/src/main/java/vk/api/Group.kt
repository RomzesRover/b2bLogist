package vk.api

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import vk.api.utils.Cover
import vk.api.utils.Utils


class Group {
    var gid: Long? = null
    var name: String? = null
    var status: String? = null
    var site: String? = null
    var photo: String? = null//50*50
    var is_closed: Boolean? = null
    var is_member: Boolean? = null

    var photo_medium: String? = null//100*100
    var photo_big: String? = null//200*200
    var description: String? = null
    var wiki_page: String? = null
    var fixed_post: Long? = null
    var can_see_all_posts: Boolean? = null//can_see_all_posts=false означает что стена закрыта
    var is_admin: Boolean? = null
    var admin_level: Int? = null//1-moder, 2-editor, 3-admin
    var contacts: ArrayList<Contact>? = null
    var members_count: Int? = null
    var type: Int? = null //0 - group, 1 - page, 2 - event
    var links: ArrayList<Link>? = null
    var cover_Enabled = 0 //0 - disabled, 1 - enabled
    var covers: ArrayList<Cover>? = null

    companion object {
        @Throws(JSONException::class)
        fun parse(o: JSONObject): Group {
            val g = Group()
            g.gid = o.getLong("id")
            g.name = Utils.unescape(o.getString("name"))
            g.status = Utils.unescape(if (o.has("status")) o.getString("status") else null)
            g.site = Utils.unescape(if (o.has("site")) o.getString("site") else null)
            g.photo = o.optString("photo_50")
            g.photo_medium = o.optString("photo_100")
            g.photo_big = o.optString("photo_200")
            val is_closed = o.optString("is_closed")
            if (is_closed != null)
                g.is_closed = is_closed == "1"
            val is_member = o.optString("is_member")
            if (is_member != null)
                g.is_member = is_member == "1"
            g.description = Utils.unescape(o.optString("description", null))
            g.wiki_page = Utils.unescape(o.optString("wiki_page", null))

            //fixed post
            g.fixed_post = o.optLong("fixed_post", -1)//may be just false - boolean. If failed to parse long it means no post is fixed.
            if (g.fixed_post == -1L)
                g.fixed_post = null

            if (o.has("can_see_all_posts"))
                g.can_see_all_posts = o.optInt("can_see_all_posts", 1) == 1

            //if doesn't exist it means value is unknown
            if (o.has("is_admin"))
            //opt because there may be something unparseable
                g.is_admin = o.optInt("is_admin", 0) == 1

            //if doesn't exist it means value is unknown
            if (o.has("admin_level"))
            //opt because there may be something unparseable
                g.admin_level = o.optInt("admin_level", 1)

            val jcontacts = o.optJSONArray("contacts")
            if (jcontacts != null) {
                g.contacts = ArrayList<Contact>()
                for (i in 0 until jcontacts.length()) {
                    val jcontact = jcontacts.get(i) as JSONObject
                    val contact = Contact.parse(jcontact)
                    if (contact != null)
                        g.contacts!!.add(contact)
                }
            }

            var jcover = o.optJSONObject("cover")
            if (jcover != null){
                g.cover_Enabled = jcover.getInt("enabled")
                if (g.cover_Enabled == 1){
                    val jcovers = jcover.optJSONArray("images")
                    if (jcovers != null) {
                        g.covers = ArrayList<Cover>()
                        for (i in 0 until jcovers.length()) {
                            val jcover = jcovers.get(i) as JSONObject
                            val cover = Cover.parse(jcover)
                            if (cover != null)
                                g.covers!!.add(cover)
                        }
                    }
                }
            }

            //if doesn't exist it means value is unknown
            if (o.has("members_count"))
            //opt because there may be something unparseable
                g.members_count = o.optInt("members_count", 0)
            if (o.has("type")) {
                val str_type = o.optString("type")
                if ("group" == str_type)
                    g.type = 0
                else if ("page" == str_type)
                    g.type = 1
                else if ("event" == str_type)
                    g.type = 2
            }

            val jlinks = o.optJSONArray("links")
            if (jlinks != null) {
                g.links = ArrayList()
                for (i in 0 until jlinks.length()) {
                    val jlink = jlinks.get(i) as JSONObject
                    val link = Link.parseFromGroup(jlink)
                    if (link != null)
                        g.links!!.add(link)
                }
            }
            return g
        }

        @Throws(JSONException::class)
        fun parseGroups(jgroups: JSONArray): ArrayList<Group> {
            val groups = ArrayList<Group>()
            for (i in 0 until jgroups.length()) {
                //для метода groups.get первый элемент - количество
                if (jgroups.get(i) !is JSONObject)
                    continue
                val jgroup = jgroups.get(i) as JSONObject
                val group = Group.parse(jgroup)
                groups.add(group)
            }
            return groups
        }
    }
}