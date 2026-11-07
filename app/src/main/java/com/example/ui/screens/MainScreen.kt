package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.PigeonTab
import com.example.ui.PigeonverseViewModel
import com.example.ui.theme.PigeonBorder
import com.example.ui.theme.PigeonEmerald
import com.example.ui.theme.PigeonGold
import com.example.ui.theme.PigeonGoldLight
import com.example.ui.theme.PigeonObsidian
import com.example.ui.theme.PigeonSurfaceCard
import com.example.ui.theme.PigeonSurfaceDark
import com.example.ui.theme.PigeonSurfaceElevated
import com.example.ui.theme.PigeonTextPrimary
import com.example.ui.theme.PigeonTextSecondary
import com.example.ui.theme.PigeonViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: PigeonverseViewModel,
    onSignOut: () -> Unit
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showingAdminScreen by remember { mutableStateOf(false) }

    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    BackHandler(enabled = showingAdminScreen) {
        showingAdminScreen = false
    }

    if (showingAdminScreen) {
        AdminScreen(
            viewModel = viewModel,
            onBack = { showingAdminScreen = false }
        )
        return
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(PigeonObsidian)) {
        val isWide = maxWidth >= 600.dp

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, PigeonGold, CircleShape)
                                    .background(PigeonSurfaceCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.pigeon_luxury_icon_1793978056609),
                                    contentDescription = "Pigeonverse",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "PIGEONVERSE",
                                        color = PigeonGold,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 2.sp
                                    )
                                    if (userProfile?.isVerifiedBreeder == true) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified Loft",
                                            tint = PigeonEmerald,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = userProfile?.loftName?.ifBlank { "Exclusive Pigeon Species Platform" }
                                        ?: "Exclusive Pigeon Species Platform",
                                    color = PigeonTextSecondary,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    },
                    actions = {
                        if (userProfile?.isAdmin == true || userProfile?.email.equals("orphicfaizan@gmail.com", ignoreCase = true)) {
                            Surface(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable { showingAdminScreen = true },
                                shape = RoundedCornerShape(8.dp),
                                color = PigeonGold.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PigeonGold)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = PigeonGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ADMIN", color = PigeonGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Profile Avatar
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PigeonViolet.copy(alpha = 0.25f))
                                .clickable { viewModel.selectTab(PigeonTab.PROFILE) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile?.displayName?.take(1)?.uppercase() ?: "P",
                                color = PigeonViolet,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PigeonSurfaceDark,
                        titleContentColor = PigeonTextPrimary
                    ),
                    modifier = Modifier.statusBarsPadding()
                )
            },
            bottomBar = {
                if (!isWide) {
                    NavigationBar(
                        containerColor = PigeonSurfaceDark,
                        contentColor = PigeonTextSecondary,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        NavigationBarItem(
                            selected = currentTab == PigeonTab.DASHBOARD,
                            onClick = { viewModel.selectTab(PigeonTab.DASHBOARD) },
                            icon = { Icon(imageVector = Icons.Default.GridView, contentDescription = "Dashboard") },
                            label = { Text("Dashboard", fontSize = 10.sp) },
                            colors = navigationColors()
                        )
                        NavigationBarItem(
                            selected = currentTab == PigeonTab.INVENTORY,
                            onClick = { viewModel.selectTab(PigeonTab.INVENTORY) },
                            icon = { Icon(imageVector = Icons.Default.Inventory, contentDescription = "My Loft") },
                            label = { Text("My Loft", fontSize = 10.sp) },
                            colors = navigationColors()
                        )
                        NavigationBarItem(
                            selected = currentTab == PigeonTab.STORES,
                            onClick = { viewModel.selectTab(PigeonTab.STORES) },
                            icon = { Icon(imageVector = Icons.Default.Storefront, contentDescription = "Stores") },
                            label = { Text("Stores", fontSize = 10.sp) },
                            colors = navigationColors()
                        )
                        NavigationBarItem(
                            selected = currentTab == PigeonTab.AI_STUDIO,
                            onClick = { viewModel.selectTab(PigeonTab.AI_STUDIO) },
                            icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Avian AI") },
                            label = { Text("AI Studio", fontSize = 10.sp) },
                            colors = navigationColors()
                        )
                        NavigationBarItem(
                            selected = currentTab == PigeonTab.DOCTORS,
                            onClick = { viewModel.selectTab(PigeonTab.DOCTORS) },
                            icon = { Icon(imageVector = Icons.Default.HealthAndSafety, contentDescription = "Doctors") },
                            label = { Text("Health", fontSize = 10.sp) },
                            colors = navigationColors()
                        )
                        NavigationBarItem(
                            selected = currentTab == PigeonTab.ORDERS,
                            onClick = { viewModel.selectTab(PigeonTab.ORDERS) },
                            icon = { Icon(imageVector = Icons.Default.Chat, contentDescription = "Escrow & Messages") },
                            label = { Text("Escrow", fontSize = 10.sp) },
                            colors = navigationColors()
                        )
                        NavigationBarItem(
                            selected = currentTab == PigeonTab.PROFILE,
                            onClick = { viewModel.selectTab(PigeonTab.PROFILE) },
                            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile", fontSize = 10.sp) },
                            colors = navigationColors()
                        )
                    }
                }
            },
            containerColor = PigeonObsidian
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isWide) {
                    NavigationRail(
                        containerColor = PigeonSurfaceDark,
                        contentColor = PigeonTextSecondary,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        NavigationRailItem(
                            selected = currentTab == PigeonTab.DASHBOARD,
                            onClick = { viewModel.selectTab(PigeonTab.DASHBOARD) },
                            icon = { Icon(imageVector = Icons.Default.GridView, contentDescription = "Dashboard") },
                            label = { Text("Dashboard") },
                            colors = railNavigationColors()
                        )
                        NavigationRailItem(
                            selected = currentTab == PigeonTab.INVENTORY,
                            onClick = { viewModel.selectTab(PigeonTab.INVENTORY) },
                            icon = { Icon(imageVector = Icons.Default.Inventory, contentDescription = "My Loft") },
                            label = { Text("My Loft") },
                            colors = railNavigationColors()
                        )
                        NavigationRailItem(
                            selected = currentTab == PigeonTab.STORES,
                            onClick = { viewModel.selectTab(PigeonTab.STORES) },
                            icon = { Icon(imageVector = Icons.Default.Storefront, contentDescription = "Stores") },
                            label = { Text("Stores") },
                            colors = railNavigationColors()
                        )
                        NavigationRailItem(
                            selected = currentTab == PigeonTab.AI_STUDIO,
                            onClick = { viewModel.selectTab(PigeonTab.AI_STUDIO) },
                            icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Avian AI") },
                            label = { Text("AI Studio") },
                            colors = railNavigationColors()
                        )
                        NavigationRailItem(
                            selected = currentTab == PigeonTab.DOCTORS,
                            onClick = { viewModel.selectTab(PigeonTab.DOCTORS) },
                            icon = { Icon(imageVector = Icons.Default.HealthAndSafety, contentDescription = "Doctors") },
                            label = { Text("Health") },
                            colors = railNavigationColors()
                        )
                        NavigationRailItem(
                            selected = currentTab == PigeonTab.ORDERS,
                            onClick = { viewModel.selectTab(PigeonTab.ORDERS) },
                            icon = { Icon(imageVector = Icons.Default.Chat, contentDescription = "Escrow") },
                            label = { Text("Escrow") },
                            colors = railNavigationColors()
                        )
                        NavigationRailItem(
                            selected = currentTab == PigeonTab.PROFILE,
                            onClick = { viewModel.selectTab(PigeonTab.PROFILE) },
                            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile") },
                            colors = railNavigationColors()
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    when (currentTab) {
                        PigeonTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                        PigeonTab.INVENTORY -> InventoryScreen(viewModel = viewModel)
                        PigeonTab.STORES -> StoresScreen(viewModel = viewModel)
                        PigeonTab.AI_STUDIO -> AiStudioScreen(viewModel = viewModel)
                        PigeonTab.DOCTORS -> DoctorsScreen(viewModel = viewModel)
                        PigeonTab.ORDERS -> OrdersAndChatScreen(viewModel = viewModel)
                        PigeonTab.PROFILE -> ProfileScreen(
                            viewModel = viewModel,
                            onOpenAdmin = { showingAdminScreen = true },
                            onSignOut = onSignOut
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun navigationColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Color(0xFF140D00),
    selectedTextColor = PigeonGold,
    indicatorColor = PigeonGold,
    unselectedIconColor = PigeonTextSecondary,
    unselectedTextColor = PigeonTextSecondary
)

@Composable
private fun railNavigationColors() = NavigationRailItemDefaults.colors(
    selectedIconColor = Color(0xFF140D00),
    selectedTextColor = PigeonGold,
    indicatorColor = PigeonGold,
    unselectedIconColor = PigeonTextSecondary,
    unselectedTextColor = PigeonTextSecondary
)
