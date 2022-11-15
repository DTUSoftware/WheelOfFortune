package dk.dtu.s215827.wheeloffortune.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dk.dtu.s215827.wheeloffortune.Action
import dk.dtu.s215827.wheeloffortune.GameStatus
import dk.dtu.s215827.wheeloffortune.R

@Composable
fun ActionButton(
    status: GameStatus,
    onClick: (action: Action) -> Unit
) {
    // If not done playing (finished all words), and wheel not already spinning,
    // allow starting a new game or spinning the wheel
    if (
        status != GameStatus.DONE &&
        status != GameStatus.WHEEL_SPINNING &&
        status != GameStatus.PLAYING
    ) {
        if (
            status == GameStatus.TURN_DONE_CORRECT ||
            status == GameStatus.TURN_DONE_WRONG ||
            status == GameStatus.TURN_DONE_LOST ||
            status == GameStatus.NEW_GAME
        ) {
            Button(onClick = { onClick(Action.SPIN) }) {
                Text(text = stringResource(R.string.spin_wheel_button))
            }
        } else {
            Button(onClick = { onClick(Action.NEW_GAME) }) {
                Text(
                    text =
                    if (status != GameStatus.NOT_PLAYING) stringResource(R.string.play_again_button)
                    else stringResource(R.string.play_button)
                )
            }
        }
    }
    // If done with all words, give new message, and onClick,
    // repopulate the words and start from scratch
    else if (status == GameStatus.DONE) {
        Button(onClick = { onClick(Action.REPOPULATE) }) {
            Text(text = stringResource(R.string.repopulate_button))
        }
    }
}
