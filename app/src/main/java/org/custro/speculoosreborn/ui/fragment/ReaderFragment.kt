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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.slider.Slider
import org.custro.speculoosreborn.databinding.FragmentReaderBinding
import org.custro.speculoosreborn.ui.ReaderAdapter
import org.custro.speculoosreborn.ui.model.ReaderModel
import org.custro.speculoosreborn.utils.fromByteArray
import org.custro.speculoosreborn.utils.matToBitmap

class ReaderFragment : Fragment() {
    private var _binding: FragmentReaderBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //Un seul viewModel pour tous les fragments. Similaire à compose. On peut zussi sauvearder
    // la derniere page dans la DB
    //private val model: ReaderModel by viewModels({requireParentFragment()})
    private var _model: ReaderModel? = null
    private val model get() = _model!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.getParcelable<Uri>("mangaUri")?.let {
            Log.d("ReaderFragment", "Uri is $it")

            _model = ViewModelProvider(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return modelClass.getConstructor(Uri::class.java).newInstance(it)
                }
            }).get()

        }

        _binding = FragmentReaderBinding.inflate(inflater, container, false)
        val view = binding.root

        //findNavController().navigate(R.id.action_initFragment_to_settingsFragment)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        //buttons
        binding.nextButton.setOnClickListener {
            model.onIndexInc()
        }
        binding.previousButton.setOnClickListener {
            model.onIndexDec()
        }*/
        binding.middleButton.setOnClickListener {
            when (binding.pageBottomSheet.visibility) {
                VISIBLE -> binding.pageBottomSheet.visibility = GONE
                GONE -> binding.pageBottomSheet.visibility = VISIBLE
                INVISIBLE -> Unit
            }
        }

        //background
        //TODO: make this readable
        if (PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getBoolean("enable_background", false)
        ) {
            val settBackgroundUri = Uri.parse(
                PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .getString("background", "")
            )
            if (settBackgroundUri != Uri.EMPTY) {
                requireActivity().contentResolver.openInputStream(settBackgroundUri)?.readBytes()
                    ?.let {
                        val background = BitmapDrawable(resources, matToBitmap(fromByteArray(it)))
                        background.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
                        binding.pageView.background = background
                    }
            }
        } else { //TODO remove non-free background from assets
            val background = BitmapDrawable(
                resources,
                BitmapFactory.decodeStream(context?.assets?.open("background.png"))
            )
            background.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
            binding.pageView.background = background
        }


        //viewpager
        val pageView = binding.pageView
        pageView.offscreenPageLimit = 4
        model.renderer.observe(viewLifecycleOwner) { renderer ->
            binding.pageView.adapter = ReaderAdapter(this, renderer)
        }
        pageView.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
 binding.pageBottomSheet.visibility = GONE
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.pageSlider.value = position.toFloat()
            }
        })

        //slider
        val slider = binding.pageSlider
        slider.isTickVisible = false
        slider.valueFrom = 0f
        model.maxIndex.observe(viewLifecycleOwner) {
            slider.valueTo = if (slider.valueFrom < it) it.toFloat() else slider.valueFrom + 1
        }
        slider.stepSize = 1f
        slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being started
            }

            override fun onStopTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being stopped
                pageView.currentItem = slider.value.toInt()
            }
        })

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
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            //100ms needed to get correct size
            Handler(Looper.getMainLooper()).postDelayed({
                /*model.onSizeChange(
                    Pair(
                        binding.pageImageView.width,
                        binding.pageImageView.height
                    )
                )*/
            }, 100)
        }, 300)

        //Log.d("ReaderFragment","Fragment resumed")
    }

    override fun onStop() {
        super.onStop()
        //restore system bars
        ViewCompat.getWindowInsetsController(requireView())
            ?.show(WindowInsetsCompat.Type.systemBars())
        //restore cutouts
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
    }

}