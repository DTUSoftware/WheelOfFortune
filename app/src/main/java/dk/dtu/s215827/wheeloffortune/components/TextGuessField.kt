package dk.dtu.s215827.wheeloffortune.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dk.dtu.s215827.wheeloffortune.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextGuessField(viewModel: PlayerViewModel, onNewGuess: (guess: String) -> Unit) {
    var guess by remember { mutableStateOf("") }
    TextField(value = guess, onValueChange = {
        if (it.isNotEmpty()) {
            if (it[it.length - 1] == '\n') {
                viewModel.guess(guess)
                guess = ""
            } else {
                // Only take the first letter, if it is a letter
                val letter = it.uppercase()[0].toString()
                if (letter.matches(Regex("[a-zA-z\\s]*"))) {
                    guess = letter
                }
            }
        } else {
            guess = it
        }
        onNewGuess(guess)
    })
}
