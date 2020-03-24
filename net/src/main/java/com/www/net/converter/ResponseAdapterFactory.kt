package com.www.net.converter

import android.util.Log
import com.www.net.Result
import com.www.net.exception.LvNetWorkException
import com.www.net.exception.VerifyResult
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type

class ResponseAdapterFactory(private val isLog: Boolean) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Result::class.java) {
            return null
        }
        return ResponseAdapter<Result>(isLog)
    }

    class ResponseAdapter<R>(val isLog: Boolean) : CallAdapter<R, Result> {

        override fun responseType(): Type {
            return Result::class.java
        }

        override fun adapt(call: Call<R>): Result {
            val execute = call.execute()
            val result: Result = execute.body() as Result
            result.response = execute
            if (isLog) {
                Log.d("LvHttp：", result.value)
            }
            if (!VerifyResult.verify(result.value)) {
                throw LvNetWorkException("${ResponseAdapterFactory::class.java.name}：请求出错，请重试\n${result.value}")
            }
            return result
        }
    }
}