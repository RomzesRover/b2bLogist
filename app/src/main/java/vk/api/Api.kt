package vk.api

import org.json.JSONException
import org.json.JSONObject
import vk.api.connectivity.Connectivity
import vk.api.utils.Params
import vk.api.utils.Utils
import java.io.IOException


object Api {
    private val API_VERSION = "5.92"
    private val API_VERSION_OLD = "5.52"

    //http://vk.com/dev/wall.get
    @Throws(IOException::class, JSONException::class)
    fun getWallMessages(owner_id: Long?, count: Int, offset: Int, filter: String): ArrayList<WallMessage> {
        val params = Params("wall.get")
        params.put("owner_id", owner_id)
        if (count > 0)
            params.put("count", count)
        params.put("offset", offset)
        params.put("filter", filter) //owner, others, all - default
        val root = Connectivity.sendRequest(params, API_VERSION)
        val response = root.optJSONObject("response")
        val array = response.optJSONArray("items")
        val wmessages = ArrayList<WallMessage>()
        val category_count = array.length()
        for (i in 0 until category_count) {
            val o = array.get(i) as JSONObject
            val wm = WallMessage.parse(o)
            wmessages.add(wm)
        }
        return wmessages
    }

    //http://vk.com/dev/groups.getById
    @Throws(IOException::class, JSONException::class)
    fun getGroups(uids: Collection<Long>?, domain: String?, fields: String?): ArrayList<Group>? {
        if (uids == null && domain == null)
            return null
        if (uids!!.size == 0 && domain == null)
            return null
        val params = Params("groups.getById")
        val str_uids: String?
        if (uids != null && uids.size > 0)
            str_uids = Utils.arrayToString(uids)
        else
            str_uids = domain
        params.put("group_ids", str_uids)
        params.put("fields", fields ?: "")
        val root = Connectivity.sendRequest(params, API_VERSION)
        val array = root.optJSONArray("response")
        //determine addresses
        val groupListToReturn = Group.parseGroups(array)
        groupListToReturn.forEach {
            it.addresses = getGroupAddressesById(it.gid)
        }
        return groupListToReturn
    }

    //http://vk.com/dev/groups.getById
    @Throws(IOException::class, JSONException::class)
    fun getGroupAddressesById(uid: Long?): ArrayList<GroupAddress>? {
        if (uid == null)
            return null
        val params = Params("groups.getAddresses")
        params.put("group_id", uid)
        val root = Connectivity.sendRequest(params, API_VERSION)
        val array = root.optJSONObject("response").optJSONArray("items")
        return GroupAddress.parseGroupAddresses(array)
    }

    //http://vk.com/dev/wall.getComments
    @Throws(IOException::class, JSONException::class)
    fun getCommentsOldApi(owner_id: Long, post_id: Long, count: Int, offset: Int, extended: Boolean): Comments {
        val params = Params("wall.getComments")
        params.put("owner_id", owner_id)
        params.put("post_id", post_id)
        if (count > 0)
            params.put("count", count)
        params.put("offset", offset)
        params.put("extended", if (extended) 1 else 0)
        val root = Connectivity.sendRequest(params, API_VERSION_OLD)
        val response = root.optJSONObject("response")
        return Comments.parse(response)
    }
}