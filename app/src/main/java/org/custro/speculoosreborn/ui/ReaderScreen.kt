package org.custro.speculoosreborn.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.custro.speculoosreborn.ui.model.ReaderModel

@ExperimentalAnimationApi
@Composable
fun ReaderScreen(readerModel: ReaderModel = viewModel()) {
    val index: Int by readerModel.index.observeAsState(0)
    val maxIndex: Int by readerModel.maxIndex.observeAsState(0)
    val image: ImageBitmap by readerModel.image.observeAsState(ImageBitmap(1, 1))
    val isBlackBorders: Boolean by readerModel.isBlackBorders.observeAsState(false)
    val hiddenSlider: Boolean by readerModel.hiddenSlider.observeAsState(false)
    val background: ImageBitmap by readerModel.background.observeAsState(ImageBitmap(1, 1))
    Log.d("TapBox", "hiddenSlider: $hiddenSlider")
    TiledImage(bitmap = background, contentDescription = "background")
    Page(bitmap = image,
        isBlackBorders = isBlackBorders,
        onIndexInc = { readerModel.onIndexInc() },
        onIndexDec = { readerModel.onIndexDec() },
        onHiddenSliderSwitch = { readerModel.onHiddenSliderSwitch() },
        onHiddenSliderHid = { readerModel.onHiddenSliderChange(true) },
        onSizeChange = { readerModel.onSizeChange(it) }
    )
    SliderView(
        index = index,
        maxIndex = maxIndex,
        onIndexChange = { readerModel.onIndexChange(it) },
        hidden = hiddenSlider
    )
}

@Composable
fun Page(
    bitmap: ImageBitmap,
    isBlackBorders: Boolean,
    onIndexInc: () -> Unit,
    onIndexDec: () -> Unit,
    onHiddenSliderSwitch: () -> Unit,
    onHiddenSliderHid: () -> Unit,
    onSizeChange: (Pair<Int, Int>) -> Unit
) {
    // set up all transformation states
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        val newScale = scale * zoomChange
        scale = if (newScale < 1.05f) {
            1f
        } else {
            newScale
        }
        val newRotation = (rotation + rotationChange) % 360
        rotation = if (-1.5f < newRotation && newRotation < 1.5f || scale < 1.05f) {
            0f
        } else {
            newRotation
        }
        val newOffset = offset + offsetChange
        //Log.d("Offset", "${newOffset}")
        offset = if (newOffset.getDistanceSquared() < 1000 || scale < 1.05f) {
            Offset.Zero
        } else {
            newOffset
        }
    }
    Box(
        modifier = Modifier.tapDetect(
            onIndexInc,
            onIndexDec,
            onHiddenSliderSwitch,
            onHiddenSliderHid
        )
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = "page",
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { onSizeChange(Pair(it.size.width, it.size.height)) }
                .transformable(state = state)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    rotationZ = 0F,//rotation,
                    translationX = offset.x,
                    translationY = offset.y
                )
        )
    }
}

fun Modifier.tapDetect(
    onIndexInc: () -> Unit,
    onIndexDec: () -> Unit,
    onHiddenSliderSwitch: () -> Unit,
    onHiddenSliderHid: () -> Unit
): Modifier = then(run {
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
fun IndexSlider(
    index: Int,
    maxIndex: Int,
    onIndexChange: (Int) -> Unit,
    onSlidingIndexChange: (Int) -> Unit
) {
    var sliderPosition by remember { mutableStateOf(0.0f) }
    var sliding by remember { mutableStateOf(false) }
    Slider(value = if (sliding) sliderPosition else index.toFloat(),
        steps = 0,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        onValueChange = {
            sliding = true; sliderPosition = it; onSlidingIndexChange(sliderPosition.toInt())
        },
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
    var dispIndex by remember { mutableStateOf(index) }
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
                text = dispIndex.toString(),
                style = TextStyle(shadow = Shadow(Color.White, Offset.Zero, 4F))
            )
            IndexSlider(
                index = index,
                maxIndex = maxIndex,
                onIndexChange = onIndexChange,
                onSlidingIndexChange = { dispIndex = it }
            )
        }
    }
}

@Composable
fun TiledImage(
    bitmap: ImageBitmap,
    contentDescription: String?,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    var width: Int by remember { mutableStateOf(0) }
    var height: Int by remember { mutableStateOf(0) }
    val nWidth: Int by remember { mutableStateOf(width / bitmap.width + 2) }
    val nHeight: Int by remember { mutableStateOf(height / bitmap.height + 2) }
    Column(modifier = Modifier.onGloballyPositioned {
        width = it.size.width; height = it.size.height
    }) {
        repeat(nWidth) {
            Row {
                repeat(nHeight) {
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