package com.kire.audio.presentation.ui.cross_screen_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kire.audio.R
import com.kire.audio.presentation.functional.bounceClick
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme

@Composable
fun ScrollToTopButton(
    onClick: () -> Unit,
    scrollToTopButtonPadding: PaddingValues = PaddingValues(bottom = 128.dp, end = 28.dp)
) {

    Box(
        modifier = Modifier
            .padding(scrollToTopButtonPadding)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .bounceClick { onClick() }
                .wrapContentSize()
                .shadow(
                    elevation = 5.dp,
                    spotColor = Color.Black,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(AudioExtendedTheme.extendedColors.controlElementsBackground),
            contentAlignment = Alignment.Center

        ) {

            Icon(
                painter = painterResource(id = R.drawable.scroll_to_top_button),
                "Scroll To Top",
                tint = AudioExtendedTheme.extendedColors.button,
                modifier = Modifier
                    .padding(6.dp)
                    .size(54.dp)
            )
        }
    }
}