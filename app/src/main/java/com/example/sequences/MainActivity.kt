package com.example.sequences

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sequences.ui.theme.SequencesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SequencesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SequencesGame()
                }
            }
        }
    }
}

@Composable
fun SequencesGame(modifier: Modifier = Modifier) {
    Text(
        text = "Sequences Game",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun SequencesGamePreview() {
    SequencesTheme {
        SequencesGame()
    }
}