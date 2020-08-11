package com.lvhttp.net.converter

import android.annotation.TargetApi
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.lvhttp.net.LvHttp
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.Body
import java.lang.reflect.Type


/**
 * @name LvConverterFactory
 * @package com.www.net.converter
 * @author 345 QQ:1831712732
 * @time 2020/6/22 20:21
 * @description ConverterFactory
 */
@Suppress("UNCHECKED_CAST")
class LvDefaultConverterFactory(private val gson: Gson) : Converter.Factory() {

    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.RUNTIME)
    internal annotation class Chunked

    companion object {
        fun create(gson: Gson): LvDefaultConverterFactory {
            return LvDefaultConverterFactory(gson)
        }
    }

    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return LvResponseBodyConverter<Any>(gson, type)
    }


    override fun requestBodyConverter(
        type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        var isBody = false
        var isChunked = false
        for (annotation in parameterAnnotations) {
            isBody = isBody or (annotation is Body)
            isChunked = isChunked or (annotation is Chunked)
        }
        return if (!isBody || !isChunked) {
            null
        } else null


    }


    class LvResponseBodyConverter<T>(private val gson: Gson, private val type: Type) :
        Converter<ResponseBody, T> {
        @RequiresApi(Build.VERSION_CODES.P)

        override fun convert(value: ResponseBody): T? {
            val string = value.string()
            if (LvHttp.getIsLogging()) {
                Log.e("LvHttp：type = $type \n", "  result = $string ")
            }
            if (type == String::class.java || type::class.java.isPrimitive) {
                return string as T
            }
            return gson.fromJson(string, type)
        }

    }


}