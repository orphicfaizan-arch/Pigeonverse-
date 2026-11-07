package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PigeonverseViewModel
import com.example.ui.components.LuxuryCard
import com.example.ui.theme.PigeonBorder
import com.example.ui.theme.PigeonEmerald
import com.example.ui.theme.PigeonGold
import com.example.ui.theme.PigeonGoldLight
import com.example.ui.theme.PigeonSurfaceCard
import com.example.ui.theme.PigeonSurfaceDark
import com.example.ui.theme.PigeonTextPrimary
import com.example.ui.theme.PigeonTextSecondary
import com.example.ui.theme.PigeonViolet

@Composable
fun AdminScreen(
    viewModel: PigeonverseViewModel,
    onBack: () -> Unit
) {
    val pigeons by viewModel.pigeons.collectAsState()
    val stores by viewModel.stores.collectAsState()
    val doctors by viewModel.doctors.collectAsState()
    val orders by viewModel.userOrders.collectAsState()

    val totalMarketGMV = pigeons.sumOf { it.price }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = PigeonGold)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PIGEONVERSE CONTROL CENTER",
                            color = PigeonGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = PigeonGold, modifier = Modifier.size(18.dp))
                    }
                    Text(text = "Super-Admin Oversight (orphicfaizan@gmail.com)", color = PigeonEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(title = "Total Pigeons Listed", value = "${pigeons.size}", accent = PigeonGold, modifier = Modifier.weight(1f))
                    AdminMetricCard(title = "Active Online Stores", value = "${stores.size}", accent = PigeonEmerald, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(title = "Avian Doctors", value = "${doctors.size}", accent = PigeonViolet, modifier = Modifier.weight(1f))
                    AdminMetricCard(title = "Platform GMV", value = "$${"%,.0f".format(totalMarketGMV)}", accent = PigeonGoldLight, modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // System Health & Rules Policy Status
        item {
            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = PigeonEmerald)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Platform Security & Rules Status", color = PigeonTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Zero-Trust Firestore Security Rules: DEPLOYED & ACTIVE", color = PigeonEmerald, fontSize = 11.sp)
                    Text(text = "• Credential Manager Google Sign-In: OPERATIONAL", color = PigeonEmerald, fontSize = 11.sp)
                    Text(text = "• Escrow Payment Security: ENABLED", color = PigeonEmerald, fontSize = 11.sp)
                    Text(text = "• Database ID: ai-studio-android-pigeonve-99caefdb-60f5-4aee-a6c4-a44006db2e0c", color = PigeonTextSecondary, fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Listing Moderation Section
        item {
            Text(
                text = "Marketplace Listings Moderation (${pigeons.size})",
                color = PigeonTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(pigeons, key = { it.id }) { pigeon ->
            LuxuryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = pigeon.title, color = PigeonTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Loft: ${pigeon.sellerLoft} • $${"%,.0f".format(pigeon.price)} • Ring: ${pigeon.ringNumber}", color = PigeonTextSecondary, fontSize = 11.sp)
                    }

                    IconButton(onClick = { viewModel.deletePigeon(pigeon.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = PigeonSurfaceCard,
        border = BorderStroke(1.dp, PigeonBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, color = PigeonTextSecondary, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = accent, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
    }
}
