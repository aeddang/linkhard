package com.ironleft.linkhard.api

import com.google.gson.annotations.SerializedName



data class Response<T> (
    @SerializedName("status") val status: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("msg") val message: String,
    @SerializedName("data") val data: T
)


data class HardInfoData(@SerializedName("userType") val userType: String? )