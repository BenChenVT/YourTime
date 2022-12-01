package com.example.yourtime

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListFragment : Fragment(), RecyclerViewAdapter.OnItemClickListener {

    private lateinit var viewModel: TimeViewModel
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userRecyclerView = view.findViewById(R.id.recycler_view)
        userRecyclerView.layoutManager = LinearLayoutManager(context)
        userRecyclerView.setHasFixedSize(true)
        adapter = RecyclerViewAdapter(this)
        userRecyclerView.adapter = adapter

        viewModel = ViewModelProvider(this).get(TimeViewModel::class.java)
        viewModel.allEvents.observe(viewLifecycleOwner) {
            adapter.updateUserList(it)
        }
    }

    override fun onItemClick(user: Event, position: Int) {
        TODO("Not yet implemented")
    }
}