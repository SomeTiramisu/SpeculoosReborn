package org.custro.speculoosreborn.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.custro.speculoosreborn.databinding.FragmentInitBinding
import org.custro.speculoosreborn.databinding.FragmentReaderBinding
import org.custro.speculoosreborn.ui.InitScreen
import org.custro.speculoosreborn.ui.ReaderScreen
import org.custro.speculoosreborn.ui.model.InitModel
import org.custro.speculoosreborn.ui.model.ReaderModel

@ExperimentalAnimationApi
class ReaderFragment : Fragment() {
    private var _binding: FragmentReaderBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val model: ReaderModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.getParcelable<Uri>("mangaUri")?.let {
            Log.d("ReaderFragment", "Uri is $it")
            model.onUriChange(it)
        }

        _binding = FragmentReaderBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.composeView.setContent {
            ReaderScreen(model)
        }

        //findNavController().navigate(R.id.action_initFragment_to_settingsFragment)
        return view
    }

}