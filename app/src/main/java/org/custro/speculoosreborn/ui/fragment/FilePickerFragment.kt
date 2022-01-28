package org.custro.speculoosreborn.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.custro.speculoosreborn.databinding.FragmentFilepickerBinding
import org.custro.speculoosreborn.databinding.FragmentInitBinding
import org.custro.speculoosreborn.ui.FilePickerScreen
import org.custro.speculoosreborn.ui.InitScreen
import org.custro.speculoosreborn.ui.model.FilePickerModel
import org.custro.speculoosreborn.ui.model.InitModel

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
class FilePickerFragment : Fragment() {
    private var _binding: FragmentFilepickerBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val model: FilePickerModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilepickerBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.composeView.setContent {
            FilePickerScreen(model)
        }

        //findNavController().navigate(R.id.action_initFragment_to_settingsFragment)
        return view
    }
}