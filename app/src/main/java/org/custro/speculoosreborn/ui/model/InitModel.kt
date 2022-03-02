package org.custro.speculoosreborn.ui.model

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.room.MangaEntity
import org.custro.speculoosreborn.utils.MangaUtils

class InitModel : ViewModel() {
/*
    val mangas: LiveData<List<MangaEntity>> = liveData {
        emit(App.db.mangaDao().getAll().map { entity ->
            withContext(Dispatchers.Default) {
                MangaUtils.correctManga(entity)
            }
            Log.d("InitModel", "mapped to entity")
            entity
        })
    }*/

    val mangas: LiveData<List<MangaEntity>> = App.db.mangaDao().getDistinctAllFlow().map {
        it.map { entity ->
            withContext(Dispatchers.Default) {
                MangaUtils.correctManga(entity)
            }
            Log.d("InitModel", "mapped to entity")
            entity

        }
    }.asLiveData(viewModelScope.coroutineContext)
/*
    val mangas: LiveData<List<MangaEntity>> =  App.db.mangaDao().getAllLiveData().map {
        it.map { entity ->
            viewModelScope.launch(Dispatchers.Default) {
                MangaUtils.correctManga(entity)
            }
            Log.d("InitModel", "mapped to entity")
            entity

        }
    }*/
}