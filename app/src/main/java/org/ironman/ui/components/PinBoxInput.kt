/*
 * Iron Will - Streak & Habit Tracker
 * Copyright (C) 2026 Mahadi Hasan <islammdmahadi943@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */


package org.ironman.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ironman.ui.theme.DarkSurfaceVariant
import org.ironman.ui.theme.PrimaryCyan
import org.ironman.ui.theme.TextMuted

@Composable
fun PinBoxInput(
    pinValue: String,
    onPinChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isMasked: Boolean = true,
    focusRequester: FocusRequester? = null
) {
    val fallbackFocusRequester = remember { FocusRequester() }
    val effectiveFocusRequester = focusRequester ?: fallbackFocusRequester
    val keyboardController = LocalSoftwareKeyboardController.current

    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = pinValue,
        onValueChange = {
            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                onPinChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier.focusRequester(effectiveFocusRequester),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    effectiveFocusRequester.requestFocus()
                    keyboardController?.show()
                }
            ) {
                Box(modifier = Modifier.size(1.dp)) {
                    innerTextField()
                }

                Row(
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
        }
    )
}
