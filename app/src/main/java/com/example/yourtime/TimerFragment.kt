package com.example.yourtime

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TimerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimerFragment : Fragment() {

    private lateinit var reportButton: Button
    private lateinit var eventListButton: Button
    private lateinit var startPauseButton: Button
    private lateinit var finishButton: Button
    private lateinit var vm: TimeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_timer, container, false)
        reportButton = view.findViewById(R.id.ReportButton)
        eventListButton = view.findViewById(R.id.EventListButton)
        startPauseButton = view.findViewById(R.id.PlayStop_Button)
        finishButton = view.findViewById(R.id.Finish_Button)



        vm = ViewModelProvider(requireActivity()).get(TimeViewModel::class.java)

        val v: View = inflater.inflate(R.layout.fragment_timer, container, false)

        //这里liveTime[0]和liveTime[1]是指分钟和秒后期应该加上小时需要在viewModel里面改
        // 两个string应该连上显示在timeText上  这里用plus不知道行不行
        val minute: TextView = v.findViewById(R.id.TimeText) as TextView
        vm.getLiveTime().observe(viewLifecycleOwner, Observer { liveTime ->
            minute.findViewById<TextView>(R.id.TimeText).text = liveTime[0].toString().plus(liveTime[1].toString())
        })



        return v
    }


    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        // timerButton is the start and stop button
        eventListButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // TODO: go to Event list page
                v?.findNavController()?.navigate(R.id.action_timerFragment_to_listFragment)
            }
        })


        reportButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // TODO: go to report page
                v?.findNavController()?.navigate(R.id.action_timerFragment_to_reportFragment)
            }
        })

        // go to lap time list fragment
        startPauseButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                vm.hitTimer()
                // update button text after the state has been changed
                if(vm.getState() == TimeViewModel.TimerState.Running){
                    //TODO: 这里要切换按钮上play和stop的图案
//                    val switchButton: Button = v?.findViewById(R.id.timerButton) as Button
//                    switchButton.text = "STOP"
                }
                if(vm.getState() == TimeViewModel.TimerState.Stopped ||
                    vm.getState() == TimeViewModel.TimerState.Paused){
                      //TODO: 这里要切换按钮上play和stop的图案
//                    val switchButton: Button = v?.findViewById(R.id.timerButton) as Button
//                    switchButton.text = "START"
                }
            }
        })


        // take down a lap time
        finishButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //TODO: 小方块按钮 当这个按下时应该跳出来弹窗 输入event title 和 description

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