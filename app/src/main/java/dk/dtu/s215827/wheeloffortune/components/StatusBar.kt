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
import androidx.compose.ui.res.painterResource
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
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row() {
            for (i in 1..lives) {
                Icon(painter = painterResource(id = R.drawable.baseline_heart_24), contentDescription = "Full Heart")
            }
            for (i in 1..5-lives) {
                Icon(painter = painterResource(id = R.drawable.baseline_heart_border_24), contentDescription = "Empty Heart")
            }
        }
        Text(text = "$points$")
    }

    // Status message
    when (status) {
        GameStatus.PLAYING -> {
            Text(text = "Playing to win $possibleEarnings$ per letter")
        }

        GameStatus.WON -> {
            Text(text = "Game Won! Play again?")
        }

        GameStatus.LOST -> {
            Text(text = "Game Lost... Try again?")
        }

        GameStatus.DONE -> {
            Text(text = "Wow, you completed them all! Try again?")
        }

        GameStatus.WHEEL_SPINNING -> {
            Text(text = "Spinning...")
        }

        GameStatus.TURN_DONE_CORRECT -> {
            Text(text = "Correct! Spin again!")
        }

        GameStatus.TURN_DONE_WRONG -> {
            Text(text = "Wrong! Try again!")
        }

        GameStatus.TURN_DONE_LOST -> {
            Text(text = "Lost a turn! Try again!")
        }

        else -> {}
    }
}
