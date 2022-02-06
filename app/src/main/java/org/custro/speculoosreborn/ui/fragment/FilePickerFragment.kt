package org.custro.speculoosreborn.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu.NONE
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.custro.speculoosreborn.databinding.FragmentFilePickerBinding
import org.custro.speculoosreborn.ui.FileListAdapter
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


        val adapter = FileListAdapter {}

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }


        binding.menu.setOnClickListener { v ->
            val popup = PopupMenu(context, v)
            model.externalDirs.onEachIndexed { index, file ->
                popup.menu.add(NONE, index, NONE, model.getExternalDirName(index))
            }
            popup.setOnMenuItemClickListener { menuItem ->
                binding.menu.setText(menuItem.title)
                //Log.d("FilePicker", model.externalDirs[menuItem.itemId].listFiles()!!.toList().map { Uri.fromFile(it) }.toString())
                adapter.submitList(model.externalDirs[menuItem.itemId].listFiles()!!.toList())
                true
            }
            popup.show()

        }

    }

}