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

    fun adduser(email: String, username: String, age: String, weight: String, height: String, cuisine: String) {
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
                cuisine = cuisine
            )

            usersRef.child(userId).setValue(users)
                .addOnSuccessListener {}
                .addOnFailureListener {}
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
        database.child("TasteBudsDB").orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onResult(!snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(false)
                }
            })
    }
}