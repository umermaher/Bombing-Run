package com.umermahar.bombingrun.main

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MainViewModel: ViewModel() {

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
        }
    }

    private fun reset() {
        val polygonPoints = generateComplexPolygonPoints(sides = 6)

        // Calculate the centroid as the pivot point
        val sumX = polygonPoints.sumOf { it.x.toDouble() }
        val sumY = polygonPoints.sumOf { it.y.toDouble() }
        val centroid = Offset(sumX.toFloat() / polygonPoints.size, sumY.toFloat() / polygonPoints.size)

        // Calculate polygonPoints on graph
        val polygonPointsOnGraph = polygonPoints.map { point ->
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

        Log.i("centroid", centroid.toString())
        Log.i("polygonPoints", polygonPoints.toString())
        Log.i("polygonPointsOnGraph", polygonPointsOnGraph.toString())

        _state.update {
            MainState(
                polygon = Polygon(
                    polygonPoints = polygonPoints,
                    polygonPointsOnGraph = polygonPointsOnGraph,
                    centroid = centroid
                ),
                simulatedBombingRun = generateRandomOffsetsFromPolygonVertices(
                    polygonPoints = polygonPointsOnGraph
                ).map { offset ->
                    Bomb(
                        offset = offset,
                        result = BombResult.UNKNOWN
                    )
                },
            )
        }
    }

    private fun guessBomb(guessedResult: BombResult) {
        val bomb = state.value.selectedBomb ?: return

        if(bomb.result != BombResult.UNKNOWN || currentSelectedBombIndex == null)
            return

        val centroid = state.value.polygon.centroid
        val offset = bomb.offset

        val isPointInPolygon = isPointInPolygon(
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

    private fun isPointInPolygon(polygonPoints: List<Offset>, point: Offset): Boolean {
        var intersectionCount = 0
        val pointCount = polygonPoints.size

        for (i in polygonPoints.indices) {
            val currentPoint = polygonPoints[i]
            val nextPoint = polygonPoints[(i + 1) % pointCount]

            // Check if the ray intersects with the edge of the polygon
            if (rayIntersectsEdge(point, currentPoint, nextPoint)) {
                intersectionCount++
            }
        }

        // If the number of intersections is odd, the point is inside the polygon
        return intersectionCount % 2 != 0
    }

    private fun rayIntersectsEdge(point: Offset, vertex1: Offset, vertex2: Offset): Boolean {
        val (x, y) = point
        val (x1, y1) = vertex1
        val (x2, y2) = vertex2

        // Check if the point is outside the vertical bounds of the edge
        if (y < minOf(y1, y2) || y > maxOf(y1, y2)) {
            return false
        }

        // Check if the point is to the right of both endpoints of the edge
        if (x > maxOf(x1, x2)) {
            return false
        }

        // Calculate the intersection of the ray with the edge
        if (x < minOf(x1, x2)) {
            return true
        }

        // If x is between x1 and x2, calculate the intersection
        val slope = (y2 - y1) / (x2 - x1)
        val intersectX = x1 + (y - y1) / slope

        return x <= intersectX
    }

    private fun generateRandomPolygonPoints(sides: Int): List<Offset> {
        val random = Random(seed = System.currentTimeMillis())
        val centerX = 0f
        val centerY = 0f
        val radius = 300f

        return List(sides) { index ->
            val angle = 2.0 * Math.PI * index / sides
            val randomRadius = radius + random.nextFloat() * 100 - 50 // Adding some randomness to the radius
            Offset(
                x = (centerX + randomRadius * cos(angle)).toFloat(),
                y = (centerY + randomRadius * sin(angle)).toFloat()
            )
        }
    }

    private fun generateComplexPolygonPoints(sides: Int): List<Offset> {
        val random = Random(seed = System.currentTimeMillis())
        val centerX = 0f
        val centerY = 0f
        val baseRadius = 250f

        return List(sides) { index ->
            // Introduce slight randomness to the angle
            val baseAngle = 2.0 * Math.PI * index / sides
            val randomAngle = baseAngle + (random.nextDouble() - 0.5) * 0.4 // Adds angle jitter

            // Introduce randomness to the radius
            val randomRadius = baseRadius + random.nextFloat() * 150 - 45 // Bigger variance


            // Calculate the point using the randomized angle and radius
            Offset(
                x = (centerX + randomRadius * cos(randomAngle)).toFloat(),
                y = (centerY + randomRadius * sin(randomAngle)).toFloat()
            )
        }
    }

    private fun generateRandomOffsetsFromPolygonVertices(
        polygonPoints: List<Offset>,
        numberOfOffsets: Int = NUMBER_OF_OFFSETS
    ): List<Offset> {
        if(polygonPoints.size < 2)
            return emptyList()
        // Find the minimum and maximum x and y values from the polygonPoints list
        val minX = polygonPoints.minOf { it.x - 150 }
        val maxX = polygonPoints.maxOf { it.x + 150 }
        val minY = polygonPoints.minOf { it.y - 150 }
        val maxY = polygonPoints.maxOf { it.y + 150 }

        // Generate random offsets within the specified range
        return List(numberOfOffsets) {
            val randomX = Random.nextFloat() * (maxX - minX) + minX
            val randomY = Random.nextFloat() * (maxY - minY) + minY
            Offset(randomX, randomY)
        }
    }

    companion object{
        const val NUMBER_OF_OFFSETS = 10
    }
}