package com.example.recipeorganizer.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: DatabaseReference,
    private val repository: Repository
) : ViewModel()  {

    private val _loggedin = MutableLiveData<Boolean>()
    val loggedin: LiveData<Boolean> = _loggedin
    private val _signedup = MutableLiveData<Boolean>()
    val signedup: LiveData<Boolean> = _signedup
    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username
    private val _age = MutableStateFlow<String?>(null)
    val age: StateFlow<String?> = _age
    private val _heightt = MutableStateFlow<String?>(null)
    val heightt: StateFlow<String?> = _heightt
    private val _weight = MutableStateFlow<String?>(null)
    val weight: StateFlow<String?> = _weight
    private val _cuisine = MutableStateFlow<String?>(null)
    val cuisine: StateFlow<String?> = _cuisine
    private val _email = MutableStateFlow<String?>(null)
    val email: StateFlow<String?> = _email
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        _loggedin.value = firebaseAuth.currentUser != null
    }

    fun signin(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loggedin.value = true
                    val userId = firebaseAuth.currentUser?.uid
                    if (userId != null) {
                        fetchUsername(userId)
                    }
                } else {
                    _errorMessage.value = "Wrong Email/Password"
                }
            }
    }

    fun signup(email: String, password: String, username: String, age: String, weight: String, height: String, cuisine: String) {
        repository.checkUsernameAvailability(username) { isAvailable ->
            if (isAvailable) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            repository.adduser(email, username, age, weight, height, cuisine)
                            _signedup.value = true
                            Log.d("Firebase", "Check")
                        } else {
                            _errorMessage.value = "Email Already in use"
                            Log.d("Firebase", "Failed")
                        }
                    }
            } else {
                _errorMessage.value = "Username Already in Use"
                Log.d("Firebase", "1Failed")
            }
        }
    }

    fun signout() {
        firebaseAuth.signOut()
        _loggedin.value = false
    }

    fun fetchUsername(userId: String) {
        database.child("FoodAppDB").child(userId).child("username")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    Log.d("Firebase Response:", "${snapshot.value}")
                    _username.value = snapshot.value.toString()
                }
            }
    }

    fun getuserid() : String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}