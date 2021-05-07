package com.rhl.getjabbed.main

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.rhl.getjabbed.model.Error
import com.rhl.getjabbed.model.auth.AuthToken
import com.rhl.getjabbed.model.auth.Transaction
import com.rhl.getjabbed.model.beneficiary.Beneficiaries
import com.rhl.getjabbed.model.bookingdetails.Appointment
import com.rhl.getjabbed.model.bookingdetails.BookingDetails
import com.rhl.getjabbed.model.session.CenterList
import com.rhl.getjabbed.model.state.DistrictList
import com.rhl.getjabbed.model.state.StateList
import com.rhl.getjabbed.mvi_base.MviViewModel
import com.rhl.getjabbed.mvi_base.OnCompleteListener
import com.rhl.getjabbed.util.TAG
import com.rhl.getjabbed.util.sha256
import java.util.*

class MainViewModel(application: Application) : MviViewModel<MainState, MainEffect, MainAction>(
    application, MainState(), MainEffect()
) {
    var retry = false
    var mobile: String? = null
    override fun onEvent(action: MainAction) {
        when (action) {
            is MainAction.Login -> {
                currentViewState = currentViewState.copy(loading = true)
                mobile = action.mobile
                login(mobile!!)
            }
            is MainAction.GetStates -> {
                getStates()
            }
            is MainAction.SelectState -> {
                getDistricts(action.state.stateId)
            }
            is MainAction.SelectDistrict -> {
                currentViewState = currentViewState.copy(selectedDistrict = action.district)
            }
            is MainAction.GetSessions -> {
                retry = true
                getSessions(action.pinFilter)
            }
            is MainAction.RemoveBeneficiary -> {
                currentViewState.selectedBeneficiaries.remove(action.beneficiary)
            }
            is MainAction.SelectBeneficiary -> {
                currentViewState.selectedBeneficiaries.add(action.beneficiary)
            }
            is MainAction.StopSearch -> {
                retry = false
            }
        }
    }

    private fun getSessions(pinFilter: String) {
        if (currentViewState.authToken.isNullOrEmpty()) {
            Log.d(TAG, "token: ${currentViewState.authToken}")
            sendError(Error(-1, "Please login"))
            return
        }

        Log.d(TAG, "bnf: ${currentViewState.selectedBeneficiaries}")
        if (currentViewState.selectedBeneficiaries.isEmpty()) {
            Log.d(TAG, "bnf: ${currentViewState.selectedBeneficiaries}")
            sendError(Error(-1, "Please select beneficiary"))
            return
        }

        var vaccine: String? = null
        var age = 0
        for (bnf in currentViewState.selectedBeneficiaries) {
            if (vaccine == null) {
                vaccine = bnf.vaccine
            } else if (vaccine != bnf.vaccine) {
                sendError(Error(-1, "Please select ppl only with same vaccine"))
                return
            }

            val bnfAge = Calendar.getInstance().get(Calendar.YEAR) - bnf.birthYear.toInt()
            if (age == 0) {
                age = if (bnfAge >= 45) 45 else 18
            } else if (age == 45 && bnfAge < 45) {
                sendError(Error(-1, "Please select all ppl only within same age group"))
                return
            }
        }

        if (currentViewState.selectedDistrict != null && currentViewState.authToken != null) {
            MainRepository.getSessions(
                vaccine,
                currentViewState.selectedDistrict!!,
                currentViewState.authToken!!,
                object : OnCompleteListener<CenterList> {
                    override fun onSuccess(data: CenterList) {
                        if (!data.centers.isNullOrEmpty()) {
                            val availableSessions =
                                data.centers.filter {
                                    it.sessions.any { session ->
                                        session.availableCapacity >= currentViewState.selectedBeneficiaries.size
                                                && session.minAgeLimit == age

                                    } && it.pincode.toString().startsWith(pinFilter)
                                }
                            if (availableSessions.isNotEmpty()) {
                                val bnfs =
                                    currentViewState.selectedBeneficiaries.map { it.beneficiaryReferenceId }
                                val dose = if (vaccine.isNullOrEmpty()) 1 else 2
                                val session = availableSessions[0].sessions.first { session ->
                                    session.availableCapacity > currentViewState.selectedBeneficiaries.size
                                            && session.minAgeLimit == age
                                }
                                val bookingDetails = BookingDetails(
                                    bnfs,
                                    dose,
                                    session.sessionId,
                                    session.slots[0]
                                )
                                MainRepository.bookSession(
                                    bookingDetails,
                                    currentViewState.authToken!!,
                                    object : OnCompleteListener<Appointment> {
                                        override fun onSuccess(data: Appointment) {
                                            retry = false
                                            currentViewState =
                                                currentViewState.copy(bookingId = data.appointmentId)
                                        }

                                        override fun onFailure(error: Error) {
                                            login(mobile!!)
                                            sendError(error)
                                        }
                                    }
                                )
                            }
                            val size = data.centers.size
                            val count18 = data.centers.flatMap { it.sessions }
                                .filter { it.minAgeLimit == 18 }.size
                            val countPin = data.centers.filter {
                                it.pincode.toString().startsWith(pinFilter)
                            }.size

                            currentViewState =
                                currentViewState.copy(
                                    availableSessions = availableSessions,
                                    total = size,
                                    count18 = count18,
                                    countPin = countPin
                                )
                        }
                        Handler(Looper.getMainLooper()).postDelayed({
                            currentEffect = MainEffect(retry = retry)
                        }, 4500)
                    }

                    override fun onFailure(error: Error) {
                        if (error.code == 401) {
                            currentViewState =
                                currentViewState.copy(authToken = null, otp = null, txnId = null)
                            login(mobile!!)
                        }
                        Handler(Looper.getMainLooper()).postDelayed({
                            currentEffect = MainEffect(retry = retry)
                        }, 4500)
                        sendError(error)
                    }
                })
        } else {
            if (currentViewState.selectedDistrict == null)
                sendError(Error(-1, "select district"))
            if (currentViewState.authToken == null)
                sendError(Error(-1, "please login "))
        }
    }

    private fun getStates() {
        MainRepository.getStates(object : OnCompleteListener<StateList> {
            override fun onSuccess(data: StateList) {
                currentViewState = currentViewState.copy(stateList = data)
            }

            override fun onFailure(error: Error) {
                sendError(error)
            }
        })
    }

    private fun getDistricts(stateId: Int) {
        MainRepository.getDistricts(stateId, object : OnCompleteListener<DistrictList> {
            override fun onSuccess(data: DistrictList) {
                currentViewState =
                    currentViewState.copy(districtList = data, selectedDistrict = null)
            }

            override fun onFailure(error: Error) {
                sendError(error)
            }

        })
    }

    private fun login(mobile: String) {
        Log.d(TAG, "login")
        MainRepository.login(
            mobile,
            object : OnCompleteListener<Transaction> {
                override fun onSuccess(data: Transaction) {
                    Log.d(TAG, "data $data")
                    currentViewState = currentViewState.copy(txnId = data.txnId, loading = true)
                }

                override fun onFailure(error: Error) {
                    currentViewState = currentViewState.copy(loading = false)
                    sendError(error)
                }
            })
    }

    init {
        setupHotLineCalls()
    }

    override fun onHotLineCall(call: Pair<String, Any>) {
        Log.i(TAG, "Event : ${call.first}")
        when (call.first) {
            "OTP" -> {
                Log.d(TAG, "SHA of OTP  ${(call.second as String).sha256}")
                currentViewState = currentViewState.copy(otp = call.second as String)
                verifyOtp(call)
            }
        }
    }

    private fun verifyOtp(call: Pair<String, Any>) {
        MainRepository.verification(
            call.second as String,
            currentViewState.txnId!!,
            object : OnCompleteListener<AuthToken> {
                override fun onSuccess(data: AuthToken) {
                    currentViewState =
                        currentViewState.copy(authToken = data.token, loading = false)
                    currentEffect = MainEffect(retry = retry)
                    getBeneficiaries()
                }

                override fun onFailure(error: Error) {
                    currentViewState = currentViewState.copy(loading = false)
                    sendError(error)
                }
            })
    }

    private fun getBeneficiaries() {
        MainRepository.getBeneficiaries(currentViewState.authToken!!,
            object : OnCompleteListener<Beneficiaries> {
                override fun onSuccess(data: Beneficiaries) {
                    currentViewState = currentViewState.copy(beneficiaries = data)
                }

                override fun onFailure(error: Error) {
                    sendError(error)
                }
            })
    }

    private fun sendError(error: Error) {
        Log.e(TAG, "Error $error")
        currentEffect = MainEffect("ERROR ${error.code} :: ${error.msg}")
    }
}