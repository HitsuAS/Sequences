package com.example.sequences

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sequences.data.Storage
import com.example.sequences.model.Question
import com.example.sequences.ui.SequencesViewModel
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
fun SequencesGame(
    modifier: Modifier = Modifier,
    sequencesViewModel: SequencesViewModel = viewModel()
) {

    val sequencesUiState by sequencesViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            when(sequencesUiState.currentPage) {
                0 -> SequencesTopAppBar(label = "Sequences", showLogo = true)
                1 -> SequencesTopAppBar(
                    label = "Question ${sequencesUiState.currentQuestionNumber + 1}/${sequencesUiState.questionsQuantity}"
                )
                else -> SequencesTopAppBar(label = "Results")
            }
        }
    ) { contentPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (sequencesUiState.currentPage) {
                0 -> WelcomeAndChooseQuantityScreen(
                    value = sequencesViewModel.quantityInput,
                    onValueChange = { sequencesViewModel.updateQuantityInput(it) },
                    onClick = { sequencesViewModel.onContinueClick() },
                    isValid = sequencesUiState.isValueValid
                )

                1 -> QuestionsScreen(
                    questionNum = sequencesUiState.currentQuestionNumber,
                    isChosen = sequencesUiState.isDifficultyChosen,
                    isValueValid = sequencesUiState.isValueValid,
                    difficultyInput = sequencesViewModel.difficultyInput,
                    updateDifficulty = { sequencesViewModel.updateDifficulty(it) },
                    onStartClick = { sequencesViewModel.onStartClick() },
                    answerInput = sequencesViewModel.answerInput,
                    updateAnswer = { sequencesViewModel.updateAnswer(it) },
                    isAnswered = sequencesUiState.isAnswered,
                    onSubmitClick = { sequencesViewModel.onSubmitClick() },
                    onNextClick = { sequencesViewModel.onNextClick() },
                    isLastQuestion = sequencesUiState.isLastQuestion,
                    onShowResultsClick = { sequencesViewModel.goToNextPage() }
                )

                2 -> ResultsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SequencesTopAppBar(label: String, showLogo: Boolean = false) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(showLogo) {
                    Image(
                        modifier = Modifier.size(64.dp),
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                        contentDescription = null
                    )
                }
                Text(text = label)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
fun WelcomeAndChooseQuantityScreen(
    value: String,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.9f))
        Text(
            text = "Welcome",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.weight(0.1f))
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
            keyboardActions = KeyboardActions(onDone = { onClick() }),
            isError = !isValid,
            supportingText = { if(!isValid) Text(text = "Invalid Input. Value must be greater than 0") }
        )
        Button(onClick = onClick) {
            Text(text = "Continue")
        }
        Spacer(modifier = Modifier.weight(2f))
    }
}

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

@Composable
fun ResultsScreen() {
    LazyColumn(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        items(Storage.questions) { question ->
            QuestionResultCard(question = question)
        }
    }
}

@Composable
fun QuestionResultCard(question: Question) {
    var isExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val isScrollable = scrollState.canScrollForward || scrollState.canScrollBackward

    val color by animateColorAsState(
        targetValue = if (isExpanded) Color(0xFFDAF1DD)
        else MaterialTheme.colorScheme.secondaryContainer,
        label = ""
    )

    Card(
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = color)
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
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 8.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .horizontalScroll(scrollState)
                    )
                    if(isScrollable) Text(text = "The sequence is scrollable")
                    Text(text = "Your answer is: ${Storage.answers[question.questionNum]}")
                    Text(text = "The answer is: ${question.answer}")
                }
            }
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