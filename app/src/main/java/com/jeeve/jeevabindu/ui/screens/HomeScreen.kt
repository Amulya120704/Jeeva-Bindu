package com.jeeve.jeevabindu.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.jeeve.jeevabindu.ui.AppViewModel
import com.jeeve.jeevabindu.ui.theme.EmergencyRed

private enum class HomeTab(val label: String, val icon: ImageVector) {
    Directory("Directory", Icons.Default.People),
    Alerts("Alerts", Icons.Default.Emergency),
    Health("My Health", Icons.Default.Favorite),
    Post("Post SOS", Icons.Default.Bloodtype)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: AppViewModel, startOnAlerts: Boolean = false) {
    var selectedTab by rememberSaveable {
        mutableIntStateOf(if (startOnAlerts) 1 else 0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jeeva-Bindu") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EmergencyRed,
                    titleContentColor = androidx.compose.ui.graphics.Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar {
                HomeTab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> DirectoryScreen(viewModel, Modifier.padding(padding))
            1 -> AlertsScreen(viewModel, Modifier.padding(padding))
            2 -> HealthScreen(viewModel, Modifier.padding(padding))
            3 -> PostEmergencyScreen(viewModel, Modifier.padding(padding))
        }
    }
}
