package org.custro.speculoosreborn.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.custro.speculoosreborn.R
import org.custro.speculoosreborn.databinding.FragmentInitBinding
import org.custro.speculoosreborn.ui.InitScreen
import org.custro.speculoosreborn.ui.model.InitModel

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
        binding.composeView.setContent {
            InitScreen(
                initModel = model,
                navigateToFilePicker = { findNavController().navigate(R.id.action_initFragment_to_filePickerFragment) },
                setManga = { /*TODO*/ },
                navigateToReaderScreen = { findNavController().navigate(R.id.action_initFragment_to_readerFragment) },
                navigateToSettingsScreen = { findNavController().navigate(R.id.action_initFragment_to_settingsFragment)}
            )
        }

        //findNavController().navigate(R.id.action_initFragment_to_settingsFragment)
        return view
    }
}