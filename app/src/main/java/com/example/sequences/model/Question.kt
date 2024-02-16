package com.example.sequences.model

import kotlin.math.sqrt

data class Question(val difficulty: Int) {

    val questionSequence: String = buildString {
        repeat (difficulty) {
            when ((0..2).random()) {
                0 -> append(('0'..'9').random())
                1 -> append(('a'..'z').random())
                2 -> append(('A'..'Z').random())
            }
        }
    }

    val questionType: QuestionType = when ((0..2).random()) {
        0 -> QuestionType.EVEN
        1 -> QuestionType.ODD
        else -> QuestionType.PRIME
    }

    var answer: Int = 0

    init {
        var currentNum = ""

        questionSequence.forEach { char ->

            if (char in '0'..'9') currentNum += char
            else if (currentNum != "") {
                when(questionType) {
                    QuestionType.EVEN -> if (currentNum.toInt().isEven()) answer++
                    QuestionType.ODD -> if (currentNum.toInt().isOdd()) answer++
                    QuestionType.PRIME -> if (currentNum.toInt().isOdd()) answer++
                }
                currentNum = ""
            }

            if (currentNum != "") {
                when (questionType) {
                    QuestionType.EVEN -> if (currentNum.toInt().isEven()) answer++
                    QuestionType.ODD -> if (currentNum.toInt().isOdd()) answer++
                    QuestionType.PRIME -> if (currentNum.toInt().isOdd()) answer++
                }
            }

        }

    }
}

enum class QuestionType { EVEN, ODD, PRIME }

fun Int.isEven() = this % 2 == 0
fun Int.isOdd() = this % 2 != 0
fun Int.isPrime() : Boolean {
    if(this < 2 ) return false
    for(i in 2 until sqrt(this.toFloat()).toInt()){
        if(this % i == 0) return false
    }
    return true
}