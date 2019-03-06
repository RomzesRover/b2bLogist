package vk.api.connectivity

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import vk.api.utils.Params
import vk.api.utils.Utils
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream

object Connectivity {
    private val BASE_URL = "https://api.vk.com/method/"
    private val API_VERSION = "5.92"
    private val TAG = "VKAPI"
    private var enable_compression = true
    private val accessToken = "476d1af3476d1af3476d1af3c5470402c64476d476d1af31b13955079b57e24d50dcd87"

    @Throws(IOException::class, JSONException::class)
    fun sendRequest(params: Params): JSONObject {
        return sendRequest(params, false)
    }

    private val MAX_TRIES = 3
    @Throws(IOException::class, JSONException::class)
    private fun sendRequest(params: Params, is_post: Boolean): JSONObject {
        val url = getSignedUrl(params, is_post)
        var body = ""
        if (is_post)
            body = params.getParamsString()
        Log.i(TAG, "url=$url")
        if (body.length != 0)
            Log.i(TAG, "body=$body")
        var response = ""
        for (i in 1..MAX_TRIES) {
            try {
                if (i != 1)
                    Log.i(TAG, "try $i")
                response = sendRequestInternal(url, body, is_post)
                break
            } catch (ex: javax.net.ssl.SSLException) {
                processNetworkException(i, ex)
            } catch (ex: java.net.SocketException) {
                processNetworkException(i, ex)
            }

        }
        Log.i(TAG, "response=$response")
        return JSONObject(response)
    }

    @Throws(IOException::class)
    private fun processNetworkException(i: Int, ex: IOException) {
        ex.printStackTrace()
        if (i == MAX_TRIES)
            throw ex
    }

    @Throws(IOException::class)
    private fun sendRequestInternal(url: String, body: String, is_post: Boolean): String {
        var connection: HttpURLConnection? = null
        try {
            connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 30000
            connection.readTimeout = 30000
            connection.useCaches = false
            connection.doOutput = is_post
            connection.doInput = true
            connection.requestMethod = if (is_post) "POST" else "GET"
            if (enable_compression)
                connection.setRequestProperty("Accept-Encoding", "gzip")
            if (is_post)
                connection.outputStream.write(body.toByteArray(charset("UTF-8")))
            val code = connection.responseCode
            Log.i(TAG, "code=$code")
            //It may happen due to keep-alive problem http://stackoverflow.com/questions/1440957/httpurlconnection-getresponsecode-returns-1-on-second-invocation
            if (code == -1)
                throw RuntimeException("Network error")
            //может стоит проверить на код 200
            //on error can also read error stream from connection.
            var `is`: InputStream = BufferedInputStream(connection.inputStream, 8192)
            val enc = connection.getHeaderField("Content-Encoding")
            if (enc != null && enc.equals("gzip", ignoreCase = true))
                `is` = GZIPInputStream(`is`)
            return Utils.convertStreamToString(`is`)
        } finally {
            connection?.disconnect()
        }
    }

    private fun getSignedUrl(params: Params, is_post: Boolean): String {
        params.put("access_token", accessToken)
        if (!params.contains("v"))
            params.put("v", API_VERSION)

        var args = ""
        if (!is_post)
            args = params.getParamsString()

        return BASE_URL + params.methodName + "?" + args
    }
}