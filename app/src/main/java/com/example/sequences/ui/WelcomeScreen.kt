package com.example.sequences.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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


@Composable
fun WelcomeScreen(
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