package com.rhl.getjabbed.main

import com.rhl.getjabbed.model.beneficiary.Beneficiaries
import com.rhl.getjabbed.model.beneficiary.Beneficiary
import com.rhl.getjabbed.model.session.Center
import com.rhl.getjabbed.model.state.District
import com.rhl.getjabbed.model.state.DistrictList
import com.rhl.getjabbed.model.state.State
import com.rhl.getjabbed.model.state.StateList
import com.rhl.getjabbed.mvi_base.MviAction
import com.rhl.getjabbed.mvi_base.MviEffect
import com.rhl.getjabbed.mvi_base.MviState

data class MainState(
    val loading: Boolean = false,
    val txnId: String? = null,
    val otp: String? = null,
    val authToken: String? = null,
    val beneficiaries: Beneficiaries? = null,
    val selectedBeneficiaries: ArrayList<Beneficiary> = ArrayList(),
    val stateList: StateList? = null,
    val districtList: DistrictList? = null,
    val selectedDistrict: District? = null,
    val availableSessions: List<Center>? = null,
    val bookingId: String? = null,
    val total: Int = 0,
    val count18: Int = 0,
    val countPin: Int = 0
) : MviState()

data class MainEffect(
    val error: String? = null,
    val retry: Boolean = false,
) : MviEffect()

sealed class MainAction : MviAction() {
    data class Login(val mobile: String) : MainAction()
    object GetStates : MainAction()
    data class SelectState(val state: State) : MainAction()
    data class SelectDistrict(val district: District) : MainAction()
    data class GetSessions(val pinFilter: String) : MainAction()
    data class SelectBeneficiary(val beneficiary: Beneficiary) : MainAction()
    data class RemoveBeneficiary(val beneficiary: Beneficiary) : MainAction()
    object StopSearch : MainAction()
}
