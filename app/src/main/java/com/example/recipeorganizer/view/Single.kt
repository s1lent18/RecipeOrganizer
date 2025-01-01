package com.example.recipeorganizer.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.recipeorganizer.ui.theme.Oswald
import com.example.recipeorganizer.viewmodel.DisplayRecipesViewModel
import com.example.recipeorganizer.viewmodel.navigation.Screens

@Composable
fun Single(
    navController: NavController,
    displayrecipesviewmodel: DisplayRecipesViewModel
) {
    Surface {

        val recipe by displayrecipesviewmodel.recipefullinfo.collectAsStateWithLifecycle()
        val ingredients by displayrecipesviewmodel.ingredientsrecipes.collectAsStateWithLifecycle()
        val nutrients by displayrecipesviewmodel.nutrientsinfo.collectAsStateWithLifecycle()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            ConstraintLayout (
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                if (recipe == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val (navigationrow, imagebox, time, desc) = createRefs()



                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fraction = 0.35f)
                            .constrainAs(imagebox) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = recipe!!.image),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.7f), // Fade-in at top
                                                Color.Transparent,             // Fully visible in the middle
                                                Color.Transparent,             // Fully visible in the middle
                                                Color.Black.copy(alpha = 0.9f)  // Fade-in at bottom
                                            ),
                                            startY = 0f,
                                            endY = Float.POSITIVE_INFINITY
                                        )
                                    )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.constrainAs(navigationrow) {
                            top.linkTo(parent.top, margin = 20.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.percent(0.95f)
                        },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                navController.navigate(route = Screens.Home.route)
                            }
                        ) {
                            Icon(
                                Icons.Default.ArrowBackIosNew,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }

                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .offset(y = (-40).dp)
                            .padding(start = 20.dp)
                            .constrainAs(time) {
                                top.linkTo(imagebox.bottom, margin = 20.dp)
                                start.linkTo(parent.start)
                                width = Dimension.percent(0.3f)
                            },
                        horizontalArrangement = Arrangement.Start
                    ) {

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color(0xFF212121)),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .padding(top = 5.dp, start = 5.dp, bottom = 5.dp)
                                )

                                Text(
                                    "${recipe!!.readyInMinutes} Minutes",
                                    modifier = Modifier.padding(end = 8.dp, top = 5.dp, bottom = 5.dp),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Column (
                        modifier = Modifier.constrainAs(desc) {
                            top.linkTo(time.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.percent(0.9f)
                        },
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            recipe!!.title,
                            fontSize = 20.sp,
                            fontFamily = Oswald
                        )
                    }
                }
            }
        }
    }
}