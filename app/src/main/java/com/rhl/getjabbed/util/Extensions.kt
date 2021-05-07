package com.rhl.getjabbed.util

import android.content.Context
import android.graphics.Rect
import android.os.SystemClock
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import androidx.transition.Slide
import androidx.transition.TransitionManager
import java.security.MessageDigest
import java.util.*

const val NO_NETWORK = 1
const val NETWORK_ERROR = 2
const val UNKNOWN_ERROR = 3


fun View.setSingleClickListener(debounceTime: Long = 1500L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.slideUp(duration: Int = 300) {
    val transition = Slide(Gravity.BOTTOM);
    transition.duration = duration.toLong();
    transition.addTarget(this);
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
    visible()
}


fun View.isReallyVisible(): Boolean {
    if (!this.isShown) {
        return false
    }
    val viewRect = Rect()
    this.getGlobalVisibleRect(viewRect)
    val displayMetrics = this.context.resources.displayMetrics

    val screen = Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)

    return screen.intersect(viewRect) //screen.contains(viewRect)
}

fun String.capitalizeWords(): String = split(" ")
    .joinToString(" ") { it.toLowerCase(Locale.ENGLISH).capitalize() }

/**
 * Create a formatted CharSequence from a string resource containing arguments and HTML formatting
 *
 * The string resource must be wrapped in a CDATA section so that the HTML formatting is conserved.
 *
 * Example of an HTML formatted string resource:
 * <string name="html_formatted"><![CDATA[ bold text: <B>%1$s</B> ]]></string>
 */
fun Context.getFormattedString(@StringRes id: Int, vararg args: Any?): CharSequence {
    val text = String.format(getString(id), *args)
    return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT)
}

val Any.TAG: String
    get() {
        return if (!javaClass.isAnonymousClass) {
            val name = javaClass.simpleName
            if (name.length <= 23) name else name.substring(0, 23)// first 23 chars
        } else {
            val name = javaClass.name
            if (name.length <= 23) name else name.substring(
                name.length - 23,
                name.length
            )// last 23 chars
        }
    }

val String.sha256: String
    get() {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(this.toByteArray())
        return bytes.toHex()
    }

fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}