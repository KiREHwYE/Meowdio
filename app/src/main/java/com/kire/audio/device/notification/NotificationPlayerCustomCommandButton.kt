package com.kire.audio.device.notification

import android.os.Bundle
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import com.kire.audio.R

private const val CUSTOM_COMMAND_PREVIOUS_ACTION_ID = "PREVIOUS"
private const val CUSTOM_COMMAND_PLAY_ACTION_ID = "PLAY"
private const val CUSTOM_COMMAND_PAUSE_ACTION_ID = "PAUSE"
private const val CUSTOM_COMMAND_NEXT_ACTION_ID = "NEXT"

enum class NotificationPlayerCustomCommandButton(
    val customAction: String,
    val commandButton: CommandButton,
) {
    PREVIOUS(
        customAction = CUSTOM_COMMAND_PREVIOUS_ACTION_ID,
        commandButton = CommandButton.Builder()
            .setDisplayName("Previous")
            .setSessionCommand(SessionCommand(CUSTOM_COMMAND_PREVIOUS_ACTION_ID, Bundle()))
            .setIconResId(R.drawable.baseline_skip_previous_24)
            .build()
    ),
    PLAY(
        customAction = CUSTOM_COMMAND_PLAY_ACTION_ID,
        commandButton = CommandButton.Builder()
            .setDisplayName("Play")
            .setSessionCommand(SessionCommand(CUSTOM_COMMAND_PLAY_ACTION_ID, Bundle()))
            .setIconResId(R.drawable.baseline_pause_24)
            .build()
    ),
    PAUSE(
        customAction = CUSTOM_COMMAND_PAUSE_ACTION_ID,
        commandButton = CommandButton.Builder()
            .setDisplayName("Pause")
            .setSessionCommand(SessionCommand(CUSTOM_COMMAND_PAUSE_ACTION_ID, Bundle()))
            .setIconResId(R.drawable.baseline_pause_24)
            .build()
    ),
    NEXT(
        customAction = CUSTOM_COMMAND_NEXT_ACTION_ID,
        commandButton = CommandButton.Builder()
            .setDisplayName("Next")
            .setSessionCommand(SessionCommand(CUSTOM_COMMAND_NEXT_ACTION_ID, Bundle()))
            .setIconResId(R.drawable.baseline_skip_next_24)
            .build()
    );
}