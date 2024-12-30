package com.example.recipeorganizer.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.recipeorganizer.ui.theme.Chewy
import com.example.recipeorganizer.ui.theme.Oswald
import com.example.recipeorganizer.ui.theme.main

@Composable
fun Home() {
    Surface {
        val image = "https://imgs.search.brave.com/YPCn_gZZ-7-pVx_lhOks6Cgvr4UrXXzOkSPKYuXD9pY/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90NC5m/dGNkbi5uZXQvanBn/LzA5LzA5LzQ2Lzg3/LzM2MF9GXzkwOTQ2/ODcxMF8wOHVTc0Yw/clVtNlhDMmlUaWls/aFU3MUg5R3k3NU04/Qy5qcGc"
        val painter = rememberAsyncImagePainter(model = image)
        val imageState = painter.state

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            ConstraintLayout (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp)
            ) {

                val (inforow) = createRefs()

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
                        modifier = Modifier.fillMaxHeight(),
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
                                color = Color.White,
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}