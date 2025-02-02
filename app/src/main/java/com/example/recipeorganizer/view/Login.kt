package com.example.recipeorganizer.view

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipeorganizer.R
import com.example.recipeorganizer.ui.theme.main
import com.example.recipeorganizer.ui.theme.sec
import com.example.recipeorganizer.ui.theme.text
import com.example.recipeorganizer.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Input(
    label : String,
    value : String,
    onValueChange: (String) -> Unit,
    trailingIcon: (@Composable () -> Unit)? = null,
    color: Color,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    TextField(
        modifier = modifier
            .fillMaxWidth(fraction = 0.8f),
        label = {
            Text(
                label,

                )
        },
        value = value,
        onValueChange = onValueChange,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = color,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedLabelColor = main,
            unfocusedLabelColor = main,
            disabledLabelColor = main,
            focusedTextColor = main,
            unfocusedTextColor = main,
            disabledTextColor = main
        ),
        shape = RoundedCornerShape(10.dp),
        textStyle = TextStyle(
            fontSize = 12.sp
        )
    )
}

@Composable
fun Login(
    authviewmodel: AuthViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        var clicked by remember { mutableStateOf(false) }
        val isLoading by authviewmodel.loading.collectAsState()
        var requestreceived by remember { mutableStateOf(false) }
        val keyboardController = LocalSoftwareKeyboardController.current
        var passwordvisibility by remember { mutableStateOf(false) }
        val (username, setusername) = remember { mutableStateOf("") }
        val (password, setpassword) = remember { mutableStateOf("") }
        val icon = if (passwordvisibility) painterResource(id = R.drawable.eye) else painterResource(id = R.drawable.lock)

        LaunchedEffect (clicked) {
            if(clicked) {
                authviewmodel.signin(email = username, password = password)
                requestreceived = true
                clicked = false
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
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        Log.d("LoginButton", "Username: $username, Password: $password")
                        clicked = true
                        keyboardController?.hide()
                    }
                    else if (username.isEmpty()) {
                        Toast.makeText(context, "Enter username", Toast.LENGTH_SHORT).show()
                    }
                    else if (password.isEmpty()) {
                        Toast.makeText(context, "Enter password", Toast.LENGTH_SHORT).show()
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
                Text("Login")
            }
        } else {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
        }
        AddHeight(30.dp)
    }
}