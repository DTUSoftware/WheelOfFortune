package dk.dtu.s215827.wheeloffortune.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dk.dtu.s215827.wheeloffortune.Action
import dk.dtu.s215827.wheeloffortune.GameStatus
import dk.dtu.s215827.wheeloffortune.PlayerViewModel
import dk.dtu.s215827.wheeloffortune.R
import dk.dtu.s215827.wheeloffortune.WheelResult

@Composable
fun Wheel(
    status: GameStatus,
    wheelPosition: Float,
    wheelResult: WheelResult,
    onClick: (action: Action) -> Unit
) {
    val rotation = remember { mutableStateOf(0f) }
    val spinnable = remember { mutableStateOf(true) }

    // Composable to make the wheel spin and update the rotation
    WheelSpinEffect(status, wheelPosition, wheelResult) {
        rotation.value = it
    }

    LaunchedEffect(status) {
        spinnable.value = (status == GameStatus.TURN_DONE_CORRECT ||
                status == GameStatus.TURN_DONE_WRONG ||
                status == GameStatus.TURN_DONE_LOST ||
                status == GameStatus.NEW_GAME ||
                status == GameStatus.NOT_PLAYING
                )
    }

    // The actual wheel
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = rememberVectorPainter(image = Icons.Default.KeyboardArrowDown),
            contentDescription = null, // decorative
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(50.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.wheel_of_fortune_noloseaturn),
            contentDescription = stringResource(R.string.wheel_of_fortune_imagedescriptor),
            modifier = Modifier
                .rotate(rotation.value)
                .size(250.dp)
                .clickable(enabled = spinnable.value) {
                    // Same as action button, basically

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
                            onClick(Action.SPIN)
                        } else {
                            onClick(Action.NEW_GAME)
                        }
                    }
                    // If done with all words, give new message, and onClick,
                    // repopulate the words and start from scratch
                    else if (status == GameStatus.DONE) {
                        onClick(Action.REPOPULATE)
                    }
                }
        )
    }
}
