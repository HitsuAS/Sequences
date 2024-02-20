package com.example.sequences

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.sequences.model.Storage
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
    var currentPage by remember { mutableIntStateOf(0) }

    var quantityInput by remember { mutableStateOf("") }
    var quantity = 0

    var questionNum by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            SequencesTopAppBar(
                label = when(currentPage) {
                    0 -> "Sequences"
                    1 -> "Question ${questionNum + 1}/$quantity"
                    else -> "Results"
                }
            )
        }
    ) {contentPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentPage) {
                0 -> WelcomeAndChooseQuantityScreen(
                    value = quantityInput,
                    onValueChange = { quantityInput = it },
                    onClick = {
                        quantity = quantityInput.toInt()
                        currentPage++
                    },
                    validateQuantity = { quantityInput.toIntOrNull() != null && quantityInput.toInt() > 0 }
                )

                1 -> QuestionsScreen(
                    quantity = quantity,
                    questionNum = questionNum,
                    questionNumAdd = { questionNum++ },
                    onShowResultsClick = { currentPage++ }
                )

                2 -> ResultsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SequencesTopAppBar(label: String) {
    CenterAlignedTopAppBar(
        title = { Text(text = label) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
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
            supportingText = { if(!isValid) Text(text = "Invalid Input. Value must be greater than 0") }
        )
        Button(onClick = onContinueClick) {
            Text(text = "Continue")
        }
    }
}

@Composable
fun QuestionsScreen(
    quantity: Int,
    questionNum: Int,
    questionNumAdd: () -> Unit,
    onShowResultsClick: () -> Unit
) {
    var isChosen by remember { mutableStateOf(false) }

    var difficultyInput by remember { mutableStateOf("") }
    var difficulty: Int

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (isChosen) {
            false -> ChooseDifficultyScreen(
                value = difficultyInput,
                onValueChange = { difficultyInput = it },
                validateDifficulty = { difficultyInput.toIntOrNull() != null && difficultyInput.toInt() > 2 },
                onClick = {
                    difficulty = difficultyInput.toInt()
                    Storage.questions.add(Question(difficulty = difficulty, questionNum = questionNum))
                    isChosen = true
                }
            )
            true -> QuestionScreen(
                question = Storage.questions[questionNum],
                isLastQuestion = questionNum == quantity - 1,
                onNextClick = {
                    questionNumAdd()
                    difficultyInput = ""
                    difficulty = 0
                    isChosen = false
                },
                onShowResultsClick = onShowResultsClick
            )
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
        supportingText = { if(!isValid) Text(text = "Invalid Input. Value should be greater than 2") }
    )
    Button(onClick = onStartClick) {
        Text(text = "Start")
    }
}

@Composable
fun QuestionScreen(
    question: Question,
    isLastQuestion: Boolean,
    onNextClick: () -> Unit,
    onShowResultsClick: () -> Unit
) {
    var answerInput by remember { mutableStateOf("") }
    var answer: Int

    var isValid by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()
    val isScrollable = scrollState.canScrollForward || scrollState.canScrollBackward

    var isAnswered by remember { mutableStateOf(false) }

    val onSubmitClick = {
        isValid = (answerInput.toIntOrNull() != null && answerInput.toInt() > -1)
        if(isValid) {
            answer = answerInput.toInt()
            isAnswered = true
            Storage.answers.add(answer)
        }
    }

    val onNextQuestionClick = {
        onNextClick()
        answerInput = ""
        answer = 0
    }

    Text(text = "How many ${question.questionType} numbers in this sequence?")
    Text(
        text = question.questionSequence,
        modifier = Modifier.horizontalScroll(scrollState)
    )
    if(isScrollable) Text(text = "The sequence is scrollable")
    OutlinedTextField(
        label = { Text(text = "Answer") },
        value = answerInput,
        onValueChange = { answerInput = it },
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
            onClick = if(isLastQuestion) onShowResultsClick else onNextQuestionClick
        ) {
            Text(
                text = if (isLastQuestion) "Show Results" else "Next Question"
            )
        }
    }
}

@Composable
fun ResultsScreen() {

    LazyColumn(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        items(Storage.questions) {question ->
            QuestionResultCard(question = question)
        }
    }
}

@Composable
fun QuestionResultCard(question: Question) {
    var isExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val isScrollable = scrollState.canScrollForward || scrollState.canScrollBackward

    Card(
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(text = "Question ${question.questionNum + 1}")
                if (isExpanded) {
                    Text(text = "${question.questionType} Numbers:")
                    Text(
                        text = question.questionSequence,
                        modifier = Modifier.horizontalScroll(scrollState)
                    )
                    if(isScrollable) Text(text = "The sequence is scrollable")
                    Text(text = "Your answer is: ${Storage.answers[question.questionNum]}")
                    Text(text = "The answer is: ${question.answer}")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { isExpanded = !isExpanded }) {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Show less" else "Show more"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SequencesGamePreview() {
    SequencesTheme {
        SequencesGame()
    }
}