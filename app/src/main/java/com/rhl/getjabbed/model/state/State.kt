package com.rhl.getjabbed.model.state


import com.google.gson.annotations.SerializedName

data class State(
    @SerializedName("state_id")
    val stateId: Int,
    @SerializedName("state_name")
    val stateName: String,
    @SerializedName("state_name_l")
    val stateNameL: String
)