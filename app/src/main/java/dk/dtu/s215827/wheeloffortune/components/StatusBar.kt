package dk.dtu.s215827.wheeloffortune.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dk.dtu.s215827.wheeloffortune.GameStatus
import dk.dtu.s215827.wheeloffortune.PlayerViewModel

@Composable
fun StatusBar(viewModel: PlayerViewModel) {
    val status by viewModel.status.collectAsState()
    val lives by viewModel.lives.collectAsState()
    val points by viewModel.points.collectAsState()
    val possibleEarnings by viewModel.currentPossibleEarning.collectAsState()

    Text(text = "$lives lives | $points$")

    when (status) {
        GameStatus.PLAYING -> {
            Text(text = "Playing to win $possibleEarnings$ per. letter")
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
