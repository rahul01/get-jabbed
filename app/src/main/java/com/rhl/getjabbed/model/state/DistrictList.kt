package com.rhl.getjabbed.model.state


import com.google.gson.annotations.SerializedName

data class DistrictList(
    @SerializedName("districts")
    val districts: List<District>,
    @SerializedName("ttl")
    val ttl: Int
)