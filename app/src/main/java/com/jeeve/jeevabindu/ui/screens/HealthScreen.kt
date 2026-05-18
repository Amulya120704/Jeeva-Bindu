package com.jeeve.jeevabindu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jeeve.jeevabindu.ui.AppViewModel
import com.jeeve.jeevabindu.ui.theme.ReadyGreen
import com.jeeve.jeevabindu.ui.theme.TextSecondary

@Composable
fun HealthScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val user by viewModel.currentUser.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Donor Health Tracker",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            "90-day rule: after each donation you must wait 90 days before donating again.",
            color = TextSecondary
        )

        if (user == null) {
            Text("Complete registry to track your eligibility.")
            return
        }

        val ready = user!!.isReadyToDonate
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (ready) ReadyGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(user!!.name, fontWeight = FontWeight.Bold)
                Text("${user!!.bloodGroup} • 📍 ${user!!.location}")
                Text(
                    viewModel.eligibilityText(user),
                    modifier = Modifier.padding(top = 8.dp),
                    color = if (ready) ReadyGreen else TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("If you donate today:", fontWeight = FontWeight.Medium)
                Text(
                    "Next eligibility: ${viewModel.previewEligibilityAfterDonation()}",
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Button(
            onClick = { viewModel.recordDonation() },
            modifier = Modifier.fillMaxWidth(),
            enabled = ready,
            colors = ButtonDefaults.buttonColors(containerColor = ReadyGreen)
        ) {
            Text("Record Donation Today")
        }

        if (!ready) {
            Text(
                "You are in the 90-day recovery window.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
