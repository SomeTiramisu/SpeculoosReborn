package org.custro.speculoosreborn.ui.model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.flow.flow
import org.custro.speculoosreborn.room.MangaEntity
import org.custro.speculoosreborn.utils.CacheUtils
import java.io.File

class MangaCardModel(val entity: MangaEntity) : ViewModel() {
    val cover = flow { emit(CacheUtils.get(entity.coverId)) }
    val name = Uri.parse(entity.uri).path?.split(":")?.last()?.split("/")?.last()
}