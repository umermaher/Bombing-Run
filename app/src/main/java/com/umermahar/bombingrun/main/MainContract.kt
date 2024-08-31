package com.umermahar.bombingrun.main

import androidx.compose.ui.geometry.Offset

data class MainState(
    val polygon: Polygon = Polygon(), // Polygon points on Screen
    val bombedPoints: List<Bomb> = emptyList(),
    val simulatedBombingRun: List<Bomb> = emptyList(),
    val shouldShowSelectBombResultSheet: Boolean = false,
    val selectedBomb: Bomb? = null,
    val score: Int = 0,
)

sealed interface MainEvent {
    data object OnResetButtonClick: MainEvent
    data class OnGuessBombClick(val bomb: Bomb, val index: Int): MainEvent
    data object ToggleSelectBombResultSheet: MainEvent
    data class SelectedBombResult(val guessedBombResult: BombResult): MainEvent
}

data class Polygon(
    val polygonPoints: List<Offset> = emptyList(), // Polygon points on Screen
    val polygonPointsOnGraph: List<Offset> = emptyList(), // Polygon points on Graph
    val centroid: Offset = Offset(0f, 0f)
)

data class Bomb(
    val offset: Offset,
    val result: BombResult,
    val isGuessedCorrect: Boolean = false
)

enum class BombResult {
    HIT, MISS, UNKNOWN
}