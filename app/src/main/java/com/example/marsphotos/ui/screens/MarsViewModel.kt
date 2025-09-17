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

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.ui.screens.network.MarsApi
import com.example.marsphotos.ui.screens.network.MarsPhoto
import com.example.marsphotos.ui.screens.repository.MarsDao
import com.example.marsphotos.ui.screens.repository.MarsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

sealed interface MarsUiState {
    data class Success(val numPhotosText: String) : MarsUiState
    object Error: MarsUiState
    object Loading: MarsUiState
}

@HiltViewModel
class MarsViewModel @Inject constructor(application: Application, dao: MarsDao) : AndroidViewModel(application) {
    /** The mutable State that stores the status of the most recent request */
    var marsUiState: MarsUiState by mutableStateOf(MarsUiState.Loading)
        private set

    val marsRepository: MarsRepository = MarsRepository(dao)
    //fun getMarsPhotoList() = marsPhotoMap.values.toList()



    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getMarsPhotos()
    }

    /**
     * Gets Mars photos information from the Mars API Retrofit service.
     */
    private fun getMarsPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            marsUiState = MarsUiState.Loading
            val repositoryLoadJob = launch {
                delay(50)
                marsRepository.init()
            }
            marsUiState = try {
                //val listResult = MarsApi.getPhotosTest()
                val listResult = MarsApi.retrofitService.getPhotos()

                /**
                 *  Test module to split result
                 *
                 */
                val listResultSplit = mutableListOf<MarsPhoto>()
                repositoryLoadJob.join()
                val start = marsRepository.getMarsPhotoList().size
                val end = if(listResult.size>start+4)
                     start+4
                else
                     listResult.size - 1
                for(i in start..end){
                    listResultSplit.add(listResult[i])
                }
                /**
                 *
                 *
                 */
                marsRepository.putAllMarsPhoto(marsPhoto = Array<MarsPhoto>(listResultSplit.size) { i: Int -> listResultSplit[i] })
                marsRepository.putAll(marsPhoto =  Array<MarsPhoto>(listResultSplit.size) { i: Int -> listResultSplit[i] })
                MarsUiState.Success("Success: ${marsRepository.getMarsPhotoList().size} Mars photo retrieved")
            } catch (e: IOException){
                repositoryLoadJob.join()
                MarsUiState.Error
            } catch (e: HttpException) {
                repositoryLoadJob.join()
                MarsUiState.Error
            }
        }
    }
}

