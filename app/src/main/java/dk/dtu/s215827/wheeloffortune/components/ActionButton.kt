package dk.dtu.s215827.wheeloffortune.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dk.dtu.s215827.wheeloffortune.GameStatus
import dk.dtu.s215827.wheeloffortune.PlayerViewModel

@Composable
fun ActionButton(viewModel: PlayerViewModel) {
    val status by viewModel.status.collectAsState()

    if (status == GameStatus.PLAYING) {
        WordGuessing(viewModel)
    } else if (status != GameStatus.DONE && status != GameStatus.WHEEL_SPINNING) {
        if (status == GameStatus.TURN_DONE_CORRECT || status == GameStatus.TURN_DONE_WRONG || status == GameStatus.TURN_DONE_LOST || status == GameStatus.NEW_GAME) {
            Button(onClick = { viewModel.spinWheel() }) {
                Text(text = "Spin Wheel")
            }
        } else {
            Button(onClick = { viewModel.newGame() }) {
                Text(text = if (status != GameStatus.NOT_PLAYING) "Play Again" else "Play")
            }
        }
    } else if (status == GameStatus.DONE) {
        Button(onClick = { viewModel.populateWords(); viewModel.newGame() }) {
            Text(text = "Repopulate and start a new game?")
        }
    }
}
