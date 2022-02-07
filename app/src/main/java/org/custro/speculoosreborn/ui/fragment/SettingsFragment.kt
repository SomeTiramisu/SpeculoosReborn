package org.custro.speculoosreborn.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.custro.speculoosreborn.R

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val getContent = registerForActivityResult(ActivityResultContracts.OpenDocument() ) { uri: Uri? ->
            if(uri != null) {
                requireActivity().contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                preferenceManager.sharedPreferences?.edit()?.putString("background", uri.toString())?.apply()
            }
        }

        findPreference<Preference>("background")?.setOnPreferenceClickListener {
            getContent.launch(arrayOf("image/*"))
            true
        }

    }

}