package com.example.yourtime

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TimeViewModel : ViewModel() {

    var address: String = ""
    var coordinates: String = ""
    private val repository: UserRepository = UserRepository().getInstance()
    private val _allEvents = MutableLiveData<List<Event>>()
    val allEvents: LiveData<List<Event>> = _allEvents

    var image: Bitmap? = null
    var imageToken: String = "-1"

    var position = 0

    enum class TimerState {
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
    var latestDuration = 0L
    private var timerState = TimerState.Stopped
    private lateinit var startTime: String

    // initialization
    init {
        repository.loadUsers(_allEvents)
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
                Log.e("NIlu_TAG", "Hello World")
            }
        }, 3000)
    }

    // function for start the timer
    /**
     * function for start the timer
     * TA: don't need io.scope(UI thread) here
     * Prof: timer can be put in UI thread
     */
    fun hitTimer() {
        if (timerState == TimerState.Stopped) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            startTime = current.format(formatter)
        }
        // if the timer is currently not running, then run it
        if (timerState == TimerState.Stopped || timerState == TimerState.Paused) {
            timer = Timer() // this was in the --> init{} may not be correct
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    // increment time
                    timerLengthSeconds += 1
                    liveTime.postValue(getMinSec())
                }
            }, 10, 10)
            timerState = TimerState.Running
            return
        }

        // if the timer is currently running, we will pause it
        if (timerState == TimerState.Running) {
            timer.cancel()
            timerState = TimerState.Paused
            return
        }
    }

    /**
     * function for reset the timer
     */
    fun resetTimer(): Boolean {
        // if the timer is currently Paused or Running
        if (timerState == TimerState.Paused || timerState == TimerState.Running) {
            timer.cancel()
            latestDuration = timerLengthSeconds
            timerLengthSeconds = 0
            liveTime.postValue(getMinSec())
            timerState = TimerState.Stopped
            return true
        }
        startTime = "0"
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
    fun getMinSec(): LongArray {
        return longArrayOf(
            timerLengthSeconds / 3600,
            (timerLengthSeconds - (timerLengthSeconds / 3600) * 3600) / 60,
            timerLengthSeconds % 60
        )
    }

    /**
     * get the status of the timer
     */
    fun getState(): TimerState {
        return timerState
    }

    /**
     * get all the event from the list that sync from firebase
     */
    fun getAllEvent(): MutableLiveData<List<Event>> {
        return _allEvents
    }

    /**
     * get current time duration for event fragment, when a new
     * event just been recorded
     */
    fun getDuration(): String {
        var hour = latestDuration / 3600
        var min = (latestDuration - hour * 3600) / 60
        var sec = latestDuration % 60
        return "${
            if (hour.toInt() == 0) ""
            else if (hour.toInt() == 1) "1 hour "
            else "$hour hours "
        }${
            if (min.toString() == "0") "${sec} seconds"
            else if (min.toString().length == 2) "${min} minutes"
            else if (min.toString() == "1") "${min} minute"
            else "${min} minutes"
        }"
    }

    /**
     * return a long type that will be stored in firebase
     * eg. 4min3sec will be stored as 243
     */
    fun getRawTime(): Long {
        return latestDuration
    }

    /**
     * get the starting time for an event
     */
    fun getStart(): String {
        return startTime
    }

    /**
     * get the size of current eventlist
     */
    fun getSize(): Int? {
        return allEvents.value?.size
    }


    /**
     * this function will get current coordinate and address and
     * later on event fragment will use it
     */
    fun getAddress() {

    }

    fun deleteItem(fbIndex: Int) {
        repository.deleteEvent(fbIndex, _allEvents)
    }


}