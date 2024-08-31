package com.umermahar.bombingrun.main.components.polygon

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.umermahar.bombingrun.R
import com.umermahar.bombingrun.main.Bomb
import com.umermahar.bombingrun.main.BombResult
import com.umermahar.bombingrun.main.MainState
import com.umermahar.bombingrun.main.Polygon
import com.umermahar.bombingrun.utils.bitmapFromDrawableResource
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun RandomPolygonWithDimensions(
    modifier: Modifier = Modifier,
    polygon: Polygon,
    bombedPoints: List<Bomb>
) {

    val polygonPoints = polygon.polygonPoints
    val centroid = polygon.centroid

    // Calculate the bounding box
    val minX = polygonPoints.minOf { it.x }
    val maxX = polygonPoints.maxOf { it.x }
    val minY = polygonPoints.minOf { it.y }
    val maxY = polygonPoints.maxOf { it.y }

    // Calculate width and height of the polygon
    val width = maxX - minX
    val height = maxY - minY

    // Calculate distances from pivot to minX and minY
    val distanceToMinX = centroid.x - minX
    val distanceToMinY = centroid.y - minY

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground
    val primary = MaterialTheme.colorScheme.primary

    val context = LocalContext.current

    // State to control Plane animation
    var startPlaneAnimation by remember { mutableStateOf(false) }
    var bombedPointCount by remember {
        mutableIntStateOf(0)
    }
    // Get the screen width in pixels
    val configuration = LocalConfiguration.current
    val screenWidthPx = configuration.screenWidthDp * LocalDensity.current.density

    // Animate the composable position from start to end
    val animatedXOffsetOfPlan by animateFloatAsState(
        targetValue = if (startPlaneAnimation) screenWidthPx else -500f,
        animationSpec = tween(durationMillis = 3000, easing = LinearEasing), label = ""
    )

    // Handle animation trigger and visibility of the last circle
    LaunchedEffect (key1 = bombedPoints) {
        if(bombedPointCount < bombedPoints.size) {
            startPlaneAnimation = true
            bombedPointCount = bombedPoints.size
            delay(2000)
            startPlaneAnimation = false
        } else {
            bombedPointCount = bombedPoints.size
        }
    }

    // Offset for the plane position
    val planeOffset = remember(animatedXOffsetOfPlan) {
        Offset(
            x = if(startPlaneAnimation) {
                animatedXOffsetOfPlan
            } else -500f,
            y = (bombedPoints.lastOrNull()?.offset?.y?:0f) - 30f
        )
    }

    val explode by remember(planeOffset) {
        derivedStateOf {
            bombedPoints.lastOrNull() != null && planeOffset.x > bombedPoints.last().offset.x
        }
    }

    // Parameters for explosion
    val numParticles = 20
    val explosionRadius by animateDpAsState(
        targetValue = if (explode) 30.dp else 0.dp,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing), label = ""
    )

    Canvas(modifier = modifier) {
        val paint = Paint().apply {
            color = onBackgroundColor.toArgb()
            textSize = 30f // Set the desired text size
        }

        // Draw the polygon with respect to the pivot (centroid)
        drawPath(
            path = Path().apply {
                moveTo(polygonPoints.first().x, polygonPoints.first().y)
                polygonPoints.drop(1).forEach { point ->
                    lineTo(point.x, point.y)
                }
                close()
            },
            color = onSurfaceColor,
            style = Stroke(width = 10f)
        )

        // Draw numbers at each vertex and calculate the relative position to the pivot
//        polygonPoints.forEachIndexed { index, point ->
//            drawContext.canvas.nativeCanvas.drawText(
//                "${index + 1}: (%.2f, %.2f)".format(state.polygonPointsOnGraph[index].x, state.polygonPointsOnGraph[index].y),
//                point.x,
//                point.y,
//                paint
//            )
//        }

        // Draw the pivot point for reference
        drawCircle(
            color = primary,
            center = centroid,
            radius = 10f
        )

        bombedPoints.forEachIndexed { index, bombedPoint ->
            drawCircle(
                color = if (index == bombedPoints.lastIndex && bombedPoint.offset.x > planeOffset.x && startPlaneAnimation) {
                    Color.Transparent
                } else {
                    if (bombedPoint.result == BombResult.HIT) {
                        Color.Green
                    } else Color.Red
                },
                center = bombedPoint.offset,
                radius = 10f
            )

                if (explode && index == bombedPoints.lastIndex) {
                    drawExplosionParticles(
                        size = explosionRadius.toPx(),
                        numParticles = numParticles,
                        center = bombedPoint.offset
                    )
                }
        }

        bombedPoints.lastOrNull()?.let {
            drawImage(
                image = context.bitmapFromDrawableResource(resourceId = R.drawable.ic_plane, width = 60, height = 60),
                topLeft = planeOffset
            )
        }

        // Draw the width line at the bottom
        drawLine(
            color = Color.Blue,
            start = Offset(minX, maxY + 90), // 20dp below the polygon
            end = Offset(maxX, maxY + 90),
            strokeWidth = 5f
        )
        drawContext.canvas.nativeCanvas.drawText(
            "Width: %.2f m".format(width),
            (minX + maxX) / 2,
            maxY + 130, // Position the text below the width line
            paint
        )

        // Draw the height line on the right
        drawLine(
            color = Color.Blue,
            start = Offset(maxX + 90, minY), // 20dp to the right of the polygon
            end = Offset(maxX + 90, maxY),
            strokeWidth = 5f
        )
        drawContext.canvas.nativeCanvas.drawText(
            "Height: %.2f m".format(height),
            maxX - 150, // Position the text beside the height line
            minY - 80,
            paint
        )

        // Draw the distance from pivot to minX line
        drawLine(
            color = Color.Magenta,
            start = Offset(minX, maxY + 40),
            end = Offset(centroid.x, maxY + 40),
            strokeWidth = 5f
        )
        drawContext.canvas.nativeCanvas.drawText(
            "Pivot to Min X: %.2f m".format(distanceToMinX),
            (minX + centroid.x) / 2,
            maxY + 70, // Position the text above the width line
            paint
        )

        // Draw the distance from pivot to minY line
        drawLine(
            color = Color.Magenta,
            start = Offset(maxX + 40, minY),
            end = Offset(maxX + 40, centroid.y),
            strokeWidth = 5f
        )
        drawContext.canvas.nativeCanvas.drawText(
            "Pivot to Min Y: %.2f m".format(distanceToMinY),
            maxX - 150, // Position the text beside the height line
            minY - 40,
            paint
        )
    }
}

private fun DrawScope.drawExplosionParticles(size: Float, numParticles: Int, center: Offset) {
    val particleColor = Color.Red
    val random = java.util.Random()

    for (i in 0 until numParticles) {
        val angle = random.nextFloat() * 360
        val distance = random.nextFloat() * size
        val x = (center.x + distance * kotlin.math.cos(Math.toRadians(angle.toDouble()))).toFloat()
        val y = (center.y + distance * kotlin.math.sin(Math.toRadians(angle.toDouble()))).toFloat()

        drawCircle(
            color = particleColor,
            radius = 5f,
            center = Offset(x, y)
        )
    }
}