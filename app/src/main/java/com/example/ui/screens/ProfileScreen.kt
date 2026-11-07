package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.auth.AuthManager
import com.example.ui.PigeonverseViewModel
import com.example.ui.components.LuxuryCard
import com.example.ui.components.TierBadge
import com.example.ui.components.VerifiedBreederBadge
import com.example.ui.theme.PigeonBorder
import com.example.ui.theme.PigeonEmerald
import com.example.ui.theme.PigeonGold
import com.example.ui.theme.PigeonGoldLight
import com.example.ui.theme.PigeonSurfaceCard
import com.example.ui.theme.PigeonSurfaceDark
import com.example.ui.theme.PigeonSurfaceElevated
import com.example.ui.theme.PigeonTextPrimary
import com.example.ui.theme.PigeonTextSecondary
import com.example.ui.theme.PigeonViolet

@Composable
fun ProfileScreen(
    viewModel: PigeonverseViewModel,
    onOpenAdmin: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val profile by viewModel.userProfile.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp)
    ) {
        // Profile Header Card
        item {
            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(PigeonGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile?.displayName?.take(1)?.uppercase() ?: "P",
                                color = PigeonGold,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile?.displayName.orEmpty().ifBlank { "Breeder" },
                                    color = PigeonTextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                TierBadge(tier = profile?.subscriptionTier ?: "Free")
                            }

                            Text(
                                text = profile?.loftName.orEmpty().ifBlank { "Heritage Pigeon Loft" },
                                color = PigeonGoldLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = profile?.email.orEmpty(),
                                color = PigeonTextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        IconButton(onClick = { showEditProfileDialog = true }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile", tint = PigeonGold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (profile?.isVerifiedBreeder == true) {
                        VerifiedBreederBadge()
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (!profile?.bio.isNullOrBlank()) {
                        Text(
                            text = profile?.bio.orEmpty(),
                            color = PigeonTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (!profile?.location.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = PigeonTextSecondary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = profile?.location.orEmpty(), color = PigeonTextSecondary, fontSize = 11.sp)
                            }
                        }
                        if (!profile?.phone.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = PigeonTextSecondary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = profile?.phone.orEmpty(), color = PigeonTextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Admin Access Banner (if user is admin or orphicfaizan@gmail.com)
        if (profile?.isAdmin == true || profile?.email.equals("orphicfaizan@gmail.com", ignoreCase = true)) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable { onOpenAdmin() },
                    shape = RoundedCornerShape(12.dp),
                    color = PigeonGold.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, PigeonGold)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = PigeonGold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Open Admin Dashboard", color = PigeonGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Platform control, stats & listing management", color = PigeonGoldLight, fontSize = 11.sp)
                            }
                        }
                        Button(
                            onClick = onOpenAdmin,
                            colors = ButtonDefaults.buttonColors(containerColor = PigeonGold, contentColor = Color(0xFF140D00)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Control", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Subscription Plans Section
        item {
            Text(
                text = "Breeder Subscription Tiers",
                color = PigeonTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Unlock higher listing capacities, verified badges, and doctor hotline",
                color = PigeonTextSecondary,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )
        }

        item {
            SubscriptionTierCard(
                tierName = "Free Flyer",
                price = "$0 / Forever",
                features = listOf("Up to 3 Active Pigeon Listings", "Standard Marketplace Discovery", "PigeonPay Escrow Protection", "Buyer-Seller Direct Messaging"),
                isActive = profile?.subscriptionTier.equals("Free", ignoreCase = true) || profile?.subscriptionTier.isNullOrBlank(),
                onSelectTier = { viewModel.upgradeSubscriptionTier("Free") },
                accentColor = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            SubscriptionTierCard(
                tierName = "Elite Loft (Premium)",
                price = "$19 / Month",
                features = listOf("Up to 25 Active Pigeon Listings", "Official Verified Breeder Badge", "Priority Marketplace Placement", "10% Avian Doctor Consult Discount", "Loft Analytics & Performance Stats"),
                isActive = profile?.subscriptionTier.equals("Premium", ignoreCase = true),
                onSelectTier = { viewModel.upgradeSubscriptionTier("Premium") },
                accentColor = PigeonViolet
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            SubscriptionTierCard(
                tierName = "Pigeon Mogul (Pro)",
                price = "$49 / Month",
                features = listOf("Unlimited Active Pigeon Listings", "Dedicated Online Pigeon Store URL", "Zero Marketplace Commission", "24/7 Direct Avian Doctor Hotline", "Championship Bloodline DNA Badging"),
                isActive = profile?.subscriptionTier.equals("Pro", ignoreCase = true),
                onSelectTier = { viewModel.upgradeSubscriptionTier("Pro") },
                accentColor = PigeonGold
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Sign Out Button
        item {
            OutlinedButton(
                onClick = {
                    AuthManager.signOut(
                        context = context,
                        credentialManager = credentialManager,
                        onSignOutComplete = onSignOut,
                        scope = scope
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFEF4444))
            ) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFEF4444))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out of Pigeonverse", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentProfile = profile,
            onDismiss = { showEditProfileDialog = false },
            onSave = { displayName, loftName, location, bio, phone ->
                viewModel.updateProfile(displayName, loftName, location, bio, phone)
                showEditProfileDialog = false
            }
        )
    }
}

@Composable
private fun SubscriptionTierCard(
    tierName: String,
    price: String,
    features: List<String>,
    isActive: Boolean,
    onSelectTier: () -> Unit,
    accentColor: Color
) {
    LuxuryCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = tierName, color = accentColor, fontSize = 15.sp, fontWeight = FontWeight.Black)
                    Text(text = price, color = PigeonTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                if (isActive) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PigeonEmerald.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, PigeonEmerald)
                    ) {
                        Text(
                            text = "CURRENT PLAN",
                            color = PigeonEmerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = onSelectTier,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Upgrade", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                features.forEach { feat ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = feat, color = PigeonTextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditProfileDialog(
    currentProfile: com.example.data.model.UserProfile?,
    onDismiss: () -> Unit,
    onSave: (displayName: String, loftName: String, location: String, bio: String, phone: String) -> Unit
) {
    var displayName by remember { mutableStateOf(currentProfile?.displayName.orEmpty()) }
    var loftName by remember { mutableStateOf(currentProfile?.loftName.orEmpty()) }
    var location by remember { mutableStateOf(currentProfile?.location.orEmpty()) }
    var bio by remember { mutableStateOf(currentProfile?.bio.orEmpty()) }
    var phone by remember { mutableStateOf(currentProfile?.phone.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Breeder Profile", color = PigeonGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Fancier Display Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = loftName,
                    onValueChange = { loftName = it },
                    label = { Text("Breeding Loft Name") },
                    placeholder = { Text("e.g. Royal Flanders Aviary") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (City, Country)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Breeder Bio / Heritage") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(displayName, loftName, location, bio, phone) },
                colors = ButtonDefaults.buttonColors(containerColor = PigeonGold, contentColor = Color(0xFF140D00))
            ) {
                Text("Save Profile", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PigeonTextSecondary)
            }
        },
        containerColor = PigeonSurfaceDark
    )
}
