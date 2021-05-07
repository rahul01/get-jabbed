package com.rhl.getjabbed.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.rhl.getjabbed.mvi_base.HotLine
import com.rhl.getjabbed.util.TAG

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Get SMS map from Intent
        if (context == null || intent == null || intent.action == null) {
            return
        }
        if (intent.action != (Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            return
        }
        val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        for (message in smsMessages) {

            Log.d(
                TAG,
                "Message from ${message.displayOriginatingAddress} : ${message.messageBody}"
            )
            val body = message.messageBody
            if (body.contains("CoWIN")) {
                val otp = message.messageBody.substring(body.indexOf("is ") + 3, body.indexOf("."))
                Log.d(TAG, "OPT: $otp")
                HotLine.post(Pair("OTP", otp))
            }
        }
    }
}