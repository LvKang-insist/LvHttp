package com.www.net.converter

import android.annotation.SuppressLint
import android.util.Log
import com.www.net.Result
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.Type

class ResponseAdapterFactory(private val isLog: Boolean) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        Log.e("---------------------","+++++++++")
        if (getRawType(returnType) != Result::class.java) {
            return null
        }
        Log.e("---------------------","0000000000")
        return ResponseAdapter<Result>(isLog)
    }

    class ResponseAdapter<R>(val isLog: Boolean) : CallAdapter<R, Result> {

        override fun responseType(): Type {
            Log.e("---------------------","0000000000")
            return Result::class.java
        }

        @SuppressLint("LongLogTag")
        override fun adapt(call: Call<R>): Result? {
            Log.e("0000000000","0000000000")
            var execute: Response<R>? = null
            try {
                execute = call.execute()
            } catch (e: Exception) {
                Log.e("ResponseAdapterFactory}", e.printStackTrace().toString())
                execute = null
            } finally {
                return if (execute != null) {
                    val result = execute.body() as Result
                    result.response = execute.raw()
                    if (isLog) {
                        Log.e("LvHttp：", result.value)
                    }
                    result
                } else {
                    return null
                }
            }
        }
    }
}