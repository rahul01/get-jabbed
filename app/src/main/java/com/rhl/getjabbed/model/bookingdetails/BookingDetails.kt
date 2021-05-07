package com.rhl.getjabbed.model.bookingdetails


import com.google.gson.annotations.SerializedName

data class BookingDetails(
    @SerializedName("beneficiaries")
    val beneficiaries: List<String>,
    @SerializedName("dose")
    val dose: Int,
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("slot")
    val slot: String
)