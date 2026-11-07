package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.Appointment
import com.example.data.model.Conversation
import com.example.data.model.Doctor
import com.example.data.model.Message
import com.example.data.model.Order
import com.example.data.model.Pigeon
import com.example.data.model.Store
import com.example.data.model.UserProfile
import com.example.data.util.OperationType
import com.example.data.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PigeonverseRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun currentUserId(): String? = auth.currentUser?.uid
    fun currentUserEmail(): String? = auth.currentUser?.email

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Pigeonverse.")
    }

    // ----------------------------------------------------
    // USER PROFILE
    // ----------------------------------------------------

    fun observeUserProfile(userId: String): Flow<UserProfile?> = callbackFlow {
        val path = "users/$userId"
        val registration = db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.GET, path)
                    close(error)
                    return@addSnapshotListener
                }
                val profile = snapshot?.toObject(UserProfile::class.java)
                trySend(profile)
            }
        awaitClose { registration.remove() }
    }

    suspend fun createOrUpdateUserProfile(
        displayName: String,
        photoUrl: String? = null,
        loftName: String = "",
        isBreeder: Boolean = false,
        location: String = "",
        bio: String = "",
        phone: String = "",
        subscriptionTier: String = "Free"
    ): Result<Unit> {
        val uid = requireUserId()
        val email = auth.currentUser?.email ?: ""
        val isAdminEmail = email.equals("orphicfaizan@gmail.com", ignoreCase = true)
        val path = "users/$uid"

        return try {
            val userRef = db.collection("users").document(uid)
            val existingSnapshot = userRef.get().await()

            if (!existingSnapshot.exists()) {
                val payload = mapOf(
                    "userId" to uid,
                    "email" to email,
                    "displayName" to displayName.ifBlank { "Pigeon Fancier" },
                    "photoUrl" to photoUrl,
                    "loftName" to loftName,
                    "isBreeder" to isBreeder,
                    "isVerifiedBreeder" to isAdminEmail, // Admin auto-verified
                    "location" to location,
                    "bio" to bio,
                    "phone" to phone,
                    "subscriptionTier" to subscriptionTier,
                    "isAdmin" to isAdminEmail,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ).filterValues { it != null }

                userRef.set(payload).await()
            } else {
                val updates = mutableMapOf<String, Any>(
                    "displayName" to displayName,
                    "loftName" to loftName,
                    "isBreeder" to isBreeder,
                    "location" to location,
                    "bio" to bio,
                    "phone" to phone,
                    "subscriptionTier" to subscriptionTier,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                photoUrl?.let { updates["photoUrl"] = it }
                if (isAdminEmail) {
                    updates["isAdmin"] = true
                    updates["isVerifiedBreeder"] = true
                }
                userRef.update(updates).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun upgradeSubscription(tier: String): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid"
        return try {
            db.collection("users").document(uid).update(
                mapOf(
                    "subscriptionTier" to tier,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // PIGEONS (MARKETPLACE & INVENTORY)
    // ----------------------------------------------------

    fun observeAllPigeons(): Flow<List<Pigeon>> = callbackFlow {
        val path = "pigeons"
        val registration = db.collection(path)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                val pigeons = snapshot?.toObjects(Pigeon::class.java) ?: emptyList()
                trySend(pigeons)
            }
        awaitClose { registration.remove() }
    }

    fun observeUserPigeons(userId: String): Flow<List<Pigeon>> = callbackFlow {
        val path = "pigeons"
        val registration = db.collection(path)
            .whereEqualTo("sellerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                val pigeons = snapshot?.toObjects(Pigeon::class.java) ?: emptyList()
                trySend(pigeons)
            }
        awaitClose { registration.remove() }
    }

    suspend fun addPigeon(pigeon: Pigeon): Result<String> {
        val uid = requireUserId()
        val path = "pigeons"
        return try {
            val docRef = db.collection(path).document()
            val payload = pigeon.toMap() + mapOf(
                "sellerId" to uid,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updatePigeon(pigeonId: String, updates: Map<String, Any?>): Result<Unit> {
        requireUserId()
        val path = "pigeons/$pigeonId"
        return try {
            val filteredUpdates = updates.filterValues { it != null } + mapOf(
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("pigeons").document(pigeonId).update(filteredUpdates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun deletePigeon(pigeonId: String): Result<Unit> {
        requireUserId()
        val path = "pigeons/$pigeonId"
        return try {
            db.collection("pigeons").document(pigeonId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // STORES
    // ----------------------------------------------------

    fun observeAllStores(): Flow<List<Store>> = callbackFlow {
        val path = "stores"
        val registration = db.collection(path)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                val stores = snapshot?.toObjects(Store::class.java) ?: emptyList()
                trySend(stores)
            }
        awaitClose { registration.remove() }
    }

    fun observeUserStore(userId: String): Flow<Store?> = callbackFlow {
        val path = "stores"
        val registration = db.collection(path)
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                val store = snapshot?.documents?.firstOrNull()?.toObject(Store::class.java)
                trySend(store)
            }
        awaitClose { registration.remove() }
    }

    suspend fun createOrUpdateStore(store: Store): Result<String> {
        val uid = requireUserId()
        val path = "stores"
        return try {
            val existing = db.collection(path).whereEqualTo("ownerId", uid).get().await()
            val docRef = if (!existing.isEmpty) {
                existing.documents.first().reference
            } else {
                db.collection(path).document()
            }

            val payload = store.toMap() + mapOf(
                "ownerId" to uid,
                "createdAt" to (if (!existing.isEmpty) existing.documents.first().getTimestamp("createdAt") ?: FieldValue.serverTimestamp() else FieldValue.serverTimestamp()),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // DOCTORS & APPOINTMENTS
    // ----------------------------------------------------

    fun observeDoctors(): Flow<List<Doctor>> = callbackFlow {
        val path = "doctors"
        val registration = db.collection(path)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                val doctors = snapshot?.toObjects(Doctor::class.java) ?: emptyList()
                trySend(doctors)
            }
        awaitClose { registration.remove() }
    }

    fun observeUserAppointments(userId: String): Flow<List<Appointment>> = callbackFlow {
        val path = "appointments"
        val registration = db.collection(path)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                val appointments = snapshot?.toObjects(Appointment::class.java) ?: emptyList()
                trySend(appointments)
            }
        awaitClose { registration.remove() }
    }

    suspend fun bookAppointment(appointment: Appointment): Result<String> {
        val uid = requireUserId()
        val path = "appointments"
        return try {
            val docRef = db.collection(path).document()
            val payload = appointment.toMap() + mapOf(
                "userId" to uid,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateAppointmentStatus(appointmentId: String, status: String): Result<Unit> {
        requireUserId()
        val path = "appointments/$appointmentId"
        return try {
            db.collection("appointments").document(appointmentId).update(
                mapOf(
                    "status" to status,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // ORDERS & ESCROW
    // ----------------------------------------------------

    fun observeUserOrders(userId: String): Flow<List<Order>> = callbackFlow {
        val path = "orders"
        val registration = db.collection(path)
            .whereEqualTo("buyerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.toObjects(Order::class.java) ?: emptyList()
                trySend(orders)
            }
        awaitClose { registration.remove() }
    }

    suspend fun createOrder(order: Order): Result<String> {
        val uid = requireUserId()
        val path = "orders"
        return try {
            val docRef = db.collection(path).document()
            val payload = order.toMap() + mapOf(
                "buyerId" to uid,
                "status" to "escrow_held",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()

            // Automatically mark pigeon as reserved
            if (order.pigeonId.isNotBlank()) {
                db.collection("pigeons").document(order.pigeonId).update(
                    mapOf(
                        "status" to "reserved",
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun releaseEscrowWithOtp(orderId: String): Result<Unit> {
        requireUserId()
        val path = "orders/$orderId"
        return try {
            db.collection("orders").document(orderId).update(
                mapOf(
                    "status" to "completed",
                    "escrowStatus" to "released_to_seller",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> {
        requireUserId()
        val path = "orders/$orderId"
        return try {
            db.collection("orders").document(orderId).update(
                mapOf(
                    "status" to newStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // CONVERSATIONS & MESSAGES (BUYER-SELLER CHAT)
    // ----------------------------------------------------

    fun observeUserConversations(userId: String): Flow<List<Conversation>> {
        val buyerFlow = callbackFlow<List<Conversation>> {
            val reg = db.collection("conversations")
                .whereEqualTo("buyerId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        handleFirestoreError(error, OperationType.LIST, "conversations")
                        close(error)
                        return@addSnapshotListener
                    }
                    val list = snapshot?.toObjects(Conversation::class.java) ?: emptyList()
                    trySend(list)
                }
            awaitClose { reg.remove() }
        }

        val sellerFlow = callbackFlow<List<Conversation>> {
            val reg = db.collection("conversations")
                .whereEqualTo("sellerId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        handleFirestoreError(error, OperationType.LIST, "conversations")
                        close(error)
                        return@addSnapshotListener
                    }
                    val list = snapshot?.toObjects(Conversation::class.java) ?: emptyList()
                    trySend(list)
                }
            awaitClose { reg.remove() }
        }

        return kotlinx.coroutines.flow.combine(buyerFlow, sellerFlow) { bList, sList ->
            (bList + sList).distinctBy { it.id }.sortedByDescending { it.updatedAt?.toDate()?.time ?: 0L }
        }
    }

    fun observeMessages(conversationId: String): Flow<List<Message>> = callbackFlow {
        val path = "conversations/$conversationId/messages"
        val registration = db.collection("conversations")
            .document(conversationId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                trySend(messages)
            }
        awaitClose { registration.remove() }
    }

    suspend fun getOrCreateConversation(
        sellerId: String,
        sellerName: String,
        pigeonId: String,
        pigeonTitle: String,
        pigeonBreed: String = "",
        pigeonPrice: Double = 0.0
    ): Result<String> {
        val buyerId = requireUserId()
        val buyerProfile = auth.currentUser?.displayName ?: "Buyer"
        val path = "conversations"
        return try {
            val query = db.collection(path)
                .whereEqualTo("buyerId", buyerId)
                .whereEqualTo("sellerId", sellerId)
                .get()
                .await()

            val existing = query.documents.firstOrNull { it.getString("pigeonId") == pigeonId }
            if (existing != null) {
                return Result.success(existing.id)
            }

            val docRef = db.collection(path).document()
            val payload = mapOf(
                "buyerId" to buyerId,
                "buyerName" to buyerProfile,
                "sellerId" to sellerId,
                "sellerName" to sellerName,
                "participantIds" to "$buyerId:$sellerId",
                "pigeonId" to pigeonId,
                "pigeonTitle" to pigeonTitle,
                "pigeonBreed" to pigeonBreed,
                "pigeonPrice" to pigeonPrice,
                "lastMessage" to "Inquiry initiated for $pigeonTitle",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun sendMessage(
        conversationId: String,
        text: String,
        senderName: String,
        messageType: String = "text",
        offerAmount: Double = 0.0,
        isBreeder: Boolean = false
    ): Result<Unit> {
        val uid = requireUserId()
        val path = "conversations/$conversationId/messages"
        return try {
            val messageRef = db.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .document()

            val payload = mapOf(
                "conversationId" to conversationId,
                "senderId" to uid,
                "senderName" to senderName,
                "text" to text,
                "messageType" to messageType,
                "offerAmount" to offerAmount,
                "isBreeder" to isBreeder,
                "createdAt" to FieldValue.serverTimestamp()
            )
            messageRef.set(payload).await()

            // Update parent lastMessage and timestamp
            db.collection("conversations").document(conversationId).update(
                mapOf(
                    "lastMessage" to if (messageType == "offer") "Offered: $text" else text,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // INITIAL SEEDING (FOR FIRST LAUNCH)
    // ----------------------------------------------------

    suspend fun seedInitialDataIfEmpty() {
        try {
            val pigeonCheck = db.collection("pigeons").limit(1).get().await()
            if (pigeonCheck.isEmpty) {
                val seedPigeons = listOf(
                    Pigeon(
                        sellerId = "sys_breeder_1",
                        sellerName = "Vandenabeele Dynasty",
                        sellerLoft = "Royal Flanders Loft",
                        title = "Olympic Ace Turbo 700 - 1st National",
                        breed = "Racing Homer",
                        gender = "Cock",
                        ringNumber = "BELG-2023-4192084",
                        birthYear = 2023,
                        colorMorph = "Iridescent Blue Bar",
                        price = 3850.0,
                        location = "Ghent, Belgium",
                        description = "Direct grandson of Bliksem. Winner of 1st National Limoges (620 km) against 14,210 birds. Exceptional wing buoyancy, strong chest keel and rich amber iris.",
                        imageUrl = null,
                        status = "available",
                        vaccinated = true,
                        pedigreeAvailable = true
                    ),
                    Pigeon(
                        sellerId = "sys_breeder_2",
                        sellerName = "Lord Sterling Aviaries",
                        sellerLoft = "Windsor Heritage Loft",
                        title = "Majestic Pure White Jacobin Show Pair",
                        breed = "Jacobin",
                        gender = "Pair",
                        ringNumber = "UK-2024-J8819",
                        birthYear = 2024,
                        colorMorph = "Snow White Silk",
                        price = 1450.0,
                        location = "Surrey, United Kingdom",
                        description = "Exquisite high-cushion mane with symmetrical hood feathers covering the head in complete circular rosette. Supreme Champion UK Avian Show.",
                        imageUrl = null,
                        status = "available",
                        vaccinated = true,
                        pedigreeAvailable = true
                    ),
                    Pigeon(
                        sellerId = "sys_breeder_3",
                        sellerName = "Al-Khalifa Prestige Lofts",
                        sellerLoft = "Desert Falcon Avian",
                        title = "Saxon Fairy Swallow - Blue White Barred",
                        breed = "Saxon Fairy Swallow",
                        gender = "Hen",
                        ringNumber = "UAE-2023-SS109",
                        birthYear = 2023,
                        colorMorph = "Lavender Swallow Morph",
                        price = 920.0,
                        location = "Dubai, UAE",
                        description = "Double-crested with magnificent full foot muffs measuring 18cm. Purebred German lineage with pristine cap feather symmetry.",
                        imageUrl = null,
                        status = "available",
                        vaccinated = true,
                        pedigreeAvailable = true
                    ),
                    Pigeon(
                        sellerId = "sys_breeder_1",
                        sellerName = "Vandenabeele Dynasty",
                        sellerLoft = "Royal Flanders Loft",
                        title = "Golden Arrow Long Distance Hen",
                        breed = "Racing Homer",
                        gender = "Hen",
                        ringNumber = "BELG-2024-5002188",
                        birthYear = 2024,
                        colorMorph = "Dark Checker Pied",
                        price = 2200.0,
                        location = "Bruges, Belgium",
                        description = "Bred for extreme weather headwinds. Sire won 2nd Barcelona International (1,050 km). Inoculated with certified bloodline DNA certificate.",
                        imageUrl = null,
                        status = "available",
                        vaccinated = true,
                        pedigreeAvailable = true
                    ),
                    Pigeon(
                        sellerId = "sys_breeder_4",
                        sellerName = "Bokhara Imperials",
                        sellerLoft = "Central Silk Aviaries",
                        title = "Imperial Bokhara Trumpeter",
                        breed = "Bokhara Trumpeter",
                        gender = "Cock",
                        ringNumber = "UZ-2023-BT774",
                        birthYear = 2023,
                        colorMorph = "Glossy Raven Black",
                        price = 1650.0,
                        location = "Tashkent / Munich",
                        description = "Famous musical trumpet vocalization with immense feather crest and rose head cap. Supreme vitality and gentle disposition.",
                        imageUrl = null,
                        status = "available",
                        vaccinated = true,
                        pedigreeAvailable = true
                    )
                )

                for (pigeon in seedPigeons) {
                    val ref = db.collection("pigeons").document()
                    ref.set(
                        pigeon.toMap() + mapOf(
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }

            val storeCheck = db.collection("stores").limit(1).get().await()
            if (storeCheck.isEmpty) {
                val seedStores = listOf(
                    Store(
                        ownerId = "sys_breeder_in_1",
                        ownerName = "Mohammed Faizan",
                        storeName = "Bengaluru Royal Homer Loft",
                        tagline = "Championship Long-Distance Racing & Homer Bloodlines",
                        description = "State-of-the-art climate regulated aviary in Bangalore South. Breeding South India Racing Pigeon Club champions with documented GPS telemetry and pedigree rings.",
                        location = "Jayanagar, Bengaluru",
                        city = "Bangalore",
                        stateRegion = "Karnataka",
                        distanceKm = 2.8,
                        pincode = "560041",
                        upiId = "royalhomer.blr@okaxis",
                        acceptsInrUpi = true,
                        verified = true,
                        totalSales = 312,
                        rating = 4.98
                    ),
                    Store(
                        ownerId = "sys_breeder_in_2",
                        ownerName = "Sardar Harpreet Singh",
                        storeName = "Punjab Champion Fantail & Shiraz Loft",
                        tagline = "Ludhiana Heritage Fancy & Exhibition Aviaries",
                        description = "Specializing in high-cushion pure white Fantails, Lahore Shirazi, and high flying Punjabi pigeons. Certified avian vaccinated with full parentage certificates.",
                        location = "Ludhiana Grand Trunk, Punjab",
                        city = "Punjab",
                        stateRegion = "Punjab",
                        distanceKm = 7.4,
                        pincode = "141001",
                        upiId = "punjabpigeons@paytm",
                        acceptsInrUpi = true,
                        verified = true,
                        totalSales = 224,
                        rating = 4.96
                    ),
                    Store(
                        ownerId = "sys_breeder_in_3",
                        ownerName = "Vikramaditya Oberoi",
                        storeName = "Delhi NCR SkyLords Loft",
                        tagline = "Elite National Race Winners & Belgian Imports",
                        description = "Direct descendants of Janssen and Vandenabeele strains acclimated to Indian climatic terrains. Secured with Pigeonverse Dual-Key Escrow & UPI instant release.",
                        location = "Sainik Farms, South Delhi",
                        city = "Delhi NCR",
                        stateRegion = "Delhi NCR",
                        distanceKm = 11.2,
                        pincode = "110062",
                        upiId = "skylords.delhi@okhdfcbank",
                        acceptsInrUpi = true,
                        verified = true,
                        totalSales = 189,
                        rating = 4.94
                    ),
                    Store(
                        ownerId = "sys_breeder_in_4",
                        ownerName = "Mirza Aslam Baig",
                        storeName = "Hyderabad Heritage Avian Loft",
                        tagline = "Preserving Rare Shirazi, Lotan & Mukhi Pigeons",
                        description = "Decades of heritage lineage from historic Deccan lofts. Offering DNA-certified pairs, ring banded with nationwide climate-safe avian transport.",
                        location = "Banjara Hills, Hyderabad",
                        city = "Hyderabad",
                        stateRegion = "Telangana",
                        distanceKm = 16.5,
                        pincode = "500034",
                        upiId = "heritageavian.hyd@ybl",
                        acceptsInrUpi = true,
                        verified = true,
                        totalSales = 145,
                        rating = 4.92
                    ),
                    Store(
                        ownerId = "sys_breeder_1",
                        ownerName = "Luc Vandenabeele",
                        storeName = "Royal Flanders Championship Loft",
                        tagline = "Centuries of Racing Homer Supremacy",
                        description = "Internationally renowned racing loft with over 40 National 1st prizes. State-of-the-art genetics laboratory, temperature-regulated winter aviaries and climate-controlled shipping.",
                        location = "Flanders, Belgium",
                        city = "International",
                        stateRegion = "Belgium",
                        distanceKm = 48.0,
                        pincode = "8000",
                        upiId = "vandenabeele@intlupi",
                        acceptsInrUpi = true,
                        verified = true,
                        totalSales = 248,
                        rating = 4.98
                    ),
                    Store(
                        ownerId = "sys_breeder_2",
                        ownerName = "Lord Arthur Sterling",
                        storeName = "Sterling Avian Heritage Lofts",
                        tagline = "World-Class Jacobin & Exhibition Breeds",
                        description = "Specializing in the preservation of rare Victorian fancy pigeons, championship Jacobins and Saxon Swallows with documented pedigrees dating back to 1912.",
                        location = "Surrey, UK",
                        city = "International",
                        stateRegion = "UK",
                        distanceKm = 62.0,
                        pincode = "GU1",
                        upiId = "sterlinglofts@intlupi",
                        acceptsInrUpi = true,
                        verified = true,
                        totalSales = 132,
                        rating = 4.95
                    )
                )

                for (store in seedStores) {
                    val ref = db.collection("stores").document()
                    ref.set(
                        store.toMap() + mapOf(
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }

            val doctorCheck = db.collection("doctors").limit(1).get().await()
            if (doctorCheck.isEmpty) {
                val seedDoctors = listOf(
                    Doctor(
                        name = "Dr. Julian Sterling, DVM",
                        title = "Chief Avian Orthopedic & Respiratory Surgeon",
                        qualification = "DVM, Dip. ECZM (Avian), Royal Veterinary College",
                        experienceYears = 18,
                        fee = 120.0,
                        rating = 4.97,
                        clinicLocation = "London Avian Medical Pavilion / Telehealth",
                        availableDays = "Mon - Fri, 09:00 - 18:00 GMT"
                    ),
                    Doctor(
                        name = "Dr. Elena Rostova, PhD",
                        title = "Pigeon Genetics & Fertility Specialist",
                        qualification = "PhD Avian Biology, DVM University of Zurich",
                        experienceYears = 14,
                        fee = 95.0,
                        rating = 4.92,
                        clinicLocation = "Alpine Avian Research Center / Video Consult",
                        availableDays = "Mon, Wed, Thu, Sat"
                    ),
                    Doctor(
                        name = "Dr. Tariq Al-Mansoor",
                        title = "Racing Performance & Parasitology Expert",
                        qualification = "BVSc, MSc Racing Pigeon Physiology",
                        experienceYears = 22,
                        fee = 140.0,
                        rating = 5.0,
                        clinicLocation = "Gulf Falcon & Pigeon Hospital, Dubai",
                        availableDays = "Everyday 10:00 - 20:00 GST"
                    )
                )

                for (doc in seedDoctors) {
                    val ref = db.collection("doctors").document()
                    ref.set(doc.toMap()).await()
                }
            }
        } catch (_: Exception) {
            // Seeding failures are non-fatal
        }
    }
}
