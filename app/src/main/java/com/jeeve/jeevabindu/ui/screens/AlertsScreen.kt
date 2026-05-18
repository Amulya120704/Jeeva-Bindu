package com.jeeve.jeevabindu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.jeeve.jeevabindu.data.local.EmergencyPostEntity
import com.jeeve.jeevabindu.ui.AppViewModel
import com.jeeve.jeevabindu.ui.theme.EmergencyRed
import com.jeeve.jeevabindu.ui.theme.ReadyGreen
import com.jeeve.jeevabindu.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlertsScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val posts by viewModel.emergencyPosts.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            "Emergency Alerts",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = EmergencyRed
        )
        Text(
            "Community broadcasts — tap I'm Coming so others know you're responding",
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (posts.isEmpty()) {
            Text("No active alerts. Stay ready.", modifier = Modifier.padding(top = 24.dp))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(posts, key = { it.id }) { post ->
                    EmergencyPostCard(
                        post = post,
                        canRespond = currentUser?.isReadyToDonate == true &&
                            currentUser?.bloodGroup == post.bloodGroup,
                        onRespond = { viewModel.respondToPost(post.id) },
                        viewModel = viewModel,
                        currentUserId = currentUser?.id
                    )
                }
            }
        }
    }
}

@Composable
private fun EmergencyPostCard(
    post: EmergencyPostEntity,
    canRespond: Boolean,
    onRespond: () -> Unit,
    viewModel: AppViewModel,
    currentUserId: Long?
) {
    val responses by viewModel.observeResponses(post.id).collectAsState(initial = emptyList())
    val time = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(post.createdAtMillis))
    val alreadyResponded = responses.any { it.donorId == currentUserId }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = EmergencyRed.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                post.message,
                fontWeight = FontWeight.Bold,
                color = EmergencyRed,
                style = MaterialTheme.typography.titleMedium
            )
            Text("📍 ${post.location}", style = MaterialTheme.typography.bodyMedium)
            Text(time, style = MaterialTheme.typography.bodySmall, color = TextSecondary)

            if (responses.isNotEmpty()) {
                val responseText = if (alreadyResponded) {
                    if (responses.size > 1) "You and ${responses.size - 1} others are responding" else "You are responding"
                } else {
                    "${responses.size} person(s) responding"
                }
                Text(
                    responseText,
                    modifier = Modifier.padding(top = 8.dp),
                    color = ReadyGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onRespond,
                    enabled = canRespond && !alreadyResponded,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ReadyGreen,
                        disabledContainerColor = if (alreadyResponded) ReadyGreen.copy(alpha = 0.6f) else ReadyGreen.copy(alpha = 0.4f)
                    )
                ) {
                    Text(if (alreadyResponded) "✓ Responding" else "I'm Coming")
                }
            }
        }
    }
}
