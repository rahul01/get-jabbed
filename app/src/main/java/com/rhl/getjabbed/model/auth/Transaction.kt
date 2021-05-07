package com.rhl.getjabbed.model.auth


import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("txnId")
    val txnId: String
)