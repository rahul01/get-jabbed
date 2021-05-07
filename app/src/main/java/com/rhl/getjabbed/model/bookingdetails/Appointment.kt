package com.rhl.getjabbed.model.bookingdetails


import com.google.gson.annotations.SerializedName

data class Appointment(
    @SerializedName("appointment_id")
    val appointmentId: String
)