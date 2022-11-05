package dk.dtu.s215827.wheeloffortune.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dk.dtu.s215827.wheeloffortune.PlayerViewModel

@Composable
fun WordGuessing(viewModel: PlayerViewModel) {
    var guess by remember { mutableStateOf("") }

    TextGuessField(viewModel) {
        guess = it
    }
    Button(onClick = {
        viewModel.guess(guess)
        guess = ""
    }) {
        Text(text = "Guess")
    }
}
