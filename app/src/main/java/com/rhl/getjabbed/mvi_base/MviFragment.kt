package com.rhl.getjabbed.mvi_base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

abstract class MviFragment<VM, S, E, A>(private val _class: Class<VM>) : Fragment()
        where S : MviState, E : MviEffect, A : MviAction,
              VM : MviViewModel<S, E, A> {

    protected var viewModel: VM? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(activity!!.application)
        ).get(_class)
    }

    /**
     * must call super in children
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel?.viewState?.observe(viewLifecycleOwner, Observer(::render))
        viewModel?.viewEffects?.observe(viewLifecycleOwner, EventObserver(::applyEffect))

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    abstract fun render(state: S)
    abstract fun applyEffect(viewEffect: E)
}