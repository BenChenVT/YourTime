package com.example.yourtime

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*

class UserRepository {
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("events")

    @Volatile
    private var INSTANCE: UserRepository? = null

    fun getInstance(): UserRepository {
        return INSTANCE ?: synchronized(this) {
            val instance = UserRepository()
            INSTANCE = instance
            instance
        }
    }

    fun loadUsers(userList: MutableLiveData<List<Event>>) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val _userList: List<Event> = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(Event::class.java)!!
                    }
                    userList.postValue(_userList)
                } catch (_: Exception) {
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}