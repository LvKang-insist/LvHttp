package com.www.net.converter

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

/**
 * @name LvConverterFactory
 * @package com.www.net.converter
 * @author 345 QQ:1831712732
 * @time 2020/6/22 20:21
 * @description
 */
class LvDefaultConverterFactory(private val gson: Gson) : Converter.Factory() {

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
        return LvResponseBodyConverter(gson, type)
    }


    class LvResponseBodyConverter<T>(private val gson: Gson, private val type: Type) :
        Converter<ResponseBody, T> {
        @RequiresApi(Build.VERSION_CODES.P)
        override fun convert(value: ResponseBody): T? {
            val string = value.string()
            Log.e("---------", type.typeName)
            if (type == String::class.java || type::class.java.isPrimitive) {
                return string as T
            }
            val bodyType = getParameterUpperBound(type as ParameterizedType)
            if (bodyType != null)
                if (bodyType == String::class.java || bodyType::class.java.isPrimitive) {
                    return string as T
                }

            return gson.fromJson(string, type)
        }

        private fun getParameterUpperBound(type: ParameterizedType): Type? {
            //获取全部 type
            val types = type.actualTypeArguments
            require(types.isNotEmpty()) { "Index " + 0 + " not in range [0," + types.size + ") for " + type }
            val paramType = types[0]
            //如果是通配符，取上限
            return if (paramType is WildcardType) {
                paramType.upperBounds[0]
            } else paramType
        }
    }


}