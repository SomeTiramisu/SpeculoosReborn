package org.custro.speculoosreborn.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.R
import org.custro.speculoosreborn.databinding.FragmentInitBinding
import org.custro.speculoosreborn.ui.MangaCardAdapter
import org.custro.speculoosreborn.ui.model.InitModel
import org.custro.speculoosreborn.ui.model.MangaCardModel
import org.custro.speculoosreborn.utils.CacheUtils
import org.custro.speculoosreborn.utils.MangaUtils

class InitFragment : Fragment() {
    private var _binding: FragmentInitBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val getContent =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                Log.d("InitFragment", uri.toString())

                requireActivity().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {  //ugly ?
                    val entity = MangaUtils.genMangaEntity(uri)
                    App.db.mangaDao().insert(entity)
                }

            }
        }

    private val model: InitModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInitBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView
        val adapter = MangaCardAdapter(viewLifecycleOwner) {
            //TODO: handle restart with new file and same uri
            val bundle = bundleOf("mangaUri" to it)
            Log.d("SEE", "navigating")
            findNavController().navigate(
                R.id.action_initFragment_to_readerFragment,
                bundle
            )
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter


        model.mangas.observe(viewLifecycleOwner) {
            Log.d("InitFragment", "Mangas: $it")
            adapter.submitList(it.map(::MangaCardModel))
        }


        val appBar = binding.toolbar
        appBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add -> {
                    //findNavController().navigate(R.id.action_initFragment_to_filePickerFragment)
                    getContent.launch(arrayOf("*/*"))
                    true
                }
                R.id.settings -> {
                    findNavController().navigate(R.id.action_initFragment_to_settingsFragment)
                    true
                }
                else -> false
            }
        }
    }
}