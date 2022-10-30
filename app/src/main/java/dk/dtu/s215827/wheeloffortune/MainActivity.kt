package dk.dtu.s215827.wheeloffortune

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import dk.dtu.s215827.wheeloffortune.ui.theme.WheelOfFortuneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = PlayerViewModel()
        setContent {
            WheelOfFortuneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WheelOfFortune(viewModel)
                }
            }
        }
    }
}

sealed class WheelResult(var label: String, var type: Int, var points: Int) {
    object ThousandPoints : WheelResult("1000", 0, 1000)
}

@Composable
fun CharBox(char: Char?) {
    if (char != null) {
        if (char == ' ') {
            Spacer(modifier = Modifier.width(20.dp))
        } else {
            Text(
                text = char.toString(),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(25)
                    )
                    .width(40.dp),
                color = MaterialTheme.colorScheme.inverseOnSurface,
                fontSize = 10.em,
                textAlign = TextAlign.Center
            )
        }
    } else {
        Text(
            text = "_",
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(25)
                )
                .width(40.dp),
            color = Color.Unspecified,
            fontSize = 10.em,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Word(word: String, revealedCharArray: List<Char>) {
    Text(text = word)

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
    ) {
        items(word.length) {
            val char = word[it]
            CharBox(if (char == ' ' || revealedCharArray.contains(char)) char else null)
        }
    }
}

@Composable
fun Wheel(onSpin: (WheelResult) -> Unit) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WheelOfFortune(viewModel: PlayerViewModel) {
    val currentWord by viewModel.currentWord.collectAsState()
    val currentCategory by viewModel.currentCategory.collectAsState()
    val revealedChars by viewModel.revealedLetters.collectAsState()
    val status by viewModel.status.collectAsState()
    val lives by viewModel.lives.collectAsState()
    val points by viewModel.points.collectAsState()

    var guess by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Wheel of Fortune")
        Text(text = "$lives lives | $points points")
        Text(text = revealedChars.toString())

        when (status) {
            2 -> {
                Text(text = "GAME WON!!!")
            }

            3 -> {
                Text(text = "GAME LOST...")
            }

            4 -> {
                Text(text = "Wow, you completed them all!")
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Word(currentWord, revealedChars)
            Wheel {

            }

            if (currentCategory.isNotEmpty()) {
                Text(text = currentCategory)
            }

            if (status == 1) {
                TextField(value = guess, onValueChange = {
                    // Match for letters
                    if (it.matches(Regex("[a-zA-z\\s]*"))) {
                        guess = it.take(1).uppercase()
                    }
                })
                Button(onClick = {
                    viewModel.guessChar(guess[0])
                    guess = ""
                }) {
                    Text(text = "Guess")
                }
            } else if (status != 4) {
                Button(onClick = { viewModel.newGame() }) {
                    Text(text = if (status != 0) "Play Again" else "Play")
                }
            }

            Button(onClick = { viewModel.newGame() }) {
                Text(text = "Debug")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val viewModel = PlayerViewModel()
    WheelOfFortuneTheme {
        WheelOfFortune(viewModel)
    }
}