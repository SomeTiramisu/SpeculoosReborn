package org.custro.speculoosreborn.ui.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import org.custro.speculoosreborn.room.MangaEntity
import org.custro.speculoosreborn.utils.CacheUtils

class MangaCardModel(val entity: MangaEntity): ViewModel() {
    val cover = CacheUtils.get(entity.coverId)
    val name = Uri.parse(entity.uri).lastPathSegment?.split(":")?.last()
}