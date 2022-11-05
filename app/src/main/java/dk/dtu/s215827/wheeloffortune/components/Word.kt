package dk.dtu.s215827.wheeloffortune.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.dtu.s215827.wheeloffortune.PlayerViewModel

@Composable
fun Word(viewModel: PlayerViewModel) {
    val word by viewModel.currentWord.collectAsState()
    val category by viewModel.currentCategory.collectAsState()
    val revealedChars by viewModel.revealedLetters.collectAsState()

    // Lazy row with word from viewModel
    // Sadly this can overflow, and couldn't find a real solution to that overflow online
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
    ) {
        items(word.length) {
            val char = word[it]
            CharBox(if (char == ' ' || revealedChars.contains(char)) char else null)
        }
    }

    // Show category under word
    if (category.isNotEmpty()) {
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = category)
    }
}
