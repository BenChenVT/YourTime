package com.example.yourtime

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * A simple [Fragment] subclass.
 * Use the [EventFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventFragment : Fragment() {

    private lateinit var viewModel: TimeViewModel
    private lateinit var database: DatabaseReference

    private var note = "-1"
    private lateinit var coordinates:String
    private var duration = "-1"
    private lateinit var address: String
    private lateinit var photo: String
    private var start = "-1"
    private lateinit var title: String


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


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference
        viewModel = ViewModelProvider(requireActivity()).get(TimeViewModel::class.java)

//        var decription = view.findViewById<EditText>(R.id.editTextQucikNote)

        val position = arguments?.getInt("position")?:0 // which will be an integer type

        // when position is -1, meaning we need to create a new even, otherwise, user enter event from list fragment
        if(position == -1){
            duration = viewModel.getRawTime().toString()
            start = viewModel.getStart()
            // todo: get address and location in viewModel
            view.findViewById<TextView>(R.id.TimeText).text = "On ${viewModel.getStart()}\nYou were at [location placeholder]\nYou finish this event with time of ${viewModel.getDuration()}"
        }
        else{
            viewModel.getAllEvent().observe(viewLifecycleOwner, Observer { eventList ->
                when (eventList[position].title) {
                    "work" -> view.findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.work)
                    "exercise" -> view.findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.exercise)
                    else -> { // Note the block
                        print("x is neither 1 nor 2")
                    }
                }
                view.findViewById<TextView>(R.id.QuickNoteText).text = eventList[position].note
                view.findViewById<TextView>(R.id.TimeText).text = "On ${eventList[position].start}\nYou were at${eventList[position].start}\n" +
                        "You finish this event with time of${eventList[position].duration}" // this is wrong because duration is raw string
                view.findViewById<TextView>(R.id.LocationText).text = "You did this event at ${eventList[position].address}"
            })
        }


        // when this button is clicked, we need to update an event to firebase anyway
        (view.findViewById(R.id.imageButtonBack) as ImageButton).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                v?.findNavController()?.navigate(R.id.action_eventFragment_to_listFragment)
                // here we will need to upload an event to firebase.


                // starting time

                // if position is -1  add a new data
                // else update a new data
                if(position == -1){
                    // todo: add a new data to the firebase
                }
                else{
                    // todo: change a existing event at position in allevent list
                }



            }
        })

        (view.findViewById(R.id.imageButtonTimer) as ImageButton).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                v?.findNavController()?.navigate(R.id.action_eventFragment_to_timerFragment)
            }
        })

        // when user modified the note, we need to update
        (view.findViewById(R.id.saveChangeButton) as ImageButton).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                view.findViewById<TextView>(R.id.QuickNoteText).text = view.findViewById<EditText>(R.id.editTextQucikNote).text
                note = view.findViewById<TextView>(R.id.QuickNoteText).text.toString()
            }
        })
    }
}