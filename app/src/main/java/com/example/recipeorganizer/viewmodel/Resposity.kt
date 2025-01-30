package com.example.recipeorganizer.viewmodel

import com.example.recipeorganizer.models.dataprovider.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Repository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun adduser(email: String, username: String) {
        val usersRef = database.child("FoodAppDB")
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            val user = Users(
                username = username,
                email = email
            )

            usersRef.child(userId).setValue(user)
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