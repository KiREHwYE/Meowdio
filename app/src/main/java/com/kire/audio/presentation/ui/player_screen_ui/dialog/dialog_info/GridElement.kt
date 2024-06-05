package com.kire.audio.presentation.ui.player_screen_ui.dialog.dialog_info

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.nonScaledSp

@Composable
fun GridElement(
    text: String,
    switcher: Boolean,
    isFirst:Boolean,
    isEnabled: Boolean = false,
    isEditable: Boolean = false,
    isImageURI: Boolean = false,
    updateText: ((String) -> Unit)? = null,
    changeOpenDialog: ((Boolean) -> Unit)? = null
){

    var newText by rememberSaveable { mutableStateOf(text) }

    if (switcher)
        Text(
            text = text,
            color = AudioExtendedTheme.extendedColors.primaryText,
            fontSize = 16.sp.nonScaledSp,
            fontWeight = FontWeight.Bold,
            lineHeight = 20.sp.nonScaledSp,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier
                .padding(top = if (isFirst) 18.dp else 0.dp)
        )
    else
        BasicTextField(
            modifier = Modifier
                .background(
                    Color.Transparent,
                    MaterialTheme.shapes.small,
                )
                .fillMaxWidth(0.5f)
                .padding(top = if (isFirst) 18.dp else 0.dp)
                .pointerInput(isEnabled && isEditable && isImageURI) {
                    detectTapGestures {
                        if (isEnabled && isEditable && isImageURI && changeOpenDialog != null)
                            changeOpenDialog(true)
                    }
                },
            value = newText,
            onValueChange = {
                newText = it.also {
                    if (updateText != null && newText != text)
                        updateText(it)
                }
            },
            enabled = isEnabled && isEditable && !isImageURI,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            textStyle = LocalTextStyle.current.copy(
                color = if (isEnabled && isEditable) AudioExtendedTheme.extendedColors.orangeAccents else AudioExtendedTheme.extendedColors.lyricsText,
                fontSize = 13.sp.nonScaledSp,
                fontWeight = FontWeight.W600,
                lineHeight = 20.sp.nonScaledSp
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier
                        .weight(1f)
                    ) {
                        innerTextField()
                    }
                }
            }
        )
}