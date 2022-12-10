package com.example.yourtime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListFragment : Fragment(), RecyclerViewAdapter.OnItemClickListener {

    private lateinit var viewModel: TimeViewModel
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Events"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_list, container, false)

        // Navigate from list fragment to timer fragment
        val backButton = v.findViewById<ImageButton>(R.id.back_to_timer)
        backButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_listFragment_to_timerFragment)
        }
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userRecyclerView = view.findViewById(R.id.recycler_view)
        userRecyclerView.layoutManager = LinearLayoutManager(context)
        userRecyclerView.setHasFixedSize(true)
        adapter = RecyclerViewAdapter(this)
        userRecyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[TimeViewModel::class.java]
        viewModel.allEvents.observe(viewLifecycleOwner) {
            adapter.updateUserList(it)
        }
    }

    override fun onItemClick(user: Event, position: Int) {
        view?.findNavController()
            ?.navigate(R.id.action_listFragment_to_eventFragment, Bundle().apply {
                putInt("position", position)
            })
    }
}