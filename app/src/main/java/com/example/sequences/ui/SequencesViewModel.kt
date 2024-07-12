package com.example.sequences.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.sequences.model.SequencesUiState
import com.example.sequences.data.Storage
import com.example.sequences.model.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SequencesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SequencesUiState())
    val uiState: StateFlow<SequencesUiState> = _uiState.asStateFlow()

    var quantityInput by mutableStateOf("")
        private set

    var difficultyInput by mutableStateOf("")
        private set

    var answerInput by mutableStateOf("")
        private set

    init {
        resetGame()
    }

    fun resetGame() {
        Storage.questions.clear()
        Storage.answers.clear()
        _uiState.value = SequencesUiState()
        quantityInput = ""
        difficultyInput = ""
        answerInput = ""
    }

    fun topAppBarPhrase(currentPage: Int): String {
        return when(currentPage) {
            0 -> "Sequences"
            1 -> "Question ${_uiState.value.currentQuestionNumber + 1}/${_uiState.value.questionsQuantity}"
            else -> "Results"
        }
    }

    private fun goToNextPage() {
        _uiState.update { currentState ->
            currentState.copy(currentPage = currentState.currentPage.plus(1))
        }
    }

    fun updateQuantityInput(input: String) {
        quantityInput = input
    }

    private fun setQuantity() {
        _uiState.update { currentState ->
            currentState.copy(questionsQuantity = quantityInput.toInt(), isValueValid = true)
        }
    }

    fun onContinueClick() {
        if(validateQuantity()) {
            setQuantity()
            goToNextPage()
            if(_uiState.value.questionsQuantity == 1) {
                _uiState.update { currentState ->
                    currentState.copy(isLastQuestion = true)
                }
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(isValueValid = false)
            }
        }
    }

    private fun validateQuantity(): Boolean = quantityInput.toIntOrNull() != null && quantityInput.toInt() > 0

    fun updateDifficulty(input: String) {
        difficultyInput = input
    }

    private fun setQuestion() {
        Storage.questions.add(Question(difficultyInput.toInt(), _uiState.value.currentQuestionNumber))
        _uiState.update { currentState ->
            currentState.copy(isDifficultyChosen = true, isValueValid = true)
        }
    }

    fun onStartClick() {
        if (validateDifficulty()) {
            setQuestion()
        } else {
            _uiState.update { currentState ->
                currentState.copy(isValueValid = false)
            }
        }
    }

    private fun validateDifficulty(): Boolean = difficultyInput.toIntOrNull() != null && difficultyInput.toInt() > 2

    fun updateAnswer(input: String) {
        answerInput = input
    }

    fun onSubmitClick() {
        if(validateAnswer()) {
            Storage.answers.add(answerInput.toInt())
            _uiState.update {currentState ->
                currentState.copy(isAnswered = true)
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(isValueValid = false)
            }
        }
    }

    fun onNextClick() {
        difficultyInput = ""
        answerInput = ""
        _uiState.update { currentState ->
            currentState.copy(
                isValueValid = true,
                isDifficultyChosen = false,
                isAnswered = false,
                currentQuestionNumber = currentState.currentQuestionNumber.plus(1),
                isLastQuestion = currentState.currentQuestionNumber.plus(1) == currentState.questionsQuantity - 1
            )
        }
    }

    private fun validateAnswer(): Boolean = answerInput.toIntOrNull() != null && answerInput.toInt() > -1

    fun onShowResultsClick() {
        goToNextPage()
    }
}