package net.kibotu.schlachtensee.extensions

import android.view.View

/**
 * An OnClickListener that sends only the first click of a given Time Interval
 *
 * @param defaultInterval time frame to skip until next click is send
 * @param onThrottleFirstClick callback
 */
class ThrottleFirstClickListener(
    private var defaultInterval: Int = 1000,
    private val onThrottleFirstClick: (View) -> Unit
) : View.OnClickListener {

    private var lastTimeClicked: Long = 0

    override fun onClick(view: View) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = System.currentTimeMillis()
        onThrottleFirstClick(view)
    }
}

fun View.setOnClickListenerThrottled(
    defaultInterval: Int = 1000,
    onThrottleFirstClick: (View) -> Unit
): ThrottleFirstClickListener {
    val throttleFirstClickListener = ThrottleFirstClickListener(defaultInterval = defaultInterval) {
        onThrottleFirstClick(it)
    }
    setOnClickListener(throttleFirstClickListener)
    return throttleFirstClickListener
}
