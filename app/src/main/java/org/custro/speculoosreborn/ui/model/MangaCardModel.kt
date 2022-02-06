package org.custro.speculoosreborn.ui.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.room.Manga
import org.custro.speculoosreborn.utils.emptyBitmap

class MangaCardModel: ViewModel() {
    private lateinit var manga: Manga
    private val _cover = MutableLiveData(emptyBitmap())
    val cover: LiveData<Bitmap> = _cover
    val uri
        get() = manga.uri
    val localUri
        get() = manga.localUri

    fun onMangaChange(value: Manga) { //assuming manga is valid
        Log.d("MangaCardModel", "Display ${value.uri}")
        manga = value
        viewModelScope.launch(Dispatchers.Default) {
            val coverUri = Uri.parse(manga.cover)
            val coverImage = BitmapFactory.decodeFile(coverUri.toFile().path)
            _cover.postValue(coverImage)
        }
    }
}