package com.rhl.getjabbed.mvi_base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

abstract class MviViewModel<S, E, A>(application: Application, state: S, effect: E) : AndroidViewModel(application)
        where S : MviState, E : MviEffect, A : MviAction {
    val viewState: MutableLiveData<S> = MutableLiveData()
    protected var currentViewState = state
        // always copy previous state when making changes
        set(value) {
            field = value
            viewState.value = value
        }
    protected var currentViewStateAsync = state
        // always copy previous state when making changes
        set(value) {
            field = value
            viewState.postValue(value)
        }
    val viewEffects: MutableLiveData<Event<E>> = MutableLiveData()
    protected var currentEffect = effect
        //Always create a new object when passing an effect
        set(value) {
            field = value
            viewEffects.value = Event(value)
        }

    protected var currentEffectAsync = effect
        set(value) {
            field = value
            viewEffects.postValue(Event(value))
        }

    abstract fun onEvent(action: A)

    private var call: Observer<Pair<String, Any>>? = null

    /**
     *  call this method to listen for hotline calls
     */
    fun setupHotLineCalls() {
        call = Observer<Pair<String, Any>> {
            onHotLineCall(it)
        }

        HotLine.listen(call!!)// ignore deprecation here
    }

    override fun onCleared() {
        call?.let {
            HotLine.hangUp(call!!)// ignore deprecation here
        }
        super.onCleared()
    }

    /**
     * override this to get hotline calls
     */
    open fun onHotLineCall(call: Pair<String, Any>) {}
}