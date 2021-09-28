package org.custro.speculoosreborn

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.room.Manga
import org.custro.speculoosreborn.room.correctManga
import org.custro.speculoosreborn.room.isMangaValid

class InitModel: ViewModel() {

    fun getMangas(): LiveData<List<Manga>> {
        return  Transformations.map(App.db.mangaDao().getAll()) { it ->
            it.map { m ->
                viewModelScope.launch(Dispatchers.Default) {
                    correctManga(m)
                }
                m
            }.filter { m ->
                isMangaValid(m)
            }
        }
    }

    fun deleteManga(uri: String) {
        viewModelScope.launch(Dispatchers.Default) {
            App.db.mangaDao().deleteUri(uri)
            Log.d("MainModel", "deleted: $uri")
        }
    }
}
