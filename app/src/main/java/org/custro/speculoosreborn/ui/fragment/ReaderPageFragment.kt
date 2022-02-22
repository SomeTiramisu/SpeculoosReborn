package org.custro.speculoosreborn.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.custro.speculoosreborn.databinding.FragmentReaderBinding
import org.custro.speculoosreborn.databinding.FragmentReaderPageBinding
import org.custro.speculoosreborn.renderer.Renderer
import org.custro.speculoosreborn.ui.model.ReaderModel
import org.custro.speculoosreborn.ui.model.ReaderPageModel
import org.custro.speculoosreborn.ui.model.RendererStoreModel

class ReaderPageFragment: Fragment() {
    private var _binding: FragmentReaderPageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var _uri: Uri? = null
    private val uri get() = _uri!!
    private var _index: Int? = null
    private val index get() = _index!!

    private val model: ReaderPageModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return modelClass.getConstructor(Int::class.java)
                    .newInstance(index)
            }
        }
    }


    private val rendererModel: RendererStoreModel by viewModels(::requireParentFragment) {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return modelClass.getConstructor(Uri::class.java).newInstance(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.getParcelable<Uri>("mangaUri")?.let {
            _uri = it
        }
        arguments?.getInt("index")?.let {
            _index = it
        }
        rendererModel.renderer.observe(viewLifecycleOwner) {
            model.renderer = it
        }

        _binding = FragmentReaderPageBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.image.observe(viewLifecycleOwner) {
            binding.pageImageView.setImageBitmap(it)
        }

    }

    override fun onResume() {
        super.onResume()

        //TODO: handle size change
    }
}