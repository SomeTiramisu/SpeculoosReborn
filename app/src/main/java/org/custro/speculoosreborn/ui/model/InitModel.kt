package org.custro.speculoosreborn.ui.model

import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.room.MangaEntity
import org.custro.speculoosreborn.utils.CacheUtils
import org.custro.speculoosreborn.utils.MangaUtils
import org.custro.speculoosreborn.utils.emptyBitmap

class InitModel: ViewModel() {

    val mangas: LiveData<List<MangaEntity>> = Transformations.map(App.db.mangaDao().getAll()) {
        it.map { entity ->
            viewModelScope.launch(Dispatchers.Default) {
                MangaUtils.correctManga(entity)
            }
            entity
        }
    }
}