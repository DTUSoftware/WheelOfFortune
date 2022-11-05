package dk.dtu.s215827.wheeloffortune.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

@Composable
fun CharBox(char: Char?) {
    if (char != null) {
        if (char == ' ') {
            // Fill with a spacer to simulate a space
            Spacer(modifier = Modifier.width(20.dp))
        } else {
            // Show the letter
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
        // We fill in an actual char, but make it invisible, just to get the correct spacing
        Text(
            text = "_",
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(25)
                )
                .width(40.dp),
            color = Color.Transparent,
            fontSize = 10.em,
            textAlign = TextAlign.Center
        )
    }
}
