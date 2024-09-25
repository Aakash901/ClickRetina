package com.example.clickretina.utils

import android.os.Handler

//create a class to show the loading animation to user

class LoadingAnimationHandler(private val callback: (String) -> Unit) {
    private var currentIndex = 0
    private val dotArray = arrayOf(". . . .", ". . .", ".. .", ".")

    private val handler = Handler()
    private val runnable = object : Runnable {
        override fun run() {
            currentIndex = (currentIndex + 1) % dotArray.size
            callback(dotArray[currentIndex])
            handler.postDelayed(this, 300)
        }
    }

    fun startAnimation() {
        handler.post(runnable)
    }

    fun stopAnimation() {
        handler.removeCallbacks(runnable)
    }
}
