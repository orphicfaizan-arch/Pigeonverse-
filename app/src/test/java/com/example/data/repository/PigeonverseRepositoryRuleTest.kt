package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.Pigeon
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID
import org.junit.Ignore

@Ignore("Requires local android-all shadow jar in Robolectric environment; rules are validated via node firestore.test.js")
class PigeonverseRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun addPigeon_validPayload_createsDocumentAndReturnsId() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = PigeonverseRepository(firestore)

        val pigeon = Pigeon(
            sellerId = auth.currentUser!!.uid,
            sellerName = "Alice Loft",
            sellerLoft = "Grand Alice Loft",
            title = "Champion Racing Homer Ace",
            breed = "Racing Homer",
            gender = "Cock",
            ringNumber = "BELG-2024-${UUID.randomUUID().toString().take(6)}",
            birthYear = 2024,
            colorMorph = "Blue Bar",
            price = 750.0,
            location = "Ghent, Belgium",
            description = "Olympic bloodline pigeon.",
            status = "available"
        )

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.addPigeon(pigeon)
        }
        assertTrue(result.isSuccess)
        val pigeonId = result.getOrThrow()
        assertTrue(pigeonId.isNotEmpty())
    }

    @Test
    fun updatePigeon_asOwner_succeeds() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = PigeonverseRepository(firestore)

        val pigeon = Pigeon(
            sellerId = auth.currentUser!!.uid,
            title = "Exquisite Jacobin Show Bird",
            breed = "Jacobin",
            price = 600.0,
            status = "available"
        )
        val pigeonId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.addPigeon(pigeon).getOrThrow()
        }

        val updateResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.updatePigeon(pigeonId, mapOf("price" to 850.0))
        }
        assertTrue(updateResult.isSuccess)
    }

    @Test
    fun updatePigeon_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val aliceRepo = PigeonverseRepository(firestore)
        val pigeon = Pigeon(
            sellerId = auth.currentUser!!.uid,
            title = "Rare Saxon Swallow",
            breed = "Saxon Fairy Swallow",
            price = 500.0,
            status = "available"
        )
        val pigeonId = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.addPigeon(pigeon).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = PigeonverseRepository(firestore)
        val updateResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.updatePigeon(pigeonId, mapOf("price" to 100.0))
        }
        assertTrue(updateResult.isFailure)
        val exception = updateResult.exceptionOrNull()
        val firestoreException = checkNotNull(exception?.unwrapFirestoreException()) {
            "Expected FirebaseFirestoreException but got $exception"
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
    }

    @Test
    fun observeUserPigeons_unauthenticated_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = PigeonverseRepository(firestore)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeUserPigeons("some_target_id").first()
            }
            fail("Expected exception due to unauthenticated access")
        } catch (exception: Exception) {
            val firestoreException = checkNotNull(exception.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $exception"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
        }
    }


    private companion object {
        const val ALICE_EMAIL = "alice@pigeonverse.test"
        const val BOB_EMAIL = "bob@pigeonverse.test"
        const val DEFAULT_TIMEOUT_MS = 6000L
        const val FLOW_TIMEOUT_MS = 4000L
    }
}
