package com.umermahar.bombingrun.main

import androidx.compose.ui.geometry.Offset
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class PolygonHelper {

    fun generateRandomPoints(sides: Int, level: Level = Level.NORMAL): List<Offset> {
        val points = generateComplexPolygonPoints(sides = sides)
        return if(level == Level.HARD) {
            points.shuffled()
        } else points
    }

    fun isPointInPolygon(polygonPoints: List<Offset>, point: Offset): Boolean {
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


    fun generateRandomOffsetsFromPolygonVertices(
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
            val variance = if(index % 2 == 0) 35 else 85
            val randomRadius = baseRadius + random.nextFloat() * 150 - variance // Bigger variance

            // Calculate the point using the randomized angle and radius
            Offset(
                x = (centerX + randomRadius * cos(randomAngle)).toFloat(),
                y = (centerY + randomRadius * sin(randomAngle)).toFloat()
            )
        }
    }

    companion object{
        const val NUMBER_OF_OFFSETS = 10
    }
}