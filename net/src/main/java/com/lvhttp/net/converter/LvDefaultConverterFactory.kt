package com.lvhttp.net.converter

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.lvhttp.net.LvHttp
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSink
import okio.Okio
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.Body
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.lang.reflect.Type
import java.nio.charset.Charset


/**
 * @name LvConverterFactory
 * @package com.www.net.converter
 * @author 345 QQ:1831712732
 * @time 2020/6/22 20:21
 * @description ConverterFactory
 */
@Suppress("UNCHECKED_CAST")
class LvDefaultConverterFactory(private val gson: Gson) : Converter.Factory() {


    companion object {
        fun create(gson: Gson): LvDefaultConverterFactory {
            return LvDefaultConverterFactory(gson)
        }
    }

    override fun requestBodyConverter(
        type: Type?,
        parameterAnnotations: Array<Annotation?>?,
        methodAnnotations: Array<Annotation?>?,
        retrofit: Retrofit?
    ): Converter<*, RequestBody> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return GsonRequestBodyConverter(gson, adapter)
    }

    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        return LvResponseBodyConverter<Any>(gson, type)
    }


    class LvResponseBodyConverter<T>(private val gson: Gson, private val type: Type) :
        Converter<ResponseBody, T> {
        @RequiresApi(Build.VERSION_CODES.P)

        override fun convert(value: ResponseBody): T? {
            val string = value.string()
            if (LvHttp.getIsLogging()) {
                Log.e("LvHttp：type = $type", "\n  result = $string ")
            }
            if (type == String::class.java || type::class.java.isPrimitive) {
                return string as T
            }
            return gson.fromJson(string, type)
        }

    }

}

internal class GsonRequestBodyConverter<T>(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>
) : Converter<T, RequestBody> {
    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        val writer: Writer =
            OutputStreamWriter(buffer.outputStream(), UTF_8)
        val jsonWriter = gson.newJsonWriter(writer)
        adapter.write(jsonWriter, value)
        jsonWriter.close()
        return buffer.readByteString().toRequestBody(MEDIA_TYPE)
    }

    companion object {
        private val MEDIA_TYPE: MediaType = "application/json; charset=UTF-8".toMediaType()
        private val UTF_8 = Charset.forName("UTF-8")
    }
}