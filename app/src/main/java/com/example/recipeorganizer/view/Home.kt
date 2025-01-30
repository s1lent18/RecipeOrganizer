package com.example.recipeorganizer.view

import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.recipeorganizer.models.dataprovider.OptionRows
import com.example.recipeorganizer.ui.theme.Chewy
import com.example.recipeorganizer.ui.theme.Oswald
import com.example.recipeorganizer.ui.theme.main
import com.example.recipeorganizer.viewmodel.AuthViewModel
import com.example.recipeorganizer.viewmodel.DisplayRecipesViewModel
import com.example.recipeorganizer.viewmodel.navigation.Screens

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
            fontFamily = Oswald,
            fontSize = 20.sp
        )
    } else {
        Text(
            text = text,
            color = Color.Gray,
            modifier = Modifier.clickable { onClick() },
            fontFamily = Oswald,
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
fun Home(
    authviewmodel: AuthViewModel = hiltViewModel(),
    navController: NavController,
    displayrecipesviewmodel : DisplayRecipesViewModel = hiltViewModel(),
    onLoadMore: (offset: Int) -> Unit,
    loadAnother: (type: String, clear: Boolean) -> Unit,
    searchRecipes: (query: String) -> Unit,
    specificRecipe: (id: Int) -> Unit
) {
    Surface {
        val image = "https://imgs.search.brave.com/YPCn_gZZ-7-pVx_lhOks6Cgvr4UrXXzOkSPKYuXD9pY/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90NC5m/dGNkbi5uZXQvanBn/LzA5LzA5LzQ2Lzg3/LzM2MF9GXzkwOTQ2/ODcxMF8wOHVTc0Yw/clVtNlhDMmlUaWls/aFU3MUg5R3k3NU04/Qy5qcGc"
        val painter = rememberAsyncImagePainter(model = image)
        val imageState = painter.state
        var searchQuery by remember { mutableStateOf("") }
        val selectedOption = remember { mutableStateOf("BreakFast") }
        val loadingoption = remember { mutableStateOf("") }
        val recipes by displayrecipesviewmodel.homerecipes.collectAsStateWithLifecycle()
        val searchrecipes by displayrecipesviewmodel.searchrecipes.collectAsStateWithLifecycle()
        val total by displayrecipesviewmodel.total.collectAsStateWithLifecycle()
        val gridState = rememberLazyStaggeredGridState()
        val username by authviewmodel.username.collectAsState(initial = null)

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
                        Text(
                            "Hello, $username",
                            color = main,
                            fontSize = 20.sp,
                            fontFamily = Chewy
                        )
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
                            .clickable {
                                authviewmodel.signout()
                                navController.navigate(route = Screens.Landing.route)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (imageState is AsyncImagePainter.State.Loading) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.Center)
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
                                selectedOption.value = OptionRows[option].first
                                loadingoption.value = OptionRows[option].second
                                loadAnother(loadingoption.value, true)
                            }
                        )
                        AddWidth(12.dp)
                    }
                }

                if (recipes.isNotEmpty()) {
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
                } else {
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
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}