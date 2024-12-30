package com.example.recipeorganizer.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.recipeorganizer.R
import com.example.recipeorganizer.ui.theme.main
import com.example.recipeorganizer.ui.theme.sec
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.navigation.NavController
import com.example.recipeorganizer.ui.theme.Oswald
import com.example.recipeorganizer.viewmodel.navigation.Screens

@Composable
fun AddWidth(space: Dp) {
    Spacer(modifier = Modifier.width(space))
}

@Composable
fun AddHeight(space: Dp) {
    Spacer(modifier = Modifier.height(space))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Landing(
    navController: NavController
) {
    Surface {
        var isSheetopen by rememberSaveable { mutableStateOf(false) }
        var selection by remember { mutableStateOf("Login") }
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp)
            ) {
                val (iconbox, loginbutton, welcometext) = createRefs()

                Box(
                    modifier = Modifier
                        .border(
                            brush = SolidColor(Color.Transparent),
                            width = 2.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .fillMaxWidth(fraction = 0.9f)
                        .fillMaxHeight(fraction = 0.46f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(main)
                        .constrainAs(iconbox) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.foodapp),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }

                Row (
                    modifier = Modifier.constrainAs(welcometext) {
                        top.linkTo(iconbox.bottom, margin = 30.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.percent(0.8f)
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Your personal guide to discovering healthy and delicious recipes, tailored just for you!",
                        color = main,
                        fontSize = 20.sp,
                        fontFamily = Oswald,
                        textAlign = TextAlign.Center,
                        lineHeight = 35.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        minLines = 2,
                        maxLines = 3,
                        overflow = Ellipsis,

                    )
                }

                Column(
                    modifier = Modifier.constrainAs(loginbutton) {
                        top.linkTo(welcometext.bottom, margin = 50.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, margin = 20.dp)
                    },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    Row (
                        modifier = Modifier.fillMaxWidth(fraction = 0.85f),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                selection = "Login"
                                isSheetopen = true
                            },
                            modifier = Modifier
                                .height(50.dp)
                                .weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = main,
                                contentColor = sec
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Login")
                        }

                        AddWidth(10.dp)

                        Button(
                            onClick = {
                                selection = "Signup"
                                isSheetopen = true
                            },
                            modifier = Modifier
                                .height(50.dp)
                                .weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = main,
                                contentColor = sec
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Signup")
                        }
                    }

                    Button(
                        onClick = {
                            navController.navigate(route = Screens.Home.route)
                        },
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.85f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = main,
                            contentColor = sec
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Continue as Guest")
                    }
                }
            }

            if (isSheetopen) {
                ModalBottomSheet(
                    sheetState = bottomSheetState,
                    onDismissRequest = {
                        isSheetopen = false
                    },
                    containerColor = main
                ) {
                    if (selection == "Login") {
                        Login(navController)
                    }
                    else if(selection == "Signup") {
                        Signup(navController)
                    }
                }
            }
        }
    }
}