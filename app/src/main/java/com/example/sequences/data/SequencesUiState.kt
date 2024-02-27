package com.example.sequences.data

data class SequencesUiState(
    val currentPage: Int = 0,
    val questionsQuantity: Int = 1,
    val currentQuestionNumber: Int = 0,
    val isValueValid: Boolean = true,
    val isDifficultyChosen: Boolean = false,
    val isAnswered: Boolean = false,
    val isLastQuestion: Boolean = false
)
