package org.custro.speculoosreborn

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.custro.speculoosreborn.databinding.ActivityMainBinding
import org.custro.speculoosreborn.ui.model.InitModel
import org.custro.speculoosreborn.ui.model.ReaderModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        //WindowCompat.setDecorFitsSystemWindows(window, false)

        showOnCutout()
        setContentView(view)
        /*
        setContent {
            AppTheme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = colors.isLight
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                }

                MainNavigation(
                    readerModel = readerModel,
                    initModel = initModel,
                    showSystemUi = { showSystemUiNew() },
                    hideSystemUi = { hideSystemUiNew() }
                )
            }
        }*/
    }

    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    private fun checkPermissions() {
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                !Environment.isExternalStorageManager()
            } else {
                true
            }
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val intent = Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    ),
                    1
                )
            }
        }
    }

    private fun showOnCutout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

}