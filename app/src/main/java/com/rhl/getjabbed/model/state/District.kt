package com.rhl.getjabbed.model.state


import com.google.gson.annotations.SerializedName

data class District(
    @SerializedName("district_id")
    val districtId: Int,
    @SerializedName("district_name")
    val districtName: String,
    @SerializedName("district_name_l")
    val districtNameL: String,
    @SerializedName("state_id")
    val stateId: Int
)