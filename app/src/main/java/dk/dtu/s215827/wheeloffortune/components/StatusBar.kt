package dk.dtu.s215827.wheeloffortune.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dk.dtu.s215827.wheeloffortune.GameStatus
import dk.dtu.s215827.wheeloffortune.PlayerViewModel
import dk.dtu.s215827.wheeloffortune.R

@Composable
fun StatusBar(viewModel: PlayerViewModel) {
    val status by viewModel.status.collectAsState()
    val lives by viewModel.lives.collectAsState()
    val points by viewModel.points.collectAsState()
    val possibleEarnings by viewModel.currentPossibleEarning.collectAsState()

    // Lives and points/cash
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row() {
            for (i in 1..lives) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_heart_24),
                    contentDescription = null // decorative
                )
            }
            for (i in 1..5 - lives) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_heart_border_24),
                    contentDescription = null // decorative
                )
            }

            // For accessibility
            Text(
                text = stringResource(R.string.lives).replace("{lives}", lives.toString()),
                color = Color.Transparent
            )
        }
        Text(text = stringResource(R.string.points).replace("{points}", points.toString()))
    }

    // Status message
    when (status) {
        GameStatus.PLAYING -> {
            Text(
                text = stringResource(R.string.gamestatus_playing).replace(
                    "{possibleEarnings}",
                    possibleEarnings.toString()
                )
            )
        }

        GameStatus.WON -> {
            Text(text = stringResource(R.string.gamestatus_won))
        }

        GameStatus.LOST -> {
            Text(text = stringResource(R.string.gamestatus_lost))
        }

        GameStatus.DONE -> {
            Text(text = stringResource(R.string.gamestatus_done))
        }

        GameStatus.WHEEL_SPINNING -> {
            Text(text = stringResource(R.string.gamestatus_wheelspinning))
        }

        GameStatus.TURN_DONE_CORRECT -> {
            Text(text = stringResource(R.string.gamestatus_correct))
        }

        GameStatus.TURN_DONE_WRONG -> {
            Text(text = stringResource(R.string.gamestatus_wrong))
        }

        GameStatus.TURN_DONE_LOST -> {
            Text(text = stringResource(R.string.gamestatus_turnlost))
        }

        else -> {}
    }
}
