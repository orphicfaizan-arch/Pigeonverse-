package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Appointment
import com.example.data.model.Conversation
import com.example.data.model.Doctor
import com.example.data.model.Message
import com.example.data.model.Order
import com.example.data.model.Pigeon
import com.example.data.model.Store
import com.example.data.model.UserProfile
import com.example.data.repository.PigeonverseRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import android.graphics.Bitmap
import com.example.data.ai.AiRole
import com.example.data.ai.ChatMessage
import com.example.data.ai.GroundingCitation
import com.example.data.ai.ImageStudioUiState
import com.example.data.ai.PigeonverseAiRepository
import com.google.firebase.ai.Chat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

enum class PigeonTab(val title: String) {
    DASHBOARD("Dashboard"),
    INVENTORY("My Loft"),
    STORES("Stores"),
    AI_STUDIO("Avian AI"),
    DOCTORS("Doctors"),
    ORDERS("Escrow"),
    PROFILE("Profile");

    companion object {
        val MARKET = DASHBOARD
    }
}


class PigeonverseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PigeonverseRepository(application)

    private val _currentTab = MutableStateFlow(PigeonTab.MARKET)
    val currentTab: StateFlow<PigeonTab> = _currentTab.asStateFlow()

    private val _selectedBreedFilter = MutableStateFlow("All Breeds")
    val selectedBreedFilter: StateFlow<String> = _selectedBreedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _healthFilter = MutableStateFlow("All")
    val healthFilter: StateFlow<String> = _healthFilter.asStateFlow()

    // Indian Currency & FX Toggle (INR vs USD)
    private val _selectedCurrency = MutableStateFlow("INR")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    fun toggleCurrency() {
        _selectedCurrency.value = if (_selectedCurrency.value == "INR") "USD" else "INR"
    }

    fun setCurrency(currency: String) {
        _selectedCurrency.value = currency
    }

    // Nearby Stores Search & Radius Filters
    private val _storeRadiusFilter = MutableStateFlow("All")
    val storeRadiusFilter: StateFlow<String> = _storeRadiusFilter.asStateFlow()

    private val _storeCityFilter = MutableStateFlow("All Hubs")
    val storeCityFilter: StateFlow<String> = _storeCityFilter.asStateFlow()

    private val _storeSearchQuery = MutableStateFlow("")
    val storeSearchQuery: StateFlow<String> = _storeSearchQuery.asStateFlow()

    fun setStoreRadius(radius: String) {
        _storeRadiusFilter.value = radius
    }

    fun setStoreCity(city: String) {
        _storeCityFilter.value = city
    }

    fun setStoreSearchQuery(query: String) {
        _storeSearchQuery.value = query
    }

    fun setHealthFilter(filter: String) {
        _healthFilter.value = filter
    }

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _pigeons = MutableStateFlow<List<Pigeon>>(emptyList())
    val pigeons: StateFlow<List<Pigeon>> = _pigeons.asStateFlow()

    private val _userInventory = MutableStateFlow<List<Pigeon>>(emptyList())
    val userInventory: StateFlow<List<Pigeon>> = _userInventory.asStateFlow()

    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores.asStateFlow()

    private val _userStore = MutableStateFlow<Store?>(null)
    val userStore: StateFlow<Store?> = _userStore.asStateFlow()

    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> = _doctors.asStateFlow()

    private val _userAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val userAppointments: StateFlow<List<Appointment>> = _userAppointments.asStateFlow()

    private val _userOrders = MutableStateFlow<List<Order>>(emptyList())
    val userOrders: StateFlow<List<Order>> = _userOrders.asStateFlow()

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _selectedConversationId = MutableStateFlow<String?>(null)
    val selectedConversationId: StateFlow<String?> = _selectedConversationId.asStateFlow()

    private val _activeMessages = MutableStateFlow<List<Message>>(emptyList())
    val activeMessages: StateFlow<List<Message>> = _activeMessages.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        val authListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                initializeUserData(user.uid, user.email ?: "")
            }
        }
        Firebase.auth.addAuthStateListener(authListener)
    }

    fun selectTab(tab: PigeonTab) {
        _currentTab.value = tab
    }

    fun setBreedFilter(breed: String) {
        _selectedBreedFilter.value = breed
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    private fun initializeUserData(uid: String, email: String) {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()

            // Observe Profile
            launch {
                repository.observeUserProfile(uid).collect { profile ->
                    if (profile == null) {
                        val isAdmin = email.equals("orphicfaizan@gmail.com", ignoreCase = true)
                        repository.createOrUpdateUserProfile(
                            displayName = Firebase.auth.currentUser?.displayName ?: "Elite Pigeon Breeder",
                            photoUrl = Firebase.auth.currentUser?.photoUrl?.toString(),
                            loftName = if (isAdmin) "Pigeonverse Sovereign Loft" else "Heritage Loft",
                            isBreeder = true,
                            location = "International",
                            bio = "Avian enthusiast and passionate pigeon keeper dedicated to breed excellence.",
                            subscriptionTier = if (isAdmin) "Pro" else "Free"
                        )
                    } else {
                        _userProfile.value = profile
                    }
                }
            }

            // Observe Market Pigeons
            launch {
                repository.observeAllPigeons().collect { list ->
                    _pigeons.value = list
                }
            }

            // Observe User Inventory
            launch {
                repository.observeUserPigeons(uid).collect { list ->
                    _userInventory.value = list
                }
            }

            // Observe Stores
            launch {
                repository.observeAllStores().collect { list ->
                    _stores.value = list
                }
            }

            // Observe User Store
            launch {
                repository.observeUserStore(uid).collect { store ->
                    _userStore.value = store
                }
            }

            // Observe Doctors
            launch {
                repository.observeDoctors().collect { list ->
                    _doctors.value = list
                }
            }

            // Observe Appointments
            launch {
                repository.observeUserAppointments(uid).collect { list ->
                    _userAppointments.value = list
                }
            }

            // Observe Orders
            launch {
                repository.observeUserOrders(uid).collect { list ->
                    _userOrders.value = list
                }
            }

            // Observe Conversations
            launch {
                repository.observeUserConversations(uid).collect { list ->
                    _conversations.value = list
                }
            }
        }
    }

    fun selectConversation(conversationId: String) {
        _selectedConversationId.value = conversationId
        viewModelScope.launch {
            repository.observeMessages(conversationId).collect { msgs ->
                _activeMessages.value = msgs
            }
        }
    }

    fun closeConversation() {
        _selectedConversationId.value = null
        _activeMessages.value = emptyList()
    }

    fun sendMessage(
        text: String,
        messageType: String = "text",
        offerAmount: Double = 0.0
    ) {
        val convId = _selectedConversationId.value ?: return
        val profile = _userProfile.value
        val senderName = profile?.displayName.orEmpty().ifBlank { "Fancier" }
        val isBreeder = profile?.isBreeder == true || profile?.isVerifiedBreeder == true
        viewModelScope.launch {
            repository.sendMessage(
                conversationId = convId,
                text = text,
                senderName = senderName,
                messageType = messageType,
                offerAmount = offerAmount,
                isBreeder = isBreeder
            )
        }
    }

    fun startChatWithSeller(
        sellerId: String,
        sellerName: String = "Verified Breeder",
        pigeonId: String,
        pigeonTitle: String,
        pigeonBreed: String = "",
        pigeonPrice: Double = 0.0
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.getOrCreateConversation(
                sellerId = sellerId,
                sellerName = sellerName,
                pigeonId = pigeonId,
                pigeonTitle = pigeonTitle,
                pigeonBreed = pigeonBreed,
                pigeonPrice = pigeonPrice
            )
            _isLoading.value = false
            res.onSuccess { convId ->
                selectConversation(convId)
                _currentTab.value = PigeonTab.ORDERS
            }.onFailure { err ->
                _statusMessage.value = "Failed to start conversation: ${err.message}"
            }
        }
    }

    fun addPigeonListing(
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
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val profile = _userProfile.value
            val pigeon = Pigeon(
                sellerId = profile?.userId ?: "",
                sellerName = profile?.displayName ?: "Anonymous Loft",
                sellerLoft = profile?.loftName.orEmpty().ifBlank { "Private Loft" },
                title = title,
                breed = breed,
                gender = gender,
                ringNumber = ringNumber,
                birthYear = birthYear,
                colorMorph = colorMorph,
                price = price,
                location = location,
                description = description,
                status = "available",
                vaccinated = vaccinated,
                pedigreeAvailable = pedigreeAvailable
            )
            val res = repository.addPigeon(pigeon)
            _isLoading.value = false
            res.onSuccess {
                _statusMessage.value = "Pigeon listing published successfully!"
            }.onFailure { err ->
                _statusMessage.value = "Error adding pigeon: ${err.message}"
            }
        }
    }

    fun updatePigeonStatus(pigeonId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updatePigeon(pigeonId, mapOf("status" to newStatus))
        }
    }

    fun deletePigeon(pigeonId: String) {
        viewModelScope.launch {
            val res = repository.deletePigeon(pigeonId)
            res.onSuccess {
                _statusMessage.value = "Pigeon removed from inventory."
            }.onFailure { err ->
                _statusMessage.value = "Failed to delete: ${err.message}"
            }
        }
    }

    fun saveUserStore(
        storeName: String,
        tagline: String,
        description: String,
        location: String,
        city: String = "Bangalore",
        stateRegion: String = "Karnataka",
        pincode: String = "560001",
        upiId: String = "pigeonverse@okhdfcbank",
        distanceKm: Double = 3.5
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val profile = _userProfile.value
            val store = Store(
                ownerId = profile?.userId ?: "",
                ownerName = profile?.displayName ?: "Breeder",
                storeName = storeName,
                tagline = tagline,
                description = description,
                location = location,
                city = city,
                stateRegion = stateRegion,
                pincode = pincode,
                upiId = upiId,
                acceptsInrUpi = true,
                distanceKm = distanceKm,
                verified = profile?.isVerifiedBreeder ?: false
            )
            val res = repository.createOrUpdateStore(store)
            _isLoading.value = false
            res.onSuccess {
                _statusMessage.value = "Online Pigeon Store updated!"
            }.onFailure { err ->
                _statusMessage.value = "Failed to update store: ${err.message}"
            }
        }
    }

    fun bookDoctorAppointment(
        doctor: Doctor,
        date: String,
        timeSlot: String,
        consultType: String,
        notes: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val profile = _userProfile.value
            val appt = Appointment(
                userId = profile?.userId ?: "",
                userName = profile?.displayName ?: "Client",
                doctorId = doctor.id,
                doctorName = doctor.name,
                specialty = doctor.title,
                date = date,
                timeSlot = timeSlot,
                consultType = consultType,
                fee = doctor.fee,
                notes = notes,
                status = "confirmed"
            )
            val res = repository.bookAppointment(appt)
            _isLoading.value = false
            res.onSuccess {
                _statusMessage.value = "Appointment booked with ${doctor.name}!"
            }.onFailure { err ->
                _statusMessage.value = "Failed to book: ${err.message}"
            }
        }
    }

    fun placeOrder(
        pigeon: Pigeon,
        paymentMethod: String,
        address: String,
        currency: String = "INR",
        upiTransactionId: String = ""
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val profile = _userProfile.value
            val generatedOtp = (100000..999999).random().toString()
            val order = Order(
                buyerId = profile?.userId ?: "",
                buyerName = profile?.displayName ?: "Buyer",
                sellerId = pigeon.sellerId,
                pigeonId = pigeon.id,
                pigeonTitle = pigeon.title,
                amount = pigeon.price,
                currency = currency,
                inrAmount = pigeon.inrPrice,
                paymentMethod = paymentMethod,
                upiTransactionId = upiTransactionId.ifBlank { "UPI-${System.currentTimeMillis().toString().takeLast(8)}" },
                securityVerificationCode = generatedOtp,
                escrowStatus = "held_in_rbi_escrow",
                status = "escrow_held",
                deliveryAddress = address
            )
            val res = repository.createOrder(order)
            _isLoading.value = false
            res.onSuccess {
                _statusMessage.value = "Escrow authorized! Handover OTP: $generatedOtp. Funds locked in RBI-compliant Escrow."
                _currentTab.value = PigeonTab.ORDERS
            }.onFailure { err ->
                _statusMessage.value = "Checkout failed: ${err.message}"
            }
        }
    }

    fun verifyAndReleaseEscrow(orderId: String, enteredCode: String, expectedCode: String) {
        if (enteredCode.trim() != expectedCode.trim()) {
            _statusMessage.value = "Invalid Handover OTP! Escrow funds remain secured."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.releaseEscrowWithOtp(orderId)
            _isLoading.value = false
            res.onSuccess {
                _statusMessage.value = "Handover OTP verified! Escrow payout released to breeder successfully."
            }.onFailure { err ->
                _statusMessage.value = "Verification failed: ${err.message}"
            }
        }
    }

    fun upgradeSubscriptionTier(tier: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.upgradeSubscription(tier)
            _isLoading.value = false
            res.onSuccess {
                _statusMessage.value = "Upgraded to $tier Tier!"
            }.onFailure { err ->
                _statusMessage.value = "Failed to upgrade: ${err.message}"
            }
        }
    }

    fun updateProfile(
        displayName: String,
        loftName: String,
        location: String,
        bio: String,
        phone: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val current = _userProfile.value
            val res = repository.createOrUpdateUserProfile(
                displayName = displayName,
                photoUrl = current?.photoUrl,
                loftName = loftName,
                isBreeder = current?.isBreeder ?: true,
                location = location,
                bio = bio,
                phone = phone,
                subscriptionTier = current?.subscriptionTier ?: "Free"
            )
            _isLoading.value = false
            res.onSuccess {
                _statusMessage.value = "Breeder Profile updated."
            }.onFailure { err ->
                _statusMessage.value = "Update failed: ${err.message}"
            }
        }
    }

    // ==========================================
    // AVIAN AI: CHATBOT, GROUNDING & IMAGE STUDIO
    // ==========================================
    private val aiRepository = PigeonverseAiRepository()

    // 1. Multi-turn Chatbot State
    private var activeChatSession: Chat? = null
    private val _currentAiRole = MutableStateFlow(AiRole.LOFT_ADVISOR)
    val currentAiRole: StateFlow<AiRole> = _currentAiRole.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "ai",
                text = "Welcome to Pigeonverse Avian AI. I am your specialized avian advisor. How can I assist your loft, breeding program, or health questions today?"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    fun selectAiRole(role: AiRole) {
        _currentAiRole.value = role
        activeChatSession = null
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "ai",
                text = "Switched to ${role.title} (${role.modelName}). ${role.subtitle}. Ready for your inquiry."
            )
        )
    }

    fun clearAiChat() {
        activeChatSession = null
        val role = _currentAiRole.value
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "ai",
                text = "Session reset for ${role.title}. How may I assist your loft?"
            )
        )
    }

    fun sendAiChatMessage(userText: String) {
        if (userText.isBlank() || _isAiGenerating.value) return
        val currentList = _chatMessages.value.toMutableList()
        currentList.add(ChatMessage(sender = "user", text = userText.trim()))
        _chatMessages.value = currentList
        _isAiGenerating.value = true

        viewModelScope.launch {
            try {
                if (activeChatSession == null) {
                    activeChatSession = aiRepository.createChatSession(_currentAiRole.value)
                }
                val chat = activeChatSession!!

                // Stream response for responsive TTFT
                val aiMsgId = java.util.UUID.randomUUID().toString()
                val tempAiMsg = ChatMessage(id = aiMsgId, sender = "ai", text = "", isStreaming = true)
                _chatMessages.value = _chatMessages.value + tempAiMsg

                val accumulated = StringBuilder()
                try {
                    aiRepository.streamChatMessage(chat, userText).collect { chunk ->
                        accumulated.append(chunk)
                        _chatMessages.value = _chatMessages.value.map { msg ->
                            if (msg.id == aiMsgId) msg.copy(text = accumulated.toString()) else msg
                        }
                    }
                    _chatMessages.value = _chatMessages.value.map { msg ->
                        if (msg.id == aiMsgId) msg.copy(isStreaming = false) else msg
                    }
                } catch (e: Exception) {
                    if (accumulated.isEmpty()) {
                        val fallbackResponse = aiRepository.sendChatMessage(chat, userText)
                        _chatMessages.value = _chatMessages.value.map { msg ->
                            if (msg.id == aiMsgId) msg.copy(text = fallbackResponse, isStreaming = false) else msg
                        }
                    } else {
                        _chatMessages.value = _chatMessages.value.map { msg ->
                            if (msg.id == aiMsgId) msg.copy(isStreaming = false) else msg
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    sender = "ai",
                    text = "Request timed out after 60 seconds. Please try again."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    sender = "ai",
                    text = "Error: ${e.localizedMessage ?: "Failed to receive AI response"}"
                )
            } finally {
                _isAiGenerating.value = false
            }
        }
    }

    // 2. Google Search & Maps Grounding
    private val _groundingResult = MutableStateFlow<String?>(null)
    val groundingResult: StateFlow<String?> = _groundingResult.asStateFlow()

    private val _groundingCitations = MutableStateFlow<List<GroundingCitation>>(emptyList())
    val groundingCitations: StateFlow<List<GroundingCitation>> = _groundingCitations.asStateFlow()

    private val _isGroundingLoading = MutableStateFlow(false)
    val isGroundingLoading: StateFlow<Boolean> = _isGroundingLoading.asStateFlow()

    private val _groundingType = MutableStateFlow("search") // "search" or "maps"
    val groundingType: StateFlow<String> = _groundingType.asStateFlow()

    fun performSearchGrounding(query: String) {
        if (query.isBlank() || _isGroundingLoading.value) return
        _isGroundingLoading.value = true
        _groundingType.value = "search"
        _groundingResult.value = null
        _groundingCitations.value = emptyList()

        viewModelScope.launch {
            try {
                val (text, citations) = aiRepository.queryWithSearchGrounding(query)
                _groundingResult.value = text
                _groundingCitations.value = citations
            } catch (e: TimeoutCancellationException) {
                _groundingResult.value = "Search Grounding query timed out after 60 seconds."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _groundingResult.value = "Search Grounding error: ${e.localizedMessage ?: "Query failed"}"
            } finally {
                _isGroundingLoading.value = false
            }
        }
    }

    fun performMapsGrounding(query: String) {
        if (query.isBlank() || _isGroundingLoading.value) return
        _isGroundingLoading.value = true
        _groundingType.value = "maps"
        _groundingResult.value = null
        _groundingCitations.value = emptyList()

        viewModelScope.launch {
            try {
                val (text, places) = aiRepository.queryWithMapsGrounding(query)
                _groundingResult.value = text
                _groundingCitations.value = places
            } catch (e: TimeoutCancellationException) {
                _groundingResult.value = "Maps Grounding query timed out after 60 seconds."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _groundingResult.value = "Maps Grounding error: ${e.localizedMessage ?: "Location query failed"}"
            } finally {
                _isGroundingLoading.value = false
            }
        }
    }

    fun clearGrounding() {
        _groundingResult.value = null
        _groundingCitations.value = emptyList()
    }

    // 3. Nano Banana Image Studio (gemini-nano-banana-2.1)
    private val _imageStudioState = MutableStateFlow<ImageStudioUiState>(ImageStudioUiState.Idle)
    val imageStudioState: StateFlow<ImageStudioUiState> = _imageStudioState.asStateFlow()

    fun generatePigeonImage(prompt: String) {
        if (prompt.isBlank()) return
        _imageStudioState.value = ImageStudioUiState.Loading("Synthesizing luxury pigeon artwork via gemini-nano-banana-2.1 (30–60s)...")
        viewModelScope.launch {
            try {
                val bitmap = aiRepository.generatePigeonImage(prompt)
                if (bitmap != null) {
                    _imageStudioState.value = ImageStudioUiState.Success(bitmap, prompt)
                } else {
                    _imageStudioState.value = ImageStudioUiState.Error("No image was returned by gemini-nano-banana-2.1.")
                }
            } catch (e: TimeoutCancellationException) {
                _imageStudioState.value = ImageStudioUiState.Error("Image synthesis timed out after 60 seconds.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _imageStudioState.value = ImageStudioUiState.Error(e.localizedMessage ?: "Image generation failed.")
            }
        }
    }

    fun editPigeonImage(inputBitmap: Bitmap, editInstruction: String) {
        if (editInstruction.isBlank()) return
        _imageStudioState.value = ImageStudioUiState.Loading("Applying modifications via gemini-nano-banana-2.1 (30–60s)...")
        viewModelScope.launch {
            try {
                val bitmap = aiRepository.editPigeonImage(inputBitmap, editInstruction)
                if (bitmap != null) {
                    _imageStudioState.value = ImageStudioUiState.Success(bitmap, editInstruction)
                } else {
                    _imageStudioState.value = ImageStudioUiState.Error("No edited image returned by model.")
                }
            } catch (e: TimeoutCancellationException) {
                _imageStudioState.value = ImageStudioUiState.Error("Image editing timed out after 60 seconds.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _imageStudioState.value = ImageStudioUiState.Error(e.localizedMessage ?: "Image editing failed.")
            }
        }
    }

    fun resetImageStudio() {
        _imageStudioState.value = ImageStudioUiState.Idle
    }
}
