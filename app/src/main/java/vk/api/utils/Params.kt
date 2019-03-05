package vk.api.utils

import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*


class Params(val methodName: String) {
    private val args = TreeMap<String, String>()

    operator fun contains(name: String): Boolean {
        return args.containsKey(name)
    }

    fun put(param_name: String, param_value: String?) {
        if (param_value == null || param_value.length == 0)
            return
        args[param_name] = param_value
    }

    fun put(param_name: String, param_value: Long?) {
        if (param_value == null)
            return
        args[param_name] = java.lang.Long.toString(param_value)
    }

    fun put(param_name: String, param_value: Int?) {
        if (param_value == null)
            return
        args[param_name] = Integer.toString(param_value)
    }

    fun putDouble(param_name: String, param_value: Double) {
        args[param_name] = java.lang.Double.toString(param_value)
    }

    fun getParamsString(): String {
        var params = ""
        try {
            for (entry in args) {
                if (params.length != 0)
                    params += "&"
                params += entry.key + "=" + URLEncoder.encode(entry.value, "utf-8")
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return params
    }
}