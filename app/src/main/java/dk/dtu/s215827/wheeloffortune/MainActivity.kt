package dk.dtu.s215827.wheeloffortune

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.dtu.s215827.wheeloffortune.components.*
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.dtu.s215827.wheeloffortune.ui.theme.WheelOfFortuneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WheelOfFortuneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WheelOfFortune()
                }
            }
        }
    }
}

@Composable
fun WheelOfFortune(viewModel: PlayerViewModel = viewModel()) {
    val status by viewModel.status.collectAsState() // to listen to status change and show status
    val lives by viewModel.lives.collectAsState()
    val points by viewModel.points.collectAsState()
    val possibleEarnings by viewModel.currentPossibleEarning.collectAsState()
    val wheelPosition by viewModel.wheelPosition.collectAsState() // to tell where to spin the wheel (0 to 360)
    val wheelResult by viewModel.currentWheelResult.collectAsState() // for applying result after animation
    val word by viewModel.currentWord.collectAsState()
    val category by viewModel.currentCategory.collectAsState()
    val revealedChars by viewModel.revealedLetters.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatusBar(status, lives, points, possibleEarnings)

        // TODO: Fix landscape orientation - it looks kinda wonky, would be nice to be able to scroll,
        // but it gets angry because of the staggeredlazylist
        if (LocalConfiguration.current.orientation == ORIENTATION_LANDSCAPE) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Wheel(status, wheelPosition, wheelResult) {
                        when (it) {
                            Action.SPIN -> {
                                viewModel.spinWheel()
                            }

                            Action.NEW_GAME -> {
                                viewModel.newGame()
                            }

                            Action.REPOPULATE -> {
                                viewModel.populateWords(); viewModel.newGame()
                            }
                        }
                    }

//                    Spacer(modifier = Modifier.height(20.dp))

                    // no need for the button, you can just click
//                    ActionButton(viewModel)
                }
                Word(word, category, revealedChars, status) {
                    viewModel.guess(it)
                }
            }

        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Wheel(status, wheelPosition, wheelResult) {
                    when (it) {
                        Action.SPIN -> {
                            viewModel.spinWheel()
                        }

                        Action.NEW_GAME -> {
                            viewModel.newGame()
                        }

                        Action.REPOPULATE -> {
                            viewModel.populateWords(); viewModel.newGame()
                        }
                    }
                }

                Word(word, category, revealedChars, status) {
                    viewModel.guess(it)
                }

                Spacer(modifier = Modifier.height(20.dp))

                ActionButton(status) {
                    when (it) {
                        Action.SPIN -> {
                            viewModel.spinWheel()
                        }

                        Action.NEW_GAME -> {
                            viewModel.newGame()
                        }

                        Action.REPOPULATE -> {
                            viewModel.populateWords(); viewModel.newGame()
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WheelOfFortuneTheme {
        WheelOfFortune()
    }
}
