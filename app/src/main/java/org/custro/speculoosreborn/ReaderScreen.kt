package org.custro.speculoosreborn

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@ExperimentalAnimationApi
@Composable
fun ReaderScreen(readerModel: ReaderModel = viewModel()) {
    val index: Int by readerModel.index.observeAsState(0)
    val maxIndex: Int by readerModel.maxIndex.observeAsState(0)
    val image: ImageBitmap by readerModel.image.observeAsState(ImageBitmap(1, 1))
    val hiddenSlider: Boolean by readerModel.hiddenSlider.observeAsState(false)
    val background: ImageBitmap by readerModel.background.observeAsState(ImageBitmap(1, 1))
    val size: Pair<Int, Int> by readerModel.size.observeAsState(Pair(0, 0))
    Log.d("TapBox", "hiddenSlider: $hiddenSlider")
    Background(bitmap = background, width = size.first, height = size.second)
    Page(bitmap = image,
        onIndexInc = { readerModel.onIndexInc() },
        onIndexDec = { readerModel.onIndexDec() },
        onHiddenSliderSwitch = { readerModel.onHiddenSliderSwitch() },
        onHiddenSliderHid = { readerModel.onHiddenSliderChange(true) }
    )
    SliderView(
        index = index,
        maxIndex = maxIndex,
        onIndexChange = { readerModel.onIndexChange(it) },
        hidden = hiddenSlider
    )
}


@Composable
fun Background(bitmap: ImageBitmap, width: Int, height: Int) {
    TiledImage(
        bitmap = bitmap, width = width, height = height, contentDescription = "background"
    )
}

@Composable
fun Page(bitmap: ImageBitmap,
         onIndexInc: () -> Unit,
         onIndexDec: () -> Unit,
         onHiddenSliderSwitch: () -> Unit,
         onHiddenSliderHid: () -> Unit) {
    // set up all transformation states
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        val newScale = scale*zoomChange
        scale = if( 0.95f < newScale && newScale < 1.05f ) {
            1f
        } else {
            newScale
        }
        val newRotation = rotation + rotationChange
        rotation = if ( -1.5f < newRotation && newRotation < 1.5f ) {
            0f
        } else {
            newRotation
        }
        val newOffset = offset + offsetChange
        Log.d("Offset", "${newOffset.getDistanceSquared()}")
        offset = if (newOffset.getDistanceSquared() < 1000) {
            Offset.Zero
        } else {
            newOffset
        }
    }
    Box(modifier = Modifier.tapDetect(
        onIndexInc,
        onIndexDec,
        onHiddenSliderSwitch,
        onHiddenSliderHid
    )) {
    Image(
        bitmap = bitmap,
        contentDescription = "page",
        modifier = Modifier.fillMaxSize().transformable(state = state).graphicsLayer(
            scaleX = scale,
            scaleY = scale,
            rotationZ = rotation,
            translationX = offset.x,
            translationY = offset.y
        )
    )}
}

fun Modifier.tapDetect(
    onIndexInc: () -> Unit,
    onIndexDec: () -> Unit,
    onHiddenSliderSwitch: () -> Unit,
    onHiddenSliderHid: () -> Unit
): Modifier = then( run {
    var width = 0
     Modifier
        .onGloballyPositioned { width = it.size.width }
        .pointerInput(null) {
            detectTapGestures { offset ->  //onTap
                Log.d("TapBox", "Touched, ${offset.x}, ${offset.y}, $width")
                if (offset.x > 2 * width / 3) {
                    onIndexInc()
                    onHiddenSliderHid()
                } else if (offset.x < width / 3) {
                    onIndexDec()
                    onHiddenSliderHid()
                } else if (offset.x > width / 3 && offset.x < 2 * width / 3) {
                    //Log.d("TapBox", "hiddenSlider: $hiddenSlider")
                    onHiddenSliderSwitch()
                }
            }
        }
})

@Composable
fun IndexSlider(index: Int, maxIndex: Int, onIndexChange: (Int) -> Unit) {
    var sliderPosition by remember { mutableStateOf(0.0f) }
    var sliding by remember { mutableStateOf(false) }
    Slider(value = if (sliding) sliderPosition else index.toFloat(),
        steps = 0,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        onValueChange = { sliding = true; sliderPosition = it },
        valueRange = if (maxIndex > 0) 0.0f..(maxIndex - 1).toFloat() else 0.0f..1.0f,
        onValueChangeFinished = {
            sliding = false
            onIndexChange(sliderPosition.toInt())
            Log.d("IndexSlider", "changed: $index, $maxIndex")
        }
    )
}

@ExperimentalAnimationApi
@Composable
fun SliderView(
    index: Int,
    maxIndex: Int,
    onIndexChange: (Int) -> Unit,
    hidden: Boolean = false
) {
    AnimatedVisibility(
        visible = !hidden,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = index.toString()
            )
            IndexSlider(
                index = index,
                maxIndex = maxIndex,
                onIndexChange = onIndexChange
            )
        }
    }
}

@Composable
fun TiledImage(
    bitmap: ImageBitmap,
    width: Int,
    height: Int,
    contentDescription: String?,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    val nwidth = width / bitmap.width + 2
    val nheight = height / bitmap.height + 2
    Column {
        repeat(nwidth) {
            Row {
                repeat(nheight) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = contentDescription,
                        alignment = Alignment.Center,
                        contentScale = ContentScale.None,
                        alpha = alpha,
                        colorFilter = colorFilter
                    )
                }
            }
        }
    }
}
