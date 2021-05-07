package com.rhl.getjabbed.model.session


import com.google.gson.annotations.SerializedName

data class Session(
    @SerializedName("available_capacity")
    val availableCapacity: Double,
    @SerializedName("date")
    val date: String,
    @SerializedName("min_age_limit")
    val minAgeLimit: Int,
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("slots")
    val slots: List<String>,
    @SerializedName("vaccine")
    val vaccine: String
)