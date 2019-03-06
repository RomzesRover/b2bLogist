package vk.api

import org.json.JSONException
import org.json.JSONObject
import vk.api.connectivity.Connectivity
import vk.api.utils.Params
import vk.api.utils.Utils
import java.io.IOException


class Api() {
    //http://vk.com/dev/wall.get
    @Throws(IOException::class, JSONException::class)
    fun getWallMessages(owner_id: Long?, count: Int, offset: Int, filter: String): ArrayList<WallMessage> {
        val params = Params("wall.get")
        params.put("owner_id", owner_id)
        if (count > 0)
            params.put("count", count)
        params.put("offset", offset)
        params.put("filter", filter) //owner, others, all - default
        val root = Connectivity.sendRequest(params)
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
    fun getGroups(uids: Collection<Long>?, domain: String?, fields: String): ArrayList<Group>? {
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
        params.put("fields", fields)
        val root = Connectivity.sendRequest(params)
        val array = root.optJSONArray("response")
        return Group.parseGroups(array)
    }
}