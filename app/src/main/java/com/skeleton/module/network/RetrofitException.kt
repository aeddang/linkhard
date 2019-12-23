package com.skeleton.module.network

import com.lib.util.Log
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RetrofitException(
    override val message: String?,
    private val response: Response<*>?,
    val kind: ErrorKind,
    private val exception: Throwable?,
    private val retrofit: Retrofit?
) : RuntimeException(message, exception) {

    private val appTag = javaClass.simpleName

    fun isTimeout(): Boolean {
        return exception is SocketTimeoutException
    }

    fun <T> getErrorBodyAs(type: Class<T>): T? {
        retrofit?.apply {
            response?.apply {
                response.errorBody()?.let {
                    val converter: Converter<ResponseBody, T> = retrofit.responseBodyConverter(type, arrayOf())
                    return converter.convert(it)
                }
            } ?: Log.e(appTag, "response is null")
        } ?: Log.e(appTag, "retrofit is null")
        return null
    }

    companion object {
        fun httpError(response: Response<*>, retrofit: Retrofit?): RetrofitException {
            val message: String = response.code().toString() + " " + response.message()
            return RetrofitException(
                message,
                response,
                ErrorKind.HTTP,
                null,
                retrofit
            )
        }

        fun networkError(exception: IOException): RetrofitException {
            return RetrofitException(
                exception.message,
                null,
                ErrorKind.NETWORK,
                exception,
                null
            )
        }

        fun unexpectedError(exception: Throwable): RetrofitException {
            return RetrofitException(
                exception.message,
                null,
                ErrorKind.UNEXPECTED,
                exception,
                null
            )
        }

        fun unknownHostError(exception: UnknownHostException): RetrofitException {
            return RetrofitException(
                exception.message,
                null,
                ErrorKind.UNKNOWNHOST,
                exception,
                null
            )
        }
    }
}