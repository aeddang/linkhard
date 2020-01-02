package com.skeleton.module.network

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okio.*
import java.io.IOException


@Suppress("UNCHECKED_CAST")
open class DownloadEventBus {
    private val mBusSubject: PublishSubject<Any> = PublishSubject.create()
    fun post(event: Any) {
        mBusSubject.onNext(event)
    }

    fun observable(): Observable<Any> {
        return mBusSubject
    }

    fun <T> filteredObservable(eventClass: Class<T>): Observable<T> {
        return mBusSubject.filter{ eventClass.isInstance(it) }.map{ it as T  }
    }
}

class ProgressEvent(val downloadIdentifier:String, val contentLength: Long, val bytesRead: Long) {
    val progress: Int = (bytesRead / (contentLength / 100f)).toInt()
    fun percentIsAvailable(): Boolean = (contentLength > 0)

}

interface DownloadProgressListener {
    fun update(
        downloadIdentifier: String,
        bytesRead: Long,
        contentLength: Long,
        done: Boolean
    )
}

class DownloadProgressResponseBody(
    private val downloadIdentifier: String,
    private val responseBody: ResponseBody,
    private val progressListener: DownloadProgressListener?
) : ResponseBody()
{
    private var bufferedSource: BufferedSource? = null
    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()))
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L
            @Throws(IOException::class)
            override fun read(sink: Buffer?, byteCount: Long): Long {
                val bytesRead = sink?.let { super.read(it, byteCount) } ?: -1L
                if (bytesRead != -1L) totalBytesRead += bytesRead
                progressListener?.update(
                    downloadIdentifier,
                    totalBytesRead,
                    responseBody.contentLength(),
                    bytesRead == -1L
                )
                return bytesRead
            }
        }
    }

}

class DownloadProgressInterceptor(val eventBus: DownloadEventBus?) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val builder = originalResponse.newBuilder()
        val downloadIdentifier = originalResponse.request().header(DOWNLOAD_IDENTIFIER_HEADER)
        val isAStream= originalResponse.header("content-type", "").equals("application/octet-stream")
        val fileIdentifierIsSet = downloadIdentifier != null && downloadIdentifier != ""
        if (isAStream && fileIdentifierIsSet) { // someone need progress informations !
            builder.body(
                DownloadProgressResponseBody(
                    downloadIdentifier!!,
                    originalResponse.body()!!,
                    object : DownloadProgressListener {
                        override fun update( downloadIdentifier: String, bytesRead: Long, contentLength: Long, done: Boolean) { // we post an event into the Bus !
                            eventBus?.post(ProgressEvent( downloadIdentifier, contentLength, bytesRead))
                        }
                    })
            )
        } else { // do nothing if it's not a file with an identifier :)
            builder.body(originalResponse.body())
        }
        return builder.build()
    }

    companion object {
        const val DOWNLOAD_IDENTIFIER_HEADER = "download-identifier"
    }


}