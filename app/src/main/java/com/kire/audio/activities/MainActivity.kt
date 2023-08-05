package com.kire.audio.activities

import android.database.CursorWindow
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.view.WindowCompat
import com.kire.audio.functional.GetPermissions
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.Field


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        try {
            val field: Field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.setAccessible(true)
            field.set(null, 100 * 1024 * 1024) //the 100MB is the new size
        } catch (e: Exception) {
//            if (DEBUG_MODE) {
//                e.printStackTrace()
//            }
        }

        setContent {
            GetPermissions(lifecycleOwner = LocalLifecycleOwner.current)
            DestinationsNavHost(navGraph = NavGraphs.root)
        }
    }
}

//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview(){
//    DestinationsNavHost(navGraph = NavGraphs.root)
//}