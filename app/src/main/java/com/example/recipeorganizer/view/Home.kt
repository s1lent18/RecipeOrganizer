package com.example.recipeorganizer.view

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.recipeorganizer.models.dataprovider.Data
import com.example.recipeorganizer.models.dataprovider.OptionRows
import com.example.recipeorganizer.ui.theme.CC
import com.example.recipeorganizer.ui.theme.Oswald
import com.example.recipeorganizer.ui.theme.main
import com.example.recipeorganizer.ui.theme.sec
import com.example.recipeorganizer.viewmodel.AuthViewModel
import com.example.recipeorganizer.viewmodel.DisplayRecipesViewModel
import com.example.recipeorganizer.viewmodel.GeminiViewModel
import com.example.recipeorganizer.viewmodel.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun TextRow(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    if (isSelected) {
        Text(
            text = text,
            modifier = Modifier.clickable { onClick() },
            fontFamily = CC,
            fontSize = 20.sp
        )
    } else {
        Text(
            text = text,
            color = Color.Gray,
            modifier = Modifier.clickable { onClick() },
            fontFamily = CC,
            fontSize = 20.sp
        )
    }
}

@Composable
fun Recipe(
    text: String,
    image: String,
    specificRecipe: (id: Int) -> Unit,
    id: Int,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .height(200.dp)
            .width(150.dp)
            .background(main)
            .clickable {
                specificRecipe(id)
                navController.navigate(route = Screens.Single.route)
            }
        ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(20.dp)
                .fillMaxWidth(fraction = 0.7f),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = image),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .border(2.dp, Color.White, CircleShape)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Text(text = text, fontFamily = Oswald, color = Color.White, modifier = Modifier.padding(5.dp), fontSize = 13.sp)
    }
}

@Composable
fun CustomComponent(
    canvasSize: Dp = 300.dp,
    indicatorValue: Int = 50,
    maxIndicatorValue: Int = 100,
    backgroundIndicatorColor: Color = Color.Blue,
    backgroundIndicatorStrokeWidth: Float = 100f,
    foregroundIndicatorColor: Color = Color.Blue,
    foregroundIndicatorStrokeWidth: Float = 100f,
//    indicatorStrokeCap: StrokeCap = StrokeCap.Round,
    bigTextFontSize: TextUnit = 20.sp,
    bigTextColor: Color = Color.Black,
    bigTextSuffix: String = "Calories",
    smallText: String = "Remaining",
    smallTextFontSize: TextUnit = 20.sp,
    smallTextColor: Color = Color.Black
) {
    var allowedIndicatorValue by remember {
        mutableIntStateOf(maxIndicatorValue)
    }
    allowedIndicatorValue = if (indicatorValue <= maxIndicatorValue) {
        indicatorValue
    } else {
        maxIndicatorValue
    }

    var animatedIndicatorValue by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(key1 = allowedIndicatorValue) {
        animatedIndicatorValue = allowedIndicatorValue.toFloat()
    }

    val percentage =
        (animatedIndicatorValue / maxIndicatorValue) * 100

    val sweepAngle by animateFloatAsState(
        targetValue = (2.4 * percentage).toFloat(),
        animationSpec = tween(1000)
    )

    val receivedValue by animateIntAsState(
        targetValue = allowedIndicatorValue,
        animationSpec = tween(1000)
    )

    val animatedBigTextColor by animateColorAsState(
        targetValue = if (allowedIndicatorValue == 0)
            Color.Blue
        else
            bigTextColor,
        animationSpec = tween(1000)
    )

    Column(
        modifier = Modifier
            .size(canvasSize)
            .drawBehind {
                val componentSize = size / 1.25f
                backgroundIndicator(
                    componentSize = componentSize,
                    indicatorColor = backgroundIndicatorColor,
                    indicatorStrokeWidth = backgroundIndicatorStrokeWidth,
//                    indicatorStokeCap = indicatorStrokeCap
                )
                foregroundIndicator(
                    sweepAngle = sweepAngle,
                    componentSize = componentSize,
                    indicatorColor = foregroundIndicatorColor,
                    indicatorStrokeWidth = foregroundIndicatorStrokeWidth,
//                    indicatorStokeCap = indicatorStrokeCap
                )
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmbeddedElements(
            bigText = receivedValue,
            bigTextFontSize = bigTextFontSize,
            bigTextColor = animatedBigTextColor,
            bigTextSuffix = bigTextSuffix,
            smallText = smallText,
            smallTextColor = smallTextColor,
            smallTextFontSize = smallTextFontSize
        )
    }
}

fun DrawScope.backgroundIndicator(
    componentSize: Size,
    indicatorColor: Color,
    indicatorStrokeWidth: Float,
    indicatorStrokeCap: StrokeCap = StrokeCap.Round // Optional default value
) {
    drawArc(
        size = componentSize,
        color = indicatorColor,
        startAngle = 150f,
        sweepAngle = 240f,
        useCenter = false,
        style = Stroke(
            width = indicatorStrokeWidth,
            cap = indicatorStrokeCap
        ),
        topLeft = Offset(
            x = (size.width - componentSize.width) / 2f,
            y = (size.height - componentSize.height) / 2f
        )
    )
}

fun DrawScope.foregroundIndicator(
    sweepAngle: Float,
    componentSize: Size,
    indicatorColor: Color,
    indicatorStrokeWidth: Float,
    indicatorStrokeCap: StrokeCap = StrokeCap.Round
) {
    drawArc(
        size = componentSize,
        color = indicatorColor,
        startAngle = 150f,
        sweepAngle = sweepAngle,
        useCenter = false,
        style = Stroke(
            width = indicatorStrokeWidth,
            cap = indicatorStrokeCap
        ),
        topLeft = Offset(
            x = (size.width - componentSize.width) / 2f,
            y = (size.height - componentSize.height) / 2f
        )
    )
}


@Composable
fun EmbeddedElements(
    bigText: Int,
    bigTextFontSize: TextUnit,
    bigTextColor: Color,
    bigTextSuffix: String,
    smallText: String,
    smallTextColor: Color,
    smallTextFontSize: TextUnit
) {
    Text(
        text = smallText,
        color = smallTextColor,
        fontSize = smallTextFontSize,
        textAlign = TextAlign.Center
    )
    Text(
        text = "$bigText $bigTextSuffix",
        color = bigTextColor,
        fontSize = bigTextFontSize,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    authviewmodel: AuthViewModel = hiltViewModel(),
    geminiviewmodel: GeminiViewModel = hiltViewModel(),
    displayrecipesviewmodel : DisplayRecipesViewModel = hiltViewModel(),
    onLoadMore: (offset: Int) -> Unit,
    specificRecipe: (id: Int) -> Unit,
    searchRecipes: (query: String) -> Unit,
    loadAnother: (type: String, clear: Boolean) -> Unit,
) {
    Surface {
        val gridState = rememberLazyStaggeredGridState()
        var searchQuery by remember { mutableStateOf("") }
        val loadingoption = remember { mutableStateOf("") }
        var showMessage by remember { mutableStateOf(false) }
        val calories by authviewmodel.calorie.collectAsState()
        val response by geminiviewmodel.response.collectAsState()
        var launchAlertBox by rememberSaveable { mutableStateOf(true) }
        val cuisinesList by authviewmodel.selectedCuisine.collectAsState()
        val selectedOption = rememberSaveable { mutableStateOf("BreakFast") }
        val username by authviewmodel.username.collectAsState(initial = null)
        val total by displayrecipesviewmodel.total.collectAsStateWithLifecycle()
        val currentCalories by authviewmodel.currentCal.collectAsStateWithLifecycle()
        var savedRecipes by remember { mutableStateOf<List<Data>>(emptyList()) }
        val recipes by displayrecipesviewmodel.homerecipes.collectAsStateWithLifecycle()
        val searchrecipes by displayrecipesviewmodel.searchrecipes.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            delay(10000)
            showMessage = true
        }

        LaunchedEffect(Unit) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            userId?.let {
                authviewmodel.fetchCalories(it)
                authviewmodel.fetchCuisines(it)
                authviewmodel.listenForCurrentCalories()
            }
        }

        LaunchedEffect(calories) {
            displayrecipesviewmodel.updateCalories(calories)
        }

        LaunchedEffect(cuisinesList) {
            displayrecipesviewmodel.updateCuisines(cuisinesList.filterIsInstance<String>())
        }

        LaunchedEffect(calories, cuisinesList) {
            if (!calories.isNullOrBlank() && cuisinesList.isNotEmpty()) {
                displayrecipesviewmodel.getRecipes(offset = 0)
            }
        }

        LaunchedEffect(Unit) {
            authviewmodel.getSavedItems { savedList ->
                savedRecipes = savedList
            }
        }

        if (launchAlertBox) {
            BasicAlertDialog(
                onDismissRequest = { launchAlertBox = false } ,
                content = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .fillMaxWidth(fraction = 0.8f)
                            .background(main),
                        contentAlignment = Alignment.Center
                    ) {
                        Column (
                            modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AddHeight(20.dp)
                            Text("Health Tip", fontSize = 20.sp, color = sec, fontFamily = CC)
                            AddHeight(20.dp)
                            response?.candidates?.get(0)?.content?.parts?.get(0)?.let {
                                Text(
                                    text = it.text,
                                    color = Color.White,
                                    fontFamily = Oswald
                                )
                            }
                            AddHeight(10.dp)
                        }
                    }
                }
            )
        }

        LaunchedEffect(gridState) {
            snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collect { lastVisibleItemIndex ->
                    Log.d("LazyGrid", "Last visible index: $lastVisibleItemIndex, Recipes size: ${recipes.size}")
                    if (lastVisibleItemIndex == recipes.size - 1) {
                        onLoadMore(recipes.size)
                    }
                }
        }

        LaunchedEffect(Unit) {
            val userid = authviewmodel.getuserid()
            if (userid != null) {
                authviewmodel.fetchUsername(userid)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConstraintLayout (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 70.dp)
            ) {
                val (inforow, searchbox, optionrow, recipedisplay) = createRefs()

                Row(
                    modifier = Modifier
                        .constrainAs(inforow) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.percent(0.9f)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (username != null) {
                            Text(
                                "Hello, $username",
                                color = main,
                                fontSize = 20.sp,
                                fontFamily = CC
                            )
                        } else {
                            Text(
                                "Hello, Guest",
                                color = main,
                                fontSize = 20.sp,
                                fontFamily = CC
                            )
                        }
                        Text(
                            "What do you want to cook today",
                            color = Color.Gray,
                            fontSize = 15.sp,
                            fontFamily = Oswald
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(45.dp)
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                authviewmodel.signout()
                                navController.navigate(route = Screens.Landing.route)
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.constrainAs(searchbox) {
                        top.linkTo(inforow.bottom, margin = 20.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.percent(0.9f)
                    },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            searchRecipes(searchQuery)
                        },
                        placeholder = { Text(text = "Search...") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Back"
                            )
                        },
                        shape = RoundedCornerShape(50.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        )
                    )

                    if (searchrecipes.isNotEmpty()) {
                        LazyColumn (
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        ) {
                            items(searchrecipes.size) { recipe ->
                                AddHeight(10.dp)
                                FloatingActionButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        specificRecipe(searchrecipes[recipe].id)
                                        navController.navigate(route = Screens.Single.route)
                                    },
                                    containerColor = main
                                ) {
                                    Row (
                                        modifier = Modifier.fillMaxWidth(fraction = 0.9f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = searchrecipes[recipe].title,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                LazyRow(
                    modifier = Modifier.constrainAs(optionrow) {
                        top.linkTo(searchbox.bottom, margin = 30.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                ) {
                    items(OptionRows.size) { option ->
                        TextRow(
                            text = OptionRows[option].first,
                            isSelected = selectedOption.value == OptionRows[option].first,
                            onClick = {
                                showMessage = false
                                if (OptionRows[option].second != "") {
                                    selectedOption.value = OptionRows[option].first
                                    loadingoption.value = OptionRows[option].second
                                    loadAnother(loadingoption.value, true)
                                } else {
                                    displayrecipesviewmodel.clearRecipes()
                                    selectedOption.value = OptionRows[option].first
                                }
                            }
                        )
                        AddWidth(12.dp)
                    }
                }

                if (selectedOption.value == "Calorie-Tracking") {

                    Column(
                        modifier = Modifier
                            .constrainAs(recipedisplay) {
                            top.linkTo(optionrow.bottom, margin = 30.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom, margin = 60.dp)
                        }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CustomComponent(
                                indicatorValue = currentCalories!!.toInt(),
                                maxIndicatorValue = calories!!.toInt() ,
                                foregroundIndicatorColor = main,
                                backgroundIndicatorColor = Color.LightGray,
                                bigTextSuffix = "Calories",
                                bigTextColor = sec,
                                smallTextColor = sec
                            )
                        }
                    }
                }

                else if (recipes.isNotEmpty() && selectedOption.value != "Calorie-Tracking") {
                    LazyVerticalStaggeredGrid(
                        state = gridState,
                        columns = StaggeredGridCells.Fixed(2),
                        verticalItemSpacing = 16.dp,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.constrainAs(recipedisplay) {
                            top.linkTo(optionrow.bottom, margin = 30.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.percent(0.9f)
                            bottom.linkTo(parent.bottom, margin = 60.dp)
                            height = Dimension.fillToConstraints
                        }
                    ) {
                        item {
                            Text(
                                text = "$total New Recipes",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        items(recipes.size) { recipe ->
                            AddHeight(80.dp)
                            Recipe(
                                text = recipes[recipe].title,
                                image = recipes[recipe].image,
                                specificRecipe = specificRecipe,
                                id = recipes[recipe].id,
                                navController = navController
                            )
                        }
                    }
                } else if (selectedOption.value == "Saved-Recipes") {
                    if (savedRecipes.isNotEmpty()) {
                        Box(
                            modifier = Modifier.constrainAs(recipedisplay) {
                                top.linkTo(optionrow.bottom, margin = 30.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom, margin = 60.dp)
                                width = Dimension.percent(0.9f)
                                height = Dimension.fillToConstraints
                            },
                        ) {
                            LazyVerticalStaggeredGrid(
                                state = gridState,
                                columns = StaggeredGridCells.Fixed(2),
                                verticalItemSpacing = 16.dp,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                item {
                                    Text(
                                        text = "${savedRecipes.size} Saved Recipes",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }

                                items(savedRecipes.size) { index ->
                                    AddHeight(80.dp)
                                    Recipe(
                                        text = savedRecipes[index].title,
                                        image = savedRecipes[index].imageUrl,
                                        specificRecipe = specificRecipe,
                                        id = savedRecipes[index].id.toInt(),
                                        navController = navController
                                    )
                                }
                            }
                        }
                    } else if (username == null) {
                        Box(
                            modifier = Modifier.constrainAs(recipedisplay) {
                                top.linkTo(optionrow.bottom, margin = 30.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom, margin = 60.dp)
                                width = Dimension.percent(0.9f)
                                height = Dimension.fillToConstraints
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Column (
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null)
                                AddHeight(20.dp)
                                Text("Please Sign-In First")
                            }
                        }
                    }
                }
                else {
                    Box(
                        modifier = Modifier.constrainAs(recipedisplay) {
                            top.linkTo(optionrow.bottom, margin = 30.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom, margin = 60.dp)
                            width = Dimension.percent(0.9f)
                            height = Dimension.fillToConstraints
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        if (showMessage) {
                            Text(
                                text = "Please relax the filters",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        } else {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}