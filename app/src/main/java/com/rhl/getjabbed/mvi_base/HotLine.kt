package com.rhl.getjabbed.mvi_base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/** event bus for view models*/
object HotLine {
    private val hotLineEvent: MutableLiveData<Pair<String, Any>> = MutableLiveData()

    @Deprecated("use setupHotLineCalls() from MviVieModel")
    fun listen(observer: Observer<Pair<String, Any>>) {
        hotLineEvent.observeForever(observer)
    }

    @Deprecated("use setupHotLineCalls() from MviVieModel")
    fun hangUp(observer: Observer<Pair<String, Any>>?) {
        if (observer != null) {
            hotLineEvent.removeObserver(observer)
        }
    }

    fun post(event: Pair<String, Any>) {
        hotLineEvent.value = event
        hotLineEvent.value = Pair("", "")// workaround for live data emitting last value on registering new observer
    }
}
