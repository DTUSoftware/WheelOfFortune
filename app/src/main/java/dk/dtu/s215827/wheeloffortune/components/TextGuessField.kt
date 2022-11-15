package dk.dtu.s215827.wheeloffortune.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextGuessField(
    onGuess: (guess: String) -> Unit,
    onNewGuess: (guess: String) -> Unit
) {
    var guess by remember { mutableStateOf("") }
    TextField(modifier = Modifier.width(100.dp), value = guess, onValueChange = {
        if (it.isNotEmpty()) {
            // Submit guess on newline / enter
            if (it[it.length - 1] == '\n') {
                onGuess(guess)
                guess = ""
            } else {
                // Only take the first letter, if it is a letter
                val letter = it.uppercase()[0].toString()
                if (letter.matches(Regex("[a-zA-z\\s]*"))) {
                    guess = letter
                }
            }
        } else {
            // if deleted letter, clear
            guess = it
        }
        // Callback with new value
        onNewGuess(guess)
    })
}
