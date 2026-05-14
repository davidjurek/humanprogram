package app.humanprogram.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import app.humanprogram.android.ui.HumanProgramApp
import app.humanprogram.android.ui.theme.HumanProgramTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HumanProgramTheme {
                HumanProgramApp()
            }
        }
    }
}
