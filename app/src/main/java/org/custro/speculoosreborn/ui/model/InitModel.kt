package org.custro.speculoosreborn.ui.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.room.MangaEntity
import org.custro.speculoosreborn.utils.MangaUtils

class InitModel : ViewModel() {

    val mangas: LiveData<List<MangaEntity>> = liveData {
        emit(App.db.mangaDao().getAll().map { entity ->
            withContext(Dispatchers.Default) {
                MangaUtils.correctManga(entity)
            }
            Log.d("InitModel", "mapped to entity")
            entity
        })
    }
}