package com.example.yourtime

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [TimerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimerFragment : Fragment() {

    private lateinit var reportButton: Button
    private lateinit var eventListButton: Button
    private lateinit var startPauseButton: FloatingActionButton
    private lateinit var finishButton: FloatingActionButton
    private lateinit var timeText: TextView
    private lateinit var vm: TimeViewModel

    private var coroutineJob: Job? = null
    var latlong: Location? = null
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var addresses: List<Address> = emptyList()

    private var address : String = ""
    private var coordinates : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "YourTime"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        vm = ViewModelProvider(requireActivity()).get(TimeViewModel::class.java)
        val v: View = inflater.inflate(R.layout.fragment_timer, container, false)
        reportButton = v.findViewById(R.id.ReportButton)
        eventListButton = v.findViewById(R.id.EventListButton)
        startPauseButton = v.findViewById(R.id.PlayStop_Button)
        finishButton = v.findViewById(R.id.Finish_Button)
        timeText = v.findViewById(R.id.TimeText)


        var progress_count = v.findViewById<me.zhanghai.android.materialprogressbar.MaterialProgressBar>(R.id.progress_count)
        progress_count.max = 60

        //这里liveTime[0]和liveTime[1]是指分钟和秒后期应该加上小时需要在viewModel里面改
//        val minute: TextView = v.findViewById(R.id.TimeText) as TextView
//        (v.findViewById(R.id.TimeText) as TextView).text = "hello"
        vm.getLiveTime().observe(viewLifecycleOwner, Observer { liveTime ->
            var hour = liveTime[0].toString()
            var min = liveTime[1].toString()
            var sec = liveTime[2].toString()
            (v.findViewById(R.id.TimeText) as TextView).text = "$hour:${
                if(min.length == 2)min
                else "0$min"
            }:${
                if(sec.length == 2)sec
                else "0$sec"
            }"

            progress_count.progress = sec.toInt()


            System.out.println("$min:${
                if(sec.length == 2)sec
                else "0$sec"
            }")
        })
        return v
    }


    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)


        // timerButton is the start and stop button
        (view.findViewById(R.id.EventListButton) as Button).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                v?.findNavController()?.navigate(R.id.action_timerFragment_to_listFragment)
            }
        })


        (view.findViewById(R.id.ReportButton) as Button).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                v?.findNavController()?.navigate(R.id.action_timerFragment_to_reportFragment)
            }
        })

        (view.findViewById(R.id.create_Button) as Button).isEnabled = false
        // go to lap time list fragment
        (view.findViewById(R.id.PlayStop_Button) as FloatingActionButton).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                vm.hitTimer()
                // update button text after the state has been changed
                if(vm.getState() == TimeViewModel.TimerState.Running){
                    startPauseButton.setImageResource(R.drawable.ic_pause)
                }
                if(vm.getState() == TimeViewModel.TimerState.Stopped ||
                    vm.getState() == TimeViewModel.TimerState.Paused){
                    startPauseButton.setImageResource(R.drawable.ic_play)
                }
                when(vm.getState()){
                    TimeViewModel.TimerState.Stopped -> (view.findViewById(R.id.create_Button) as Button).isEnabled = false
                    TimeViewModel.TimerState.Running -> (view.findViewById(R.id.create_Button) as Button).isEnabled = true
                    TimeViewModel.TimerState.Paused-> (view.findViewById(R.id.create_Button) as Button).isEnabled = true
                }
            }
        })


        // take down a lap time
        (view.findViewById(R.id.Finish_Button) as FloatingActionButton).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val success: Boolean = vm.resetTimer()
                if(!success){
                    Toast.makeText(requireActivity(), "test", Toast.LENGTH_SHORT)
                        .show()
                }
                startPauseButton.setImageResource(R.drawable.ic_play)
                when(vm.getState()){
                    TimeViewModel.TimerState.Stopped -> (view.findViewById(R.id.create_Button) as Button).isEnabled = false
                    TimeViewModel.TimerState.Running -> (view.findViewById(R.id.create_Button) as Button).isEnabled = true
                    TimeViewModel.TimerState.Paused-> (view.findViewById(R.id.create_Button) as Button).isEnabled = true
                }
            }
        })


        (view.findViewById(R.id.create_Button) as Button).setOnClickListener {
            val success: Boolean = vm.resetTimer()
            if (!success) {
                Toast.makeText(requireActivity(), "test", Toast.LENGTH_SHORT)
                    .show()
            }
            startPauseButton.setImageResource(R.drawable.ic_play)

            geocoder = activity?.let { it1 -> Geocoder(it1, Locale.getDefault()) }!!
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    for (location in p0.locations) {
                        vm.coordinates = "${latlong?.latitude}, ${latlong?.longitude}"
                    }
                }
            }
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                // request location access
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    1
                )
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )

            // get the last location identified. Also, set a listener that updates the
            // R.id.coordinates text when the location is found (on Success)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let { latlong = it }
                    vm.coordinates = "${latlong?.latitude}, ${latlong?.longitude}"
                    latlong?.let { printAddressForLocation(it) }
                }

            // add 2 seconds delay
            Handler().postDelayed({
                Log.d("Location", "Location: ${vm.coordinates}")
                Log.d("Address", "Address: ${vm.address}")
                // navigate to the create event fragment
                view.findNavController().navigate(R.id.action_timerFragment_to_eventFragment)
            }, 2000)
        }
    }

    private val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
        interval = 1000
        fastestInterval = 500
        priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun getAddress(location: Location): String {
        //adapted from https://developer.android.com/training/location/display-address.html
        //the geocoder can convert a lat/long location to an address
        //We get the location from the fusedLocationClient instance
        try {
            addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                // In this sample, we get just a single address.
                1
            ) as List<Address>
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            return "Error: Service Not Available --$ioException"

        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            return "Error: Invalid lat long used--$illegalArgumentException"
        }

        if (addresses.isEmpty())
            return "No address found :("

        return addresses[0].getAddressLine(0)
    }

    //Used to place the address value into a list.
    private fun printAddressForLocation(location: Location) {
        //If the last job is still running, kill it
        coroutineJob?.cancel()

        //create a new coroutine on the IO thread, and call getAddress on it.
        coroutineJob = CoroutineScope(Dispatchers.IO).launch {
            val addressDeferred = async {
                getAddress(location)
            }
            //Wait until the call to getAddress is complete
            val result = addressDeferred.await()
            withContext(Dispatchers.Main) {
                vm.address = result
            }
        }
    }

}