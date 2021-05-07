package com.rhl.getjabbed.model.beneficiary


import com.google.gson.annotations.SerializedName

data class Appointment(
    @SerializedName("appointment_id")
    val appointmentId: String,
    @SerializedName("block_name")
    val blockName: String,
    @SerializedName("block_name_l")
    val blockNameL: String,
    @SerializedName("center_id")
    val centerId: Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("district_name")
    val districtName: String,
    @SerializedName("district_name_l")
    val districtNameL: String,
    @SerializedName("dose")
    val dose: Int,
    @SerializedName("fee_type")
    val feeType: String,
    @SerializedName("from")
    val from: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("long")
    val long: Double,
    @SerializedName("name")
    val name: String,
    @SerializedName("name_l")
    val nameL: String,
    @SerializedName("pincode")
    val pincode: String,
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("slot")
    val slot: String,
    @SerializedName("state_name")
    val stateName: String,
    @SerializedName("state_name_l")
    val stateNameL: String,
    @SerializedName("to")
    val to: String
)