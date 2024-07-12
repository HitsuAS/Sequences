package com.example.sequences.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sequences.data.Storage
import com.example.sequences.model.Question

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