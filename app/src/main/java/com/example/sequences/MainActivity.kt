package com.example.sequences

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sequences.model.Question
import com.example.sequences.model.QuestionsAndAnswersStorage
import com.example.sequences.ui.theme.SequencesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SequencesTheme {
                SequencesGame()
            }
        }
    }
}

@Composable
fun SequencesGame(modifier: Modifier = Modifier) {
    var currentPage by remember { mutableStateOf(0) }

    var quantityInput by remember { mutableStateOf("") }
    var questionsQuantity = 0

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (currentPage) {
            0 -> WelcomeAndChooseQuantityScreen(
                value = quantityInput,
                onValueChange = { quantityInput = it },
                onClick = {
                    questionsQuantity = quantityInput.toInt()
                    currentPage++
                },
                validateQuantity = { quantityInput.toIntOrNull() != null && quantityInput.toInt() > 1}
            )

        }
    }
}

@Composable
fun WelcomeAndChooseQuantityScreen(
    value: String,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit,
    validateQuantity: () -> Boolean,
    modifier: Modifier = Modifier
) {
    var isValid by remember { mutableStateOf(true) }

    val onContinueClick = {
        isValid = validateQuantity()
        if (isValid) onClick()
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome")
        Spacer(modifier = Modifier.height(28.dp))
        Text(text = "How many questions do you want?")
        OutlinedTextField(
            label = { Text(text = "Quantity") },
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onContinueClick() }),
            isError = !isValid,
            supportingText = { Text(text = "Invalid Input") }
        )
    }
}

@Composable
fun QuestionsScreen(
    quantity: Int,
    onShowResultsClick: () -> Unit
) {
    var questionNum by remember { mutableStateOf(0) }

    var isChosen by remember { mutableStateOf(false) }

    var difficultyInput by remember { mutableStateOf("") }
    var difficulty = 0

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Question ${questionNum + 1}/$quantity")
        Spacer(modifier = Modifier.weight(1f))
        when (isChosen) {
            false -> ChooseDifficultyScreen(
                value = difficultyInput,
                onValueChange = { difficultyInput = it },
                validateDifficulty = { difficultyInput.toIntOrNull() != null && difficultyInput.toInt() > 3 },
                onClick = {
                    difficulty = difficultyInput.toInt()
                    QuestionsAndAnswersStorage.questions.add(Question(difficulty = difficulty))
                    isChosen = true
                }
            )
            true -> {}
        }
    }
}

@Composable
fun ChooseDifficultyScreen(
    value: String,
    onValueChange: (String) -> Unit,
    validateDifficulty: () -> Boolean,
    onClick: () -> Unit
) {
    var isValid by remember { mutableStateOf(true) }

    val onStartClick = {
        isValid = validateDifficulty()
        if(isValid) onClick()
    }

    Text(text = "Choose difficulty (The length of the sequence):")
    OutlinedTextField(
        label = { Text(text = "Difficulty") },
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onStartClick() }),
        isError = !isValid,
        supportingText = { Text(text = "Invalid Input. Value should be greater than 3") }
    )
    Spacer(modifier = Modifier.height(8.dp))
    Button(onClick = onStartClick) {
        Text(text = "Submit")
    }
}

@Preview(showBackground = true)
@Composable
fun SequencesGamePreview() {
    SequencesTheme {
        SequencesGame()
    }
}