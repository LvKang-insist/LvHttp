package com.www.net.converter

import android.util.Log
import com.www.net.Result
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

class ResponseConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, Result>? {
        return ResponseConverter()
    }

    class ResponseConverter : Converter<ResponseBody, Result> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): Result? {
            val string = value.string()
            return Result(string)
        }
    }
}