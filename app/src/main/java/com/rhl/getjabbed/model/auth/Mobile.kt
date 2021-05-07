package com.rhl.getjabbed.model.auth


import com.google.gson.annotations.SerializedName

data class Mobile(
    @SerializedName("mobile")
    val mobile: String,

    @SerializedName("secret")
    val secret: String = "U2FsdGVkX1/3I5UgN1RozGJtexc1kfsaCKPadSux9LY+cVUADlIDuKn0wCN+Y8iB4ceu6gFxNQ5cCfjm1BsmRQ=="
)