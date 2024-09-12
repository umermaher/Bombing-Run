package com.umermahar.bombingrun.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umermahar.bombingrun.R
import com.umermahar.bombingrun.main.Bomb
import com.umermahar.bombingrun.main.BombResult.HIT
import com.umermahar.bombingrun.main.BombResult.MISS
import com.umermahar.bombingrun.main.BombResult.UNKNOWN
import com.umermahar.bombingrun.main.MainEvent
import com.umermahar.bombingrun.utils.OptionSheetCard

@Composable
fun BombingRunTableSheetContent(
    modifier: Modifier = Modifier,
    simulatedBombingRun: List<Bomb>,
    onGuessBombClick: (Bomb, Int) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Spacer(modifier = Modifier.height(8.dp))
//        Box(
//            modifier = Modifier
//                .width(32.dp)
//                .height(4.dp)
//                .background(
//                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
//                    shape = RoundedCornerShape(10.dp)
//                )
//        )
//        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.simulated_bombing_run),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        BombingRunTable(
            modifier = Modifier
                .height(270.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                ),
            simulatedBombingRun = simulatedBombingRun,
            onGuessBombClick = onGuessBombClick
        )
    }
}

@Composable
fun BombingRunTable(
    modifier: Modifier = Modifier,
    simulatedBombingRun: List<Bomb>,
    onGuessBombClick: (Bomb, Int) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.bomber),
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.x),
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.y),
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.result),
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }

        //Divider
        Divider(
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LazyColumn {
            // Content
            itemsIndexed(simulatedBombingRun) { index, bomb ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${index + 1}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(
                        text = String.format("%.2f", bomb.offset.x),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = String.format("%.2f", bomb.offset.y),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = stringResource(
                            id = when(bomb.result) {
                                HIT -> R.string.hit
                                MISS -> R.string.miss
                                UNKNOWN -> R.string.guess
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onGuessBombClick(bomb, index)
                            },
                        textAlign = TextAlign.Center,
                        color = when(bomb.result) {
                            HIT -> Color.Green
                            MISS -> Color.Red
                            UNKNOWN -> Color.Unspecified
                        },
                        style = if(bomb.isGuessedCorrect || bomb.result == UNKNOWN) {
                            LocalTextStyle.current
                        } else LocalTextStyle.current.copy(
                            textDecoration = TextDecoration.LineThrough
                        )
                    )
                }
            }
        }
    }
}
