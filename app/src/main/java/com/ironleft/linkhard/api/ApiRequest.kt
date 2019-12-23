package com.ironleft.linkhard.api

import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ApiRequest{
    @FormUrlEncoded
    @POST(ApiConst.CHECK_HEALTH)
    fun checkHealth(
        @Field(ApiConst.FIELD_USERID) userID: String?,
        @Field(ApiConst.FIELD_USERPW) userPW: String?
    ): Single<Response<HardInfoData>>

}