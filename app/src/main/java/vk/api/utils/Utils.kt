package vk.api.utils

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter

object Utils {
    fun unescape(text: String): String {
        return text
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("<br>", "\n")
            .replace("&gt;", ">")
            .replace("&lt;", "<")
            .replace("<br/>", "\n")
            .replace("&ndash;", "-")
            .trim { it <= ' ' }
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