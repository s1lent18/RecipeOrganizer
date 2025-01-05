package com.example.recipeorganizer.view

import android.content.Context
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.recipeorganizer.R
import com.example.recipeorganizer.ui.theme.Chewy
import com.example.recipeorganizer.ui.theme.Oswald
import com.example.recipeorganizer.viewmodel.DisplayRecipesViewModel
import com.example.recipeorganizer.viewmodel.navigation.Screens

@Composable
fun PieChart(
    data: Map<String, Int>,
    radiusOuter: Dp = 80.dp,
    chartBarWidth: Dp = 35.dp,
    animDuration: Int = 1000,
) {

    val colors = listOf(
        Color(0xFF4A90E2),
        Color(0xFFF9E200),
        Color(0xFFD64D42),
        Color(0xFFF1E1D0)
    )

    val totalSum = data.values.sum()
    val floatValue = mutableListOf<Float>()

    data.values.forEachIndexed { index, values ->
        floatValue.add(index, 360 * values.toFloat() / totalSum.toFloat())
    }

    var animationPlayed by remember { mutableStateOf(false) }
    var lastValue = 0f

    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed) radiusOuter.value.coerceAtMost(200f) * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        ), label = ""
    )

    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 11f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        ), label = ""
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(start = 20.dp)
                .size(animateSize.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
                    .rotate(animateRotation)
            ) {
                floatValue.forEachIndexed { index, value ->
                    drawArc(
                        color = colors[index],
                        startAngle = lastValue,
                        sweepAngle = value,
                        useCenter = false,
                        style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                    )
                    lastValue += value
                }
            }
        }

        DetailsPieChart(
            data = data,
            colors = colors
        )
    }
}

@Composable
fun DetailsPieChart(
    data: Map<String, Int>,
    colors: List<Color>
) {
    LazyColumn(
        modifier = Modifier
            .heightIn(max = 300.dp)
            .fillMaxWidth()
    ) {
        items(data.entries.toList()) { entry ->
            DetailsPieChartItem(
                data = Pair(entry.key, entry.value),
                color = colors[data.keys.indexOf(entry.key)]
            )
        }
    }
}

@Composable
fun DetailsPieChartItem(
    data: Pair<String, Int>,
    height: Dp = 10.dp,
    color: Color
) {
    Surface(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 20.dp),
        color = Color.Transparent
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .background(
                        color = color,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .size(height)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = data.first,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                )
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = if (data.second.toString() != "calories") data.second.toString() + "g" else data.second.toString() + "kcal",
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun IngredientsRow(
    context : Context,
    image: String,
    name: String
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box (
            modifier = Modifier
                .clip(CircleShape)
                .size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = context.getString(R.string.DefaultLink) + image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        AddHeight(10.dp)

        Text(
            text = name,
            fontSize = 12.sp,
            fontFamily = Chewy
        )
    }
}

@Composable
fun Single(
    navController: NavController,
    displayrecipesviewmodel: DisplayRecipesViewModel = hiltViewModel(),
) {
    Surface {

        val recipe by displayrecipesviewmodel.recipefullinfo.collectAsStateWithLifecycle()
        val ingredients by displayrecipesviewmodel.ingredientsrecipes.collectAsStateWithLifecycle()
        val nutrients by displayrecipesviewmodel.nutrientsinfo.collectAsStateWithLifecycle()
        val context = LocalContext.current

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            ConstraintLayout (
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                if (recipe == null || nutrients == null || ingredients.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val (navigationrow, imagebox, time, desc, nuts) = createRefs()

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
                                                Color.Black.copy(alpha = 0.7f),
                                                Color.Transparent,
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.9f)
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
                            top.linkTo(parent.top, margin = 30.dp)
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
                            .height(40.dp)
                            .offset(y = (-40).dp)
                            .padding(start = 20.dp)
                            .constrainAs(time) {
                                top.linkTo(imagebox.bottom, margin = 20.dp)
                                start.linkTo(parent.start)
                                width = Dimension.percent(0.4f)
                            },
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color(0xFF212121)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row (
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
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

                    LazyColumn (
                        modifier = Modifier
                            .constrainAs(nuts) {
                                top.linkTo(desc.bottom, margin = 10.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                width = Dimension.percent(0.9f)
                                height = Dimension.wrapContent
                            }
                            .heightIn(max = 600.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        item {
                            PieChart(
                                data = mapOf(
                                    Pair("Proteins", (nutrients!!.protein.replace("g", "")).toInt()),
                                    Pair("Fats", (nutrients!!.fat.replace("g", "")).toInt()),
                                    Pair("Carbs", (nutrients!!.carbs.replace("g", "")).toInt()),
                                    Pair("Calories", (nutrients!!.calories).toInt())
                                )
                            )
                        }

                        item {
                            Text(
                                text = "Ingredients",
                                fontSize = 20.sp,
                                fontFamily = Chewy
                            )
                            AddHeight(5.dp)
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                items(ingredients.size) { ing ->
                                    IngredientsRow(
                                        context = context,
                                        image = ingredients[ing].image,
                                        name = ingredients[ing].name
                                    )
                                    AddWidth(20.dp)
                                }
                            }
                            AddHeight(40.dp)
                        }
                    }
                }
            }
        }
    }
}