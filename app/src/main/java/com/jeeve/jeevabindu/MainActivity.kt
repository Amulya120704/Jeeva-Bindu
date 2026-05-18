package com.jeeve.jeevabindu

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jeeve.jeevabindu.notifications.BloodAlertNotifier
import com.jeeve.jeevabindu.ui.AppViewModel
import com.jeeve.jeevabindu.ui.screens.HomeScreen
import com.jeeve.jeevabindu.ui.screens.RegistryScreen
import com.jeeve.jeevabindu.ui.theme.CreamBackground
import com.jeeve.jeevabindu.ui.theme.JeevaBinduTheme

class MainActivity : ComponentActivity() {

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        BloodAlertNotifier.ensureChannel(this)
        requestNotificationPermissionIfNeeded()
        // FirebaseMessaging.getInstance().subscribeToTopic("blood_alerts")

        val openAlerts = intent?.getBooleanExtra(BloodAlertNotifier.EXTRA_OPEN_ALERTS, false) == true

        setContent {
            JeevaBinduTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = CreamBackground) {
                    val viewModel: AppViewModel = viewModel()
                    val hasProfile by viewModel.hasProfile.collectAsState()

                    when (hasProfile) {
                        null -> Unit
                        false -> RegistryScreen(viewModel)
                        true -> HomeScreen(viewModel, startOnAlerts = openAlerts)
                    }
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
