package com.mirlan.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.mirlan.calculator.ui.CalculatorScreen
import com.mirlan.calculator.ui.FavoritesScreen
import com.mirlan.calculator.ui.HistoryScreen
import com.mirlan.calculator.ui.LayoutPickerScreen
import com.mirlan.calculator.ui.UpdateCard

class MainActivity : ComponentActivity() {
    private val vm: CalculatorViewModel by viewModels()
    private lateinit var updateManager: AppUpdateManager
    private var updateInfo by mutableStateOf<AppUpdateInfo?>(null)
    private var updateDismissed by mutableStateOf(false)
    private val updateLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        updateManager = AppUpdateManagerFactory.create(this)
        setContent {
            BackHandler(enabled = vm.currentScreen != Screen.Calculator) {
                vm.back()
            }
            when (vm.currentScreen) {
                is Screen.Calculator   -> CalculatorScreen(vm)
                is Screen.LayoutPicker -> LayoutPickerScreen(vm)
                is Screen.History      -> HistoryScreen(vm)
                is Screen.Favorites    -> FavoritesScreen(vm)
            }
            val info = updateInfo
            if (info != null && !updateDismissed && vm.currentScreen == Screen.Calculator) {
                UpdateCard(
                    theme = vm.selectedPreset.theme,
                    onUpdate = { startUpdate(info) },
                    onLater = { updateDismissed = true }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateManager.appUpdateInfo.addOnSuccessListener { info ->
            when {
                info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> startUpdate(info)
                info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> updateInfo = info
                else -> updateInfo = null
            }
        }
    }

    private fun startUpdate(info: AppUpdateInfo) {
        updateManager.startUpdateFlowForResult(
            info, updateLauncher, AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
        )
    }
}
