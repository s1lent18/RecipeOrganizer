package com.example.recipeorganizer

import com.example.recipeorganizer.models.dataprovider.Data
import com.example.recipeorganizer.models.dataprovider.Users
import com.example.recipeorganizer.viewmodel.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import org.junit.Assert.*
import org.mockito.ArgumentMatchers.any
import org.mockito.MockedStatic
import org.mockito.Mockito
import java.util.ArrayList

@RunWith(MockitoJUnitRunner::class)
class RepositoryTest {

    @Mock
    private lateinit var mockDatabase: FirebaseDatabase

    @Mock
    private lateinit var mockDatabaseReference: DatabaseReference

    @Mock
    private lateinit var mockChildReference: DatabaseReference

    @Mock
    private lateinit var mockUserReference: DatabaseReference

    @Mock
    private lateinit var mockSavedReference: DatabaseReference

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockUser: FirebaseUser

    @Captor
    private lateinit var valueCaptor: ArgumentCaptor<Users>

    @Captor
    private lateinit var dataCaptor: ArgumentCaptor<Data>

    private lateinit var repository: Repository

    private val testUserId = "test-user-id"

    private lateinit var firebaseDatabaseMockedStatic: MockedStatic<FirebaseDatabase>
    private lateinit var firebaseAuthMockedStatic: MockedStatic<FirebaseAuth>

    @Before
    fun setup() {
        firebaseDatabaseMockedStatic = Mockito.mockStatic(FirebaseDatabase::class.java)
        firebaseAuthMockedStatic = Mockito.mockStatic(FirebaseAuth::class.java)

        `when`(FirebaseDatabase.getInstance()).thenReturn(mockDatabase)
        `when`(FirebaseAuth.getInstance()).thenReturn(mockAuth)
        `when`(mockDatabase.reference).thenReturn(mockDatabaseReference)
        `when`(mockDatabaseReference.child(any())).thenReturn(mockChildReference)
        `when`(mockChildReference.child(any())).thenReturn(mockUserReference)
        `when`(mockUserReference.child(any())).thenReturn(mockSavedReference)
        `when`(mockSavedReference.child(any())).thenReturn(mockSavedReference)

        `when`(mockAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.uid).thenReturn(testUserId)

        repository = Repository()
    }
    @After
    fun tearDown() {
        firebaseDatabaseMockedStatic.close()
        firebaseAuthMockedStatic.close()
        Mockito.reset(mockDatabase, mockDatabaseReference, mockChildReference,
            mockUserReference, mockSavedReference, mockAuth, mockUser)
    }

    @Test
    fun `adduser should save user data to correct location`() {
        // Given
        val email = "test@example.com"
        val username = "testuser"
        val age = "25"
        val weight = "70"
        val height = "175"

        val taskMock = mock(Task::class.java) as Task<Void>
        `when`(mockUserReference.setValue(any())).thenReturn(taskMock)
        `when`(taskMock.addOnSuccessListener(any<OnSuccessListener<Void>>())).thenReturn(taskMock)
        `when`(taskMock.addOnFailureListener(any<OnFailureListener>())).thenReturn(taskMock)

        repository.adduser(email, username, age, weight, height)

        verify(mockDatabaseReference).child("FoodAppDB")
        verify(mockChildReference).child(testUserId)

        verify(mockUserReference).setValue(valueCaptor.capture())
        val capturedUser = valueCaptor.value

        assertEquals(username, capturedUser.username)
        assertEquals(email, capturedUser.email)
        assertEquals(age, capturedUser.age)
        assertEquals(height, capturedUser.height)
        assertEquals(weight, capturedUser.weight)
    }

    @Test
    fun `addDetails should save calories and cuisines`() {
        // Given
        val calories = "2000"
        val cuisines = listOf("Italian", "Mexican")

        val caloriesRef = mock(DatabaseReference::class.java)
        val cuisinesRef = mock(DatabaseReference::class.java)

        `when`(mockUserReference.child("Calories")).thenReturn(caloriesRef)
        `when`(mockUserReference.child("Cuisines")).thenReturn(cuisinesRef)

        val taskMock = mock(Task::class.java) as Task<Void>
        `when`(caloriesRef.setValue(any())).thenReturn(taskMock)
        `when`(cuisinesRef.setValue(any())).thenReturn(taskMock)
        `when`(taskMock.addOnSuccessListener(any<OnSuccessListener<Void>>())).thenReturn(taskMock)
        `when`(taskMock.addOnFailureListener(any<OnFailureListener>())).thenReturn(taskMock)

        repository.addDetails(calories, cuisines)

        verify(mockDatabaseReference).child("FoodAppDB")
        verify(mockChildReference, times(2)).child(testUserId)
        verify(mockUserReference).child("Calories")
        verify(mockUserReference).child("Cuisines")
        verify(caloriesRef).setValue(calories)
        verify(cuisinesRef).setValue(cuisines)
    }

    @Test
    fun `isSaved should call callback with true when recipe exists`() {
        val recipeId = "recipe123"
        val savedRef = mock(DatabaseReference::class.java)
        val taskMock = mock(Task::class.java) as Task<DataSnapshot>
        val snapshotMock = mock(DataSnapshot::class.java)

        `when`(mockUserReference.child("saved")).thenReturn(savedRef)
        `when`(savedRef.child(recipeId)).thenReturn(savedRef)
        `when`(savedRef.get()).thenReturn(taskMock)
        `when`(taskMock.addOnSuccessListener(any<OnSuccessListener<DataSnapshot>>())).thenAnswer { invocation ->
            val listener = invocation.getArgument(0) as OnSuccessListener<DataSnapshot>
            listener.onSuccess(snapshotMock)
            taskMock
        }
        `when`(taskMock.addOnFailureListener(any<OnFailureListener>())).thenReturn(taskMock)
        `when`(snapshotMock.exists()).thenReturn(true)

        var result = false
        repository.isSaved(recipeId) { isSaved ->
            result = isSaved
        }

        assertTrue(result)
    }

    @Test
    fun `isSaved should call callback with false when recipe does not exist`() {
        val recipeId = "recipe123"
        val savedRef = mock(DatabaseReference::class.java)
        val taskMock = mock(Task::class.java) as Task<DataSnapshot>
        val snapshotMock = mock(DataSnapshot::class.java)

        `when`(mockUserReference.child("saved")).thenReturn(savedRef)
        `when`(savedRef.child(recipeId)).thenReturn(savedRef)
        `when`(savedRef.get()).thenReturn(taskMock)
        `when`(taskMock.addOnSuccessListener(any<OnSuccessListener<DataSnapshot>>())).thenAnswer { invocation ->
            val listener = invocation.getArgument(0) as OnSuccessListener<DataSnapshot>
            listener.onSuccess(snapshotMock)
            taskMock
        }
        `when`(taskMock.addOnFailureListener(any<OnFailureListener>())).thenReturn(taskMock)
        `when`(snapshotMock.exists()).thenReturn(false)

        var result = true
        repository.isSaved(recipeId) { isSaved ->
            result = isSaved
        }
        assertFalse(result)
    }

    @Test
    fun `isSaved should call callback with false when user is not logged in`() {
        val recipeId = "recipe123"
        `when`(mockAuth.currentUser).thenReturn(null)

        var result = true
        repository.isSaved(recipeId) { isSaved ->
            result = isSaved
        }

        assertFalse(result)
    }

    @Test
    fun `save should store recipe data correctly`() {
        val recipeId = "recipe123"
        val imageUrl = "https://example.com/image.jpg"
        val title = "Delicious Recipe"

        val savedRef = mock(DatabaseReference::class.java)
        val recipeRef = mock(DatabaseReference::class.java)
        val taskMock = mock(Task::class.java) as Task<Void>

        `when`(mockUserReference.child("saved")).thenReturn(savedRef)
        `when`(savedRef.child(recipeId)).thenReturn(recipeRef)
        `when`(recipeRef.setValue(any())).thenReturn(taskMock)
        `when`(taskMock.addOnSuccessListener(any<OnSuccessListener<Void>>())).thenReturn(taskMock)
        `when`(taskMock.addOnFailureListener(any<OnFailureListener>())).thenReturn(taskMock)

        repository.save(recipeId, imageUrl, title)

        verify(mockDatabaseReference).child("FoodAppDB")
        verify(mockChildReference).child(testUserId)
        verify(mockUserReference).child("saved")
        verify(savedRef).child(recipeId)

        verify(recipeRef).setValue(dataCaptor.capture())
        val capturedData = dataCaptor.value

        assertEquals(recipeId, capturedData.id)
        assertEquals(imageUrl, capturedData.imageUrl)
        assertEquals(title, capturedData.title)
    }

    @Test
    fun `unsave should remove recipe data`() {
        val recipeId = "recipe123"
        val savedRef = mock(DatabaseReference::class.java)
        val recipeRef = mock(DatabaseReference::class.java)
        val taskMock = mock(Task::class.java) as Task<Void>

        `when`(mockUserReference.child("saved")).thenReturn(savedRef)
        `when`(savedRef.child(recipeId)).thenReturn(recipeRef)
        `when`(recipeRef.removeValue()).thenReturn(taskMock)
        `when`(taskMock.addOnSuccessListener(any<OnSuccessListener<Void>>())).thenReturn(taskMock)
        `when`(taskMock.addOnFailureListener(any<OnFailureListener>())).thenReturn(taskMock)

        repository.unsave(recipeId)

        verify(mockDatabaseReference).child("FoodAppDB")
        verify(mockChildReference).child(testUserId)
        verify(mockUserReference).child("saved")
        verify(savedRef).child(recipeId)
        verify(recipeRef).removeValue()
    }

    @Test
    fun `checkUsernameAvailability should return true when username is available`() {
        val username = "newuser"
        val snapshotMock = mock(DataSnapshot::class.java)
        val childSnapshotMock = mock(DataSnapshot::class.java)
        val usernameSnapshotMock = mock(DataSnapshot::class.java)

        val childrenList = ArrayList<DataSnapshot>()
        childrenList.add(childSnapshotMock)

        `when`(snapshotMock.children).thenReturn(childrenList)
        `when`(childSnapshotMock.child("username")).thenReturn(usernameSnapshotMock)
        `when`(usernameSnapshotMock.getValue(String::class.java)).thenReturn("existinguser")

        doAnswer { invocation ->
            val listener = invocation.getArgument(0) as ValueEventListener
            listener.onDataChange(snapshotMock)
            null
        }.`when`(mockChildReference).addListenerForSingleValueEvent(any())

        var result = false
        repository.checkUsernameAvailability(username) { isAvailable ->
            result = isAvailable
        }

        assertTrue(result)
    }

    @Test
    fun `checkUsernameAvailability should return false when username is taken`() {
        val username = "existinguser"
        val snapshotMock = mock(DataSnapshot::class.java)
        val childSnapshotMock = mock(DataSnapshot::class.java)
        val usernameSnapshotMock = mock(DataSnapshot::class.java)

        val childrenList = ArrayList<DataSnapshot>()
        childrenList.add(childSnapshotMock)

        `when`(snapshotMock.children).thenReturn(childrenList)
        `when`(childSnapshotMock.child("username")).thenReturn(usernameSnapshotMock)
        `when`(usernameSnapshotMock.getValue(String::class.java)).thenReturn("existinguser")

        doAnswer { invocation ->
            val listener = invocation.getArgument(0) as ValueEventListener
            listener.onDataChange(snapshotMock)
            null
        }.`when`(mockChildReference).addListenerForSingleValueEvent(any())

        var result = true
        repository.checkUsernameAvailability(username) { isAvailable ->
            result = isAvailable
        }

        assertFalse(result)
    }

    @Test
    fun `checkUsernameAvailability should return false on database error`() {
        val username = "newuser"
        val errorMock = mock(DatabaseError::class.java)

        doAnswer { invocation ->
            val listener = invocation.getArgument(0) as ValueEventListener
            listener.onCancelled(errorMock)
            null
        }.`when`(mockChildReference).addListenerForSingleValueEvent(any())

        var result = true
        repository.checkUsernameAvailability(username) { isAvailable ->
            result = isAvailable
        }

        assertFalse(result)
    }
}