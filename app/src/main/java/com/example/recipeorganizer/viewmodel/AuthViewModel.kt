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
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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
    private val _calorie = MutableStateFlow<String?>(null)
    val calorie: StateFlow<String?> = _calorie
    private val _selectedCuisine = MutableStateFlow<List<String?>>(emptyList())
    val selectedCuisine: StateFlow<List<String?>> = _selectedCuisine
    private val _loading = MutableLiveData<NetworkResponse<Boolean>>()
    val loading: LiveData<NetworkResponse<Boolean>> = _loading
    private val _errorMessage = MutableLiveData<String?>(null)
    private val _currentCal = MutableStateFlow<String?>(null)
    val currentCal: StateFlow<String?> = _currentCal
    private val _timeStamp = MutableStateFlow<String?>(null)
    val timeStamp: StateFlow<String?> = _timeStamp

    init {
        _loggedin.value = firebaseAuth.currentUser != null
        checkAndResetDailyCalories()
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
                            //_loggedin.value = true
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

    fun addDetails(calorie: Int, selectedCuisines: List<String>) {
        repository.addDetails(calories = calorie.toString(), selectedCuisines = selectedCuisines)
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

    fun fetchCuisines(userId: String) {
        database.child("FoodAppDB").child(userId).child("Cuisines")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val cuisines = snapshot.value as? List<*>
                    _selectedCuisine.value = cuisines?.filterIsInstance<String>() ?: emptyList()
                }
            }
    }

    fun fetchCalories(userId: String) {
        database.child("FoodAppDB").child(userId).child("Calories")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    _calorie.value = snapshot.value.toString()
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

    fun checkAndResetDailyCalories() {
        val userId = getuserid() ?: return

        database.child("FoodAppDB").child(userId).get()
            .addOnSuccessListener { snapshot ->
                val currentConsumed = snapshot.child("CurrentConsumed").getValue(Int::class.java) ?: 0
                val timestampLong = snapshot.child("TimeStamp").getValue(Long::class.java)

                if (timestampLong != null) {
                    val lastSavedDate = Instant.ofEpochMilli(timestampLong)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    val today = LocalDate.now()

                    if (lastSavedDate != today) {
                        // New day → Reset current calories
                        database.child("FoodAppDB").child(userId).child("CurrentConsumed").setValue(0)
                        database.child("FoodAppDB").child(userId).child("TimeStamp").setValue(ServerValue.TIMESTAMP)
                        _currentCal.value = "0"
                    } else {
                        // Same day → just keep the existing value
                        _currentCal.value = currentConsumed.toString()
                    }
                } else {
                    // If timestamp doesn't exist, initialize it
                    database.child("FoodAppDB").child(userId).child("TimeStamp").setValue(ServerValue.TIMESTAMP)
                    _currentCal.value = currentConsumed.toString()
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Failed to fetch daily calories data", it)
            }
    }

    fun updateConsumedCalories(caloriesToAdd: Int) {
        val userId = getuserid() ?: return

        val userRef = database.child("FoodAppDB").child(userId)

        userRef.get().addOnSuccessListener { snapshot ->
            val currentConsumed = snapshot.child("CurrentConsumed").getValue(Int::class.java) ?: 0
            val timestampLong = snapshot.child("TimeStamp").getValue(Long::class.java)

            val today = LocalDate.now()

            if (timestampLong != null) {
                val lastSavedDate = Instant.ofEpochMilli(timestampLong)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                if (lastSavedDate != today) {
                    // Not the same day → reset to new value
                    userRef.child("CurrentConsumed").setValue(caloriesToAdd)
                    userRef.child("TimeStamp").setValue(ServerValue.TIMESTAMP)
                    _currentCal.value = caloriesToAdd.toString()
                } else {
                    // Same day → add to current value
                    val newTotal = currentConsumed + caloriesToAdd
                    userRef.child("CurrentConsumed").setValue(newTotal)
                    userRef.child("TimeStamp").setValue(ServerValue.TIMESTAMP)
                    _currentCal.value = newTotal.toString()
                }
            } else {
                // No timestamp → assume fresh start
                userRef.child("CurrentConsumed").setValue(caloriesToAdd)
                userRef.child("TimeStamp").setValue(ServerValue.TIMESTAMP)
                _currentCal.value = caloriesToAdd.toString()
            }
        }.addOnFailureListener {
            Log.e("Firebase", "Failed to update consumed calories", it)
        }
    }

    fun listenForCurrentCalories() {
        val userId = getuserid() ?: return

        val userRef = database.child("FoodAppDB").child(userId).child("CurrentConsumed")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val current = snapshot.getValue(Int::class.java) ?: 0
                _currentCal.value = current.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to listen to calories", error.toException())
            }
        })
    }
}