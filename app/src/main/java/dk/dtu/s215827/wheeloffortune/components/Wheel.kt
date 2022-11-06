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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import dk.dtu.s215827.wheeloffortune.GameStatus
import dk.dtu.s215827.wheeloffortune.PlayerViewModel
import dk.dtu.s215827.wheeloffortune.R

@Composable
fun Wheel(viewModel: PlayerViewModel) {
    val rotation = remember { mutableStateOf(0f) }
    val status by viewModel.status.collectAsState()
    val spinnable = remember { mutableStateOf(false) }

    // Composable to make the wheel spin and update the rotation
    WheelSpinEffect(viewModel = viewModel) {
        rotation.value = it
    }

    LaunchedEffect(status) {
        spinnable.value = (status == GameStatus.TURN_DONE_CORRECT || status == GameStatus.TURN_DONE_WRONG || status == GameStatus.TURN_DONE_LOST || status == GameStatus.NEW_GAME)
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
                    viewModel.spinWheel()
                }
        )
    }
}
