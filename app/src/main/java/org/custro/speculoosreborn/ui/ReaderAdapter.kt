package org.custro.speculoosreborn.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.custro.speculoosreborn.renderer.Renderer
import org.custro.speculoosreborn.ui.fragment.ReaderPageFragment

class ReaderAdapter(fragment: Fragment, private val renderer: Renderer): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return renderer.pageCount
    }

    override fun createFragment(position: Int): Fragment {
        return ReaderPageFragment(renderer, position)
    }
}