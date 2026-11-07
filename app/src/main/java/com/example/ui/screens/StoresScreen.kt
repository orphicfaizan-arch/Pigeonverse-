package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Store
import com.example.ui.PigeonverseViewModel
import com.example.ui.components.LuxuryCard
import com.example.ui.components.VerifiedBreederBadge
import com.example.ui.theme.PigeonBorder
import com.example.ui.theme.PigeonEmerald
import com.example.ui.theme.PigeonGold
import com.example.ui.theme.PigeonGoldLight
import com.example.ui.theme.PigeonObsidian
import com.example.ui.theme.PigeonSurfaceCard
import com.example.ui.theme.PigeonSurfaceDark
import com.example.ui.theme.PigeonSurfaceElevated
import com.example.ui.theme.PigeonTextMuted
import com.example.ui.theme.PigeonTextPrimary
import com.example.ui.theme.PigeonTextSecondary
import com.example.ui.theme.PigeonViolet

@Composable
fun StoresScreen(viewModel: PigeonverseViewModel) {
    val stores by viewModel.stores.collectAsState()
    val userStore by viewModel.userStore.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val radiusFilter by viewModel.storeRadiusFilter.collectAsState()
    val cityFilter by viewModel.storeCityFilter.collectAsState()
    val searchQuery by viewModel.storeSearchQuery.collectAsState()

    var showStoreDialog by remember { mutableStateOf(false) }

    val filteredStores = remember(stores, radiusFilter, cityFilter, searchQuery) {
        stores.filter { store ->
            val matchesRadius = when (radiusFilter) {
                "< 5 km" -> store.distanceKm <= 5.0
                "< 10 km" -> store.distanceKm <= 10.0
                "< 25 km" -> store.distanceKm <= 25.0
                "< 50 km" -> store.distanceKm <= 50.0
                else -> true
            }

            val matchesCity = when (cityFilter) {
                "All Hubs" -> true
                else -> store.city.contains(cityFilter, ignoreCase = true) ||
                    store.location.contains(cityFilter, ignoreCase = true)
            }

            val matchesQuery = searchQuery.isBlank() ||
                store.storeName.contains(searchQuery, ignoreCase = true) ||
                store.ownerName.contains(searchQuery, ignoreCase = true) ||
                store.city.contains(searchQuery, ignoreCase = true) ||
                store.pincode.contains(searchQuery, ignoreCase = true) ||
                store.location.contains(searchQuery, ignoreCase = true) ||
                store.tagline.contains(searchQuery, ignoreCase = true) ||
                store.upiId.contains(searchQuery, ignoreCase = true)

            matchesRadius && matchesCity && matchesQuery
        }.sortedBy { it.distanceKm }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PigeonObsidian),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Banner Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.store_loft_banner_1793978102716),
                    contentDescription = "Pigeon Loft Estates",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, PigeonSurfaceDark.copy(alpha = 0.85f), PigeonSurfaceDark)
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.NearMe, contentDescription = null, tint = PigeonGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "NEARBY BREEDER LOFTS & STORES",
                            color = PigeonGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Discover Certified Pigeon Aviaries Around You",
                        color = PigeonTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Nearby Search Box
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = PigeonSurfaceDark,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, PigeonBorder)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setStoreSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search by Loft name, City, Pincode or UPI...", color = PigeonTextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = PigeonGold, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setStoreSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = PigeonTextSecondary, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = PigeonTextPrimary,
                        unfocusedTextColor = PigeonTextPrimary
                    ),
                    singleLine = true
                )
            }
        }

        // Radius & City Hub Filters
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                // Radius Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Radius:",
                        color = PigeonGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    listOf("All", "< 5 km", "< 10 km", "< 25 km", "< 50 km").forEach { radius ->
                        val selected = radiusFilter == radius
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.setStoreRadius(radius) },
                            label = { Text(radius, fontSize = 11.sp) },
                            leadingIcon = if (selected && radius != "All") {
                                { Icon(imageVector = Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(12.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PigeonGold,
                                selectedLabelColor = Color(0xFF140D00),
                                containerColor = PigeonSurfaceCard,
                                labelColor = PigeonTextSecondary
                            ),
                            border = BorderStroke(1.dp, if (selected) PigeonGold else PigeonBorder),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Metro Hubs Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hub:",
                        color = PigeonGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    listOf("All Hubs", "Bangalore", "Punjab", "Delhi NCR", "Hyderabad", "Mumbai", "International").forEach { city ->
                        val selected = cityFilter == city
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.setStoreCity(city) },
                            label = { Text(city, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PigeonViolet,
                                selectedLabelColor = Color.White,
                                containerColor = PigeonSurfaceCard,
                                labelColor = PigeonTextSecondary
                            ),
                            border = BorderStroke(1.dp, if (selected) PigeonViolet else PigeonBorder),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }
        }

        // User's Store Management Card
        item {
            LuxuryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (userStore == null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(PigeonGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddBusiness,
                                    contentDescription = null,
                                    tint = PigeonGold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Create Your Pigeon Store",
                                    color = PigeonTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "List your loft on the nearby search map & accept UPI Escrow",
                                    color = PigeonTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Button(
                                onClick = { showStoreDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PigeonGold,
                                    contentColor = Color(0xFF140D00)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Open Store", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        val store = userStore!!
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = store.storeName,
                                        color = PigeonGold,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    if (store.verified) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = PigeonEmerald, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Text(
                                    text = store.tagline.ifBlank { "Exclusive Pigeon Loft" },
                                    color = PigeonTextSecondary,
                                    fontSize = 12.sp
                                )
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = PigeonGoldLight, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${store.city}, ${store.stateRegion} (PIN ${store.pincode})",
                                        color = PigeonGoldLight,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = { showStoreDialog = true },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit Store", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(text = "Total Sales: ${store.totalSales} birds", color = PigeonTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Rating: ★ ${store.rating}", color = PigeonGoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "UPI: ${store.upiId}", color = PigeonEmerald, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Section Title with count
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NEARBY LOFTS FOUND (${filteredStores.size})",
                    color = PigeonGoldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Sorted by proximity",
                    color = PigeonTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        if (filteredStores.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = PigeonSurfaceDark,
                    border = BorderStroke(1.dp, PigeonBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = PigeonGold, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No Lofts Found in this Radius", color = PigeonTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = "Try broadening the distance filter to '< 50 km' or select 'All Hubs'.",
                            color = PigeonTextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Button(
                            onClick = {
                                viewModel.setStoreRadius("All")
                                viewModel.setStoreCity("All Hubs")
                                viewModel.setStoreSearchQuery("")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PigeonGold, contentColor = Color(0xFF140D00)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Reset Radius & Hub")
                        }
                    }
                }
            }
        } else {
            items(filteredStores, key = { it.id }) { store ->
                StoreCard(
                    store = store,
                    onContactLoft = {
                        viewModel.startChatWithSeller(
                            sellerId = store.ownerId,
                            sellerName = store.storeName,
                            pigeonId = "loft_general",
                            pigeonTitle = store.storeName,
                            pigeonBreed = "Loft Inquiry",
                            pigeonPrice = 0.0
                        )
                    }
                )
            }
        }
    }

    if (showStoreDialog) {
        StoreEditorDialog(
            initialStore = userStore,
            defaultLoftName = userProfile?.loftName.orEmpty().ifBlank { "${userProfile?.displayName}'s Royal Loft" },
            defaultLocation = userProfile?.location.orEmpty().ifBlank { "Bengaluru South, Karnataka" },
            onDismiss = { showStoreDialog = false },
            onSave = { name, tag, desc, loc, city, state, pin, upi, dist ->
                viewModel.saveUserStore(
                    storeName = name,
                    tagline = tag,
                    description = desc,
                    location = loc,
                    city = city,
                    stateRegion = state,
                    pincode = pin,
                    upiId = upi,
                    distanceKm = dist
                )
                showStoreDialog = false
            }
        )
    }
}

@Composable
private fun StoreCard(store: Store, onContactLoft: () -> Unit) {
    LuxuryCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = store.storeName,
                            color = PigeonTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (store.verified) {
                            Spacer(modifier = Modifier.width(6.dp))
                            VerifiedBreederBadge()
                        }
                    }
                    Text(
                        text = store.tagline,
                        color = PigeonGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Proximity Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PigeonGold.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, PigeonGold.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.NearMe, contentDescription = null, tint = PigeonGold, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${store.distanceKm} km",
                            color = PigeonGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = store.description,
                color = PigeonTextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Location & Pincode
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = PigeonTextSecondary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${store.location} • ${store.city} (PIN: ${store.pincode})",
                    color = PigeonTextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Indian Currency UPI & Escrow Security Badge
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = PigeonSurfaceElevated,
                border = BorderStroke(1.dp, PigeonBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = PigeonEmerald, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "UPI Escrow Verified",
                            color = PigeonEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = store.upiId,
                        color = PigeonGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = PigeonGold, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "%.1f".format(store.rating), color = PigeonGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${store.totalSales} Birds Re-homed",
                        color = PigeonTextSecondary,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val context = LocalContext.current
                    OutlinedButton(
                        onClick = {
                            val mapUri = Uri.parse("geo:0,0?q=${Uri.encode("${store.storeName}, ${store.location}, ${store.city}")}")
                            val intent = Intent(Intent.ACTION_VIEW, mapUri)
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode("${store.storeName}, ${store.location}, ${store.city}")}"))
                                context.startActivity(webIntent)
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PigeonGold),
                        border = BorderStroke(1.dp, PigeonGold.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Navigation, contentDescription = "Navigate", modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Maps", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Button(
                        onClick = onContactLoft,
                        colors = ButtonDefaults.buttonColors(containerColor = PigeonGold, contentColor = Color(0xFF140D00)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Inquire Loft", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreEditorDialog(
    initialStore: Store?,
    defaultLoftName: String,
    defaultLocation: String,
    onDismiss: () -> Unit,
    onSave: (
        storeName: String,
        tagline: String,
        description: String,
        location: String,
        city: String,
        stateRegion: String,
        pincode: String,
        upiId: String,
        distanceKm: Double
    ) -> Unit
) {
    var storeName by remember { mutableStateOf(initialStore?.storeName ?: defaultLoftName) }
    var tagline by remember { mutableStateOf(initialStore?.tagline ?: "Heritage Racing & Fancy Pigeon Aviaries") }
    var description by remember { mutableStateOf(initialStore?.description ?: "Dedicated to championship pedigree pigeons, verified DNA lineage, and healthy loft management.") }
    var location by remember { mutableStateOf(initialStore?.location ?: defaultLocation) }
    var city by remember { mutableStateOf(initialStore?.city ?: "Bangalore") }
    var stateRegion by remember { mutableStateOf(initialStore?.stateRegion ?: "Karnataka") }
    var pincode by remember { mutableStateOf(initialStore?.pincode ?: "560041") }
    var upiId by remember { mutableStateOf(initialStore?.upiId ?: "breederloft@okhdfcbank") }
    var distanceKmText by remember { mutableStateOf((initialStore?.distanceKm ?: 2.5).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialStore == null) "Open Online Pigeon Store" else "Edit Pigeon Store",
                color = PigeonGold,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = { Text("Store / Loft Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tagline,
                    onValueChange = { tagline = it },
                    label = { Text("Loft Tagline / Slogan") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City / Metro Hub") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = pincode,
                        onValueChange = { pincode = it },
                        label = { Text("Postal PIN") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = stateRegion,
                        onValueChange = { stateRegion = it },
                        label = { Text("State / Region") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = distanceKmText,
                        onValueChange = { distanceKmText = it },
                        label = { Text("Distance (km)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("Breeder UPI ID (e.g. loft@okaxis)") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("Required for Indian Currency Escrow payouts", color = PigeonGoldLight, fontSize = 10.sp) }
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Physical Aviary Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("About Your Loft & Heritage") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val dist = distanceKmText.toDoubleOrNull() ?: 3.0
                    onSave(storeName, tagline, description, location, city, stateRegion, pincode, upiId, dist)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PigeonGold, contentColor = Color(0xFF140D00))
            ) {
                Text("Save Store", fontWeight = FontWeight.Bold)
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
