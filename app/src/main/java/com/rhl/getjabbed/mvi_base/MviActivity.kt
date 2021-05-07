package com.rhl.getjabbed.mvi_base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

abstract class MviActivity<VM, S, E, A>(private val _class: Class<VM>) :
    AppCompatActivity() where S : MviState, E : MviEffect, A : MviAction,
                              VM : MviViewModel<S, E, A> {
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(
                _class
            )
        viewModel.viewState.observe(this, Observer(::render))
        viewModel.viewEffects.observe(this, EventObserver(::applyEffect))
    }

    abstract fun render(state: S)
    abstract fun applyEffect(viewEffect: E)
}