package com.example.recipeorganizer.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.recipeorganizer.R
import com.example.recipeorganizer.models.dataprovider.ToggleableInfo
import com.example.recipeorganizer.models.response.NetworkResponse
import com.example.recipeorganizer.ui.theme.main
import com.example.recipeorganizer.ui.theme.sec
import com.example.recipeorganizer.ui.theme.text
import com.example.recipeorganizer.viewmodel.AuthViewModel
import com.example.recipeorganizer.viewmodel.navigation.Screens
import kotlin.math.roundToInt

fun validateEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
    return email.matches(emailRegex)
}

fun calorieIntake(gender: Boolean, weight: Int, height: Int, active: Int, age: Int, type: Int): Int {
    var calorie = 0
    calorie += (weight * 10)
    calorie += (height * 6.25).roundToInt()
    calorie -= (5 * age)
    if (gender) {
        calorie += 5
    } else {
        calorie -= 161
    }

    calorie = if (active == 1) {
        (calorie * 1.2).roundToInt()
    }
    else if (active == 2) {
        (calorie * 1.375).roundToInt()
    }
    else if (active == 3) {
        (calorie * 1.55).roundToInt()
    }
    else if (active == 4) {
        (calorie * 1.725).roundToInt()
    }
    else {
        (calorie * 1.9).roundToInt()
    }

    calorie = if (type == 1) {
        (calorie * 0.9).roundToInt()
    }
    else if (type == 2) {
        (calorie * 1.0).roundToInt()
    }
    else {
        (calorie * 1.2).roundToInt()
    }

    return calorie
}

fun heightCal(height: String): Int {
    println(height)
    val parts = height.split("'")
    if (parts.size != 2) return 0

    val feet = parts[0].toIntOrNull() ?: return 0
    val inches = parts[1].toIntOrNull() ?: return 0

    var totalInches = (feet * 12) + inches

    val cm = (totalInches * 2.54).roundToInt()

    return cm
}

@Composable
fun Funca(
    color: Color = sec,
    text: String,
    icon : ImageVector? = null,
    tcolor: Color = main,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
        .fillMaxWidth(fraction = 0.85f)
        .height(50.dp)
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = color,
            contentColor = tcolor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null)
                AddWidth(8.dp)
            }
            Text(text, fontSize = 12.sp)
        }
    }
}

@Composable
private fun RadioButtons(selectedOption: String, onOptionSelected: (String) -> Unit) {
    val radioButtons = remember {
        mutableStateListOf(
            ToggleableInfo(false, "Male"),
            ToggleableInfo(false, "Female"),
        )
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(radioButtons.size) { index ->
            val info = radioButtons[index]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        onOptionSelected(info.text)
                    }
                    .padding(end = 16.dp)
            ) {
                RadioButton(
                    selected = selectedOption == info.text,
                    onClick = {
                        onOptionSelected(info.text)
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = sec,
                        unselectedColor = Color.White
                    )
                )
                Text(text = info.text, color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun RadioButtons1(selectedOption: String, onOptionSelected: (String) -> Unit) {
    val radioButtons = remember {
        mutableStateListOf(
            ToggleableInfo(false, "WeightLoss"),
            ToggleableInfo(false, "Maintenance"),
            ToggleableInfo(false, "Gains"),
        )
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(radioButtons.size) { index ->
            val info = radioButtons[index]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        onOptionSelected(info.text)
                    }
                    .padding(end = 16.dp)
            ) {
                RadioButton(
                    selected = selectedOption == info.text,
                    onClick = {
                        onOptionSelected(info.text)
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = sec,
                        unselectedColor = Color.White
                    )
                )
                Text(text = info.text, color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun Checkboxes(selectedCuisines: List<String>, onCuisinesSelected: (List<String>) -> Unit) {
    val checkboxes = remember {
        mutableStateListOf(
            ToggleableInfo(false, "Italian"),
            ToggleableInfo(false, "Indian"),
            ToggleableInfo(false, "American"),
            ToggleableInfo(false, "Chinese"),
            ToggleableInfo(false, "French"),
            ToggleableInfo(false, "Japanese")
        )
    }

    var triState by remember { mutableStateOf(ToggleableState.Indeterminate) }

    val toggleTriState = {
        triState = when (triState) {
            ToggleableState.Indeterminate -> ToggleableState.On
            ToggleableState.On -> ToggleableState.Off
            else -> ToggleableState.On
        }
        val updatedSelections = if (triState == ToggleableState.On) {
            checkboxes.map { it.text }
        } else {
            emptyList()
        }

        checkboxes.indices.forEach { index ->
            checkboxes[index] = checkboxes[index].copy(isChecked = triState == ToggleableState.On)
        }
        onCuisinesSelected(updatedSelections)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { toggleTriState() }
                .padding(bottom = 8.dp)
        ) {
            TriStateCheckbox(
                state = triState,
                onClick = toggleTriState,
                colors = CheckboxDefaults.colors(
                    checkedColor = sec,
                    uncheckedColor = Color.White,
                    checkmarkColor = Color.Black
                )
            )
            AddWidth(8.dp)
            Text(text = "Cuisines", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
        ) {
            items(checkboxes.size) { index ->
                val info = checkboxes[index]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Checkbox(
                        checked = info.isChecked,
                        onCheckedChange = { isChecked ->
                            checkboxes[index] = info.copy(isChecked = isChecked)

                            val updatedSelections = if (isChecked) {
                                selectedCuisines + info.text
                            } else {
                                selectedCuisines - info.text
                            }

                            onCuisinesSelected(updatedSelections)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = sec,
                            uncheckedColor = Color.White,
                            checkmarkColor = Color.Black
                        )
                    )
                    AddWidth(120.dp)
                    Text(text = info.text, color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Signup(
    navController: NavController,
    authviewmodel: AuthViewModel
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
        val (age, setage) = remember { mutableStateOf("") }
        val (height, setheight) = remember { mutableStateOf("") }
        val (weight, setweight) = remember { mutableStateOf("") }
        var calorie = remember { mutableIntStateOf(0) }
        var passwordvisibility by remember { mutableStateOf(false) }
        var requestreceived by remember { mutableStateOf(false) }
        var done by remember { mutableStateOf(false) }
        val isLoading by authviewmodel.loading.observeAsState(initial = false)
        var clicked by remember { mutableStateOf(false) }
        var click by remember { mutableIntStateOf(1) }
        var sliderpos by remember { mutableFloatStateOf(1f) }
        val keyboardController = LocalSoftwareKeyboardController.current
        var selectedType by remember { mutableStateOf("WeightLoss") }
        var selectedGender by remember { mutableStateOf("Male") }
        var selectedCuisines by remember { mutableStateOf(emptyList<String>()) }
        val icon = if (passwordvisibility) painterResource(id = R.drawable.eye) else painterResource(id = R.drawable.lock)

        LaunchedEffect (clicked) {
            if(clicked) {
                authviewmodel.signup(
                    username = username,
                    email = email,
                    password = password,
                    age = age,
                    weight = weight,
                    height = height,
                )
                clicked = false
                requestreceived = true
            }
        }

        LaunchedEffect (click) {
            if(click == 2) {
                authviewmodel.addDetails(
                    calorie = calorie.intValue,
                    selectedCuisines = selectedCuisines
                )
                click = 0
            }
        }

        AddHeight(20.dp)

        if (!done) {
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

            Input(
                label = "Age",
                value = age,
                onValueChange = setage,
                color = Color.White
            )

            AddHeight(30.dp)

            Input(
                label = "height: X'X",
                value = height,
                onValueChange = setheight,
                color = Color.White
            )

            AddHeight(30.dp)

            Input(
                label = "Weight: XXkg",
                value = weight,
                onValueChange = setweight,
                color = Color.White
            )

            AddHeight(30.dp)
        }
        else {
            Checkboxes(
                selectedCuisines = selectedCuisines,
                onCuisinesSelected = { updatedList ->
                    selectedCuisines = updatedList
                }
            )

            //AddHeight(30.dp)

            Slider(
                value = sliderpos,
                onValueChange = { newValue ->
                    sliderpos = newValue.roundToInt().toFloat()
                },
                valueRange = 1f..5f,
                steps = 3,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .graphicsLayer {
                        shape = RoundedCornerShape(8.dp)
                        clip = true
                    },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = sec,
                    inactiveTrackColor = Color.White
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            )

            AddHeight(10.dp)

            Funca(
                text = if (sliderpos == 1f) "Sedentary (little/no exercise)" else if (sliderpos == 2f) "Light exercise (1-3 days/week)" else if (sliderpos == 3f) "Moderate exercise (3-5 days/week)" else if (sliderpos == 4f) "Heavy exercise (6-7 days/week)" else "Very heavy exercise (twice a day, intense)",
                icon = Icons.AutoMirrored.Filled.DirectionsRun
            )

            AddHeight(20.dp)

            RadioButtons(
                selectedOption = selectedGender,
                onOptionSelected = { newOption ->
                    selectedGender = newOption
                }
            )

            RadioButtons1(
                selectedOption = selectedType,
                onOptionSelected = { newOption ->
                    selectedType = newOption
                }
            )

            AddHeight(20.dp)
        }

        if (!requestreceived  && !done) {
            Button(
                onClick = {
                    if (
                        username.isNotEmpty() &&
                        password.isNotEmpty() &&
                        age.isNotEmpty() &&
                        height.isNotEmpty() &&
                        weight.isNotEmpty() &&
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
        }

        if (done) {
            ElevatedButton(
                onClick = {
                    calorie.intValue = calorieIntake(
                        gender = selectedGender == "Male",
                        weight = weight.toInt(),
                        height = heightCal(height),
                        age = age.toInt(),
                        active = sliderpos.toInt(),
                        type = if (selectedType == "WeightLoss") 1 else if (selectedType == "Maintenance") 2 else 3,
                    )

                    if (calorie.intValue > 0) {
                        println(heightCal(height))
                        println(calorie)
                        click = 2
                        navController.navigate(route = Screens.Home.route)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.85f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = sec,
                    contentColor = text
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(10.dp)
            ) {
                Text("Continue")
            }
        }

        if (requestreceived) {
            when (isLoading) {
                is NetworkResponse.Failure -> {
                    Button(
                        onClick = {
                            requestreceived = false
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
                        Text("Sign-Up Failed")
                    }
                }
                NetworkResponse.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = sec)
                }
                is NetworkResponse.Success<*> -> {
                    done = true
                }
            }
        }
        AddHeight(30.dp)
    }
}
