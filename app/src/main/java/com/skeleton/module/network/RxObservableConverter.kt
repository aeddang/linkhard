package com.skeleton.module.network

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiPredicate
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import okio.BufferedSink
import okio.Okio
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription


class RxObservableConverter {

    companion object {

        fun <T> forNetwork(observable: Single<T>): Single<T> {
            return observable
                .retry(RetryPolicy.none())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }

        fun <T> forNetwork(observable: Observable<T>): Observable<T> {
            return observable
                    .retry(RetryPolicy.none())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }

        fun <T> forFile(observable: Single<T>): Single<T> {
            return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }

        fun <T> forNetwork(observable: Observable<T>, retry: BiPredicate<Int, Throwable>): Observable<T> {
            return observable
                    .retry(retry)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }

        fun <T> forFile(observable: Observable<T>): Observable<T> {
            return observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

}