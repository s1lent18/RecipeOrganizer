package com.example.recipeorganizer.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.recipeorganizer.R
import com.example.recipeorganizer.models.requests.SignupRequest
import com.example.recipeorganizer.models.response.NetworkResponse
import com.example.recipeorganizer.ui.theme.sec
import com.example.recipeorganizer.ui.theme.text
import com.example.recipeorganizer.viewmodel.AuthViewModel
import com.example.recipeorganizer.viewmodel.navigation.Screens

fun validateEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
    return email.matches(emailRegex)
}

@Composable
fun Signup(
    navController: NavController,
    authviewmodel: AuthViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        val (username, setusername) = remember { mutableStateOf("") }
        val (password, setpassword) = remember { mutableStateOf("") }
        val (email, setemail) = remember { mutableStateOf("") }
        var passwordvisibility by remember { mutableStateOf(false) }
        val signupresult = authviewmodel.signupresult.observeAsState()
        var requestreceived by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var clicked by remember { mutableStateOf(false) }
        val keyboardController = LocalSoftwareKeyboardController.current
        val icon = if (passwordvisibility) painterResource(id = R.drawable.eye) else painterResource(id = R.drawable.lock)

        LaunchedEffect (clicked) {
            if(clicked) {
                val signuprequest = SignupRequest(
                    username = username,
                    password = password,
                    email = email,
                )
                authviewmodel.signup(signuprequest)
                clicked = false
                requestreceived = true
            }
        }

        AddHeight(20.dp)

        Input(
            label = "Username",
            value = username,
            onValueChange = setusername,
            color = Color.White
        )

        AddHeight(30.dp)

        Input(
            label = "Email",
            value = email,
            onValueChange = setemail,
            color = Color.White
        )

        AddHeight(30.dp)

        Input(
            label = "Password",
            value = password,
            onValueChange = setpassword,
            color = Color.White,
            trailingIcon = {
                IconButton(
                    onClick = {
                        passwordvisibility = !passwordvisibility
                    }
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                }
            },
            visualTransformation = if (passwordvisibility) VisualTransformation.None else PasswordVisualTransformation(),
        )

        AddHeight(30.dp)

        if (!isLoading) {
            Button(
                onClick = {
                    if (
                        username.isNotEmpty() &&
                        password.isNotEmpty() &&
                        password.length >= 8 &&
                        email.isNotEmpty() &&
                        validateEmail(email)
                    ) {
                        clicked = true
                        keyboardController?.hide()
                    }
                    else if (username.isEmpty()) {
                        Toast.makeText(context, "Enter username", Toast.LENGTH_SHORT).show()
                    }
                    else if (password.isEmpty()) {
                        Toast.makeText(context, "Enter password", Toast.LENGTH_SHORT).show()
                    }
                    else if (email.isEmpty()) {
                        Toast.makeText(context, "Enter Email", Toast.LENGTH_SHORT).show()
                    }
                    else if (password.length < 8) {
                        Toast.makeText(context, "Small Length of password", Toast.LENGTH_LONG).show()
                    }
                    else if (!validateEmail(email)) {
                        Toast.makeText(context, "Invalid Format", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.85f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = sec,
                    contentColor = text
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Signup")
            }
        } else {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
        }

        AddHeight(30.dp)

        if(
            username.isNotEmpty() &&
            password.isNotEmpty() &&
            email.isNotEmpty() &&
            requestreceived
        ) {
            when (signupresult.value) {
                is NetworkResponse.Failure -> {
                    isLoading = false
                    Toast.makeText(context, "User Already Exists", Toast.LENGTH_LONG).show()
                }
                NetworkResponse.Loading -> {
                    isLoading = true
                }
                is NetworkResponse.Success -> {
                    isLoading = false
                    LaunchedEffect(Unit) {
                        Toast.makeText(context, "Account created successfully!", Toast.LENGTH_LONG).show()
                        navController.navigate(route = Screens.Home.route)
                    }
                }
                null -> { }
            }
        }
    }
}