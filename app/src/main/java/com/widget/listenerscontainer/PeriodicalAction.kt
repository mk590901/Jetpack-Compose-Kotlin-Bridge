package com.widget.listenerscontainer

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.Random

class PeriodicalAction(bridge : AppViewModel, setValue: IChanges?) {
    private var mHandler = Handler(Looper.getMainLooper())
    var size : Int = 0
    var isActive = false
        private set
    private var mSetValue : IChanges? = setValue

    // Method to start the periodical action
    fun start() {
        isActive = true
        mHandler.post(periodicalRunnable)
        Log.e(TAG, "Start")
    }

    // Method to stop the periodical action
    fun stop() {
        isActive = false
        mHandler.removeCallbacks(periodicalRunnable)
        Log.e(TAG, "Stop")
    }

    // Runnable for the periodical action
    private val periodicalRunnable: Runnable = object : Runnable {
        override fun run() {
            // Perform the periodical action here
            if (isActive) {
                if (mSetValue != null) {
                    size = bridge.size()
                    if (size > 0) {
                        val current = generateRandomNumber(0, size-1)
                        val dataWrapper = bridge.getElementByIndex(current)
                        dataWrapper.counter = generateRandomNumber(10, 99)
                        mSetValue?.setValue(
                            current,
                            DataWrapper(counter = generateRandomNumber(10, 99),
                                uuid = dataWrapper.uuid)
                        )
                    }
                }
                mHandler.postDelayed(this, PERIODICAL_INTERVAL.toLong())
            }
        }
    }

    // Constructor
    init {
        mSetValue = setValue
    }

    companion object {
        private const val TAG = "action" // 1 second
        private val mRandom = Random() //  for simulation
        private const val PERIODICAL_INTERVAL = 100 //1000 // 1 second
        fun generateRandomNumber(from: Int, to: Int): Int {
            return mRandom.nextInt(to - from + 1) + from
        }
    }
}

