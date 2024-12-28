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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.recipeorganizer.R
import com.example.recipeorganizer.ui.theme.main
import com.example.recipeorganizer.ui.theme.sec
import com.example.recipeorganizer.ui.theme.text
import kotlinx.coroutines.selects.select

@Composable
fun AddWidth(space: Dp) {
    Spacer(modifier = Modifier.width(space))
}

@Composable
fun AddHeight(space: Dp) {
    Spacer(modifier = Modifier.height(space))
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Landing() {
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
                val (iconbox, loginbutton) = createRefs()

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

                Column(
                    modifier = Modifier.constrainAs(loginbutton) {
                        top.linkTo(iconbox.bottom, margin = 50.dp)
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
                        onClick = {},
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
                        Login()
                    }
                    else if(selection == "Signup") {
                        Signup()
                    }
                }
            }
        }
    }
}