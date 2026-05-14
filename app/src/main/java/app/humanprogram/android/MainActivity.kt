package app.humanprogram.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import app.humanprogram.android.core.storage.PlannerSnapshotStore
import app.humanprogram.android.planning.HumanProgramViewModel
import app.humanprogram.android.planning.HumanProgramViewModelFactory
import app.humanprogram.android.ui.HumanProgramApp
import app.humanprogram.android.ui.theme.HumanProgramTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HumanProgramTheme {
                val viewModel: HumanProgramViewModel = viewModel(
                    factory = HumanProgramViewModelFactory(
                        snapshotStore = PlannerSnapshotStore(applicationContext)
                    )
                )
                HumanProgramApp(viewModel)
            }
        }
    }
}
