package com.umermahar.bombingrun.main

import androidx.compose.ui.geometry.Offset
import com.umermahar.bombingrun.R

data class MainState(
    val polygon: Polygon = Polygon(), // Polygon points on Screen
    val bombedPoints: List<Bomb> = emptyList(),
    val simulatedBombingRun: List<Bomb> = emptyList(),
    val shouldShowSelectBombResultSheet: Boolean = false,
    val shouldShowSelectLevelSheet: Boolean = false,
    val selectedBomb: Bomb? = null,
    val bombResultOptions: List<BombResultOption> = listOf(
        BombResultOption(
            bombResult = BombResult.HIT,
            textRes = R.string.hit
        ),
        BombResultOption(
            bombResult = BombResult.MISS,
            textRes = R.string.miss
        )
    ),
    val levelOptions: List<LevelOption> = listOf(
        LevelOption(
            level = Level.NORMAL,
            textRes = R.string.normal
        ),
        LevelOption(
            level = Level.HARD,
            textRes = R.string.hard
        )
    ),
    val score: Int = 0,
    val level: Level = Level.NORMAL
)

sealed interface MainEvent {
    data object OnResetButtonClick: MainEvent
    data class OnGuessBombClick(val bomb: Bomb, val index: Int): MainEvent
    data object ToggleSelectBombResultSheet: MainEvent
    data object ToggleLevelSheet: MainEvent
    data class SelectedBombResult(val guessedBombResult: BombResult): MainEvent
    data class OnLevelSelected(val level: Level): MainEvent
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

data class BombResultOption(
    val bombResult: BombResult,
    val textRes: Int
)

data class LevelOption(
    val level: Level,
    val textRes: Int
)

enum class BombResult {
    HIT, MISS, UNKNOWN
}

enum class Level {
    NORMAL, HARD
}