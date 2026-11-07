package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val loftName: String = "",
    val isBreeder: Boolean = false,
    val isVerifiedBreeder: Boolean = false,
    val location: String = "",
    val bio: String = "",
    val phone: String = "",
    val subscriptionTier: String = "Free", // Free, Premium, Pro
    val isAdmin: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "email" to email,
        "displayName" to displayName,
        "photoUrl" to photoUrl,
        "loftName" to loftName,
        "isBreeder" to isBreeder,
        "isVerifiedBreeder" to isVerifiedBreeder,
        "location" to location,
        "bio" to bio,
        "phone" to phone,
        "subscriptionTier" to subscriptionTier,
        "isAdmin" to isAdmin
    ).filterValues { it != null }
}

@IgnoreExtraProperties
data class Pigeon(
    @DocumentId val id: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val sellerLoft: String = "",
    val title: String = "",
    val breed: String = "Racing Homer",
    val gender: String = "Cock", // Cock, Hen, Pair
    val ringNumber: String = "",
    val birthYear: Int = 2024,
    val colorMorph: String = "Blue Bar",
    val price: Double = 0.0,
    val location: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val status: String = "available", // available, reserved, sold
    val vaccinated: Boolean = true,
    val pedigreeAvailable: Boolean = true,
    val healthStatus: String = "Vaccinated (PMV-1 & Pox)",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val ageDisplay: String
        get() {
            val diff = 2026 - birthYear
            return when {
                diff <= 0 -> "Young Bird (< 1 yr)"
                diff == 1 -> "1 Year"
                else -> "$diff Years"
            }
        }

    val healthBadge: String
        get() = if (healthStatus.isNotBlank()) healthStatus else if (vaccinated) "Vaccinated & Cleared" else "Health Checked"

    val inrPrice: Double
        get() = price * 86.0

    fun formattedPrice(isRupee: Boolean = true): String {
        return if (isRupee) {
            val inr = inrPrice.toLong()
            "₹%,d".format(inr)
        } else {
            "$%,.0f".format(price)
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "sellerId" to sellerId,
        "sellerName" to sellerName,
        "sellerLoft" to sellerLoft,
        "title" to title,
        "breed" to breed,
        "gender" to gender,
        "ringNumber" to ringNumber,
        "birthYear" to birthYear,
        "colorMorph" to colorMorph,
        "price" to price,
        "location" to location,
        "description" to description,
        "imageUrl" to imageUrl,
        "videoUrl" to videoUrl,
        "status" to status,
        "vaccinated" to vaccinated,
        "pedigreeAvailable" to pedigreeAvailable,
        "healthStatus" to healthStatus
    ).filterValues { it != null }
}


@IgnoreExtraProperties
data class Store(
    @DocumentId val id: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val storeName: String = "",
    val tagline: String = "",
    val description: String = "",
    val location: String = "",
    val city: String = "Bangalore",
    val stateRegion: String = "Karnataka",
    val distanceKm: Double = 3.5,
    val pincode: String = "560001",
    val upiId: String = "pigeonverse@okhdfcbank",
    val acceptsInrUpi: Boolean = true,
    val bannerUrl: String? = null,
    val logoUrl: String? = null,
    val verified: Boolean = false,
    val totalSales: Int = 0,
    val rating: Double = 5.0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "ownerId" to ownerId,
        "ownerName" to ownerName,
        "storeName" to storeName,
        "tagline" to tagline,
        "description" to description,
        "location" to location,
        "city" to city,
        "stateRegion" to stateRegion,
        "distanceKm" to distanceKm,
        "pincode" to pincode,
        "upiId" to upiId,
        "acceptsInrUpi" to acceptsInrUpi,
        "bannerUrl" to bannerUrl,
        "logoUrl" to logoUrl,
        "verified" to verified,
        "totalSales" to totalSales,
        "rating" to rating
    ).filterValues { it != null }
}

@IgnoreExtraProperties
data class Doctor(
    @DocumentId val id: String = "",
    val name: String = "",
    val title: String = "",
    val qualification: String = "",
    val experienceYears: Int = 0,
    val fee: Double = 0.0,
    val rating: Double = 5.0,
    val clinicLocation: String = "",
    val availableDays: String = "Mon - Fri",
    val avatarUrl: String? = null
) {
    val feeInr: Double
        get() = fee * 86.0

    fun formattedFee(isRupee: Boolean = true): String {
        return if (isRupee) "₹%,d".format(feeInr.toLong()) else "$%,.0f".format(fee)
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "title" to title,
        "qualification" to qualification,
        "experienceYears" to experienceYears,
        "fee" to fee,
        "rating" to rating,
        "clinicLocation" to clinicLocation,
        "availableDays" to availableDays,
        "avatarUrl" to avatarUrl
    ).filterValues { it != null }
}

@IgnoreExtraProperties
data class Appointment(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val doctorId: String = "",
    val doctorName: String = "",
    val specialty: String = "",
    val date: String = "",
    val timeSlot: String = "",
    val consultType: String = "Video Call", // Video Call, Clinic Visit, Loft Inspection
    val fee: Double = 0.0,
    val notes: String = "",
    val status: String = "pending", // pending, confirmed, completed, cancelled
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "userName" to userName,
        "doctorId" to doctorId,
        "doctorName" to doctorName,
        "specialty" to specialty,
        "date" to date,
        "timeSlot" to timeSlot,
        "consultType" to consultType,
        "fee" to fee,
        "notes" to notes,
        "status" to status
    ).filterValues { it != null }
}

@IgnoreExtraProperties
data class Order(
    @DocumentId val id: String = "",
    val buyerId: String = "",
    val buyerName: String = "",
    val sellerId: String = "",
    val pigeonId: String = "",
    val pigeonTitle: String = "",
    val amount: Double = 0.0,
    val currency: String = "INR", // INR or USD
    val inrAmount: Double = 0.0,
    val paymentMethod: String = "UPI Escrow (BHIM/GPay)",
    val upiTransactionId: String = "",
    val securityVerificationCode: String = "", // 6-digit Handover Code
    val escrowStatus: String = "held_in_rbi_escrow", // held_in_rbi_escrow, released_to_seller, refunded
    val status: String = "escrow_held", // placed, escrow_held, transport_booked, delivered, completed, cancelled
    val deliveryAddress: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "buyerId" to buyerId,
        "buyerName" to buyerName,
        "sellerId" to sellerId,
        "pigeonId" to pigeonId,
        "pigeonTitle" to pigeonTitle,
        "amount" to amount,
        "currency" to currency,
        "inrAmount" to inrAmount,
        "paymentMethod" to paymentMethod,
        "upiTransactionId" to upiTransactionId,
        "securityVerificationCode" to securityVerificationCode,
        "escrowStatus" to escrowStatus,
        "status" to status,
        "deliveryAddress" to deliveryAddress
    ).filterValues { it != null }
}

@IgnoreExtraProperties
data class Conversation(
    @DocumentId val id: String = "",
    val buyerId: String = "",
    val buyerName: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val participantIds: String = "",
    val pigeonId: String = "",
    val pigeonTitle: String = "",
    val pigeonBreed: String = "",
    val pigeonPrice: Double = 0.0,
    val lastMessage: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val inrPrice: Double get() = pigeonPrice * 86.0

    fun toMap(): Map<String, Any?> = mapOf(
        "buyerId" to buyerId,
        "buyerName" to buyerName,
        "sellerId" to sellerId,
        "sellerName" to sellerName,
        "participantIds" to participantIds,
        "pigeonId" to pigeonId,
        "pigeonTitle" to pigeonTitle,
        "pigeonBreed" to pigeonBreed,
        "pigeonPrice" to pigeonPrice,
        "lastMessage" to lastMessage
    ).filterValues { it != null }
}

@IgnoreExtraProperties
data class Message(
    @DocumentId val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val messageType: String = "text", // text, offer, inquiry
    val offerAmount: Double = 0.0,
    val isBreeder: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    val offerInr: Double get() = offerAmount * 86.0

    fun toMap(): Map<String, Any?> = mapOf(
        "conversationId" to conversationId,
        "senderId" to senderId,
        "senderName" to senderName,
        "text" to text,
        "messageType" to messageType,
        "offerAmount" to offerAmount,
        "isBreeder" to isBreeder
    ).filterValues { it != null }
}

