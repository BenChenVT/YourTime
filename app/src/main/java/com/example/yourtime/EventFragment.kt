package com.example.yourtime

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class EventFragment : Fragment() {

    private lateinit var viewModel: TimeViewModel
    private lateinit var database: DatabaseReference

    private var note = "What have you done in this period of time"
    private var coordinates = "-1"
    private var duration = "-1"
    private var address = "-1"
    private var photo = "-1"
    private var start = "-1"
    private var title = "-1"

    private lateinit var spinner: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Preview"
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


    @SuppressLint("SetTextI18n", "QueryPermissionsNeeded")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference
        viewModel = ViewModelProvider(requireActivity())[TimeViewModel::class.java]
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

        val position = arguments?.getInt("position") ?: 0 // which will be an integer type
        viewModel.position = position
        if (position + 1 == (viewModel.allEvents.value?.size ?: -2)) {
            val deleteButton = view.findViewById<Button>(R.id.deleteButton)
            deleteButton.isEnabled = false
            deleteButton.isVisible = false
        }
        // when position is -1, meaning we need to create a new even, otherwise, user enter event from list fragment
        if (position == -1) {
            duration = viewModel.getRawTime().toString()
            start = viewModel.getStart()
            coordinates = viewModel.coordinates
            address = viewModel.address
            photo = viewModel.imageToken
            if (photo != "-1") {
                Picasso.get().load(viewModel.imageToken)
                    .into(view.findViewById<ImageView>(R.id.takePicture))
            }
            view.findViewById<TextView>(R.id.TimeText).text = "On ${viewModel.getStart()}\n" +
                    "You finish this event with time of ${viewModel.getDuration()}"
            view.findViewById<TextView>(R.id.LocationText).text = "You did this event at $address"
        } else {
            viewModel.getAllEvent().observe(viewLifecycleOwner) { eventList ->
                duration = eventList[position].duration.toString()
                start = eventList[position].start.toString()
                coordinates = eventList[position].coordinates.toString()
                address = eventList[position].address.toString()
                photo = if (viewModel.imageToken != "-1") {
                    viewModel.imageToken
                } else {
                    eventList[position].photo.toString()
                }
                note = eventList[position].note.toString()
                title = eventList[position].title.toString()
                when (title) {
                    "exercise" -> spinner.setSelection(1)
                    "game" -> spinner.setSelection(5)
                    "movie" -> spinner.setSelection(2)
                    "restaurant" -> spinner.setSelection(3)
                    "work" -> spinner.setSelection(4)
                    "other" -> spinner.setSelection(0)
                }
                val latestDuration = eventList[position].duration?.toIntOrNull()
                val hour = latestDuration?.div(3600)
                val hours = hour?.times(3600)
                val min = hours?.let { latestDuration.minus(it) }?.div(60)
                val sec = latestDuration?.rem(60)
                if (hour != null) {
                    duration =
                        "${
                            if (hour.toInt() == 0) ""
                            else if (hour.toInt() == 1) "1 hour "
                            else "$hour hours "
                        }${
                            if (min.toString() == "0") "$sec seconds"
                            else if (min.toString().length == 2) "$min minutes"
                            else if (min.toString() == "1") "$min minute"
                            else "$min minutes"
                        }"
                }
                val noteText = view.findViewById<TextView>(R.id.QuickNoteText)
                noteText.text = eventList[position].note
                view.findViewById<TextView>(R.id.TimeText).text =
                    "On ${eventList[position].start}\nYou were at${eventList[position].start}\n" +
                            "You finish this event with time of $duration" // this is wrong because duration is raw string
                view.findViewById<TextView>(R.id.LocationText).text =
                    "You did this event at ${eventList[position].address}"
                if (photo != "-1") {
                    Picasso.get().load(photo).into(view.findViewById<ImageView>(R.id.takePicture))
                }


            }
        }


        // when this button is clicked, we need to update an event to firebase anyway
        (view.findViewById(R.id.imageButtonBack) as ImageButton).setOnClickListener { v -> // here we will need to upload an event to firebase.

            // if position is -1  add a new data
            // else update a new data
            if (position == -1) {
                val size = viewModel.allEvents.value!!.size
                coordinates = viewModel.coordinates
                address = viewModel.address

                if (size == 0) {
                    val event =
                        Event("0", start, duration, note, coordinates, address, title, photo)
                    database.child("events").child("0").setValue(event)
                } else {
                    val index = viewModel.allEvents.value?.get(size - 1)?.index?.toInt()
                    val event = Event(
                        (index?.plus(1)).toString(),
                        start,
                        duration,
                        note,
                        coordinates,
                        address,
                        title,
                        photo
                    )
                    database.child("events").child((index?.plus(1)).toString()).setValue(event)
                }
            } else {
                val eventIndex = viewModel.allEvents.value?.get(position)?.index
                database.child("events").child(eventIndex.toString()).child("note")
                    .setValue(note)
                database.child("events").child(eventIndex.toString()).child("title")
                    .setValue(title)
                database.child("events").child(eventIndex.toString()).child("photo")
                    .setValue(photo)
            }
            viewModel.imageToken = "-1"
            v?.findNavController()?.navigate(R.id.action_eventFragment_to_listFragment)
        }


        (view.findViewById(R.id.deleteButton) as Button).setOnClickListener { v ->
            if (position == -1) {
                viewModel.imageToken = "-1"
                v?.findNavController()?.navigate(R.id.action_eventFragment_to_timerFragment)
            } else {
                val fbIndex = viewModel.allEvents.value?.get(position)?.index
                if (fbIndex != null) {
                    viewModel.deleteItem(fbIndex.toInt())
                }
                viewModel.imageToken = "-1"
                v?.findNavController()?.navigate(R.id.action_eventFragment_to_timerFragment)
            }
        }


        // when user modified the note, we need to update
        (view.findViewById(R.id.saveChangeButton) as ImageButton).setOnClickListener {
            val noteText = view.findViewById<TextView>(R.id.QuickNoteText)
            noteText.text = view.findViewById<EditText>(R.id.editTextQucikNote).text
            note = noteText.text.toString()
        }

        (view.findViewById(R.id.imageButtonTimer) as ImageButton).setOnClickListener { v ->
            viewModel.imageToken = "-1"
            v?.findNavController()?.navigate(R.id.action_eventFragment_to_timerFragment)
        }

        (view.findViewById(R.id.takePicture) as ImageView).setOnClickListener {
            // check storage permission
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    100
                )
            }

            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (activity?.packageManager?.let { it1 -> takePictureIntent.resolveActivity(it1) } != null) {
                startActivityForResult(takePictureIntent, 1)
            }
            Handler().postDelayed({
                view.findNavController()
                    .navigate(R.id.action_eventFragment_to_imageFragment, Bundle().apply {
                        putInt("index", position)
                    })
            }, 3000)
        }



        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                v: View?,
                position: Int,
                id: Long
            ) {
                val selected = parent?.getItemAtPosition(position).toString()
                val imageTitle = view.findViewById<ImageView>(R.id.imageView)
                when (selected) {
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            viewModel.image = data?.extras?.get("data") as Bitmap
            view?.let {
                Navigation.findNavController(it)
                    .navigate(R.id.action_eventFragment_to_imageFragment, Bundle().apply {
                        putInt("index", viewModel.position)
                    })
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}