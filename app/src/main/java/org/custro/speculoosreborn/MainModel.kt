package org.custro.speculoosreborn

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainModel: ViewModel() {
    private val _openReadNow: MutableLiveData<Boolean?> = MutableLiveData(false)
    val openReadNow: LiveData<Boolean?> = _openReadNow
}