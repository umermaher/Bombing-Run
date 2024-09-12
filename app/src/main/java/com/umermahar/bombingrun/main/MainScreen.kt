package com.umermahar.bombingrun.main

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umermahar.bombingrun.R
import com.umermahar.bombingrun.main.components.BombingRunTableSheetContent
import com.umermahar.bombingrun.main.components.CustomBottomSheetScaffold
import com.umermahar.bombingrun.main.components.SelectBombResultBottomSheet
import com.umermahar.bombingrun.main.components.polygon.RandomPolygonWithDimensions
import com.umermahar.bombingrun.utils.GeneralBottomSheet
import com.umermahar.bombingrun.utils.OptionSheetCard
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.state.collectAsState()
    MainScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    state: MainState,
    onEvent: (MainEvent) -> Unit
) {

    val activity = LocalContext.current as Activity

    val scope = rememberCoroutineScope()

    val selectLevelSheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        activity.finishAndRemoveTask()
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Reset"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onEvent(MainEvent.ToggleLevelSheet)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_level),
                            contentDescription = "Level",
                            tint = LocalContentColor.current,
                            modifier = Modifier.size(24.dp),
                        )
                    }

                    IconButton(onClick = {
                        onEvent(MainEvent.OnResetButtonClick)
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Reset"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            Box(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    modifier = Modifier
                        .padding(10.dp),
                    text = stringResource(id = R.string.score) + ": " + state.score,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                RandomPolygonWithDimensions(
                    polygon = state.polygon,
                    bombedPoints = state.bombedPoints
                )
            }

            BombingRunTableSheetContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                simulatedBombingRun = state.simulatedBombingRun,
                onGuessBombClick = { bomb, index ->
                    onEvent(MainEvent.OnGuessBombClick(bomb = bomb, index = index))
                }
            )
        }
    }

    if (state.shouldShowSelectBombResultSheet) {
        state.selectedBomb?.let {
            SelectBombResultBottomSheet(
                selectedBomb = it,
                state.bombResultOptions,
                scope = scope,
                onEvent = onEvent
            )
        }
    }

    if (state.shouldShowSelectLevelSheet) {
        GeneralBottomSheet(
            sheetState = selectLevelSheetState,
            titleRes = R.string.level,
            onDismissRequest = {
                onEvent(MainEvent.ToggleLevelSheet)
            }
        ) {
            state.levelOptions.forEach { option ->
                OptionSheetCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    txtRes = option.textRes,
                    isSelected = state.level == option.level
                ) {
                    scope.launch {
                        selectLevelSheetState.hide()
                        onEvent(MainEvent.OnLevelSelected(option.level))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRandomPolygonScreen() {
    MainScreen()
}
