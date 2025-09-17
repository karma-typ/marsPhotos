/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.marsphotos.ui.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.marsphotos.R
import com.example.marsphotos.ui.screens.network.MarsPhoto

@Composable
fun HomeScreen(
    marsViewModel: MarsViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val marsUiState = marsViewModel.marsUiState
    val marsPhotoList = marsViewModel.marsRepository.getMarsPhotoList()
    when(marsUiState){
        is MarsUiState.Success -> ResultScreen(marsUiState.numPhotosText, marsPhotoList, Modifier.padding(contentPadding))
        is MarsUiState.Error ->
            if(marsPhotoList.isEmpty())
                ErrorScreen(modifier.fillMaxSize())
            else {
                val text = stringResource(R.string.loading_failed) +
                        if(marsPhotoList.size == 1)
                            ". 1 " + stringResource(R.string.loading_failed_was_saved)
                        else
                            ". ${marsPhotoList.size} " + stringResource(R.string.loading_failed_were_saved)
                ResultScreen(
                    text,
                    marsPhotoList,
                    Modifier.padding(contentPadding)
                )
            }
        is MarsUiState.Loading -> LoadingScreen( modifier.fillMaxSize())
    }
}

/**
 * ResultScreen displaying photos retrieved.
 */
@Composable
fun ResultScreen(numPhotosText: String, marsPhotoList: List<MarsPhoto>, modifier: Modifier = Modifier) {
    Column (
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = numPhotosText, modifier = Modifier.alpha(0.5F), fontSize = 12.sp)
        }
        //Spacer(Modifier.fillMaxWidth().height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp)
        ) {
            items(marsPhotoList){ item ->
                PhotoCard(item.imgSrc)
                //PhotoCard("https://mars.jpl.nasa.gov/msl-raw-images/msss/01000/mcam/1000MR0044630880503648E02_DXXX.jpg")
            }
        }
    }
}

@Composable
fun PhotoCard(imageUrl: String ,modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.height(100.dp).clip(MaterialTheme.shapes.medium).background(Color.Gray),
        contentAlignment = Alignment.Center,

    ){
        AsyncImage(
            model = imageUrl,
            //painter = painterResource(id = R.drawable.test_photo),
            contentDescription = stringResource(R.string.mars_photo),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading),
        modifier = modifier.size(200.dp)
    )
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
    }
}











@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
        Column {
            //ResultScreen("marsUiState.photos", Modifier.fillMaxWidth())
            Spacer(Modifier.fillMaxWidth())
            LoadingScreen(Modifier.fillMaxSize())
        }
}
