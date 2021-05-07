package com.rhl.getjabbed.model.beneficiary


import com.google.gson.annotations.SerializedName

data class Beneficiaries(
    @SerializedName("beneficiaries")
    val beneficiaries: List<Beneficiary>
)