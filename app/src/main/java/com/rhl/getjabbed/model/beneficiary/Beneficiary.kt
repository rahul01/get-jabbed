package com.rhl.getjabbed.model.beneficiary


import com.google.gson.annotations.SerializedName

data class Beneficiary(
    @SerializedName("appointments")
    val appointments: List<Appointment>,
    @SerializedName("beneficiary_reference_id")
    val beneficiaryReferenceId: String,
    @SerializedName("birth_year")
    val birthYear: String,
    @SerializedName("comorbidity_ind")
    val comorbidityInd: String,
    @SerializedName("dose1_date")
    val dose1Date: String,
    @SerializedName("dose2_date")
    val dose2Date: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("mobile_number")
    val mobileNumber: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("photo_id_number")
    val photoIdNumber: String,
    @SerializedName("photo_id_type")
    val photoIdType: String,
    @SerializedName("vaccination_status")
    val vaccinationStatus: String,
    @SerializedName("vaccine")
    val vaccine: String = ""
)