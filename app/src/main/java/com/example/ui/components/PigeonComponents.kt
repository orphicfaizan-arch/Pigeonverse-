package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PigeonEmerald
import com.example.ui.theme.PigeonGold
import com.example.ui.theme.PigeonGoldLight
import com.example.ui.theme.PigeonSurfaceCard
import com.example.ui.theme.PigeonViolet

@Composable
fun VerifiedBreederBadge(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = PigeonEmerald.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, PigeonEmerald.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Verified Breeder",
                tint = PigeonEmerald,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "VERIFIED LOFT",
                color = PigeonEmerald,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun TierBadge(tier: String, modifier: Modifier = Modifier) {
    val (badgeColor, textColor) = when (tier.lowercase()) {
        "pro" -> PigeonGold to Color(0xFF1A1200)
        "premium" -> PigeonViolet to Color.White
        else -> Color(0xFF475569) to Color(0xFFE2E8F0)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = badgeColor
    ) {
        Text(
            text = tier.uppercase(),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun StatusPill(status: String, modifier: Modifier = Modifier) {
    val (color, text) = when (status.lowercase()) {
        "available" -> PigeonEmerald to "AVAILABLE"
        "reserved" -> PigeonGold to "RESERVED"
        "sold" -> Color(0xFFEF4444) to "SOLD"
        "escrow_held" -> PigeonGold to "ESCROW SECURED"
        "transport_booked" -> PigeonViolet to "IN TRANSIT"
        "delivered" -> PigeonEmerald to "DELIVERED"
        "completed" -> PigeonEmerald to "COMPLETED"
        else -> Color.Gray to status.uppercase()
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.6f))
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun LuxuryCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = PigeonSurfaceCard,
        border = BorderStroke(1.dp, Color(0xFF2A3644)),
        tonalElevation = 2.dp
    ) {
        content()
    }
}
