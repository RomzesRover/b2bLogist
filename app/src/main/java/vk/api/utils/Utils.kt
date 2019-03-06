package vk.api.utils

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter

object Utils {
    fun unescape(text: String?): String? {
        return when (text){
            null -> null
            else -> text
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("<br>", "\n")
                .replace("&gt;", ">")
                .replace("&lt;", "<")
                .replace("<br/>", "\n")
                .replace("&ndash;", "-")
                .trim { it <= ' ' }
        }
    }


    fun <T> arrayToString(items: Collection<T>?): String? {
        if (items == null)
            return null
        var str_cids = ""
        for (item in items) {
            if (str_cids.isNotEmpty())
                str_cids += ','.toString()
            str_cids += item
        }
        return str_cids
    }

    @Throws(IOException::class)
    fun convertStreamToString(`is`: InputStream): String {
        val r = InputStreamReader(`is`)
        val sw = StringWriter()
        val buffer = CharArray(1024)
        try {
            var n: Int = r.read(buffer)
            while (true){
                sw.write(buffer, 0, n)
                n = r.read(buffer)
                if (n == -1)
                    break
            }
        } finally {
            try {
                `is`.close()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

        }
        return sw.toString()
    }
}