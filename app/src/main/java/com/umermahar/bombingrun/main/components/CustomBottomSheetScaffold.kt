package com.umermahar.bombingrun.main.components

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.umermahar.bombingrun.main.components.ExpandedType.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheetScaffold(
    sheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val bottomSheetSt = rememberStandardBottomSheetState(
        skipHiddenState = true,
        initialValue = SheetValue.PartiallyExpanded
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetSt)
    var peekHeight: Int by remember { mutableIntStateOf(0) }

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            BottomSheetGestureWrapper(
                onExpandTypeChanged = {
                    scope.launch {
                        peekHeight = when (it) {
                            COLLAPSED -> 90
                            FULL -> screenHeight - 46
                            HALF -> screenHeight / 2
                            SEMI_FULL -> (screenHeight / 1.5).toInt()
                        }
                        bottomSheetSt.partialExpand() // Smooth animation to desired height
                    }
                }
            ) {
                sheetContent()
            }
        },
        sheetPeekHeight = peekHeight.dp, // <------- Important
        modifier = Modifier.fillMaxSize(),
        sheetShadowElevation = 0.dp,
        sheetDragHandle = null,
    ) {
        content(it)
    }
}


@Composable
private fun BottomSheetGestureWrapper(
    modifier: Modifier = Modifier,
    onExpandTypeChanged: (ExpandedType) -> Unit,
    content: @Composable () -> Unit
) {

    var expandedType by remember {
        mutableStateOf(COLLAPSED)
    }

    var isUpdated = false

    LaunchedEffect(key1 = expandedType) {
        onExpandTypeChanged(expandedType)
    }

    Box(
        modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        if (!isUpdated) {
                            expandedType = when {
                                dragAmount < 0 && expandedType == COLLAPSED -> {
                                    HALF
                                }

                                dragAmount < 0 && expandedType == HALF -> {
                                    SEMI_FULL
                                }

                                dragAmount < 0 && expandedType == SEMI_FULL -> {
                                    FULL
                                }

                                dragAmount > 0 && expandedType == FULL -> {
                                    SEMI_FULL
                                }

                                dragAmount > 0 && expandedType == SEMI_FULL -> {
                                    HALF
                                }

                                dragAmount > 0 && expandedType == HALF -> {
                                    COLLAPSED
                                }

                                else -> {
                                    expandedType
                                }
                            }
                            isUpdated = true
                        }
                    },
                    onDragEnd = {
                        isUpdated = false
                    }
                )
            }
    ) {
        content()
    }
}

enum class ExpandedType {
    HALF, SEMI_FULL, FULL, COLLAPSED
}