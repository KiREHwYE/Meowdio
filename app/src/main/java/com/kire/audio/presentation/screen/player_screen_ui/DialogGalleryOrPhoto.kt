package com.kire.audio.presentation.screen.player_screen_ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.kire.audio.BuildConfig
import com.kire.audio.data.repository.functional.createImageFile
import com.kire.audio.presentation.functional.bounceClick
import com.kire.audio.screen.functional.getContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogGalleryOrPhoto(
    changeOpenDialog: (Boolean) -> Unit,
    updateUri: (Uri) -> Unit,
    imageUri: Uri?,
    defaultImageUri: Uri?
){

    val context = getContext()

    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { newUri ->
            if (newUri == null) return@rememberLauncherForActivityResult

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_$timeStamp.jpg"

            val input = context.contentResolver.openInputStream(newUri) ?: return@rememberLauncherForActivityResult
            val outputFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName)

            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }

            input.close()

            val localUri = FileProvider.getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.provider",
                outputFile
            )

            updateUri(localUri)
        }
    )

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { isTaken ->
                if (isTaken)
                    updateUri(uri)
            }
        )


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(uri)
        } else {  }
    }

    BasicAlertDialog(
        onDismissRequest = {
            changeOpenDialog(false)
        }
    ) {

        Row(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(size = 24.dp)
                )
                .padding(
                    top = 28.dp,
                    bottom = 28.dp,
                    start = 32.dp,
                    end = 32.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Icon(
                imageVector = Icons.Rounded.PhotoCamera,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(52.dp)
                    .bounceClick {
                        val permissionCheckResult =
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                            cameraLauncher.launch(uri)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
            )
            Icon(
                imageVector = Icons.Rounded.PhotoLibrary,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(52.dp)
                    .bounceClick {
                        galleryLauncher.launch("image/*")
                    }
            )

            if (imageUri != defaultImageUri)
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(52.dp)
                        .bounceClick {
                            defaultImageUri?.let {
                                updateUri(it)
                            }
                        }
                )
        }
    }
}