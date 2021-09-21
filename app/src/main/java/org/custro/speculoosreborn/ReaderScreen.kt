package org.custro.speculoosreborn

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
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
    Background(bitmap = background, width = size.first, height = size.second)
    Page(bitmap = image)
    TapBox(
        index = index,
        maxIndex = maxIndex,
        onIndexChange = { readerModel.onIndexChange(it) },
        width = size.first,
        hiddenSlider = hiddenSlider,
        onHiddenSliderChange = { readerModel.onHiddenSliderChange(it) }
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
fun Page(bitmap: ImageBitmap) {
    // set up all transformation states
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        /*if( 0.9f < scale && scale < 1.1f ) {
            scale = 1f
        }*/
        rotation += rotationChange
        /*if ( -3f < scale && scale < 3f ) {
            rotation = 0f
        }*/
        offset += offsetChange
    }
    Image(
        bitmap = bitmap,
        contentDescription = "page",
        modifier = Modifier.fillMaxSize()/*.transformable(state = state).graphicsLayer(
            scaleX = scale,
            scaleY = scale,
            rotationZ = rotation,
            translationX = offset.x,
            translationY = offset.y
        )*/
    )
}

@Composable
fun TapBox(
    index: Int,
    maxIndex: Int,
    onIndexChange: (Int) -> Unit,
    width: Int,
    hiddenSlider: Boolean,
    onHiddenSliderChange: (Boolean) -> Unit
) {
    val key = index + maxIndex * (if (hiddenSlider) 0 else 1)
    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(key) {
            detectTapGestures { offset ->  //onTap
                Log.d("TapBox", "Touched, $index, $maxIndex")
                if (offset.x > 2 * width / 3 && index < maxIndex - 1) {
                    onIndexChange(index + 1)
                } else if (offset.x < width / 3 && index > 0) {
                    onIndexChange(index - 1)
                }
                if (!hiddenSlider && index >= 0 && index <= maxIndex - 1) {
                    onHiddenSliderChange(true)
                } else if (offset.x > width / 3 && offset.x < 2 * width / 3) {
                    onHiddenSliderChange(false)
                }
            }
        })
}

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
