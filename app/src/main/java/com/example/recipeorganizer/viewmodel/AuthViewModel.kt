package com.example.recipeorganizer.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipeorganizer.models.dataprovider.Data
import com.example.recipeorganizer.models.response.NetworkResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
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
    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username
    private val _loading = MutableLiveData<NetworkResponse<Boolean>>()
    val loading: LiveData<NetworkResponse<Boolean>> = _loading
    private val _errorMessage = MutableLiveData<String?>(null)

    init {
        _loggedin.value = firebaseAuth.currentUser != null
    }

    fun signin(email: String, password: String) {
        _loading.value = NetworkResponse.Loading
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loading.value = NetworkResponse.Success(task.result.user != null)
                    _loggedin.value = true
                    val userId = firebaseAuth.currentUser?.uid
                    if (userId != null) {
                        fetchUsername(userId)
                    }
                } else {
                    _errorMessage.value = "Wrong Email/Password"
                    _loading.value = NetworkResponse.Failure("")
                }
            }
    }

    fun signup(email: String, password: String, username: String, age: String, weight: String, height: String) {
        _loading.value = NetworkResponse.Loading
        repository.checkUsernameAvailability(username) { isAvailable ->
            if (isAvailable) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _loggedin.value = true
                            _loading.value = NetworkResponse.Success(task.result.user != null)
                            repository.adduser(email, username, age, weight, height)
                            Log.d("Firebase", "Check")
                        } else {
                            _loading.value = NetworkResponse.Failure("")
                            _errorMessage.value = "Email Already in use"
                            Log.d("Firebase", "Failed")
                        }
                    }
            } else {
                _loading.value = NetworkResponse.Failure("")
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

    fun getSavedItems(callback: (List<Data>) -> Unit) {
        val usersRef = database.child("FoodAppDB")
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val savedRef = usersRef.child(userId).child("saved")

            savedRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val savedItems = mutableListOf<Data>()

                    for (child in snapshot.children) {
                        val id = child.key ?: ""
                        val imageUrl = child.child("imageUrl").getValue(String::class.java) ?: ""
                        val title = child.child("title").getValue(String::class.java) ?: ""

                        if (id.isNotEmpty() && imageUrl.isNotEmpty()) {
                            savedItems.add(Data(id, imageUrl, title))
                        }
                    }

                    callback(savedItems)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error fetching saved items", error.toException())
                }
            })
        }
    }
}