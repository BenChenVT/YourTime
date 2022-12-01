package com.example.yourtime

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList

class TimeViewModel : ViewModel() {

    private val repository: UserRepository = UserRepository().getInstance()
    private val _allEvents = MutableLiveData<List<Event>>()
    val allEvents: LiveData<List<Event>> = _allEvents

    init {
        repository.loadUsers(_allEvents)
    }

    enum class TimerState{
        Stopped, Paused, Running
    }
    // do everyting to keep tarck of the time
    // every logic thing about time
    // 1. start/stop
    // 2. store/delete time
    // 3. take down lep time, delete lap time

    // need mutable live data class here to store the time
    private val liveTime = MutableLiveData<LongArray>()
    private lateinit var timer: Timer
    private var timerLengthSeconds = 0L
    private var timerState = TimerState.Stopped

    // initialization
    init {
        liveTime.value = longArrayOf(timerLengthSeconds, timerLengthSeconds, timerLengthSeconds)
    }


    // all function about time logic
    /**
     * function that make the timer ticking
     */
    inline fun Timer.scheduleAtFixedRate(
        delay: Long,
        period: Long,
        crossinline action: TimerTask.() -> Unit
    ) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                Log.e("NIlu_TAG","Hello World")
            }
        }, 3000)
    }

    // function for start the timer
    /**
     * function for start the timer
     * TA: don't need io.scope(UI thread) here
     * Prof: timer can be put in UI thread
     */
    fun hitTimer(){
        // if the timer is currently not running, then run it
        if(timerState == TimerState.Stopped || timerState == TimerState.Paused){
            timer = Timer() // this was in the --> init{} may not be correct
            timer.scheduleAtFixedRate(object: TimerTask(){
                override fun run() {
                    // increment time
                    timerLengthSeconds += 1
                    liveTime.postValue(getMinSec())
                }
            },1000, 1000)
            timerState = TimerState.Running
            return
        }

        // if the timer is currently running, we will pause it
        if(timerState == TimerState.Running){
            timer.cancel()
            timerState = TimerState.Paused
            return
        }
    }

    /**
     * function for reset the timer
     */
    fun resetTimer(): Boolean{
        // if the timer is currently Paused or Running
        if(timerState == TimerState.Paused || timerState == TimerState.Running){
            timer.cancel()
            timerLengthSeconds = 0
            liveTime.postValue(getMinSec())
            timerState = TimerState.Stopped
            return true
        }
        return false
    }
    /**
     * get the live time
     */
    fun getLiveTime(): MutableLiveData<LongArray> {
        return liveTime
    }
    /**
     * return the min and sec
     * with array[0] = hour, array[1] = second
     */
    fun getMinSec(): LongArray{
        return longArrayOf(timerLengthSeconds / 3600, timerLengthSeconds / 60, timerLengthSeconds % 60)
    }
    /**
     * get the status of the timer
     */
    fun getState(): TimerState{
        return timerState
    }
}