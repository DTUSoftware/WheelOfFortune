package dk.dtu.s215827.wheeloffortune.components

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import dk.dtu.s215827.wheeloffortune.GameStatus
import dk.dtu.s215827.wheeloffortune.PlayerViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Word(viewModel: PlayerViewModel) {
    val word by viewModel.currentWord.collectAsState()
    val category by viewModel.currentCategory.collectAsState()
    val revealedChars by viewModel.revealedLetters.collectAsState()
    val status by viewModel.status.collectAsState()

    val size = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) 0.8f else 1.0f

    Column(
        modifier = Modifier.fillMaxWidth(size),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Lazy row with word from viewModel
        // Sadly this can overflow, and couldn't find a real solution to that overflow online
        LazyVerticalStaggeredGrid(
            modifier = Modifier.padding(5.dp),
            columns = StaggeredGridCells.Adaptive(minSize = 30.dp),
//        contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(word.length) {
                val char = word[it]
                // pass char if space (CharBox parses that as no box), if revealed, if lost or if char is not guessable ( . ! ? - )
                CharBox(if (char == ' ' || revealedChars.contains(char) || status == GameStatus.LOST || !char.toString().matches(Regex("[a-zA-z\\s]*"))) char else null)
            }
        }

        // Show category under word
        if (category.isNotEmpty()) {
            Text(text = category)
        }

        // If playing, show word guesser
        if (status == GameStatus.PLAYING) {
            Spacer(Modifier.height(5.dp))
            WordGuessing(viewModel)
        }
    }
}
