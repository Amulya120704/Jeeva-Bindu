package com.jeeve.jeevabindu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jeeve.jeevabindu.data.BloodGroup
import com.jeeve.jeevabindu.data.DonorUiModel
import com.jeeve.jeevabindu.ui.AppViewModel
import com.jeeve.jeevabindu.ui.theme.ReadyGreen
import com.jeeve.jeevabindu.ui.theme.TextSecondary
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val filter by viewModel.bloodGroupFilter.collectAsState()
    val donors by viewModel.donors.collectAsState()
    val showOnlyAvailable by viewModel.showOnlyAvailable.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            "Live Donor Directory",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Location-first • ${donors.filter { it.isReadyToDonate }.size} ready to donate",
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = !showOnlyAvailable,
                onClick = { viewModel.setShowOnlyAvailable(false) },
                label = { Text("All") }
            )
            FilterChip(
                selected = showOnlyAvailable,
                onClick = { viewModel.setShowOnlyAvailable(true) },
                label = { Text("Ready Now") }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BloodGroup.entries.take(4).forEach { group ->
                FilterChip(
                    selected = filter == group.label,
                    onClick = { viewModel.setBloodGroupFilter(if (filter == group.label) null else group.label) },
                    label = { Text(group.label) }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BloodGroup.entries.drop(4).forEach { group ->
                FilterChip(
                    selected = filter == group.label,
                    onClick = { viewModel.setBloodGroupFilter(if (filter == group.label) null else group.label) },
                    label = { Text(group.label) }
                )
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(donors, key = { it.id }) { donor ->
                DonorCard(donor)
            }
        }
    }
}

@Composable
private fun DonorCard(donor: DonorUiModel) {
    val statusColor = if (donor.isReadyToDonate) ReadyGreen else TextSecondary
    val statusText = if (donor.isReadyToDonate) {
        "Ready to Donate"
    } else {
        val date = donor.eligibilityDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        "Eligible $date"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (donor.isReadyToDonate) {
                ReadyGreen.copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(donor.name, fontWeight = FontWeight.SemiBold)
                    Text("📍 ${donor.location}", style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    donor.bloodGroup,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text("Age ${donor.age} • ${donor.phone}", style = MaterialTheme.typography.bodySmall)
            Text(statusText, color = statusColor, fontWeight = FontWeight.Medium)
        }
    }
}
