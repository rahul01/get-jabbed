package com.rhl.getjabbed.model.session


import com.google.gson.annotations.SerializedName

data class CenterList(
    @SerializedName("centers")
    val centers: List<Center>
)