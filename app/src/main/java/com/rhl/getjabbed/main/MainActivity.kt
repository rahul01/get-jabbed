package com.rhl.getjabbed.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rhl.getjabbed.R
import com.rhl.getjabbed.model.beneficiary.Beneficiary
import com.rhl.getjabbed.model.state.DistrictList
import com.rhl.getjabbed.model.state.StateList
import com.rhl.getjabbed.mvi_base.MviActivity
import com.rhl.getjabbed.util.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

const val MY_PERMISSIONS_REQUEST_SMS = 1

class MainActivity :
    MviActivity<MainViewModel, MainState, MainEffect, MainAction>(MainViewModel::class.java) {

    private val beneficiaryViews = ArrayList<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        beneficiaryViews.add(checkBox0)
        beneficiaryViews.add(checkBox1)
        beneficiaryViews.add(checkBox2)
        beneficiaryViews.add(checkBox3)

        SharedPref.init(this)
        editTextNumber.setText(SharedPref.getString(SharedPref.MOBILE))
        pinCodeFilter.setText(SharedPref.getString(SharedPref.PINFILTER))
        if (editTextNumber.text.toString().isNotEmpty()) {
//            viewModel.onEvent(MainAction.Login(editTextNumber.text.toString()))
        }

        requestPermission(this)

        viewModel.onEvent(MainAction.GetStates)
        btnStart.setSingleClickListener {
            Log.d(TAG, "login")
            loginProgress.visible()
            viewModel.onEvent(MainAction.Login(editTextNumber.text.toString()))
        }

        btnSearch.setSingleClickListener {
            Log.d(TAG, "search")
            val stopSearch = getString(R.string.stop_search)
            val search = getString(R.string.search)
            if (btnSearch.text == search) {
                btnSearch.text = stopSearch
                viewModel.onEvent(MainAction.GetSessions(pinCodeFilter.text.toString().trim()))
                SharedPref.setValue(SharedPref.PINFILTER, pinCodeFilter.text.toString())
                attempt = 0
            } else {
                btnSearch.text = search
                viewModel.onEvent(MainAction.StopSearch)
            }

        }
    }

    private fun requestPermission(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECEIVE_MMS
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECEIVE_WAP_PUSH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_MMS,
                    Manifest.permission.RECEIVE_WAP_PUSH
                ),
                MY_PERMISSIONS_REQUEST_SMS
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        val granted = if (checkPermissionGranted(
                requestCode,
                permissions,
                grantResults
            )
        ) "permission granted" else "permission not granted"
        Toast.makeText(this, granted, Toast.LENGTH_SHORT).show()
    }

    private fun checkPermissionGranted(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ): Boolean {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_SMS -> {
                // If request is cancelled, the result arrays are empty.
                return (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            }
        }
        return false
    }

    @SuppressLint("SetTextI18n")
    override fun render(state: MainState) {

        if (!state.bookingId.isNullOrEmpty()) {
            output.text = "BOOKED!! ${state.bookingId}"
            btnSearch.text = "BOOKED!!"
            btnStart.text = "BOOKED!!"
            textView.text = "BOOKED!! ${state.bookingId}"
        }

        if (state.loading) {
            loginProgress.visible()
            if (state.txnId != null && state.otp == null) {
                output.text = "Waiting for otp..."
            } else if (state.otp != null) {
                output.text = "Loading beneficiaries..."
            }
        } else {
            loginProgress.gone()
        }

        if (!state.beneficiaries?.beneficiaries.isNullOrEmpty()) {
            val list = state.beneficiaries?.beneficiaries!!
            for (index in list.indices) {
                val beneficiary = list[index]
                val age = Calendar.getInstance().get(Calendar.YEAR) - beneficiary.birthYear.toInt()
                beneficiaryViews[index].text =
                    "  ${beneficiary.name} - $age | ${beneficiary.vaccine}"
                beneficiaryViews[index].visible()

                beneficiaryViews[index].tag = beneficiary
                beneficiaryViews[index].setOnCheckedChangeListener { view, checked ->
                    val bnf = (view.tag as Beneficiary)
                    if (checked) {
                        viewModel.onEvent(MainAction.SelectBeneficiary(bnf))
                    } else {
                        viewModel.onEvent(MainAction.RemoveBeneficiary(bnf))
                    }
                }
            }
        }

        if (state.stateList != null && spinnerState.adapter == null) {
            showStates(state.stateList)
        }
        if (state.districtList != null && state.selectedDistrict == null) {
            showDistricts(state.districtList)
        }

        if (state.total > 0) {
            output.text =
                "attempt:$attempt | ${state.total} sessions | ${state.count18} for 18+ | ${state.countPin} in Pin prefix | ${state.availableSessions?.size} matching"
            if (!state.availableSessions.isNullOrEmpty()) {
                for (center in state.availableSessions) {
                    for (session in center.sessions) {
                        output.append("\n\nCap: ${session.availableCapacity} | ${center.name} ${center.pincode}")
                    }
                }
            }
        }
    }

    var attempt = 0
    override fun applyEffect(viewEffect: MainEffect) {
        if (!viewEffect.error.isNullOrEmpty()) {
            output.text = viewEffect.error
        }

        if (viewEffect.retry) {
            attempt++
            viewModel.onEvent(MainAction.GetSessions(pinCodeFilter.text.toString().trim()))
        }
    }

    private fun showStates(stateList: StateList) {
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            stateList.states.map { it.stateName }
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerState.adapter = adapter
            val index = stateList.states.indexOfFirst {
                it.stateId == SharedPref.getInt(
                    SharedPref.STATE,
                    21 // Maharastra
                )
            }
            spinnerState.setSelection(index)
        }
        spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                viewModel.onEvent(MainAction.SelectState(stateList.states[position]))
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun showDistricts(districtList: DistrictList) {
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            districtList.districts.map { it.districtName }
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerDist.adapter = adapter
            val index = districtList.districts.indexOfFirst {
                it.districtId == SharedPref.getInt(
                    SharedPref.DIST,
                    363 // pune
                )
            }
            spinnerDist.setSelection(index)
        }
        spinnerDist.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                viewModel.onEvent(MainAction.SelectDistrict(districtList.districts[position]))
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

}