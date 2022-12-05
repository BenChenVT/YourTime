package com.example.yourtime

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton

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


        (view.findViewById(R.id.create_Button) as Button).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val success: Boolean = vm.resetTimer()
                if(!success){
                    Toast.makeText(requireActivity(), "test", Toast.LENGTH_SHORT)
                        .show()
                }
                startPauseButton.setImageResource(R.drawable.ic_play)
                view.findNavController().navigate(R.id.action_timerFragment_to_eventFragment, Bundle().apply {
                    putInt("position", -1)
                })
            }
        })


//        // cancel the last one lap
//        (view.findViewById(R.id.cancelLapButton) as Button).setOnClickListener(object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                // not implemented won't work
//            }
//        })
    }

}