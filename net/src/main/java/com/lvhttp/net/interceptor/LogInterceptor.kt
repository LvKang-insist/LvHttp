package com.lvhttp.net.interceptor


import android.util.Log
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.EOFException
import java.lang.Exception
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * @name LogInterceptor
 * @package com.tidycar.carzki.remote.interceptor
 * @author 345 QQ:1831712732
 * @time 2021/01/26 11:42
 * @description 日志拦截器
 */
class LogInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val requestBody = request.body
        val requestBuffer = StringBuffer()


        val contentType = requestBody?.contentType()
        val charset: Charset =
            contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
        //请求日志
        requestBuffer.apply {
            append("{url:${request.url}} \n")
            append("{method:${request.method}} \n")
            append("{token:${request.headers["ACCESS-TOKEN"]}} \n")
            if (requestBody != null && !bodyHasUnknownEncoding(request.headers) && !requestBody.isDuplex() && !requestBody.isOneShot()) {
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                if (buffer.isProbablyUtf8()) {
                    append("{arguments:{${buffer.readString(charset)}}}\n")
                }
            }
        }

        val response = chain.proceed(request)
        try {
            val responseBody = response.body!!

            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val bufferResponse = source.buffer

            requestBuffer.apply {
                append("\n{Code:${response.code}}\n")
                append("{URL：${response.request.url}}\n")
                if (!bufferResponse.isProbablyUtf8()) {
                    append("<-- END HTTP (binary - byte body omitted)")
                } else {
                    append("body：${bufferResponse.clone().readString(charset)}")
                }
            }
            Log.d("LvHttp ---- END HTTP>", requestBuffer.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }

    private fun Buffer.isProbablyUtf8(): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = size.coerceAtMost(64)
            copyTo(prefix, 0, byteCount)
            for (i in 0 until 16) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (_: EOFException) {
            return false // Truncated UTF-8 sequence.
        }
    }
}