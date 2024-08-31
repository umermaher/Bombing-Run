package com.umermahar.bombingrun.initial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.umermahar.bombingrun.R
import com.umermahar.bombingrun.utils.Screen

@Composable
fun InitialScreen(
    navigate: (route: String) -> Unit,
) {

    Box(
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            navigate(Screen.MainScreen.route)
        }) {
            Text(text = stringResource(id = R.string.start))
        }
    }

}