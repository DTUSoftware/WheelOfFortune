package dk.dtu.s215827.wheeloffortune.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dk.dtu.s215827.wheeloffortune.GameStatus
import dk.dtu.s215827.wheeloffortune.R

@Composable
fun StatusBar(
    status: GameStatus,
    lives: Int,
    points: Int,
    possibleEarnings: Int
) {
    var statusMessage = ""

    // Status message
    when (status) {
        GameStatus.PLAYING -> {
            statusMessage = stringResource(R.string.gamestatus_playing).replace(
                "{possibleEarnings}",
                possibleEarnings.toString()
            )
        }

        GameStatus.WON -> {
            statusMessage = stringResource(R.string.gamestatus_won)
        }

        GameStatus.LOST -> {
            statusMessage = stringResource(R.string.gamestatus_lost)
        }

        GameStatus.DONE -> {
            statusMessage = stringResource(R.string.gamestatus_done)
        }

        GameStatus.WHEEL_SPINNING -> {
            statusMessage = stringResource(R.string.gamestatus_wheelspinning)
        }

        GameStatus.TURN_DONE_CORRECT -> {
            statusMessage = stringResource(R.string.gamestatus_correct)
        }

        GameStatus.TURN_DONE_WRONG -> {
            statusMessage = stringResource(R.string.gamestatus_wrong)
        }

        GameStatus.TURN_DONE_LOST -> {
            statusMessage = stringResource(R.string.gamestatus_turnlost)
        }

        else -> {}
    }

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
                color = Color.Transparent,
                modifier = Modifier.size(0.dp)
            )
        }

        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (statusMessage.isNotEmpty()) {
                Text(text = statusMessage)
            }
        }

        Text(text = stringResource(R.string.points).replace("{points}", points.toString()))
    }

    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
        if (statusMessage.isNotEmpty()) {
            Text(text = statusMessage)
        }
    }
}
