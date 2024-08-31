package com.umermahar.bombingrun.main.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.umermahar.bombingrun.R
import com.umermahar.bombingrun.main.Bomb
import com.umermahar.bombingrun.main.BombResult
import com.umermahar.bombingrun.main.MainEvent
import com.umermahar.bombingrun.utils.GeneralBottomSheet
import com.umermahar.bombingrun.utils.OptionSheetCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBombResultBottomSheet(
    selectedBomb: Bomb,
    onEvent: (MainEvent) -> Unit
) {

    val selectBombResultSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    GeneralBottomSheet(
        sheetState = selectBombResultSheetState,
        titleRes = R.string.select_result,
        onDismissRequest = {
            onEvent(MainEvent.ToggleSelectBombResultSheet)
        }
    ) {
        Column {

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(id = R.string.x),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = String.format("%.2f", selectedBomb.offset.x)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.size(16.dp))

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp), // Set the corner radius here
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(id = R.string.y),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = String.format("%.2f", selectedBomb.offset.y)
                        )
                    }
                }
            }

            OptionSheetCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                txtRes = R.string.hit,
            ) {
                scope.launch {
                    selectBombResultSheetState.hide()
                    onEvent(MainEvent.SelectedBombResult(BombResult.HIT))
                }
            }

            OptionSheetCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                txtRes = R.string.miss,
            ) {
                scope.launch {
                    selectBombResultSheetState.hide()
                    onEvent(MainEvent.SelectedBombResult(BombResult.MISS))
                }
            }
        }
    }
}