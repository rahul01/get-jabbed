package com.rhl.getjabbed.main

import android.annotation.SuppressLint
import android.util.Log
import com.rhl.getjabbed.model.Error
import com.rhl.getjabbed.model.auth.AuthToken
import com.rhl.getjabbed.model.auth.Mobile
import com.rhl.getjabbed.model.auth.Otp
import com.rhl.getjabbed.model.auth.Transaction
import com.rhl.getjabbed.model.beneficiary.Beneficiaries
import com.rhl.getjabbed.model.bookingdetails.BookingDetails
import com.rhl.getjabbed.model.session.CenterList
import com.rhl.getjabbed.model.state.District
import com.rhl.getjabbed.model.state.DistrictList
import com.rhl.getjabbed.model.state.StateList
import com.rhl.getjabbed.mvi_base.OnCompleteListener
import com.rhl.getjabbed.network.ApiClient
import com.rhl.getjabbed.util.SharedPref
import com.rhl.getjabbed.util.TAG
import com.rhl.getjabbed.util.sha256
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

object MainRepository {

    fun login(mobile: String, listener: OnCompleteListener<Transaction>) {
        SharedPref.setValue(SharedPref.MOBILE, mobile)
        val call = ApiClient.service.login(Mobile(mobile))
        enqueue(call, listener, "login")
    }

    fun verification(otp: String, txnId: String, listener: OnCompleteListener<AuthToken>) {
        val call = ApiClient.service.verification(Otp(otp.sha256, txnId))
        enqueue(call, listener, "verification")
    }

    fun getBeneficiaries(token: String, listener: OnCompleteListener<Beneficiaries>) {
        SharedPref.setValue(SharedPref.TOKEN, token)
        val call = ApiClient.service.getBeneficiaries("Bearer $token")
        enqueue(call, listener, "getBeneficiaries")
    }

    fun getStates(listener: OnCompleteListener<StateList>) {
        val call = ApiClient.service.getStates()
        enqueue(call, listener, "getStates")
    }

    fun getDistricts(stateId: Int, listener: OnCompleteListener<DistrictList>) {
        SharedPref.setValue(SharedPref.STATE, stateId)
        val call = ApiClient.service.getDistricts(stateId.toString())
        enqueue(call, listener, "getDistricts")
    }

    fun bookSession(
        bookingDetails: BookingDetails,
        token: String,
        listener: OnCompleteListener<com.rhl.getjabbed.model.bookingdetails.Appointment>
    ) {
        val call = ApiClient.service.bookSession(bookingDetails, "Bearer $token")
        enqueue(call, listener, "bookSession")
    }

    @SuppressLint("SimpleDateFormat")
    fun getSessions(
        vaccine: String?,
        district: District,
        token: String,
        listener: OnCompleteListener<CenterList>
    ) {
        SharedPref.setValue(SharedPref.DIST, district.districtId)
        val spf = SimpleDateFormat("dd-MM-yyyy")
        val calendar = Calendar.getInstance()
//        val today = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = calendar.time


        val params: MutableMap<String, Any> = HashMap()
        params["district_id"] = district.districtId
        params["date"] = spf.format(tomorrow)
        if (!vaccine.isNullOrEmpty()) {
            params["vaccine"] = vaccine
        }
        val call = ApiClient.service.getSession(params, "Bearer $token")
        enqueue(call, listener, "getSessions")
    }


    private fun <T> enqueue(
        call: Call<T>,
        listener: OnCompleteListener<T>,
        source: String? = ""
    ) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                processResponse(response, listener)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                t.printStackTrace()
                error(listener, source)
            }
        })
    }

    private fun <T> processResponse(
        response: Response<T>,
        listener: OnCompleteListener<T>
    ) {
        if (response.isSuccessful) {
            Log.d(TAG, "call success")
            listener.onSuccess(response.body()!!)
        } else {
            if (response.code() == 403) {
                listener.onFailure(Error(response.code(), "High Traffic! Try again later"))
            } else {
                listener.onFailure(Error(response.code(), response.errorBody().toString()))
            }
        }
    }

    private fun <T> error(listener: OnCompleteListener<T>, source: String?) {
        listener.onFailure(Error(-1, "Server error : $source"))
    }

}