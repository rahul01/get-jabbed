package com.rhl.getjabbed.mvi_base

import com.rhl.getjabbed.model.Error

/**
 * Listener for callbacks when an operation completes.
 */
interface OnCompleteListener<T> {
    fun onSuccess(data: T)
    fun onFailure(error: Error)
}