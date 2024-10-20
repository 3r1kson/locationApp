package com.example.locationapp

import android.content.Context
import android.os.Handler
import android.os.Looper

class VolumeKeyListener(
    private val context: Context,
    private val resetTime: Long = 1000L,
    private val viewModel: LocationViewModel,
    private val locationUtils: LocationUtils
) {
    private var volumeDownPressCount = 0

    fun onVolumeDownPress() {
        volumeDownPressCount++

        println(volumeDownPressCount)
        val location = viewModel.location.value
        val address = location?.let { locationUtils.reverseGeocodelocation(it) }

        if (volumeDownPressCount == 3) {
            println("CLICKED 3 TIMES")
            println(location)
            location?.let {
                showLocationNotification(context, it, address ?: "Address not available")
            }
            volumeDownPressCount = 0
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                volumeDownPressCount = 0
            }, resetTime)
        }
    }
}
