package org.custro.speculoosreborn.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu.NONE
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import org.custro.speculoosreborn.R
import org.custro.speculoosreborn.databinding.FragmentFilePickerBinding
import org.custro.speculoosreborn.renderer.MangaRenderer
import org.custro.speculoosreborn.renderer.PdfRenderer
import org.custro.speculoosreborn.ui.FileListAdapter
import org.custro.speculoosreborn.ui.QuickpathAdapter
import org.custro.speculoosreborn.ui.model.FilePickerModel

class FilePickerFragment : Fragment() {
    private var _binding: FragmentFilePickerBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val model: FilePickerModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilePickerBinding.inflate(inflater, container, false)
        val view = binding.root
        //binding.composeView.setContent {
        //    FilePickerScreen(model)
        //}
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val quickpathAdapter = QuickpathAdapter {
            model.onDirChange(it)
        }
        binding.quickPath.apply {
            layoutManager = LinearLayoutManager(context).apply { orientation = HORIZONTAL }
            adapter = quickpathAdapter
        }

        //TODO: support check may be not needed here
        val fileListAdapter = FileListAdapter({
            val uri = Uri.fromFile(it)
            if(MangaRenderer.isSupported(uri) || PdfRenderer.isSupported(uri)) {
                model.insertManga(Uri.fromFile(it))
            }
        }, {
            model.onDirChange(it)
        })

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fileListAdapter
        }

        model.currentDir.observe(viewLifecycleOwner) {
            fileListAdapter.submitList(it.listFiles()!!.toList())
            quickpathAdapter.submitDir(it)
        }


        //Overriding getFilter to show full selection everytime
        val menuAdapter = object: ArrayAdapter<String>(requireContext(), R.layout.item_externals_dir, model.externalDirs.mapIndexed { i, _ -> model.getExternalDirName(i) }) {
            override fun getFilter(): Filter {
                return object: Filter() {
                    override fun performFiltering(p0: CharSequence?): FilterResults? { return null }
                    override fun publishResults(p0: CharSequence?, p1: FilterResults?) {}
                }
            }
        }
        binding.menu.apply {
            setText(model.getExternalDirName(model.currentExternalDirIndex.value!!), false)
            setAdapter(menuAdapter)
            setOnItemClickListener { _, _, _, id ->
                model.onExternalDirChange(id.toInt())
            }
        }
    }

}