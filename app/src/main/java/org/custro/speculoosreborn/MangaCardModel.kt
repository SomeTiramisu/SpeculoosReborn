package org.custro.speculoosreborn

import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.libtiramisuk.utils.fromByteArray
import org.custro.speculoosreborn.libtiramisuk.utils.matToBitmap
import org.custro.speculoosreborn.libtiramisuk.utils.toByteArray
import org.custro.speculoosreborn.room.Manga

class MangaCardModel: ViewModel() {
    private lateinit var manga: Manga
    private val _cover = MutableLiveData(ImageBitmap(1, 1))
    val cover: LiveData<ImageBitmap> = _cover
    val uri
        get() = manga.uri

    fun onMangaChange(value: Manga) {
        Log.d("MangacardModel", "Display ${value.uri.toString()}")
        manga = value
        viewModelScope.launch(Dispatchers.Default) {
            val coverUri = Uri.parse(manga.cover)
            val coverImage = matToBitmap(
                fromByteArray(
                    App.instance.contentResolver.openInputStream(coverUri)!!
                        .readBytes()
                )
            ).asImageBitmap()
            _cover.postValue(coverImage)
        }
    }

}