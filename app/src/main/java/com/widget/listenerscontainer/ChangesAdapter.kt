package com.widget.listenerscontainer

import android.util.Log

class ChangesAdapter (bridge : AppViewModel) : IChanges {
    private val TAG = "ChangesAdapter"
    private var model = bridge
    private val mChangesListeners: ArrayList<IChanges> = ArrayList()
    private lateinit var action : PeriodicalAction
    private val mListenerLock = Any()

    fun registerListener(callback: IChanges) {
        synchronized(mListenerLock) {
            if (mChangesListeners.contains(callback)) {
                return
            }
            mChangesListeners.add(callback)
            action = PeriodicalAction(model, callback) // ????
        }
        Log.d(TAG, "+ [" + mChangesListeners.size + "]")
    }

    fun unregisterListener(callback: IChanges) {
        synchronized(mListenerLock) { mChangesListeners.remove(callback) }
        Log.d(TAG, "- [" + mChangesListeners.size + "]")
    }

    private fun size() : Int {
        var result: Int
        synchronized(mListenerLock) {
            result = mChangesListeners.size
        }
        return result
    }

    fun start() {
        action.start()
    }

    fun stop() {
        action.stop()
    }

    override
    fun setValue(index: Int, value: DataWrapper) {

        println("ChangesAdapter: $index->$value")

        var size: Int
        synchronized(mListenerLock) { size = mChangesListeners.size }
        if (size == 0) {
            return
        }
        action.size = size()
        for (i in 0 until size) {
            mChangesListeners[i].setValue(index, value)
            action.start()
        }
    }

    companion object {
        @get:Synchronized
        var instance: ChangesAdapter? = null
            private set

        @Synchronized
        fun newInstance(bridge : AppViewModel) {
            if (instance == null) {
                instance = ChangesAdapter(bridge)
            }
        }
    }
}

