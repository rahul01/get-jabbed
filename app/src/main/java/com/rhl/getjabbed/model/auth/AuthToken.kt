package com.rhl.getjabbed.model.auth


import com.google.gson.annotations.SerializedName

data class AuthToken(
    @SerializedName("isNewAccount")
    val isNewAccount: String,
    @SerializedName("token")
    val token: String
)