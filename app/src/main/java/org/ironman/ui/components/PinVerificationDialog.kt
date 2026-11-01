package org.ironman.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ironman.ui.theme.AccentRed
import org.ironman.ui.theme.TextMuted
import org.ironman.ui.theme.TextPrimary

@Composable
fun PinVerificationDialog(
    correctPin: String,
    title: String = "Security PIN Required",
    subtitle: String = "Enter your 4-digit PIN to proceed:",
    onDismiss: () -> Unit,
    onPinSuccess: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = subtitle,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(16.dp))

                PinBoxInput(
                    pinValue = pinInput,
                    onPinChange = {
                        pinInput = it
                        isError = false
                        if (it.length == 4) {
                            if (it == correctPin) {
                                onPinSuccess()
                            } else {
                                isError = true
                            }
                        }
                    },
                    focusRequester = focusRequester
                )

                if (isError) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Incorrect PIN. Try again.", color = AccentRed)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pinInput == correctPin) {
                        onPinSuccess()
                    } else {
                        isError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
            ) {
                Text("Confirm", color = TextPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
