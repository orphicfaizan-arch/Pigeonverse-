package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Conversation
import com.example.data.model.Message
import com.example.data.model.Order
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
import com.example.ui.theme.PigeonTextMuted
import com.example.ui.theme.PigeonTextPrimary
import com.example.ui.theme.PigeonTextSecondary
import com.example.ui.theme.PigeonViolet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersAndChatScreen(viewModel: PigeonverseViewModel) {
    val orders by viewModel.userOrders.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val selectedConvId by viewModel.selectedConversationId.collectAsState()
    val activeMessages by viewModel.activeMessages.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val pigeons by viewModel.pigeons.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()

    var activeSubTab by remember { mutableIntStateOf(if (selectedConvId != null) 1 else 0) } // 0: Escrow Orders, 1: Messages
    var checkoutPigeonTarget by remember { mutableStateOf<Pigeon?>(null) }

    val activeConv = remember(selectedConvId, conversations) {
        conversations.firstOrNull { it.id == selectedConvId }
    }

    if (selectedConvId != null && activeConv != null) {
        ChatDetailView(
            conversation = activeConv,
            messages = activeMessages,
            currentUserId = userProfile?.userId ?: "",
            selectedCurrency = selectedCurrency,
            onBack = { viewModel.closeConversation() },
            onSendMessage = { text, type, offerAmt ->
                viewModel.sendMessage(text, type, offerAmt)
            },
            onDirectCheckout = { pigeonId, pigeonTitle, amount ->
                val matchedPigeon = pigeons.firstOrNull { it.id == pigeonId }
                    ?: Pigeon(
                        id = pigeonId,
                        sellerId = activeConv.sellerId,
                        sellerName = activeConv.sellerName,
                        title = pigeonTitle,
                        breed = activeConv.pigeonBreed.ifBlank { "Racing Homer" },
                        price = if (amount > 0) amount else (if (activeConv.pigeonPrice > 0) activeConv.pigeonPrice else 500.0)
                    )
                checkoutPigeonTarget = matchedPigeon
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            TabRow(
                selectedTabIndex = activeSubTab,
                containerColor = PigeonSurfaceDark,
                contentColor = PigeonGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                        color = PigeonGold
                    )
                }
            ) {
                Tab(
                    selected = activeSubTab == 0,
                    onClick = { activeSubTab = 0 },
                    text = { Text("Escrow Orders (${orders.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeSubTab == 1,
                    onClick = { activeSubTab = 1 },
                    text = { Text("Breeder Chat (${conversations.size})", fontWeight = FontWeight.Bold) }
                )
            }

            if (activeSubTab == 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = PigeonSurfaceElevated,
                            border = BorderStroke(1.dp, PigeonBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = PigeonEmerald, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Pigeonverse Dual-Key Escrow Vault",
                                        color = PigeonGold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Funds are locked in RBI-compliant escrow. Handover OTP releases payment only upon live physical bird inspection.",
                                        color = PigeonTextSecondary,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    if (orders.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = PigeonTextSecondary, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("No Orders Yet", color = PigeonTextPrimary, fontWeight = FontWeight.Bold)
                                    Text("When you buy pigeons via Escrow, your active orders appear here.", color = PigeonTextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    } else {
                        items(orders, key = { it.id }) { order ->
                            OrderCard(
                                order = order,
                                onReleaseEscrow = { ord, code ->
                                    viewModel.verifyAndReleaseEscrow(ord.id, code, ord.securityVerificationCode)
                                }
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = PigeonSurfaceElevated,
                            border = BorderStroke(1.dp, PigeonBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = PigeonGold, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Real-Time Breeder Negotiation & Inquiries",
                                        color = PigeonGold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Negotiate pedigrees, live transport, and lock escrow agreements in real-time.",
                                        color = PigeonTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    if (conversations.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = PigeonTextSecondary, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("No Active Inquiries", color = PigeonTextPrimary, fontWeight = FontWeight.Bold)
                                    Text("Tap 'Chat Seller' on any pigeon profile to begin direct negotiation.", color = PigeonTextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    } else {
                        items(conversations, key = { it.id }) { conv ->
                            ConversationCard(
                                conv = conv,
                                currentUserId = userProfile?.userId ?: "",
                                onClick = { viewModel.selectConversation(conv.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (checkoutPigeonTarget != null) {
        val pigeon = checkoutPigeonTarget!!
        ChatEscrowModal(
            pigeon = pigeon,
            selectedCurrency = selectedCurrency,
            onDismiss = { checkoutPigeonTarget = null },
            onConfirmOrder = { method, address, currency, upiId ->
                viewModel.placeOrder(
                    pigeon = pigeon,
                    paymentMethod = method,
                    address = address,
                    currency = currency,
                    upiTransactionId = upiId
                )
                checkoutPigeonTarget = null
            }
        )
    }
}

@Composable
private fun OrderCard(
    order: Order,
    onReleaseEscrow: (Order, String) -> Unit
) {
    var showVerifyDialog by remember { mutableStateOf(false) }
    var enteredOtp by remember { mutableStateOf("") }

    val amountDisplay = if (order.currency == "INR" || order.inrAmount > 0.0) {
        val inrVal = if (order.inrAmount > 0.0) order.inrAmount.toLong() else (order.amount * 86.0).toLong()
        "₹%,d".format(inrVal)
    } else {
        "$%,.0f".format(order.amount)
    }

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = order.pigeonTitle, color = PigeonTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Order #${order.id.takeLast(6).uppercase()} • ${order.paymentMethod}", color = PigeonGoldLight, fontSize = 11.sp)
                }
                StatusPill(status = order.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Escrow Total", color = PigeonTextSecondary, fontSize = 10.sp)
                    Text(
                        text = amountDisplay,
                        color = PigeonGold,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                if (order.upiTransactionId.isNotBlank()) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "UPI Ref", color = PigeonTextSecondary, fontSize = 10.sp)
                        Text(
                            text = order.upiTransactionId,
                            color = PigeonEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Delivery Destination: ${order.deliveryAddress}",
                color = PigeonTextSecondary,
                fontSize = 11.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 6-Digit Handover Verification OTP Card (Security Feature)
            if (order.securityVerificationCode.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PigeonSurfaceElevated,
                    border = BorderStroke(1.dp, PigeonGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = PigeonGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "HANDOVER SECURITY CODE",
                                    color = PigeonGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Text(
                                text = "OTP: [ ${order.securityVerificationCode} ]",
                                color = PigeonGoldLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                        }

                        Text(
                            text = if (order.status == "escrow_held") {
                                "🔒 Share this 6-digit code with breeder ONLY after physical arrival, health check & leg ring inspection."
                            } else {
                                "✅ Handover code verified. Escrow payment released to breeder."
                            },
                            color = PigeonTextSecondary,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Interactive Escrow Action
            if (order.status == "escrow_held") {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { showVerifyDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PigeonEmerald,
                        contentColor = Color(0xFF002418)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Confirm Delivery & Release Escrow", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showVerifyDialog) {
        AlertDialog(
            onDismissRequest = { showVerifyDialog = false },
            title = {
                Text(
                    text = "Release Escrow Payout",
                    color = PigeonGold,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter the 6-digit handover OTP to confirm you have physically received and inspected ${order.pigeonTitle}.",
                        color = PigeonTextSecondary,
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = { enteredOtp = it },
                        label = { Text("6-Digit Handover OTP") },
                        placeholder = { Text(order.securityVerificationCode) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text(
                        text = "Once verified, $amountDisplay will be released from RBI Escrow directly into breeder's registered UPI ID.",
                        color = PigeonEmerald,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReleaseEscrow(order, enteredOtp)
                        showVerifyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PigeonEmerald, contentColor = Color(0xFF002418))
                ) {
                    Text("Verify & Release", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showVerifyDialog = false }) {
                    Text("Cancel", color = PigeonTextSecondary)
                }
            },
            containerColor = PigeonSurfaceDark
        )
    }
}

@Composable
private fun ConversationCard(
    conv: Conversation,
    currentUserId: String,
    onClick: () -> Unit
) {
    val isBuyer = conv.buyerId == currentUserId
    val counterpart = if (isBuyer) conv.sellerName.ifBlank { "Breeder Loft" } else conv.buyerName.ifBlank { "Buyer" }

    LuxuryCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(PigeonGold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = PigeonGold, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = counterpart,
                        color = PigeonTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (isBuyer) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.Verified, contentDescription = "Verified Breeder", tint = PigeonEmerald, modifier = Modifier.size(14.dp))
                    }
                }
                Text(
                    text = "Re: ${conv.pigeonTitle.ifBlank { "Pigeon Inquiries" }}",
                    color = PigeonGoldLight,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = conv.lastMessage.ifBlank { "Tap to negotiate transaction" },
                    color = PigeonTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (conv.pigeonPrice > 0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PigeonSurfaceElevated,
                    border = BorderStroke(1.dp, PigeonBorder)
                ) {
                    Text(
                        text = "₹%,d".format(conv.inrPrice.toLong()),
                        color = PigeonGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatDetailView(
    conversation: Conversation,
    messages: List<Message>,
    currentUserId: String,
    selectedCurrency: String,
    onBack: () -> Unit,
    onSendMessage: (text: String, type: String, offerAmount: Double) -> Unit,
    onDirectCheckout: (pigeonId: String, title: String, amount: Double) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var showOfferDialog by remember { mutableStateOf(false) }
    var offerPriceText by remember { mutableStateOf(if (conversation.pigeonPrice > 0) conversation.pigeonPrice.toString() else "800") }

    val listState = rememberLazyListState()
    val isBuyer = conversation.buyerId == currentUserId
    val counterpartTitle = if (isBuyer) conversation.sellerName.ifBlank { "Verified Breeder Loft" } else conversation.buyerName.ifBlank { "Buyer Fancier" }

    val isRupee = selectedCurrency == "INR"
    val inrFormatted = "₹%,d".format(conversation.inrPrice.toLong())
    val usdFormatted = "$%,.0f".format(conversation.pigeonPrice)

    // Auto-scroll to bottom on new messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PigeonSurfaceDark)
            .padding(bottom = 76.dp)
    ) {
        // Chat Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PigeonSurfaceCard,
            border = BorderStroke(1.dp, PigeonBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = PigeonGold)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = counterpartTitle, color = PigeonTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        if (isBuyer) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = PigeonEmerald, modifier = Modifier.size(15.dp))
                        }
                    }
                    Text(text = "Real-Time Pigeonverse Encrypted Channel", color = PigeonEmerald, fontSize = 10.sp)
                }
            }
        }

        // Marketplace Pigeon Dossier & Escrow Action Banner
        if (conversation.pigeonTitle.isNotBlank() && conversation.pigeonId != "loft_general") {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = PigeonSurfaceElevated,
                border = BorderStroke(1.dp, PigeonGold.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PigeonViolet.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Pets, contentDescription = null, tint = PigeonGold, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = conversation.pigeonTitle,
                                color = PigeonTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isRupee) "$inrFormatted ($usdFormatted)" else "$usdFormatted ($inrFormatted)",
                                color = PigeonGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onDirectCheckout(
                                conversation.pigeonId,
                                conversation.pigeonTitle,
                                conversation.pigeonPrice
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PigeonGold, contentColor = Color(0xFF140D00)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Secure Escrow", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Message Feed
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 6.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isMe = msg.senderId == currentUserId
                val timeStr = remember(msg.createdAt) {
                    val date = msg.createdAt?.toDate()
                    if (date != null) {
                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
                    } else "Just now"
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isMe) 14.dp else 2.dp,
                            bottomEnd = if (isMe) 2.dp else 14.dp
                        ),
                        color = if (isMe) PigeonGold else PigeonSurfaceElevated,
                        contentColor = if (isMe) Color(0xFF140D00) else PigeonTextPrimary,
                        border = if (!isMe) BorderStroke(1.dp, PigeonBorder) else null,
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            if (!isMe && msg.senderName.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = msg.senderName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PigeonGoldLight)
                                    if (msg.isBreeder) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = PigeonEmerald, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }

                            if (msg.messageType == "offer") {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isMe) Color(0x26000000) else PigeonSurfaceCard,
                                    border = BorderStroke(1.dp, PigeonGold),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.LocalOffer, contentDescription = null, tint = PigeonGold, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "MARKETPLACE ESCROW OFFER",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = PigeonGold
                                            )
                                        }
                                        Text(
                                            text = "₹%,d ($%,.0f)".format(msg.offerInr.toLong(), msg.offerAmount),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isMe) Color(0xFF140D00) else PigeonGoldLight
                                        )
                                        if (!isMe) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Button(
                                                onClick = {
                                                    onDirectCheckout(
                                                        conversation.pigeonId,
                                                        conversation.pigeonTitle,
                                                        msg.offerAmount
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = PigeonEmerald, contentColor = Color(0xFF002418)),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text("Accept & Deposit Escrow", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            Text(text = msg.text, fontSize = 13.sp, lineHeight = 17.sp)

                            Text(
                                text = timeStr,
                                fontSize = 9.sp,
                                color = if (isMe) Color(0x99140D00) else PigeonTextMuted,
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick Marketplace Negotiation Prompts Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = PigeonGold.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, PigeonGold),
                modifier = Modifier.clickable { showOfferDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.LocalOffer, contentDescription = null, tint = PigeonGold, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Propose Escrow Offer", color = PigeonGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            listOf(
                "📜 Can you share the official DNA pedigree papers?",
                "🩺 Has this bird received PMV-1 and Pox vaccines?",
                "🚚 What is the transit timeline to my loft location?",
                "🎥 Can you send a video of the wing feathers and flight?",
                "💰 Ready to authorize Pigeonverse Escrow deposit."
            ).forEach { prompt ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PigeonSurfaceElevated,
                    border = BorderStroke(1.dp, PigeonBorder),
                    modifier = Modifier.clickable { onSendMessage(prompt, "text", 0.0) }
                ) {
                    Text(
                        text = prompt,
                        color = PigeonTextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask about pedigrees, rings, video...", color = PigeonTextSecondary, fontSize = 12.sp) },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PigeonGold,
                    unfocusedBorderColor = PigeonBorder,
                    focusedContainerColor = PigeonSurfaceCard,
                    unfocusedContainerColor = PigeonSurfaceCard,
                    focusedTextColor = PigeonTextPrimary,
                    unfocusedTextColor = PigeonTextPrimary
                ),
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText, "text", 0.0)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .background(PigeonGold, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF140D00))
            }
        }
    }

    if (showOfferDialog) {
        AlertDialog(
            onDismissRequest = { showOfferDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocalOffer, contentDescription = null, tint = PigeonGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Propose Escrow Offer", color = PigeonGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Propose an official offer for ${conversation.pigeonTitle}. Once accepted by the breeder, funds will be locked in Pigeonverse Escrow.",
                        color = PigeonTextSecondary,
                        fontSize = 11.sp
                    )
                    OutlinedTextField(
                        value = offerPriceText,
                        onValueChange = { offerPriceText = it },
                        label = { Text("Offer Amount (USD $)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    val numericOffer = offerPriceText.toDoubleOrNull() ?: 0.0
                    Text(
                        text = "Equivalent Indian Currency: ₹%,d INR".format((numericOffer * 86.0).toLong()),
                        color = PigeonEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = offerPriceText.toDoubleOrNull() ?: 0.0
                        if (num > 0) {
                            onSendMessage(
                                "I would like to propose an escrow offer of $num USD (₹%,d INR) for this bird.".format((num * 86.0).toLong()),
                                "offer",
                                num
                            )
                        }
                        showOfferDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PigeonGold, contentColor = Color(0xFF140D00))
                ) {
                    Text("Send Official Offer", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showOfferDialog = false }) {
                    Text("Cancel", color = PigeonTextSecondary)
                }
            },
            containerColor = PigeonSurfaceDark
        )
    }
}

@Composable
private fun ChatEscrowModal(
    pigeon: Pigeon,
    selectedCurrency: String,
    onDismiss: () -> Unit,
    onConfirmOrder: (paymentMethod: String, address: String, currency: String, upiId: String) -> Unit
) {
    var currency by remember { mutableStateOf(selectedCurrency) }
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
                            text = "Breed: ${pigeon.breed} • Age: ${pigeon.ageDisplay}",
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
                            text = "• Dual-Key RBI Escrow: Payment held in a regulated nodal account.\n• 6-Digit Handover OTP: Generated on booking. Release to breeder ONLY after live inspection.\n• 48-Hour Live Health Guarantee: 100% refund if health mismatch is found.",
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
