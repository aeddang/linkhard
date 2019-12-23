package com.ironleft.linkhard.api

import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

object ApiConst {

    const val TAG = "ApiConst"

    const val API_PATH = "http://linkhard.com/"
    private const val API_VERSION = "v1"

    const val CHECK_HEALTH = "$API_VERSION/checkhealth"

    const val FIELD_USERID = "userID"
    const val FIELD_USERPW = "userPW"

}
