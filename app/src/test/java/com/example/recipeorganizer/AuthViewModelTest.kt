package com.example.recipeorganizer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.recipeorganizer.models.dataprovider.Data
import com.example.recipeorganizer.viewmodel.Repository
import com.example.recipeorganizer.viewmodel.AuthViewModel
import com.example.recipeorganizer.models.response.NetworkResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.Mockito.*
import org.mockito.Mockito.anyString
import org.mockito.kotlin.any



@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var repository: Repository
    private lateinit var authViewModel: AuthViewModel
    private lateinit var mockUser: FirebaseUser
    private lateinit var mockAuthResult: AuthResult
    private lateinit var mockDataSnapshot: DataSnapshot
    private lateinit var loadingObserver: Observer<NetworkResponse<Boolean>>
    private lateinit var authTask: Task<AuthResult>
    private lateinit var dataTask: Task<DataSnapshot>


    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        firebaseAuth = mock(FirebaseAuth::class.java)
        databaseReference = mock(DatabaseReference::class.java)
        repository = mock(Repository::class.java)
        mockUser = mock(FirebaseUser::class.java)
        mockAuthResult = mock(AuthResult::class.java)
        mockDataSnapshot = mock(DataSnapshot::class.java)
        loadingObserver = mock(Observer::class.java) as Observer<NetworkResponse<Boolean>>

        authTask = mock(Task::class.java) as Task<AuthResult>
        dataTask = mock(Task::class.java) as Task<DataSnapshot>

        configureMockTask(authTask, mockAuthResult)
        configureMockTask(dataTask, mockDataSnapshot)

        `when`(mockUser.uid).thenReturn("test-user-id")

        val childReference = mock(DatabaseReference::class.java)
        val userIdReference = mock(DatabaseReference::class.java)
        val dataReference = mock(DatabaseReference::class.java)

        `when`(databaseReference.child("FoodAppDB")).thenReturn(childReference)
        `when`(childReference.child(anyString())).thenReturn(userIdReference)
        `when`(userIdReference.child(anyString())).thenReturn(dataReference)
        `when`(dataReference.get()).thenReturn(dataTask)

        `when`(authTask.isSuccessful).thenReturn(true)
        `when`(authTask.result).thenReturn(mockAuthResult)
        `when`(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(authTask)

        authViewModel = AuthViewModel(firebaseAuth, databaseReference, repository)
        authViewModel.loading.observeForever(loadingObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        authViewModel.loading.removeObserver(loadingObserver)
    }



    @Test
    fun `init sets loggedin to true when current user is not null`() {
        `when`(firebaseAuth.currentUser).thenReturn(mockUser)

        val viewModel = AuthViewModel(firebaseAuth, databaseReference, repository)

        assertTrue(viewModel.loggedin.value!!)
    }

    @Test
    fun `init sets loggedin to false when current user is null`() {
        `when`(firebaseAuth.currentUser).thenReturn(null)

        val viewModel = AuthViewModel(firebaseAuth, databaseReference, repository)

        assertFalse(viewModel.loggedin.value!!)
    }

    @Test
    fun `signin updates state correctly on success`() {
        val email = "test@example.com"
        val password = "password123"

        val mockGetTask = mock(Task::class.java) as Task<DataSnapshot>
        val mockDataSnap = mock(DataSnapshot::class.java)
        configureMockTask(mockGetTask, mockDataSnap)

        `when`(firebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(authTask)
        `when`(mockAuthResult.user).thenReturn(mockUser)
        `when`(firebaseAuth.currentUser).thenReturn(mockUser)

        val mockChildRef = mock(DatabaseReference::class.java)
        val mockUserRef = mock(DatabaseReference::class.java)
        val mockUsernameRef = mock(DatabaseReference::class.java)

        `when`(databaseReference.child("FoodAppDB")).thenReturn(mockChildRef)
        `when`(mockChildRef.child("test-user-id")).thenReturn(mockUserRef)
        `when`(mockUserRef.child("username")).thenReturn(mockUsernameRef)
        `when`(mockUsernameRef.get()).thenReturn(mockGetTask)

        doAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(authTask)
            authTask
        }.`when`(authTask).addOnCompleteListener(any())

        authViewModel.signin(email, password)

        assertTrue(authViewModel.loggedin.value!!)
        verify(loadingObserver).onChanged(NetworkResponse.Loading)
        verify(loadingObserver).onChanged(NetworkResponse.Success(true))
        verify(firebaseAuth).signInWithEmailAndPassword(email, password)
    }


    @Test
    fun `signin updates state correctly on failure`() {
        val email = "test@example.com"
        val password = "wrong-password"

        `when`(firebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(authTask)
        `when`(authTask.isSuccessful).thenReturn(false)

        doAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(authTask)
            null
        }.`when`(authTask).addOnCompleteListener(any())

        authViewModel.signin(email, password)

        assertFalse(authViewModel.loggedin.value!!)
        verify(loadingObserver).onChanged(NetworkResponse.Loading)
        verify(loadingObserver).onChanged(NetworkResponse.Failure(""))
    }
    @Test
    fun `signup calls repository for username availability check`() = runTest {
        val email = "new@example.com"
        val password = "password123"
        val username = "newuser"
        val age = "25"
        val weight = "70"
        val height = "175"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean) -> Unit>(1)
            callback(true) // Username is available
            null
        }.`when`(repository).checkUsernameAvailability(
            anyString(),
            any()
        )

        `when`(firebaseAuth.createUserWithEmailAndPassword(
            anyString(),
            anyString()
        )).thenReturn(authTask)

        `when`(authTask.isSuccessful).thenReturn(true)
        `when`(authTask.result).thenReturn(mockAuthResult)
        `when`(mockAuthResult.user).thenReturn(mockUser)
        `when`(mockUser.uid).thenReturn("test-user-id")

        doAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(authTask)
            authTask
        }.`when`(authTask).addOnCompleteListener(Mockito.any())

        authViewModel.signup(email, password, username, age, weight, height)

        testDispatcher.scheduler.advanceUntilIdle()

        verify(repository).checkUsernameAvailability(
            anyString(),
            any()
        )

        verify(firebaseAuth).createUserWithEmailAndPassword(
            anyString(),
            anyString()
        )

        verify(repository).adduser(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString()
        )

        verify(loadingObserver).onChanged(NetworkResponse.Loading)
        verify(loadingObserver).onChanged(NetworkResponse.Success(true))
    }
    @Test
    fun `signup handles username already in use`() {
        val email = "new@example.com"
        val password = "password123"
        val username = "existinguser"
        val age = "25"
        val weight = "70"
        val height = "175"

        doAnswer { invocation ->
            val usernameArg = invocation.getArgument<String>(0)
            val callback = invocation.getArgument<(Boolean) -> Unit>(1)

            if (usernameArg == username) {
                callback(false) // Username is not available
            }
            null
        }.`when`(repository).checkUsernameAvailability(
            anyString(),
            any()
        )

        authViewModel.signup(email, password, username, age, weight, height)

        verify(firebaseAuth, never()).createUserWithEmailAndPassword(anyString(), anyString())
        verify(repository, never()).adduser(anyString(), anyString(), anyString(),
            anyString(), anyString())
        verify(loadingObserver).onChanged(NetworkResponse.Loading)
        verify(loadingObserver).onChanged(NetworkResponse.Failure(""))
    }
    @Test
    fun `signup handles firebase auth failure`() {
        val email = "new@example.com"
        val password = "password123"
        val username = "newuser"
        val age = "25"
        val weight = "70"
        val height = "175"

        doAnswer { invocation ->
            val usernameArg = invocation.getArgument<String>(0)
            val callback = invocation.getArgument<(Boolean) -> Unit>(1)

            if (usernameArg == username) {
                callback(true)
            }
            null
        }.`when`(repository).checkUsernameAvailability(
            anyString(),
            any()
        )

        val failingAuthTask: Task<AuthResult> = mock()
        `when`(failingAuthTask.isSuccessful).thenReturn(false)
        `when`(failingAuthTask.isComplete).thenReturn(true)

        `when`(firebaseAuth.createUserWithEmailAndPassword(
            anyString(),
            anyString()
        )).thenReturn(failingAuthTask)

        doAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(failingAuthTask)
            failingAuthTask
        }.`when`(failingAuthTask).addOnCompleteListener(any())

        authViewModel.signup(email, password, username, age, weight, height)

        verify(repository).checkUsernameAvailability(
            anyString(),
            any()
        )
        verify(firebaseAuth).createUserWithEmailAndPassword(
            anyString(),
            anyString()
        )
        verify(repository, never()).adduser(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString()
        )
        verify(loadingObserver).onChanged(NetworkResponse.Loading)
        verify(loadingObserver).onChanged(NetworkResponse.Failure(""))
    }
    @Test
    fun `addDetails calls repository with correct parameters`() {
        val calorie = 2000
        val selectedCuisines = listOf("Italian", "Mexican", "Indian")

        authViewModel.addDetails(calorie, selectedCuisines)

        verify(repository).addDetails(calorie.toString(), selectedCuisines)
    }

    @Test
    fun `signout updates loggedin state to false`() {
        `when`(firebaseAuth.currentUser).thenReturn(mockUser)
        authViewModel = AuthViewModel(firebaseAuth, databaseReference, repository)
        assertTrue(authViewModel.loggedin.value!!)


        authViewModel.signout()

        verify(firebaseAuth).signOut()
        assertFalse(authViewModel.loggedin.value!!)
    }

    @Test
    fun `fetchUsername updates username state when data exists`() = runTest {

        val userId = "test-user-id"
        val username = "testuser"
        val mockSnapshot = mock(DataSnapshot::class.java)

        val mockChildRef = mock(DatabaseReference::class.java)
        val mockUserRef = mock(DatabaseReference::class.java)
        val mockUsernameRef = mock(DatabaseReference::class.java)

        `when`(databaseReference.child("FoodAppDB")).thenReturn(mockChildRef)
        `when`(mockChildRef.child(userId)).thenReturn(mockUserRef)
        `when`(mockUserRef.child("username")).thenReturn(mockUsernameRef)

        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.value).thenReturn(username)

        val mockTask = mock(Task::class.java) as Task<DataSnapshot>
        `when`(mockUsernameRef.get()).thenReturn(mockTask)

        `when`(mockTask.isSuccessful).thenReturn(true)
        `when`(mockTask.result).thenReturn(mockSnapshot)
        `when`(mockTask.isComplete).thenReturn(true)

        doAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<DataSnapshot>>(0)
            listener.onComplete(mockTask)
            mockTask
        }.`when`(mockTask).addOnCompleteListener(any())

        doAnswer { invocation ->
            val listener = invocation.getArgument<OnSuccessListener<DataSnapshot>>(0)
            listener.onSuccess(mockSnapshot)
            mockTask
        }.`when`(mockTask).addOnSuccessListener(any())

        authViewModel.fetchUsername(userId)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(username, authViewModel.username.value)
    }
    @Test
    fun `fetchCuisines updates selectedCuisine state when data exists`() = runTest {
        val userId = "test-user-id"
        val cuisines = listOf("Italian", "Mexican", "Indian")
        val mockSnapshot = mock(DataSnapshot::class.java)

        val mockTask = mock(Task::class.java) as Task<DataSnapshot>

        configureMockTask(mockTask, mockSnapshot)

        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.value).thenReturn(cuisines)

        val mockChildRef = mock(DatabaseReference::class.java)
        val mockUserRef = mock(DatabaseReference::class.java)
        val mockCuisinesRef = mock(DatabaseReference::class.java)

        `when`(databaseReference.child("FoodAppDB")).thenReturn(mockChildRef)
        `when`(mockChildRef.child(userId)).thenReturn(mockUserRef)
        `when`(mockUserRef.child("Cuisines")).thenReturn(mockCuisinesRef)
        `when`(mockCuisinesRef.get()).thenReturn(mockTask)

        authViewModel.fetchCuisines(userId)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(cuisines, authViewModel.selectedCuisine.value)
    }

    @Test
    fun `fetchCalories updates calorie state when data exists`() = runTest {
        val userId = "test-user-id"
        val calories = "2000"
        val mockSnapshot = mock(DataSnapshot::class.java)

        val mockTask = mock(Task::class.java) as Task<DataSnapshot>

        configureMockTask(mockTask, mockSnapshot)

        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.value).thenReturn(calories)

        val mockChildRef = mock(DatabaseReference::class.java)
        val mockUserRef = mock(DatabaseReference::class.java)
        val mockCaloriesRef = mock(DatabaseReference::class.java)

        `when`(databaseReference.child("FoodAppDB")).thenReturn(mockChildRef)
        `when`(mockChildRef.child(userId)).thenReturn(mockUserRef)
        `when`(mockUserRef.child("Calories")).thenReturn(mockCaloriesRef)
        `when`(mockCaloriesRef.get()).thenReturn(mockTask)

        authViewModel.fetchCalories(userId)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(calories, authViewModel.calorie.value)
    }

    @Test
    fun `getuserid returns current user id`() {
        val staticFirebaseAuth = mockStatic(FirebaseAuth::class.java)
        val mockFirebaseAuth = mock(FirebaseAuth::class.java)
        staticFirebaseAuth.`when`<FirebaseAuth> { FirebaseAuth.getInstance() }.thenReturn(mockFirebaseAuth)
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.uid).thenReturn("test-user-id")

        val result = authViewModel.getuserid()

        assertEquals("test-user-id", result)

        staticFirebaseAuth.close()
    }

    @Test
    fun `getuserid returns null when user is not logged in`() {
        val staticFirebaseAuth = mockStatic(FirebaseAuth::class.java)
        val mockFirebaseAuth = mock(FirebaseAuth::class.java)
        staticFirebaseAuth.`when`<FirebaseAuth> { FirebaseAuth.getInstance() }.thenReturn(mockFirebaseAuth)
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)

        val result = authViewModel.getuserid()

        assertNull(result)

        staticFirebaseAuth.close()
    }

    @Test
    fun `getSavedItems fetches and returns saved items correctly`() {
        val staticFirebaseAuth = mockStatic(FirebaseAuth::class.java)
        val mockFirebaseAuth = mock(FirebaseAuth::class.java)
        staticFirebaseAuth.`when`<FirebaseAuth> { FirebaseAuth.getInstance() }.thenReturn(mockFirebaseAuth)
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.uid).thenReturn("test-user-id")

        val mockFoodAppDBRef = mock(DatabaseReference::class.java)
        val mockUserIdRef = mock(DatabaseReference::class.java)
        val mockSavedRef = mock(DatabaseReference::class.java)
        val mockSnapshot = mock(DataSnapshot::class.java)

        `when`(databaseReference.child("FoodAppDB")).thenReturn(mockFoodAppDBRef)
        `when`(mockFoodAppDBRef.child("test-user-id")).thenReturn(mockUserIdRef)
        `when`(mockUserIdRef.child("saved")).thenReturn(mockSavedRef)

        val item1Snapshot = mock(DataSnapshot::class.java)
        val item2Snapshot = mock(DataSnapshot::class.java)
        val mockChildren = listOf(item1Snapshot, item2Snapshot)

        `when`(mockSnapshot.children).thenReturn(mockChildren)

        `when`(item1Snapshot.key).thenReturn("123")
        val item1ImageSnapshot = mock(DataSnapshot::class.java)
        val item1TitleSnapshot = mock(DataSnapshot::class.java)
        `when`(item1Snapshot.child("imageUrl")).thenReturn(item1ImageSnapshot)
        `when`(item1Snapshot.child("title")).thenReturn(item1TitleSnapshot)
        `when`(item1ImageSnapshot.getValue(String::class.java)).thenReturn("image1.jpg")
        `when`(item1TitleSnapshot.getValue(String::class.java)).thenReturn("Recipe 1")

        `when`(item2Snapshot.key).thenReturn("456")
        val item2ImageSnapshot = mock(DataSnapshot::class.java)
        val item2TitleSnapshot = mock(DataSnapshot::class.java)
        `when`(item2Snapshot.child("imageUrl")).thenReturn(item2ImageSnapshot)
        `when`(item2Snapshot.child("title")).thenReturn(item2TitleSnapshot)
        `when`(item2ImageSnapshot.getValue(String::class.java)).thenReturn("image2.jpg")
        `when`(item2TitleSnapshot.getValue(String::class.java)).thenReturn("Recipe 2")

        val listenerCaptor = ArgumentCaptor.forClass(ValueEventListener::class.java)

        val expectedItems = listOf(
            Data("123", "image1.jpg", "Recipe 1"),
            Data("456", "image2.jpg", "Recipe 2")
        )

        var actualItems = emptyList<Data>()
        authViewModel.getSavedItems { items ->
            actualItems = items
        }

        verify(mockSavedRef).addListenerForSingleValueEvent(listenerCaptor.capture())
        listenerCaptor.value.onDataChange(mockSnapshot)

        assertEquals(expectedItems.size, actualItems.size)
        assertEquals(expectedItems[0].id, actualItems[0].id)
        assertEquals(expectedItems[0].imageUrl, actualItems[0].imageUrl)
        assertEquals(expectedItems[0].title, actualItems[0].title)
        assertEquals(expectedItems[1].id, actualItems[1].id)
        assertEquals(expectedItems[1].imageUrl, actualItems[1].imageUrl)
        assertEquals(expectedItems[1].title, actualItems[1].title)

        staticFirebaseAuth.close()
    }

    // Helper functions
    private fun <T> configureMockTask(task: Task<T>, result: T) {
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.isCanceled).thenReturn(false)
        `when`(task.isComplete).thenReturn(true)
        `when`(task.result).thenReturn(result)

        doAnswer { invocation ->
            val listener = invocation.getArgument<OnSuccessListener<T>>(0)
            listener.onSuccess(result)
            task
        }.`when`(task).addOnSuccessListener(any())
    }


}