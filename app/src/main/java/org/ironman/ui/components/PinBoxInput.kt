package org.ironman.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ironman.ui.theme.DarkSurfaceVariant
import org.ironman.ui.theme.PrimaryCyan
import org.ironman.ui.theme.TextMuted
import org.ironman.ui.theme.TextPrimary

@Composable
fun PinBoxInput(
    pinValue: String,
    onPinChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isMasked: Boolean = true
) {
    BasicTextField(
        value = pinValue,
        onValueChange = {
            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                onPinChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                repeat(4) { index ->
                    val char = when {
                        index < pinValue.length -> if (isMasked) "•" else pinValue[index].toString()
                        else -> ""
                    }
                    val isFocused = index == pinValue.length

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (isFocused) PrimaryCyan else TextMuted.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCyan,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}
