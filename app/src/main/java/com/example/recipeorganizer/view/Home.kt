package com.example.recipeorganizer.view

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.recipeorganizer.models.dataprovider.OptionRows
import com.example.recipeorganizer.ui.theme.Bebas
import com.example.recipeorganizer.ui.theme.Chewy
import com.example.recipeorganizer.ui.theme.Oswald
import com.example.recipeorganizer.ui.theme.main
import com.example.recipeorganizer.ui.theme.sec
import com.example.recipeorganizer.viewmodel.DisplayRecipesViewModel

@Composable
fun TextRow(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    if (isSelected) {
        Text(
            text = text,
            color = main,
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
fun Recipe(text: String, image: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .height(200.dp)
            .width(150.dp)
            .background(main),
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


@Preview
@Composable
fun Home(
    displayrecipesviewmodel : DisplayRecipesViewModel = hiltViewModel(),
) {
    Surface {
        val image = "https://imgs.search.brave.com/YPCn_gZZ-7-pVx_lhOks6Cgvr4UrXXzOkSPKYuXD9pY/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90NC5m/dGNkbi5uZXQvanBn/LzA5LzA5LzQ2Lzg3/LzM2MF9GXzkwOTQ2/ODcxMF8wOHVTc0Yw/clVtNlhDMmlUaWls/aFU3MUg5R3k3NU04/Qy5qcGc"
        val painter = rememberAsyncImagePainter(model = image)
        val imageState = painter.state
        var searchQuery by remember { mutableStateOf("") }
        val selectedOption = remember { mutableStateOf("BreakFast") }
        val recipes by displayrecipesviewmodel.homerecipes.collectAsStateWithLifecycle()

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
                            "Hello, User",
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
                            .size(45.dp),
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
                        onValueChange = { searchQuery = it },
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
                            text = OptionRows[option],
                            isSelected = selectedOption.value == OptionRows[option],
                            onClick = {
                                selectedOption.value = OptionRows[option]
                            }
                        )
                        AddWidth(12.dp)
                    }
                }

                LazyVerticalStaggeredGrid(
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
                            text = "${ recipes.size } New Recipes",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    items(recipes.size) { recipe ->
                        AddHeight(80.dp)
                        Recipe(
                            text = recipes[recipe].title,
                            image = recipes[recipe].image
                        )
                    }
                }

            }
        }
    }
}