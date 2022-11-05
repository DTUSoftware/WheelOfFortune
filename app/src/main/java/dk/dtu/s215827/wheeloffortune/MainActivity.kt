package dk.dtu.s215827.wheeloffortune

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
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
fun Wheel(rotation: Float = 0f) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = rememberVectorPainter(image = Icons.Default.KeyboardArrowDown),
            contentDescription = "Wheel Arrow",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(50.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.wheel_of_fortune_noloseaturn),
            contentDescription = "Wheel of Fortune",
            modifier = Modifier
                .fillMaxWidth()
                .rotate(rotation)
        )
    }
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
    val currentWheelPosition by viewModel.wheelPosition.collectAsState()
    val currentWheelResult by viewModel.currentWheelResult.collectAsState()
    val currentPossibleEarnings by viewModel.currentPossibleEarning.collectAsState()

    var guess by remember { mutableStateOf("") }

    // https://nascimpact.medium.com/jetpack-compose-working-with-rotation-animation-aeddc5899b28
    var currentRotation by remember { mutableStateOf(0f) }
    val rotation = remember { Animatable(currentRotation) }

    // We perform a launched effect for status, to spin the wheel at WHEEL_SPINNING
    LaunchedEffect(status) {
        // Check for the WHEEL_SPINNING status
        if (status == GameStatus.WHEEL_SPINNING) {
            // https://nascimpact.medium.com/jetpack-compose-working-with-rotation-animation-aeddc5899b28
            rotation.animateTo(
                // Spin from current rotation to reset, then two times around, and to wanted position
                targetValue = currentRotation + (360 - (currentRotation % 360)) + 360 * 2 - currentWheelPosition,
                animationSpec = tween(
                    durationMillis = 2500,
                    easing = LinearOutSlowInEasing
                )
            ) {
                currentRotation = value
            }

            currentWheelResult.applyResult(viewModel)

            if (currentWheelResult.type == 0) {
                viewModel.setPlaying()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
//        Text(text = "$currentWheelPosition = ${currentWheelResult.type}: ${currentWheelResult.points}")
        Text(text = "$lives lives | $points$")

//        Text(text = currentWord)
//        Text(text = revealedChars.toString())

        when (status) {
            GameStatus.PLAYING -> {
                Text(text = "Playing to win $currentPossibleEarnings$ per. letter")
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

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Wheel(rotation.value)

            Word(currentWord, revealedChars)

            if (currentCategory.isNotEmpty()) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = currentCategory)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (status == GameStatus.PLAYING) {
                TextField(value = guess, onValueChange = {
                    if (it.length > 0) {
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
                })
                Button(onClick = {
                    viewModel.guess(guess)
                    guess = ""
                }) {
                    Text(text = "Guess")
                }
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