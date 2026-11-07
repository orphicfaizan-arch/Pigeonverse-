package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ViewAgenda
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
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.ui.theme.PigeonObsidian
import com.example.ui.theme.PigeonSurfaceCard
import com.example.ui.theme.PigeonSurfaceDark
import com.example.ui.theme.PigeonSurfaceElevated
import com.example.ui.theme.PigeonTextMuted
import com.example.ui.theme.PigeonTextPrimary
import com.example.ui.theme.PigeonTextSecondary
import com.example.ui.theme.PigeonViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: PigeonverseViewModel) {
    val pigeons by viewModel.pigeons.collectAsState()
    val selectedBreed by viewModel.selectedBreedFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val healthFilter by viewModel.healthFilter.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()

    var isGridView by remember { mutableStateOf(true) }
    var selectedPigeonForDetails by remember { mutableStateOf<Pigeon?>(null) }
    var selectedPigeonForCheckout by remember { mutableStateOf<Pigeon?>(null) }

    val filteredPigeons = remember(pigeons, selectedBreed, searchQuery, healthFilter) {
        pigeons.filter { pigeon ->
            val matchesBreed = selectedBreed == "All Breeds" || pigeon.breed.equals(selectedBreed, ignoreCase = true)
            val matchesHealth = when (healthFilter) {
                "Vaccinated" -> pigeon.vaccinated
                "Pedigree" -> pigeon.pedigreeAvailable
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                pigeon.title.contains(searchQuery, ignoreCase = true) ||
                pigeon.breed.contains(searchQuery, ignoreCase = true) ||
                pigeon.ringNumber.contains(searchQuery, ignoreCase = true) ||
                pigeon.sellerLoft.contains(searchQuery, ignoreCase = true) ||
                pigeon.healthStatus.contains(searchQuery, ignoreCase = true) ||
                pigeon.location.contains(searchQuery, ignoreCase = true)
            matchesBreed && matchesHealth && matchesSearch
        }
    }

    val distinctBreedsCount = remember(pigeons) {
        pigeons.map { it.breed }.distinct().size
    }
    val vaccinatedCount = remember(pigeons) {
        pigeons.count { it.vaccinated }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 168.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(PigeonObsidian),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero Header & Metrics
        item(span = { GridItemSpan(maxLineSpan) }) {
            DashboardHeroSection(
                totalCount = pigeons.size,
                breedsCount = distinctBreedsCount,
                vaccinatedCount = vaccinatedCount,
                selectedCurrency = selectedCurrency,
                onToggleCurrency = { viewModel.toggleCurrency() }
            )
        }

        // Search Bar & View Mode Toggle
        item(span = { GridItemSpan(maxLineSpan) }) {
            DashboardSearchBar(
                searchQuery = searchQuery,
                onSearchChanged = { viewModel.setSearchQuery(it) },
                isGridView = isGridView,
                onToggleView = { isGridView = !isGridView }
            )
        }

        // Filter Bar (Breed Categories & Health Status Chips)
        item(span = { GridItemSpan(maxLineSpan) }) {
            DashboardFiltersSection(
                selectedBreed = selectedBreed,
                onSelectBreed = { viewModel.setBreedFilter(it) },
                selectedHealth = healthFilter,
                onSelectHealth = { viewModel.setHealthFilter(it) },
                selectedCurrency = selectedCurrency
            )
        }

        // Section Title
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PIGEON ROSTER (${filteredPigeons.size})",
                    color = PigeonGoldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (selectedCurrency == "INR") "Prices in ₹ INR (Escrow Protected)" else "Prices in $ USD (Escrow Protected)",
                    color = PigeonEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Empty state or Grid items
        if (filteredPigeons.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                DashboardEmptyState(onResetFilters = {
                    viewModel.setSearchQuery("")
                    viewModel.setBreedFilter("All Breeds")
                    viewModel.setHealthFilter("All")
                })
            }
        } else {
            items(filteredPigeons, key = { it.id }) { pigeon ->
                PigeonGridCard(
                    pigeon = pigeon,
                    isRupee = selectedCurrency == "INR",
                    onClick = { selectedPigeonForDetails = pigeon },
                    onBuyEscrow = { selectedPigeonForCheckout = pigeon },
                    onChat = {
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
            PigeonProfileSheetContent(
                pigeon = pigeon,
                isRupee = selectedCurrency == "INR",
                onDismiss = { selectedPigeonForDetails = null },
                onBuyEscrow = {
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
        PigeonEscrowCheckoutModal(
            pigeon = pigeon,
            currentCurrency = selectedCurrency,
            onDismiss = { selectedPigeonForCheckout = null },
            onConfirmOrder = { method, address, currency, upiId ->
                viewModel.placeOrder(
                    pigeon = pigeon,
                    paymentMethod = method,
                    address = address,
                    currency = currency,
                    upiTransactionId = upiId
                )
                selectedPigeonForCheckout = null
            }
        )
    }
}

@Composable
private fun DashboardHeroSection(
    totalCount: Int,
    breedsCount: Int,
    vaccinatedCount: Int,
    selectedCurrency: String = "INR",
    onToggleCurrency: () -> Unit = {}
) {
    LuxuryCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(PigeonGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pigeon_luxury_icon_1793978056609),
                            contentDescription = "Pigeonverse Emblem",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PIGEON PROFILES DASHBOARD",
                            color = PigeonGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Breed, Age & Clinical Health Directory",
                            color = PigeonTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Currency Toggle Pill (INR vs USD)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PigeonSurfaceElevated,
                    border = BorderStroke(1.dp, PigeonGold),
                    modifier = Modifier.clickable { onToggleCurrency() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹ INR",
                            color = if (selectedCurrency == "INR") PigeonGold else PigeonTextSecondary,
                            fontWeight = if (selectedCurrency == "INR") FontWeight.Black else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                        Text(
                            text = " | ",
                            color = PigeonBorder,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "$ USD",
                            color = if (selectedCurrency == "USD") PigeonGold else PigeonTextSecondary,
                            fontWeight = if (selectedCurrency == "USD") FontWeight.Black else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3-Metric Quick Tally Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DashboardMetricPill(
                    label = "Total Pigeons",
                    value = "$totalCount",
                    accent = PigeonGold,
                    modifier = Modifier.weight(1f)
                )
                DashboardMetricPill(
                    label = "Species Breeds",
                    value = "$breedsCount",
                    accent = PigeonViolet,
                    modifier = Modifier.weight(1f)
                )
                DashboardMetricPill(
                    label = "Vaccinated",
                    value = "$vaccinatedCount",
                    accent = PigeonEmerald,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DashboardMetricPill(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = PigeonSurfaceElevated,
        border = BorderStroke(1.dp, PigeonBorder)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(text = label, color = PigeonTextSecondary, fontSize = 9.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = accent, fontSize = 15.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun DashboardSearchBar(
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    isGridView: Boolean,
    onToggleView: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChanged,
            placeholder = { Text("Search by breed, age, health, band #...", color = PigeonTextSecondary, fontSize = 12.sp) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = PigeonGold)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChanged("") }) {
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
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DashboardFiltersSection(
    selectedBreed: String,
    onSelectBreed: (String) -> Unit,
    selectedHealth: String,
    onSelectHealth: (String) -> Unit,
    selectedCurrency: String = "INR"
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // Health Status Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val healthOptions = listOf(
                "All" to "All Health",
                "Vaccinated" to "🛡️ Vaccinated",
                "Pedigree" to "📜 Pedigree Certified"
            )
            healthOptions.forEach { (key, label) ->
                val isSel = selectedHealth == key
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSel) PigeonEmerald else PigeonSurfaceCard,
                    border = BorderStroke(1.dp, if (isSel) PigeonEmerald else PigeonBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectHealth(key) }
                ) {
                    Text(
                        text = label,
                        color = if (isSel) Color(0xFF002116) else PigeonTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }

        // Breeds Horizontal Scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PIGEON_BREEDS.forEach { breed ->
                val isSelected = selectedBreed == breed
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectBreed(breed) },
                    label = {
                        Text(
                            text = breed,
                            fontSize = 11.sp,
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
}

@Composable
fun PigeonGridCard(
    pigeon: Pigeon,
    isRupee: Boolean = true,
    onClick: () -> Unit,
    onBuyEscrow: () -> Unit,
    onChat: () -> Unit
) {
    LuxuryCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Visual Banner Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PigeonSurfaceElevated,
                                PigeonSurfaceCard
                            )
                        )
                    )
            ) {
                // Background icon accent
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = PigeonGold.copy(alpha = 0.12f),
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center)
                )

                // Top Chips Overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PigeonViolet.copy(alpha = 0.25f),
                        border = BorderStroke(0.5.dp, PigeonViolet)
                    ) {
                        Text(
                            text = pigeon.breed.uppercase(),
                            color = PigeonViolet,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    StatusPill(status = pigeon.status)
                }

                // Bottom Overlay: Price & Gender
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC0A0E14))
                            )
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isRupee) "₹%,d".format(pigeon.inrPrice.toLong()) else "$%,.0f".format(pigeon.price),
                        color = PigeonGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = pigeon.gender,
                        color = PigeonTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Card Body: Title, Breed, Age, Health Status
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = pigeon.title,
                    color = PigeonTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Band: ${pigeon.ringNumber.ifBlank { "Unbanded" }}",
                    color = PigeonTextMuted,
                    fontSize = 10.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                // AGE BADGE (Required)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PigeonSurfaceElevated,
                    border = BorderStroke(0.5.dp, PigeonBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Age",
                            tint = PigeonGoldLight,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Age: ${pigeon.ageDisplay}",
                            color = PigeonTextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // HEALTH STATUS BADGE (Required)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (pigeon.vaccinated) PigeonEmerald.copy(alpha = 0.15f) else PigeonSurfaceElevated,
                    border = BorderStroke(0.5.dp, if (pigeon.vaccinated) PigeonEmerald.copy(alpha = 0.5f) else PigeonBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = "Health Status",
                            tint = if (pigeon.vaccinated) PigeonEmerald else PigeonTextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = pigeon.healthBadge,
                            color = if (pigeon.vaccinated) PigeonEmerald else PigeonTextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = pigeon.sellerLoft.ifBlank { pigeon.sellerName },
                    color = PigeonTextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = onChat,
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(12.dp), tint = PigeonGold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chat", fontSize = 10.sp, color = PigeonGold)
                    }

                    if (pigeon.status.equals("available", ignoreCase = true)) {
                        Button(
                            onClick = onBuyEscrow,
                            modifier = Modifier
                                .weight(1.3f)
                                .height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PigeonGold,
                                contentColor = Color(0xFF140D00)
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Escrow", fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    } else {
                        OutlinedButton(
                            onClick = onClick,
                            modifier = Modifier
                                .weight(1.3f)
                                .height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Pedigree", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PigeonProfileSheetContent(
    pigeon: Pigeon,
    isRupee: Boolean = true,
    onDismiss: () -> Unit,
    onBuyEscrow: () -> Unit,
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
            modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Age & Health Highlights
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = PigeonSurfaceCard,
                border = BorderStroke(1.dp, PigeonBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = PigeonGoldLight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Age & Hatch", color = PigeonTextSecondary, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = pigeon.ageDisplay, color = PigeonTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Hatched Year ${pigeon.birthYear}", color = PigeonTextMuted, fontSize = 10.sp)
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = PigeonSurfaceCard,
                border = BorderStroke(1.dp, PigeonBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.HealthAndSafety, contentDescription = null, tint = PigeonEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Health Status", color = PigeonTextSecondary, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = pigeon.healthBadge, color = PigeonEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = if (pigeon.pedigreeAvailable) "DNA Pedigree Verified" else "Health Inspected", color = PigeonTextMuted, fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Lineage & Pedigree Dossier",
            color = PigeonTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = pigeon.description.ifBlank { "Full documented racing and show lineage available with certified DNA pedigree ring registry." },
            color = PigeonTextSecondary,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                color = PigeonSurfaceCard,
                border = BorderStroke(1.dp, PigeonBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "Loft Origin", color = PigeonTextSecondary, fontSize = 10.sp)
                    Text(text = pigeon.location, color = PigeonTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                color = PigeonSurfaceCard,
                border = BorderStroke(1.dp, PigeonBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "Escrow Price", color = PigeonTextSecondary, fontSize = 10.sp)
                    Text(
                        text = if (isRupee) "₹%,d".format(pigeon.inrPrice.toLong()) else "$%,.0f".format(pigeon.price),
                        color = PigeonGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onChatSeller,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = PigeonGold)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Chat Seller", color = PigeonGold)
            }

            if (pigeon.status.equals("available", ignoreCase = true)) {
                Button(
                    onClick = onBuyEscrow,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PigeonGold,
                        contentColor = Color(0xFF140D00)
                    )
                ) {
                    Icon(imageVector = Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Secure Escrow", fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PigeonEscrowCheckoutModal(
    pigeon: Pigeon,
    currentCurrency: String,
    onDismiss: () -> Unit,
    onConfirmOrder: (paymentMethod: String, address: String, currency: String, upiId: String) -> Unit
) {
    var currency by remember { mutableStateOf(currentCurrency) }
    var selectedMethod by remember { mutableStateOf("UPI Escrow (BHIM / GPay / PhonePe)") }
    var buyerUpiId by remember { mutableStateOf("fancier@okhdfcbank") }
    var deliveryAddress by remember { mutableStateOf("Flat 402, High-Flyer Residency, Bangalore South, KA 560041") }

    val isRupee = currency == "INR"
    val formattedTotal = if (isRupee) "₹%,d".format(pigeon.inrPrice.toLong()) else "$%,.0f".format(pigeon.price)

    val methods = listOf(
        "UPI Escrow (BHIM / GPay / PhonePe)",
        "RuPay & Indian Cards (3D Secure RBI OTP)",
        "NetBanking / IMPS (SBI, HDFC, ICICI, Axis)",
        "PigeonPay Escrow Vault"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = PigeonEmerald, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pigeonverse RBI Escrow",
                        color = PigeonGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PigeonSurfaceElevated,
                    border = BorderStroke(1.dp, PigeonGold),
                    modifier = Modifier.clickable { currency = if (currency == "INR") "USD" else "INR" }
                ) {
                    Text(
                        text = currency,
                        color = PigeonGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pigeon Summary
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PigeonSurfaceElevated,
                    border = BorderStroke(0.5.dp, PigeonBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = pigeon.title,
                            color = PigeonTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Breed: ${pigeon.breed} • Ring: #${pigeon.ringNumber} • Age: ${pigeon.ageDisplay}",
                            color = PigeonGoldLight,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Escrow Deposit:", color = PigeonTextSecondary, fontSize = 11.sp)
                            Text(text = formattedTotal, color = PigeonGold, fontSize = 15.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }

                Text(
                    text = "Select Indian Payment Gateway",
                    color = PigeonTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                methods.forEach { method ->
                    val isSelected = selectedMethod == method
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) PigeonSurfaceCard else Color.Transparent,
                        border = BorderStroke(1.dp, if (isSelected) PigeonGold else PigeonBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMethod = method }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) PigeonGold else Color.Transparent,
                                border = BorderStroke(1.dp, PigeonGold),
                                modifier = Modifier.size(12.dp)
                            ) {}
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = method,
                                color = if (isSelected) PigeonTextPrimary else PigeonTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                if (selectedMethod.startsWith("UPI")) {
                    OutlinedTextField(
                        value = buyerUpiId,
                        onValueChange = { buyerUpiId = it },
                        label = { Text("Your UPI VPA (e.g. name@okhdfcbank)", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    label = { Text("Delivery Loft / Aviary Address & Pincode", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                // Indian Payment Security Guarantee Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PigeonSurfaceElevated,
                    border = BorderStroke(1.dp, PigeonEmerald.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = PigeonEmerald, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Indian Currency Escrow Security Protocol",
                                color = PigeonEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "• Dual-Key RBI Escrow: Payment held in a regulated nodal account. Never sent directly in advance.\n• 6-Digit Handover OTP: Generated on booking. Give code to breeder ONLY after live inspection & leg ring check.\n• 48-Hour Live Health Guarantee: 100% refund if health mismatch is reported.\n• 256-Bit Bank Grade NPCI/SSL Encryption.",
                            color = PigeonTextSecondary,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmOrder(selectedMethod, deliveryAddress, currency, buyerUpiId) },
                colors = ButtonDefaults.buttonColors(containerColor = PigeonGold, contentColor = Color(0xFF140D00)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Authorize Escrow Deposit ($formattedTotal)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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

@Composable
private fun DashboardEmptyState(onResetFilters: () -> Unit) {
    LuxuryCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                tint = PigeonGold,
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Pigeon Profiles Match Filters",
                color = PigeonTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = "Try clearing search keywords or switching breed and health criteria.",
                color = PigeonTextSecondary,
                fontSize = 11.sp,
                modifier = Modifier.padding(vertical = 6.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
                onClick = onResetFilters,
                colors = ButtonDefaults.buttonColors(containerColor = PigeonGold, contentColor = Color(0xFF140D00)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Reset Filters")
            }
        }
    }
}
