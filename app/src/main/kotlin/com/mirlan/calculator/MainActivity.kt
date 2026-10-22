package com.mirlan.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.mirlan.calculator.ui.CalculatorScreen
import com.mirlan.calculator.ui.LayoutPickerScreen

class MainActivity : ComponentActivity() {
    private val vm: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BackHandler(enabled = vm.currentScreen != Screen.Calculator) {
                vm.back()
            }
            when (vm.currentScreen) {
                is Screen.Calculator   -> CalculatorScreen(vm)
                is Screen.LayoutPicker -> LayoutPickerScreen(vm)
            }
        }
    }
}
