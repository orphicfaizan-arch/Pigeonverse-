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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Pigeon
import com.example.ui.PigeonverseViewModel
import com.example.ui.components.LuxuryCard
import com.example.ui.components.StatusPill
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
fun InventoryScreen(viewModel: PigeonverseViewModel) {
    val userInventory by viewModel.userInventory.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val totalValuation = remember(userInventory) {
        userInventory.sumOf { it.price }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PigeonGold,
                contentColor = Color(0xFF140D00),
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 72.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Pigeon")
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp)
        ) {
            // Header stats
            item {
                Text(
                    text = "${userProfile?.loftName?.ifBlank { "My Loft" }} Inventory",
                    color = PigeonGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Track ring numbers, bloodline genetics, health and sale statuses",
                    color = PigeonTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                )
            }

            // Metrics row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBox(
                        title = "Loft Birds",
                        value = "${userInventory.size}",
                        accent = PigeonGold,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Available",
                        value = "${userInventory.count { it.status.equals("available", true) }}",
                        accent = PigeonEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Loft Value",
                        value = "₹%,d".format((totalValuation * 86.0).toLong()),
                        accent = PigeonViolet,
                        modifier = Modifier.weight(1.3f)
                    )
                }
            }

            if (userInventory.isEmpty()) {
                item {
                    LuxuryCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = null,
                                tint = PigeonGold,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Your Loft is Empty",
                                color = PigeonTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Register your breeding stock, show champions or young birds to list them in the marketplace.",
                                color = PigeonTextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Button(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PigeonGold, contentColor = Color(0xFF140D00)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Add First Pigeon")
                            }
                        }
                    }
                }
            } else {
                items(userInventory, key = { it.id }) { pigeon ->
                    InventoryPigeonItem(
                        pigeon = pigeon,
                        onToggleStatus = { nextStatus ->
                            viewModel.updatePigeonStatus(pigeon.id, nextStatus)
                        },
                        onDelete = {
                            viewModel.deletePigeon(pigeon.id)
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddPigeonDialog(
            defaultLocation = userProfile?.location.orEmpty().ifBlank { "International Loft" },
            onDismiss = { showAddDialog = false },
            onSave = { title, breed, gender, ring, year, color, price, loc, desc, vax, ped ->
                viewModel.addPigeonListing(title, breed, gender, ring, year, color, price, loc, desc, vax, ped)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun MetricBox(
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
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, color = PigeonTextSecondary, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = accent, fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun InventoryPigeonItem(
    pigeon: Pigeon,
    onToggleStatus: (String) -> Unit,
    onDelete: () -> Unit
) {
    LuxuryCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = pigeon.title,
                        color = PigeonTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${pigeon.breed} • ${pigeon.gender} • Ring: ${pigeon.ringNumber}",
                        color = PigeonGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                StatusPill(status = pigeon.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹%,d ($%.0f)".format(pigeon.inrPrice.toLong(), pigeon.price),
                    color = PigeonGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val nextStatus = when (pigeon.status.lowercase()) {
                        "available" -> "reserved"
                        "reserved" -> "sold"
                        else -> "available"
                    }
                    OutlinedButton(
                        onClick = { onToggleStatus(nextStatus) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Mark ${nextStatus.uppercase()}", fontSize = 10.sp)
                    }

                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPigeonDialog(
    defaultLocation: String,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        breed: String,
        gender: String,
        ringNumber: String,
        birthYear: Int,
        colorMorph: String,
        price: Double,
        location: String,
        description: String,
        vaccinated: Boolean,
        pedigreeAvailable: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedBreed by remember { mutableStateOf("Racing Homer") }
    var breedExpanded by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("Cock") }
    var ringNumber by remember { mutableStateOf("") }
    var birthYearStr by remember { mutableStateOf("2024") }
    var colorMorph by remember { mutableStateOf("Blue Bar") }
    var priceStr by remember { mutableStateOf("500") }
    var location by remember { mutableStateOf(defaultLocation) }
    var description by remember { mutableStateOf("") }
    var vaccinated by remember { mutableStateOf(true) }
    var pedigreeAvailable by remember { mutableStateOf(true) }

    val genders = listOf("Cock", "Hen", "Pair")
    val breeds = PIGEON_BREEDS.filter { it != "All Breeds" }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Pigeon to Inventory", color = PigeonGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Listing Title / Bird Name") },
                    placeholder = { Text("e.g. Ace National Long Distance") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Breed Selector
                ExposedDropdownMenuBox(
                    expanded = breedExpanded,
                    onExpandedChange = { breedExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedBreed,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pigeon Breed") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = breedExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = breedExpanded,
                        onDismissRequest = { breedExpanded = false }
                    ) {
                        breeds.forEach { b ->
                            DropdownMenuItem(
                                text = { Text(b) },
                                onClick = {
                                    selectedBreed = b
                                    breedExpanded = false
                                }
                            )
                        }
                    }
                }

                // Gender Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    genders.forEach { g ->
                        val isSel = selectedGender == g
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) PigeonGold else PigeonSurfaceCard,
                            border = BorderStroke(1.dp, if (isSel) PigeonGold else PigeonBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedGender = g }
                        ) {
                            Text(
                                text = g,
                                color = if (isSel) Color.Black else PigeonTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = ringNumber,
                    onValueChange = { ringNumber = it },
                    label = { Text("Official Ring / Leg Band Number") },
                    placeholder = { Text("e.g. BELG-2024-910482") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = birthYearStr,
                        onValueChange = { birthYearStr = it },
                        label = { Text("Birth Year") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Price (USD)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = colorMorph,
                    onValueChange = { colorMorph = it },
                    label = { Text("Color / Feather Morph") },
                    placeholder = { Text("e.g. Mealy Red, Dark Checker, Grizzle") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Loft Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Lineage & Pedigree Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = vaccinated,
                        onCheckedChange = { vaccinated = it },
                        colors = CheckboxDefaults.colors(checkedColor = PigeonEmerald)
                    )
                    Text("Paramyxovirus & Pox Vaccinated", fontSize = 12.sp, color = PigeonTextPrimary)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = pedigreeAvailable,
                        onCheckedChange = { pedigreeAvailable = it },
                        colors = CheckboxDefaults.colors(checkedColor = PigeonGold)
                    )
                    Text("Pedigree Certificate Included", fontSize = 12.sp, color = PigeonTextPrimary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    val year = birthYearStr.toIntOrNull() ?: 2024
                    onSave(
                        title.ifBlank { "$selectedBreed $selectedGender" },
                        selectedBreed,
                        selectedGender,
                        ringNumber.ifBlank { "PENDING-BAND" },
                        year,
                        colorMorph,
                        price,
                        location,
                        description,
                        vaccinated,
                        pedigreeAvailable
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PigeonGold, contentColor = Color(0xFF140D00))
            ) {
                Text("Publish to Market", fontWeight = FontWeight.Bold)
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
