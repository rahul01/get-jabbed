package com.rhl.getjabbed.model.auth


import com.google.gson.annotations.SerializedName

data class Otp(
    @SerializedName("otp")
    val otp: String,
    @SerializedName("txnId")
    val txnId: String
)