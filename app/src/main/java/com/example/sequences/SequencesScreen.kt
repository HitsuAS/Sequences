package com.example.sequences

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sequences.ui.QuestionsScreen
import com.example.sequences.ui.ResultsScreen
import com.example.sequences.ui.SequencesViewModel
import com.example.sequences.ui.WelcomeScreen

enum class SequencesScreen(val title: String) {
    Welcome(title = "Sequences"),
    Questions(title = "Questions"),
    Results(title = "Results")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SequencesTopAppBar(
    label: String,
    canNavigateUp: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showLogo = label == "Sequences"

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
        modifier = modifier,
        navigationIcon = {
            if(canNavigateUp) {
                IconButton(onClick = navigateUp) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
fun SequencesGame(modifier: Modifier = Modifier) {

    val sequencesViewModel: SequencesViewModel = viewModel()
    val sequencesUiState by sequencesViewModel.uiState.collectAsState()

    val navController: NavHostController = rememberNavController()

    Scaffold(
        topBar = { SequencesTopAppBar(
            label = sequencesViewModel.topAppBarPhrase(currentPage = sequencesUiState.currentPage),
            canNavigateUp = navController.previousBackStackEntry != null,
            navigateUp = {
                sequencesViewModel.resetGame()
                navController.popBackStack(SequencesScreen.Welcome.name, inclusive = false)
            }
        ) }
    ) { contentPadding ->

        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = SequencesScreen.Welcome.name
            ) {
                composable(route = SequencesScreen.Welcome.name) {
                    WelcomeScreen(
                        value = sequencesViewModel.quantityInput,
                        onValueChange = { sequencesViewModel.updateQuantityInput(it) },
                        onClick = {
                            sequencesViewModel.onContinueClick()
                            if (sequencesUiState.isValueValid) {
                                navController.navigate(SequencesScreen.Questions.name)
                            }
                        },
                        isValid = sequencesUiState.isValueValid
                    )
                }

                composable(route = SequencesScreen.Questions.name) {
                    QuestionsScreen(
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
                        onShowResultsClick = {
                            sequencesViewModel.onShowResultsClick()
                            navController.navigate(SequencesScreen.Results.name)
                        }
                    )
                }

                composable(route = SequencesScreen.Results.name) {
                    ResultsScreen()
                }
            }
        }
    }
}