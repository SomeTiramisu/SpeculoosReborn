package org.custro.speculoosreborn.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.R
import org.custro.speculoosreborn.databinding.FragmentInitBinding
import org.custro.speculoosreborn.room.Manga
import org.custro.speculoosreborn.ui.InitScreen
import org.custro.speculoosreborn.ui.MangaCardAdapter
import org.custro.speculoosreborn.ui.model.InitModel
import org.custro.speculoosreborn.ui.model.MangaCardModel

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
class InitFragment : Fragment() {
    private var _binding: FragmentInitBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val model: InitModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInitBinding.inflate(inflater, container, false)
        val view = binding.root


        //findNavController().navigate(R.id.action_initFragment_to_filePickerFragment)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView
        val adapter = MangaCardAdapter {
            val bundle = bundleOf("mangaUri" to it)
            findNavController().navigate(R.id.action_initFragment_to_readerFragment, bundle)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        model.mangas.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        val appBar = binding.toolbar
        appBar.setTitle(R.string.app_name)
        appBar.inflateMenu(R.menu.menu_init)
        appBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.add -> {
                    findNavController().navigate(R.id.action_initFragment_to_filePickerFragment)
                    true
                }
                R.id.settings -> {
                    findNavController().navigate(R.id.action_initFragment_to_settingsFragment)
                    true
                }
                else -> false
            }
        }
        //childFragmentManager.commit {
        //    add(R.id.fragmentContainerView2, card)
        //    setReorderingAllowed(true)
        //}
    }
}