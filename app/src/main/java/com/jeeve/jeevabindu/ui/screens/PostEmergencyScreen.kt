package com.jeeve.jeevabindu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jeeve.jeevabindu.data.BloodGroup
import com.jeeve.jeevabindu.ui.AppViewModel
import com.jeeve.jeevabindu.ui.theme.EmergencyRed
import com.jeeve.jeevabindu.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostEmergencyScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val currentUser by viewModel.currentUser.collectAsState()
    var hospital by remember { mutableStateOf("") }
    var location by remember(currentUser) { mutableStateOf(currentUser?.location.orEmpty()) }
    var bloodGroup by remember(currentUser) {
        mutableStateOf(currentUser?.bloodGroup ?: BloodGroup.O_POS.label)
    }
    var expanded by remember { mutableStateOf(false) }
    var posted by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Post Emergency",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = EmergencyRed
        )
        Text(
            "Broadcasts: \"Urgent [Blood Group] needed at [Hospital]\". Matching donors get an FCM-style alert within 5 seconds.",
            color = TextSecondary
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = EmergencyRed.copy(alpha = 0.12f))
        ) {
            Text(
                "Preview: Urgent $bloodGroup needed at ${hospital.ifBlank { "…" }}",
                modifier = Modifier.padding(12.dp),
                fontWeight = FontWeight.SemiBold,
                color = EmergencyRed
            )
        }

        OutlinedTextField(
            value = hospital,
            onValueChange = { hospital = it },
            label = { Text("Hospital name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Panchayat / Town (alert area)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = bloodGroup,
                onValueChange = {},
                readOnly = true,
                label = { Text("Blood group needed") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                BloodGroup.entries.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group.label) },
                        onClick = {
                            bloodGroup = group.label
                            expanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                viewModel.postEmergency(hospital, location, bloodGroup)
                posted = true
                hospital = ""
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
        ) {
            Text("Broadcast Blood Alert")
        }

        if (posted) {
            Text(
                "Alert sent. Check Alerts tab and notification tray (grant permission if asked).",
                color = TextSecondary
            )
        }
    }
}
