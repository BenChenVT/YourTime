package com.example.yourtime

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


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
    private var title = "-1"

    private lateinit var spinner: Spinner


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
        spinner = view.findViewById(R.id.spinnerTitle)
        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.category,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

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
                title = eventList[position].title.toString()
                when (title){
                    "exercise" -> spinner.setSelection(1)
                    "game" -> spinner.setSelection(5)
                    "movie" -> spinner.setSelection(2)
                    "restaurant" -> spinner.setSelection(3)
                    "work" -> spinner.setSelection(4)
                    "other" -> spinner.setSelection(0)
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

                // if position is -1  add a new data
                // else update a new data
                if(position == -1){

//                    val data = FirebaseData(long, lat, address, time)
//                    val data = OneLocationData(address, lat, long, time)
//                    database.child("locations").child(localID.toString()).child("address").setValue(data.address)
//                    database.child("locations").child(localID.toString()).child("lat").setValue(data.lat)
//                    database.child("locations").child(localID.toString()).child("long").setValue(data.long)
//                    database.child("locations").child(localID.toString()).child("time").setValue(data.time)

                }
                else{
                    // todo: change a existing event at position in allevent list
                }



            }
        })

        // when user modified the note, we need to update
        (view.findViewById(R.id.saveChangeButton) as ImageButton).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val noteText = view.findViewById<TextView>(R.id.QuickNoteText)
                noteText.text = view.findViewById<EditText>(R.id.editTextQucikNote).text
                note = noteText.text.toString()
            }
        })

        (view.findViewById(R.id.imageButtonTimer) as ImageButton).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                v?.findNavController()?.navigate(R.id.action_eventFragment_to_timerFragment)
            }
        })



        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                val selected = parent?.getItemAtPosition(position).toString();
                var imageTitle = view.findViewById<ImageView>(R.id.imageView)
                when (selected){
                    "game" -> imageTitle.setImageResource(R.drawable.game)
                    "exercise" -> imageTitle.setImageResource(R.drawable.exercise)
                    "movie" -> imageTitle.setImageResource(R.drawable.movie)
                    "restaurant" -> imageTitle.setImageResource(R.drawable.restaurant)
                    "work" -> imageTitle.setImageResource(R.drawable.work)
                    "other" -> imageTitle.setImageResource(R.drawable.other)
                }
                title = selected
            }

        }
    }
}