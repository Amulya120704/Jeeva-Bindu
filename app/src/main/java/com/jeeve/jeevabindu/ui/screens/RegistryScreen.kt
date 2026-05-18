package com.jeeve.jeevabindu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.jeeve.jeevabindu.ui.theme.ReadyGreen
import com.jeeve.jeevabindu.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistryScreen(viewModel: AppViewModel) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf(BloodGroup.O_POS.label) }
    var groupExpanded by remember { mutableStateOf(false) }
    var otpSent by remember { mutableStateOf(false) }

    val phoneVerified by viewModel.phoneVerified.collectAsState()
    val error by viewModel.registryError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Join the Donor Registry",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "No login — register once with your Panchayat or Town. Location-first matching for the Golden Hour.",
            color = TextSecondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ReadyGreen.copy(alpha = 0.12f))
        ) {
            Text(
                "Simulated phone verify: send OTP, then enter 123456",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it.filter { c -> c.isDigit() }.take(10) },
            label = { Text("Phone number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (!phoneVerified) {
            if (!otpSent) {
                Button(
                    onClick = {
                        if (viewModel.sendSimulatedOtp(phone)) otpSent = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = phone.length >= 10
                ) {
                    Text("Send OTP")
                }
            } else {
                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("Enter OTP") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Button(
                    onClick = { viewModel.verifyOtp(otp) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Verify Phone")
                }
            }
        } else {
            Text("✓ Phone verified", color = ReadyGreen, fontWeight = FontWeight.SemiBold)
        }

        ExposedDropdownMenuBox(
            expanded = groupExpanded,
            onExpandedChange = { groupExpanded = !groupExpanded }
        ) {
            OutlinedTextField(
                value = bloodGroup,
                onValueChange = {},
                readOnly = true,
                label = { Text("Blood group") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = groupExpanded,
                onDismissRequest = { groupExpanded = false }
            ) {
                BloodGroup.entries.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group.label) },
                        onClick = {
                            bloodGroup = group.label
                            groupExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = age,
            onValueChange = { age = it.filter { c -> c.isDigit() }.take(2) },
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Panchayat / Town") },
            placeholder = { Text("e.g. Hassan Panchayat") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = { viewModel.clearRegistryError() }) {
                Text("Dismiss")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.registerDonor(name, phone, bloodGroup, age, location)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = phoneVerified
        ) {
            Text("Register as Donor")
        }
    }
}
