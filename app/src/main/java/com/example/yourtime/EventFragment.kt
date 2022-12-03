package com.example.yourtime

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


/**
 * A simple [Fragment] subclass.
 * Use the [EventFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventFragment : Fragment() {

    private lateinit var viewModel: TimeViewModel
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_event, container, false)

        // Sign in to Firebase anonymously and get access to the database
        Firebase.auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FirebaseSignIn", "signInAnonymously:success")
            } else {
                Log.w("FirebaseSignIn", "signInAnonymously:failure", task.exception)
            }
        }

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference
        viewModel = ViewModelProvider(this).get(TimeViewModel::class.java)
    }
}