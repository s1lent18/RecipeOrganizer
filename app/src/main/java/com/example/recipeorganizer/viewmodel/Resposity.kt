package com.example.recipeorganizer.viewmodel

import com.example.recipeorganizer.models.dataprovider.Data
import com.example.recipeorganizer.models.dataprovider.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Repository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun adduser(email: String, username: String, age: String, weight: String, height: String) {
        val usersRef = database.child("FoodAppDB")
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            val users = Users(
                username = username,
                email = email,
                age = age,
                height = height,
                weight = weight,
            )

            usersRef.child(userId).setValue(users)
                .addOnSuccessListener {}
                .addOnFailureListener {}
        }
    }

    fun isSaved(id: String, callback: (Boolean) -> Unit) {
        val usersRef = database.child("FoodAppDB")
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val savedRef = usersRef.child(userId).child("saved").child(id)

            savedRef.get()
                .addOnSuccessListener { snapshot ->
                    callback(snapshot.exists())
                }
                .addOnFailureListener {
                    callback(false)
                }
        } else {
            callback(false)
        }
    }

    fun save(id: String, imageUrl: String, title: String) {
        val usersRef = database.child("FoodAppDB")
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val savedRef = usersRef.child(userId).child("saved")

            val data = Data(
                id = id,
                imageUrl = imageUrl,
                title = title
            )

            savedRef.child(id).setValue(data)
                .addOnSuccessListener {}
                .addOnFailureListener {}
        }
    }

    fun unsave(id: String) {
        val usersRef = database.child("FoodAppDB")
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val savedRef = usersRef.child(userId).child("saved")

            savedRef.child(id).removeValue()
                .addOnSuccessListener {}
                .addOnFailureListener {}
        }
    }

    fun checkUsernameAvailability(username: String, onResult: (Boolean) -> Unit) {
        database.child("FoodAppDB")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var isAvailable = true

                    for (userSnapshot in snapshot.children) {
                        val existingUsername = userSnapshot.child("username").getValue(String::class.java)
                        if (existingUsername == username) {
                            isAvailable = false
                            break
                        }
                    }

                    onResult(isAvailable)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(false) // Assume unavailable on error
                }
            })
    }

}