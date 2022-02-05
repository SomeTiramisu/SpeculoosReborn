package org.custro.speculoosreborn.ui.fragment

import android.graphics.BitmapFactory
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.os.HandlerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.slider.Slider
import org.bouncycastle.pqc.math.linearalgebra.GF2nONBElement
import org.custro.speculoosreborn.databinding.FragmentReaderBinding
import org.custro.speculoosreborn.ui.model.ReaderModel

@ExperimentalAnimationApi
class ReaderFragment : Fragment() {
    private var _binding: FragmentReaderBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //Un seul viewModel pour tous les fragments. Similaire à compose. On peut zussi sauvearder
    // la derniere page dans la DB
    private val model: ReaderModel by viewModels({requireParentFragment()})

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

        binding.nextButton.setOnClickListener {
            model.onIndexInc()
        }
        binding.previousButton.setOnClickListener {
            model.onIndexDec()
        }
        binding.middleButton.setOnClickListener {
            when(binding.pageBottomSheet.visibility) {
                VISIBLE -> binding.pageBottomSheet.visibility = GONE
                GONE -> binding.pageBottomSheet.visibility = VISIBLE
                INVISIBLE -> Unit
            }
        }



        //findNavController().navigate(R.id.action_initFragment_to_settingsFragment)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val background = BitmapDrawable(resources, BitmapFactory.decodeStream(context?.assets?.open("background.png")))
        background.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        binding.pageImageView.background = background

        //TODO: check why correct size is set
        model.image.observe(viewLifecycleOwner) {
            binding.pageImageView.setImageBitmap(it.asAndroidBitmap())
        }

        val slider = binding.pageSlider
        slider.isTickVisible = false
        slider.valueFrom = 0f
        model.maxIndex.observe(viewLifecycleOwner) {
            slider.valueTo = if(slider.valueFrom < it)  it.toFloat()  else  slider.valueFrom + 1
        }
        slider.stepSize = 1f
        slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being started
            }

            override fun onStopTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being stopped
                model.onIndexChange(slider.value.toInt())
            }
        })
        model.index.observe(viewLifecycleOwner) {
            slider.value = it.toFloat()
        }
    }

    override fun onResume() {
        super.onResume()
        //Display on cutout (ignore it)
        //delay for hiding after rotation (restart)
        Handler(Looper.getMainLooper()).postDelayed({
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
            //set fullscreen
            ViewCompat.getWindowInsetsController(requireView())?.apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            //100ms needed to get correct size
            Handler(Looper.getMainLooper()).postDelayed({ model.onSizeChange(Pair(binding.pageImageView.width, binding.pageImageView.height))}, 100)
        }, 300)

        //Log.d("ReaderFragment","Fragment resumed")
    }

    override fun onStop() {
        super.onStop()
        //restore system bars
        ViewCompat.getWindowInsetsController(requireView())?.show(WindowInsetsCompat.Type.systemBars())
        //restore cutouts
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
    }

}