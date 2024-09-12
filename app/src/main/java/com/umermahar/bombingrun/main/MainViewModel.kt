package com.umermahar.bombingrun.main

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class MainViewModel(
    private val polygonHelper: PolygonHelper = PolygonHelper()
): ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    private var currentSelectedBombIndex: Int? = null

    init {
        reset()
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            MainEvent.OnResetButtonClick -> reset()
            is MainEvent.OnGuessBombClick -> {
                if(event.bomb.result != BombResult.UNKNOWN)
                    return
                currentSelectedBombIndex = event.index
                _state.update {
                    it.copy(
                        selectedBomb = event.bomb,
                        shouldShowSelectBombResultSheet = true
                    )
                }
            }

            MainEvent.ToggleSelectBombResultSheet -> _state.update {
                it.copy(shouldShowSelectBombResultSheet = !it.shouldShowSelectBombResultSheet)
            }

            is MainEvent.SelectedBombResult -> {
                onEvent(MainEvent.ToggleSelectBombResultSheet)
                guessBomb(event.guessedBombResult)
            }

            MainEvent.ToggleLevelSheet -> _state.update {
                it.copy(shouldShowSelectLevelSheet = !it.shouldShowSelectLevelSheet)
            }

            is MainEvent.OnLevelSelected -> reset(level = event.level)
        }
    }

    private fun reset(level: Level = state.value.level) {
        _state.update {

            val polygonPoints = polygonHelper.generateRandomPoints(
                sides = 6, level = level
            )
            val centroid = polygonPoints.getCentroid()
            // Calculate polygonPoints on graph
            val polygonPointsOnGraph = polygonPoints.getPolygonPointsOnGraph()

            Log.i("centroid", centroid.toString())
            Log.i("polygonPoints", polygonPoints.toString())
            Log.i("polygonPointsOnGraph", polygonPointsOnGraph.toString())

            MainState(
                polygon = Polygon(
                    polygonPoints = polygonPoints,
                    polygonPointsOnGraph = polygonPointsOnGraph,
                    centroid = centroid
                ),
                simulatedBombingRun = polygonHelper.generateRandomOffsetsFromPolygonVertices(
                    polygonPoints = polygonPointsOnGraph
                ).map { offset ->
                    Bomb(
                        offset = offset,
                        result = BombResult.UNKNOWN
                    )
                },
                level =  level
            )
        }
    }

    private fun List<Offset>.getCentroid(): Offset {
        // Calculate the centroid as the pivot point
        val sumX = sumOf { it.x.toDouble() }
        val sumY = sumOf { it.y.toDouble() }
        val centroid = Offset(sumX.toFloat() / size, sumY.toFloat() / size)
        return centroid
    }

    private fun List<Offset>.getPolygonPointsOnGraph(): List<Offset> {
        // Calculate polygonPoints on graph
        val centroid = getCentroid()
        return map { point ->
            val relativeX = point.x - centroid.x
            val relativeY = point.y - centroid.y
            /**
             * Invert the y values: positive becomes negative, negative becomes positive
             * On the screen, y values on the screen are negative when they are above the centroid
             * On the respective graph created by user virtually, y values should be positive as above the centroid, y is positive
             * example:
             *      |  +y
             * x-   |           x+
             * -----|------------
             *      |
             *      |  -y
             * **/
            Offset(relativeX, - relativeY)
        }
    }

    private fun guessBomb(guessedResult: BombResult) {
        val bomb = state.value.selectedBomb ?: return

        if(bomb.result != BombResult.UNKNOWN || currentSelectedBombIndex == null)
            return

        val centroid = state.value.polygon.centroid
        val offset = bomb.offset

        val isPointInPolygon = polygonHelper.isPointInPolygon(
            polygonPoints = state.value.polygon.polygonPointsOnGraph,
            point = bomb.offset
        )

        val result = if(isPointInPolygon) {
            BombResult.HIT
        } else BombResult.MISS

//        convert offset on graph to offset on screen to show on the screen
        val bombedScreenPoint = Offset(
            x = offset.x + centroid.x, // Translate X relative to the centroid
            y = -offset.y + centroid.y // Invert Y and translate relative to the centroid
        )
        val bombedPoints = state.value.bombedPoints.toMutableList()
        bombedPoints.add(
            Bomb(
                offset = bombedScreenPoint,
                result = result
            )
        )

        val simulatedBombingRun = state.value.simulatedBombingRun.toMutableList()
        simulatedBombingRun.removeAt(currentSelectedBombIndex!!)

        // Guessed result will be shown instead of actual result
        simulatedBombingRun.add(
            currentSelectedBombIndex!!, bomb.copy(
                result = guessedResult,
                isGuessedCorrect = result == guessedResult
            )
        )

        _state.update {
            it.copy(
                bombedPoints = bombedPoints,
                simulatedBombingRun = simulatedBombingRun,
                score = simulatedBombingRun.count { bomb ->
                    bomb.isGuessedCorrect
                }
            )
        }
    }
}