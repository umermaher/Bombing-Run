package com.umermahar.bombingrun.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.res.ResourcesCompat

fun Context.bitmapFromDrawableResource(resourceId: Int, width: Int, height: Int): ImageBitmap {
    val drawable = ResourcesCompat.getDrawable(resources, resourceId, null)

    // Create a Bitmap with the specified width and height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    // Bind the canvas with the Bitmap
    val canvas = android.graphics.Canvas(bitmap)

    // Set the drawable bounds to the canvas size
    drawable?.setBounds(0, 0, width, height)

    // Draw the drawable onto the canvas
    drawable?.draw(canvas)

    return bitmap.asImageBitmap()
}