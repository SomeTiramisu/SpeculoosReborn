package org.custro.speculoosreborn.ui

import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.custro.speculoosreborn.renderer.Renderer
import org.custro.speculoosreborn.ui.fragment.ReaderPageFragment

class ReaderAdapter(fragment: Fragment, private val uri: Uri, private val pageCount: Int): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return pageCount
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = ReaderPageFragment()
        fragment.arguments = bundleOf("mangaUri" to uri, "index" to position)
        return fragment
    }
}