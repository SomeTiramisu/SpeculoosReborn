package org.custro.speculoosreborn.ui.fragment

import android.os.Bundle
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

class ReaderPageFragment(renderer: Renderer, index: Int) : Fragment() {
    private var _binding: FragmentReaderPageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val model: ReaderPageModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return modelClass.getConstructor(Renderer::class.java, Int::class.java)
                    .newInstance(renderer, index)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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