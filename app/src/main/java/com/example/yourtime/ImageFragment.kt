package com.example.yourtime

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream

/**
 * A simple [Fragment] subclass.
 */
class ImageFragment : Fragment() {

    private lateinit var viewModel: TimeViewModel
    private var position = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Take photo"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_image, container, false)
        viewModel = ViewModelProvider(requireActivity())[TimeViewModel::class.java]
        // sign in anonymously
        Firebase.auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("TAG", "signInAnonymously:success")
            } else {
                Log.w("TAG", "signInAnonymously:failure", task.exception)
            }
        }

        position = arguments?.getInt("index") ?: 0

        view.findViewById<ImageView>(R.id.imageTaken).setImageBitmap(viewModel.image)

        uploadImage()

        view.findViewById<Button>(R.id.save).setOnClickListener {

            view.findNavController()
                .navigate(R.id.action_imageFragment_to_eventFragment, Bundle().apply {
                    putInt("position", position)
                })
        }

        return view
    }


    private fun uploadImage() {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${position}.jpg")

        val baos = ByteArrayOutputStream()
        viewModel.image?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
        // add delay to allow time for image to upload to firebase
        runBlocking {
            delay(1000)
        }

        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Log.d("ImageFragment", "Image uploaded to firebase")
            viewModel.imageToken = uri.toString()

            Log.d("ImageFragment", "Image token is ${viewModel.imageToken}")
        }.addOnFailureListener {
            // Handle any errors
        }
    }
}