package com.rhl.getjabbed.model.state


import com.google.gson.annotations.SerializedName

data class StateList(
    @SerializedName("states")
    val states: List<State>,
    @SerializedName("ttl")
    val ttl: Int
)