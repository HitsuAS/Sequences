package com.example.sequences.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sequences.data.Storage
import com.example.sequences.model.Question

@Composable
fun QuestionsScreen(
    questionNum: Int,
    isChosen: Boolean,
    isValueValid: Boolean,
    difficultyInput: String,
    updateDifficulty: (String) -> Unit,
    onStartClick: () -> Unit,
    answerInput: String,
    updateAnswer: (String) -> Unit,
    isAnswered: Boolean,
    onSubmitClick: () -> Unit,
    onNextClick: () -> Unit,
    isLastQuestion: Boolean,
    onShowResultsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1.5f))
        when (isChosen) {
            false -> ChooseDifficultyScreen(
                value = difficultyInput,
                onValueChange = updateDifficulty,
                isValid = isValueValid,
                onClick = onStartClick
            )
            true -> QuestionScreen(
                question = Storage.questions[questionNum],
                value = answerInput,
                onValueChange = updateAnswer,
                isValid = isValueValid,
                isAnswered = isAnswered,
                onSubmitClick = onSubmitClick,
                isLastQuestion = isLastQuestion,
                onNextClick = onNextClick,
                onShowResultsClick = onShowResultsClick
            )
        }
        Spacer(modifier = Modifier.weight(2f))
    }
}

@Composable
fun ChooseDifficultyScreen(
    value: String,
    onValueChange: (String) -> Unit,
    isValid: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = "Choose difficulty (The length of the sequence):",
        textAlign = TextAlign.Center
    )
    OutlinedTextField(
        label = { Text(text = "Difficulty") },
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onClick() }),
        isError = !isValid,
        supportingText = { if(!isValid) Text(text = "Invalid Input. Value should be greater than 2") }
    )
    Button(onClick = onClick) {
        Text(text = "Start")
    }
}

@Composable
fun QuestionScreen(
    question: Question,
    value: String,
    onValueChange: (String) -> Unit,
    isValid: Boolean,
    isAnswered: Boolean,
    onSubmitClick: () -> Unit,
    isLastQuestion: Boolean,
    onNextClick: () -> Unit,
    onShowResultsClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isScrollable = scrollState.canScrollForward || scrollState.canScrollBackward

    Text(text = "How many ${question.questionType} numbers in this sequence?")
    Text(
        text = question.questionSequence,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .horizontalScroll(scrollState)
    )
    if(isScrollable) Text(text = "The sequence is scrollable")
    OutlinedTextField(
        label = { Text(text = "Answer") },
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onSubmitClick() }),
        isError = !isValid,
        supportingText = { if(!isValid) Text(text = "Invalid Input. Value should be 0 or greater") },
        enabled = !isAnswered
    )
    if(!isAnswered) {
        Button(onClick = onSubmitClick) {
            Text(text = "Submit")
        }
    } else {
        Text(text = "The answer is: ${question.answer}")
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = if(isLastQuestion) onShowResultsClick else onNextClick
        ) {
            Text(
                text = if (isLastQuestion) "Show Results" else "Next Question"
            )
        }
    }
}