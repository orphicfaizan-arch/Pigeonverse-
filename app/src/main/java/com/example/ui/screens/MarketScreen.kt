package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Pigeon
import com.example.ui.PigeonverseViewModel
import com.example.ui.components.LuxuryCard
import com.example.ui.components.StatusPill
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

val PIGEON_BREEDS = listOf(
    "All Breeds",
    "Racing Homer",
    "Jacobin",
    "Saxon Fairy Swallow",
    "Bokhara Trumpeter",
    "Fantail",
    "English Carrier",
    "Lahore",
    "Modenes",
    "King Pigeon",
    "Frillback",
    "Pouter"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(viewModel: PigeonverseViewModel) {
    val pigeons by viewModel.pigeons.collectAsState()
    val selectedBreed by viewModel.selectedBreedFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentUserProfile by viewModel.userProfile.collectAsState()

    var selectedPigeonForDetails by remember { mutableStateOf<Pigeon?>(null) }
    var selectedPigeonForCheckout by remember { mutableStateOf<Pigeon?>(null) }

    val filteredPigeons = remember(pigeons, selectedBreed, searchQuery) {
        pigeons.filter { pigeon ->
            val matchesBreed = selectedBreed == "All Breeds" || pigeon.breed.equals(selectedBreed, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                pigeon.title.contains(searchQuery, ignoreCase = true) ||
                pigeon.breed.contains(searchQuery, ignoreCase = true) ||
                pigeon.ringNumber.contains(searchQuery, ignoreCase = true) ||
                pigeon.sellerLoft.contains(searchQuery, ignoreCase = true) ||
                pigeon.location.contains(searchQuery, ignoreCase = true)
            matchesBreed && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Showcase Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_pigeon_banner_1793978089536),
                    contentDescription = "Championship Pigeon Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    PigeonSurfaceDark.copy(alpha = 0.6f),
                                    PigeonSurfaceDark
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PigeonGold,
                            contentColor = Color.Black
                        ) {
                            Text(
                                text = "EXCLUSIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GLOBAL PIGEON MARKETPLACE",
                            color = PigeonGoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Elite Bloodlines & Certified Rings",
                        color = PigeonTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search by breed, leg band #, or loft...", color = PigeonTextSecondary) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = PigeonGold)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = PigeonTextSecondary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PigeonGold,
                    unfocusedBorderColor = PigeonBorder,
                    focusedContainerColor = PigeonSurfaceCard,
                    unfocusedContainerColor = PigeonSurfaceCard,
                    focusedTextColor = PigeonTextPrimary,
                    unfocusedTextColor = PigeonTextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Breed Categories Chips Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PIGEON_BREEDS.forEach { breed ->
                    val isSelected = selectedBreed == breed
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setBreedFilter(breed) },
                        label = {
                            Text(
                                text = breed,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PigeonGold,
                            selectedLabelColor = Color(0xFF140E00),
                            containerColor = PigeonSurfaceCard,
                            labelColor = PigeonTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) PigeonGold else PigeonBorder,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }
        }

        // Header with count
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredPigeons.size} Pigeons Available",
                    color = PigeonTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Escrow Protected",
                    color = PigeonEmerald,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Pigeon Listings
        if (filteredPigeons.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = PigeonTextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No pigeons found matching '$selectedBreed'",
                            color = PigeonTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Try clearing search or selecting another breed.",
                            color = PigeonTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(filteredPigeons, key = { it.id }) { pigeon ->
                PigeonListingCard(
                    pigeon = pigeon,
                    onViewDetails = { selectedPigeonForDetails = pigeon },
                    onBuyNow = { selectedPigeonForCheckout = pigeon },
                    onChatSeller = {
                        viewModel.startChatWithSeller(
                            sellerId = pigeon.sellerId,
                            sellerName = pigeon.sellerLoft.ifBlank { pigeon.sellerName },
                            pigeonId = pigeon.id,
                            pigeonTitle = pigeon.title,
                            pigeonBreed = pigeon.breed,
                            pigeonPrice = pigeon.price
                        )
                    }
                )
            }
        }
    }

    // Detail Bottom Sheet
    if (selectedPigeonForDetails != null) {
        val pigeon = selectedPigeonForDetails!!
        ModalBottomSheet(
            onDismissRequest = { selectedPigeonForDetails = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = PigeonSurfaceDark
        ) {
            PigeonDetailSheetContent(
                pigeon = pigeon,
                onDismiss = { selectedPigeonForDetails = null },
                onBuyNow = {
                    selectedPigeonForDetails = null
                    selectedPigeonForCheckout = pigeon
                },
                onChatSeller = {
                    selectedPigeonForDetails = null
                    viewModel.startChatWithSeller(
                        sellerId = pigeon.sellerId,
                        sellerName = pigeon.sellerLoft.ifBlank { pigeon.sellerName },
                        pigeonId = pigeon.id,
                        pigeonTitle = pigeon.title,
                        pigeonBreed = pigeon.breed,
                        pigeonPrice = pigeon.price
                    )
                }
            )
        }
    }

    // Checkout / Escrow Modal
    if (selectedPigeonForCheckout != null) {
        val pigeon = selectedPigeonForCheckout!!
        CheckoutDialog(
            pigeon = pigeon,
            onDismiss = { selectedPigeonForCheckout = null },
            onConfirmOrder = { method, address ->
                viewModel.placeOrder(pigeon, method, address)
                selectedPigeonForCheckout = null
            }
        )
    }
}

@Composable
private fun PigeonListingCard(
    pigeon: Pigeon,
    onViewDetails: () -> Unit,
    onBuyNow: () -> Unit,
    onChatSeller: () -> Unit
) {
    LuxuryCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onViewDetails() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PigeonViolet.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = pigeon.breed.uppercase(),
                                color = PigeonViolet,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PigeonSurfaceElevated
                        ) {
                            Text(
                                text = pigeon.gender,
                                color = PigeonTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        if (pigeon.ringNumber.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ring: ${pigeon.ringNumber}",
                                color = PigeonGoldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = pigeon.title,
                        color = PigeonTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Loft: ${pigeon.sellerLoft.ifBlank { pigeon.sellerName }} • ${pigeon.location}",
                        color = PigeonTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                StatusPill(status = pigeon.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Specs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SpecChip(label = "Color", value = pigeon.colorMorph)
                SpecChip(label = "Hatch", value = pigeon.birthYear.toString())
                if (pigeon.vaccinated) {
                    SpecChip(label = "Health", value = "Vaccinated")
                }
                if (pigeon.pedigreeAvailable) {
                    SpecChip(label = "Pedigree", value = "Certified")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Price and Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Escrow Price", color = PigeonTextSecondary, fontSize = 11.sp)
                    Text(
                        text = "$${"%,.0f".format(pigeon.price)}",
                        color = PigeonGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onChatSeller,
                        modifier = Modifier
                            .size(40.dp)
                            .background(PigeonSurfaceElevated, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chat with Seller",
                            tint = PigeonGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (pigeon.status.equals("available", ignoreCase = true)) {
                        Button(
                            onClick = onBuyNow,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PigeonGold,
                                contentColor = Color(0xFF140D00)
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Buy with Escrow", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = onViewDetails,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "View Pedigree", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = PigeonSurfaceElevated.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, PigeonBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$label: ", color = PigeonTextSecondary, fontSize = 10.sp)
            Text(text = value, color = PigeonTextPrimary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun PigeonDetailSheetContent(
    pigeon: Pigeon,
    onDismiss: () -> Unit,
    onBuyNow: () -> Unit,
    onChatSeller: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            VerifiedBreederBadge()
            StatusPill(status = pigeon.status)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = pigeon.title,
            color = PigeonTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )

        Text(
            text = "${pigeon.breed} • ${pigeon.gender} • Ring #${pigeon.ringNumber}",
            color = PigeonGoldLight,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lineage & Health Pedigree",
            color = PigeonTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = pigeon.description.ifBlank { "Full documented racing and show lineage available with DNA certificate." },
            color = PigeonTextSecondary,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = PigeonSurfaceCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, PigeonBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Loft Location", color = PigeonTextSecondary, fontSize = 11.sp)
                    Text(text = pigeon.location, color = PigeonTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = PigeonSurfaceCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, PigeonBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Escrow Price", color = PigeonTextSecondary, fontSize = 11.sp)
                    Text(text = "$${"%,.0f".format(pigeon.price)}", color = PigeonGold, fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onChatSeller,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Chat Seller")
            }

            if (pigeon.status.equals("available", ignoreCase = true)) {
                Button(
                    onClick = onBuyNow,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PigeonGold,
                        contentColor = Color(0xFF140D00)
                    )
                ) {
                    Icon(imageVector = Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Secure Escrow", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CheckoutDialog(
    pigeon: Pigeon,
    onDismiss: () -> Unit,
    onConfirmOrder: (paymentMethod: String, address: String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("PigeonPay Escrow") }
    var deliveryAddress by remember { mutableStateOf("124 Avian Way, Royal Loft District") }

    val methods = listOf("PigeonPay Escrow", "Avian Wire Transfer", "Credit Card / Stripe", "Crypto Escrow (USDC)")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pigeonverse Secure Escrow",
                color = PigeonGold,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Purchasing: ${pigeon.title}",
                    color = PigeonTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Total Escrow Amount: $${"%,.0f".format(pigeon.price)}",
                    color = PigeonGoldLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "Funds are held securely by Pigeonverse Escrow and only released when you inspect and confirm healthy delivery of the pigeon.",
                    color = PigeonTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "Select Payment Method",
                    color = PigeonTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                methods.forEach { method ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMethod = method }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (selectedMethod == method) PigeonGold else Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(1.dp, PigeonGold),
                            modifier = Modifier.size(16.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = method, color = PigeonTextPrimary, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    label = { Text("Delivery Address / Loft Depot", fontSize = 11.sp) },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmOrder(selectedMethod, deliveryAddress) },
                colors = ButtonDefaults.buttonColors(containerColor = PigeonGold, contentColor = Color(0xFF140D00))
            ) {
                Text("Authorize Escrow", fontWeight = FontWeight.Bold)
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
