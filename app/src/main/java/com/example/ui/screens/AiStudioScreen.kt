package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AiRole
import com.example.data.ai.ChatMessage
import com.example.data.ai.GroundingCitation
import com.example.data.ai.ImageStudioUiState
import com.example.ui.PigeonTab
import com.example.ui.PigeonverseViewModel
import com.example.ui.theme.PigeonBorder
import com.example.ui.theme.PigeonEmerald
import com.example.ui.theme.PigeonGold
import com.example.ui.theme.PigeonGoldDark
import com.example.ui.theme.PigeonGoldLight
import com.example.ui.theme.PigeonObsidian
import com.example.ui.theme.PigeonSurfaceCard
import com.example.ui.theme.PigeonSurfaceDark
import com.example.ui.theme.PigeonSurfaceElevated
import com.example.ui.theme.PigeonTextMuted
import com.example.ui.theme.PigeonTextPrimary
import com.example.ui.theme.PigeonTextSecondary
import com.example.ui.theme.PigeonViolet
import kotlinx.coroutines.launch

@Composable
fun AiStudioScreen(viewModel: PigeonverseViewModel) {
    var selectedStudioTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Avian Chatbot", "Google Grounding", "Nano Banana Studio")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PigeonObsidian)
    ) {
        // Luxury Header Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PigeonSurfaceDark,
            border = BorderStroke(1.dp, PigeonBorder)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PigeonGold.copy(alpha = 0.15f))
                                .border(1.dp, PigeonGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PigeonGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Avian AI Intelligence",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = PigeonTextPrimary
                            )
                            Text(
                                text = "Gemini 4 Argon • 3.8 Flash • Nano Banana 2.1",
                                fontSize = 11.sp,
                                color = PigeonGoldLight
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PigeonEmerald.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, PigeonEmerald.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(PigeonEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live Firebase AI",
                                fontSize = 10.sp,
                                color = PigeonEmerald,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Studio Sub-Tabs
                TabRow(
                    selectedTabIndex = selectedStudioTab,
                    containerColor = Color.Transparent,
                    contentColor = PigeonGold,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedStudioTab]),
                            color = PigeonGold,
                            height = 2.5.dp
                        )
                    },
                    divider = {}
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedStudioTab == index,
                            onClick = { selectedStudioTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedStudioTab == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedStudioTab == index) PigeonGold else PigeonTextSecondary
                                )
                            }
                        )
                    }
                }
            }
        }

        // Active Tab Screen Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedStudioTab) {
                0 -> GeminiChatbotTab(viewModel = viewModel)
                1 -> GroundingHubTab(viewModel = viewModel)
                2 -> NanoBananaStudioTab(viewModel = viewModel)
            }
        }
    }
}

// ==========================================
// 1. GEMINI MULTI-TURN CHATBOT TAB
// ==========================================
@Composable
private fun GeminiChatbotTab(viewModel: PigeonverseViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    val isGenerating by viewModel.isAiGenerating.collectAsState()
    val currentRole by viewModel.currentAiRole.collectAsState()
    val listState = rememberLazyListState()
    var inputQuery by remember { mutableStateOf("") }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Role Selection Chips
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PigeonSurfaceElevated,
            border = BorderStroke(1.dp, PigeonBorder)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = "SELECT AI ADVISOR SPECIALTY & MODEL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PigeonTextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AiRole.entries.forEach { role ->
                        val isSelected = role == currentRole
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectAiRole(role) },
                            label = {
                                Text(
                                    text = role.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PigeonGold,
                                selectedLabelColor = Color(0xFF140D00),
                                containerColor = PigeonSurfaceCard,
                                labelColor = PigeonTextPrimary
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) PigeonGold else PigeonBorder
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${currentRole.subtitle}",
                    fontSize = 10.sp,
                    color = PigeonGoldLight
                )
            }
        }

        // Chat Message Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(msg = msg)
            }

            if (isGenerating) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = PigeonGold,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Synthesizing response with ${currentRole.modelName}...",
                            fontSize = 11.sp,
                            color = PigeonGoldLight
                        )
                    }
                }
            }
        }

        // Quick Suggestion Chips
        val suggestions = remember(currentRole) {
            when (currentRole) {
                AiRole.COMPLEX_GENETICS -> listOf(
                    "Punnett square for Ash-Red x Blue Bar",
                    "PMV-1 Paramyxovirus protocol & timing",
                    "Racing Homer wing shape aerodynamics",
                    "Inbreeding coefficient in line-breeding"
                )
                AiRole.LOFT_ADVISOR -> listOf(
                    "Optimal loft ventilation for high humidity",
                    "Young bird racing conditioning schedule",
                    "Protein vs fat rations during moult",
                    "Preparing pairs for breeding season"
                )
                AiRole.QUICK_ASSISTANT -> listOf(
                    "Identify ring code: BELG-2024",
                    "Daily feed quantity for 20 birds",
                    "Electrolyte mix after 400km toss",
                    "Grit and mineral essentials list"
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PigeonSurfaceDark)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            suggestions.forEach { prompt ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PigeonSurfaceElevated,
                    border = BorderStroke(1.dp, PigeonBorder),
                    modifier = Modifier.clickable {
                        inputQuery = prompt
                        viewModel.sendAiChatMessage(prompt)
                    }
                ) {
                    Text(
                        text = prompt,
                        fontSize = 10.sp,
                        color = PigeonTextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Input Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PigeonSurfaceDark,
            border = BorderStroke(1.dp, PigeonBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.clearAiChat() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Clear Chat",
                        tint = PigeonTextMuted
                    )
                }

                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    placeholder = {
                        Text(
                            text = "Ask ${currentRole.title}...",
                            fontSize = 12.sp,
                            color = PigeonTextMuted
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp),
                    maxLines = 3,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PigeonSurfaceElevated,
                        unfocusedContainerColor = PigeonSurfaceElevated,
                        focusedBorderColor = PigeonGold,
                        unfocusedBorderColor = PigeonBorder,
                        focusedTextColor = PigeonTextPrimary,
                        unfocusedTextColor = PigeonTextPrimary
                    )
                )

                IconButton(
                    onClick = {
                        if (inputQuery.isNotBlank() && !isGenerating) {
                            val textToSend = inputQuery
                            inputQuery = ""
                            viewModel.sendAiChatMessage(textToSend)
                        }
                    },
                    enabled = inputQuery.isNotBlank() && !isGenerating,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (inputQuery.isNotBlank() && !isGenerating) PigeonGold else PigeonSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (inputQuery.isNotBlank() && !isGenerating) Color(0xFF140D00) else PigeonTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMessage) {
    val isUser = msg.sender == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isUser) 14.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 14.dp
            ),
            color = if (isUser) PigeonGold else PigeonSurfaceElevated,
            contentColor = if (isUser) Color(0xFF140D00) else PigeonTextPrimary,
            border = if (!isUser) BorderStroke(1.dp, PigeonBorder) else null,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = PigeonGoldLight,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Pigeonverse Intelligence",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PigeonGoldLight
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = msg.text,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ==========================================
// 2. GOOGLE SEARCH & MAPS GROUNDING TAB
// ==========================================
@Composable
private fun GroundingHubTab(viewModel: PigeonverseViewModel) {
    val context = LocalContext.current
    val groundingResult by viewModel.groundingResult.collectAsState()
    val citations by viewModel.groundingCitations.collectAsState()
    val isLoading by viewModel.isGroundingLoading.collectAsState()
    val groundingType by viewModel.groundingType.collectAsState()

    var activeGroundingMode by remember { mutableStateOf("search") } // "search" or "maps"
    var queryText by remember { mutableStateOf("") }

    val presetSearchQueries = listOf(
        "Latest Barcelona International pigeon race results",
        "Recent avian influenza advisories for racing pigeons",
        "Top Belgian Homer bloodlines and auction record prices",
        "FCI international pigeon olympiad ring regulations"
    )

    val presetMapsQueries = listOf(
        "Avian veterinarians and pigeon bird clinics near me",
        "Pigeon grain feed suppliers and loft nutrition stores nearby",
        "Pigeon racing federation liberation race toss points",
        "Pigeon stores and certified breeders in Bangalore and Punjab"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Mode Selector: Search Grounding vs Maps Grounding
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { activeGroundingMode = "search" },
                shape = RoundedCornerShape(12.dp),
                color = if (activeGroundingMode == "search") PigeonGold.copy(alpha = 0.2f) else PigeonSurfaceCard,
                border = BorderStroke(
                    1.dp,
                    if (activeGroundingMode == "search") PigeonGold else PigeonBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = if (activeGroundingMode == "search") PigeonGold else PigeonTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Search Grounding",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeGroundingMode == "search") PigeonGold else PigeonTextPrimary
                        )
                        Text(
                            text = "Google Search live web data",
                            fontSize = 10.sp,
                            color = PigeonTextSecondary
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { activeGroundingMode = "maps" },
                shape = RoundedCornerShape(12.dp),
                color = if (activeGroundingMode == "maps") PigeonEmerald.copy(alpha = 0.2f) else PigeonSurfaceCard,
                border = BorderStroke(
                    1.dp,
                    if (activeGroundingMode == "maps") PigeonEmerald else PigeonBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (activeGroundingMode == "maps") PigeonEmerald else PigeonTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Maps Grounding",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeGroundingMode == "maps") PigeonEmerald else PigeonTextPrimary
                        )
                        Text(
                            text = "Google Maps places & locations",
                            fontSize = 10.sp,
                            color = PigeonTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Grounding Query Input
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = PigeonSurfaceCard,
            border = BorderStroke(1.dp, PigeonBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (activeGroundingMode == "search") "LIVE GOOGLE SEARCH QUERY (gemini-3.8-flash)" else "GEOSPATIAL GOOGLE MAPS QUERY (gemini-3.8-flash)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (activeGroundingMode == "search") PigeonGoldLight else PigeonEmerald
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = queryText,
                    onValueChange = { queryText = it },
                    placeholder = {
                        Text(
                            text = if (activeGroundingMode == "search") "e.g. Latest Barcelona International pigeon race winners" else "e.g. Find certified avian bird clinics and pigeon feed stores",
                            fontSize = 12.sp,
                            color = PigeonTextMuted
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PigeonSurfaceDark,
                        unfocusedContainerColor = PigeonSurfaceDark,
                        focusedBorderColor = if (activeGroundingMode == "search") PigeonGold else PigeonEmerald,
                        unfocusedBorderColor = PigeonBorder,
                        focusedTextColor = PigeonTextPrimary,
                        unfocusedTextColor = PigeonTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (queryText.isNotBlank()) {
                            if (activeGroundingMode == "search") {
                                viewModel.performSearchGrounding(queryText)
                            } else {
                                viewModel.performMapsGrounding(queryText)
                            }
                        }
                    },
                    enabled = queryText.isNotBlank() && !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeGroundingMode == "search") PigeonGold else PigeonEmerald,
                        contentColor = Color(0xFF140D00)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFF140D00),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Grounding with Google...", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    } else {
                        Icon(
                            imageVector = if (activeGroundingMode == "search") Icons.Default.Search else Icons.Default.Explore,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (activeGroundingMode == "search") "Execute Grounded Search" else "Search Google Maps Places",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Preset Inquiry Cards
        Text(
            text = "RECOMMENDED GROUNDED INQUIRIES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PigeonTextMuted
        )
        Spacer(modifier = Modifier.height(6.dp))

        val currentPresets = if (activeGroundingMode == "search") presetSearchQueries else presetMapsQueries
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            currentPresets.forEach { preset ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PigeonSurfaceElevated,
                    border = BorderStroke(1.dp, PigeonBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            queryText = preset
                            if (activeGroundingMode == "search") {
                                viewModel.performSearchGrounding(preset)
                            } else {
                                viewModel.performMapsGrounding(preset)
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (activeGroundingMode == "search") Icons.Default.Language else Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = if (activeGroundingMode == "search") PigeonGoldLight else PigeonEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = preset,
                            fontSize = 12.sp,
                            color = PigeonTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grounding Result Section
        if (groundingResult != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = PigeonSurfaceElevated,
                border = BorderStroke(1.dp, PigeonGoldDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PigeonGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (groundingType == "search") "Google Search Grounded Result" else "Google Maps Grounded Result",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PigeonGold
                            )
                        }

                        IconButton(
                            onClick = { viewModel.clearGrounding() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Dismiss", tint = PigeonTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = groundingResult!!,
                        fontSize = 13.sp,
                        color = PigeonTextPrimary,
                        lineHeight = 20.sp
                    )

                    // Grounding Sources & Citations
                    if (citations.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "VERIFIED SOURCES & PLACES (${citations.size})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PigeonGoldLight
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            citations.forEach { citation ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PigeonSurfaceDark,
                                    border = BorderStroke(1.dp, PigeonBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (citation.uri.isNotBlank()) {
                                                try {
                                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(citation.uri))
                                                    context.startActivity(browserIntent)
                                                } catch (e: Exception) {
                                                    // ignore
                                                }
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = citation.title,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = PigeonTextPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (citation.uri.isNotBlank()) {
                                                Text(
                                                    text = citation.uri,
                                                    fontSize = 9.sp,
                                                    color = PigeonTextMuted,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.OpenInNew,
                                            contentDescription = "Open",
                                            tint = PigeonGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. NANO BANANA STUDIO (CREATE & EDIT IMAGES)
// ==========================================
@Composable
private fun NanoBananaStudioTab(viewModel: PigeonverseViewModel) {
    val state by viewModel.imageStudioState.collectAsState()
    var studioMode by remember { mutableStateOf("create") } // "create" or "edit"
    var createPrompt by remember { mutableStateOf("") }
    var editInstruction by remember { mutableStateOf("") }

    val presetPrompts = listOf(
        "Grand champion Blue Bar Racing Homer with fiery golden eyes and sleek plumage on a velvet pedestal",
        "Opulent pure white Jacobin show pigeon with full majestic feather hood in a dark marble loft",
        "Official Pigeonverse gold seal pedigree certificate badge with laurel leaves and royal eagle wings",
        "Rare Saxon Fairy Swallow pigeon with immaculate feathered feet on polished walnut perch"
    )

    val editPresets = listOf(
        "Add a prestigious 1st Place Champion Gold Medal ribbon around the neck",
        "Enhance environment with dramatic golden hour sunset lighting through loft skylight",
        "Add an engraved golden champion leg band with ring number PGNV-2026",
        "Place a luxury velvet presentation cushion underneath the bird"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Mode Switch: Create vs Edit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { studioMode = "create" },
                shape = RoundedCornerShape(12.dp),
                color = if (studioMode == "create") PigeonGold.copy(alpha = 0.2f) else PigeonSurfaceCard,
                border = BorderStroke(1.dp, if (studioMode == "create") PigeonGold else PigeonBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (studioMode == "create") PigeonGold else PigeonTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Text-to-Image",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (studioMode == "create") PigeonGold else PigeonTextPrimary
                        )
                        Text(
                            text = "gemini-nano-banana-2.1",
                            fontSize = 9.sp,
                            color = PigeonTextSecondary
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { studioMode = "edit" },
                shape = RoundedCornerShape(12.dp),
                color = if (studioMode == "edit") PigeonViolet.copy(alpha = 0.2f) else PigeonSurfaceCard,
                border = BorderStroke(1.dp, if (studioMode == "edit") PigeonViolet else PigeonBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = if (studioMode == "edit") PigeonViolet else PigeonTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Edit Image",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (studioMode == "edit") PigeonViolet else PigeonTextPrimary
                        )
                        Text(
                            text = "Prompt-based bird edit",
                            fontSize = 9.sp,
                            color = PigeonTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (studioMode == "create") {
            // Text-to-Image Input Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = PigeonSurfaceCard,
                border = BorderStroke(1.dp, PigeonBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "SYNTHESIZE PIGEON MASTERPIECE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PigeonGoldLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = createPrompt,
                        onValueChange = { createPrompt = it },
                        placeholder = {
                            Text(
                                text = "Describe pigeon breed, color morph, eye color, loft setting, and lighting...",
                                fontSize = 12.sp,
                                color = PigeonTextMuted
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = PigeonSurfaceDark,
                            unfocusedContainerColor = PigeonSurfaceDark,
                            focusedBorderColor = PigeonGold,
                            unfocusedBorderColor = PigeonBorder,
                            focusedTextColor = PigeonTextPrimary,
                            unfocusedTextColor = PigeonTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.generatePigeonImage(createPrompt) },
                        enabled = createPrompt.isNotBlank() && state !is ImageStudioUiState.Loading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PigeonGold,
                            contentColor = Color(0xFF140D00)
                        )
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Generate via gemini-nano-banana-2.1", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "INSPIRATION PROMPTS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = PigeonTextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                presetPrompts.forEach { prompt ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PigeonSurfaceElevated,
                        border = BorderStroke(1.dp, PigeonBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                createPrompt = prompt
                                viewModel.generatePigeonImage(prompt)
                            }
                    ) {
                        Text(
                            text = prompt,
                            fontSize = 11.sp,
                            color = PigeonTextSecondary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        } else {
            // Edit Image Mode
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = PigeonSurfaceCard,
                border = BorderStroke(1.dp, PigeonBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "IMAGE-TO-IMAGE EDIT INSTRUCTION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PigeonViolet
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = editInstruction,
                        onValueChange = { editInstruction = it },
                        placeholder = {
                            Text(
                                text = "e.g. Add 1st place gold medal ribbon, change background to sunset loft...",
                                fontSize = 12.sp,
                                color = PigeonTextMuted
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = PigeonSurfaceDark,
                            unfocusedContainerColor = PigeonSurfaceDark,
                            focusedBorderColor = PigeonViolet,
                            unfocusedBorderColor = PigeonBorder,
                            focusedTextColor = PigeonTextPrimary,
                            unfocusedTextColor = PigeonTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val baseBitmap = createSampleBaseBitmap()
                            viewModel.editPigeonImage(baseBitmap, editInstruction)
                        },
                        enabled = editInstruction.isNotBlank() && state !is ImageStudioUiState.Loading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PigeonViolet,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Apply Edit via gemini-nano-banana-2.1", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "POPULAR EDITING PROMPTS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = PigeonTextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                editPresets.forEach { preset ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PigeonSurfaceElevated,
                        border = BorderStroke(1.dp, PigeonBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                editInstruction = preset
                                val baseBitmap = createSampleBaseBitmap()
                                viewModel.editPigeonImage(baseBitmap, preset)
                            }
                    ) {
                        Text(
                            text = preset,
                            fontSize = 11.sp,
                            color = PigeonTextSecondary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // State Feedback & Result Display
        when (val currState = state) {
            is ImageStudioUiState.Loading -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = PigeonSurfaceElevated,
                    border = BorderStroke(1.dp, PigeonGold)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = PigeonGold, strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currState.progressMessage,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PigeonTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(0.7f),
                            color = PigeonGold,
                            trackColor = PigeonSurfaceDark
                        )
                    }
                }
            }
            is ImageStudioUiState.Success -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = PigeonSurfaceElevated,
                    border = BorderStroke(1.dp, PigeonGold)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Synthesized Masterpiece",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PigeonGold
                            )
                            IconButton(onClick = { viewModel.resetImageStudio() }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = PigeonTextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Image(
                            bitmap = currState.bitmap.asImageBitmap(),
                            contentDescription = "Generated Pigeon Artwork",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Prompt: \"${currState.promptUsed}\"",
                            fontSize = 11.sp,
                            color = PigeonTextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.selectTab(PigeonTab.INVENTORY)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PigeonGold,
                                contentColor = Color(0xFF140D00)
                            )
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Use in My Loft Inventory", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
            is ImageStudioUiState.Error -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = PigeonSurfaceDark,
                    border = BorderStroke(1.dp, Color(0xFFE53935))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Generation Error",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF5252)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currState.message,
                            fontSize = 11.sp,
                            color = PigeonTextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { viewModel.resetImageStudio() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PigeonGold),
                            border = BorderStroke(1.dp, PigeonGold)
                        ) {
                            Text("Reset Studio", fontSize = 11.sp)
                        }
                    }
                }
            }
            is ImageStudioUiState.Idle -> {
                // Idle prompt
            }
        }
    }
}

// Generates a simple baseline bitmap for image-to-image editing demo
private fun createSampleBaseBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        color = android.graphics.Color.DKGRAY
    }
    canvas.drawRect(0f, 0f, 512f, 512f, paint)
    paint.color = android.graphics.Color.LTGRAY
    canvas.drawCircle(256f, 256f, 160f, paint)
    return bitmap
}
