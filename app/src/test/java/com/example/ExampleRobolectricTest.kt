package com.example

import com.example.data.model.Pigeon
import com.example.data.model.Store
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExamplePigeonverseUnitTest {

  @Test
  fun `verify pigeon inr conversion and formatting`() {
    val pigeon = Pigeon(
      id = "test-1",
      title = "Champion Homer",
      price = 100.0
    )
    assertEquals(8600.0, pigeon.inrPrice, 0.01)
    assertEquals("₹8,600", pigeon.formattedPrice(isRupee = true))
    assertEquals("$100", pigeon.formattedPrice(isRupee = false))
  }

  @Test
  fun `verify store nearby distance logic`() {
    val store = Store(
      id = "store-1",
      storeName = "Bengaluru Royal Lofts",
      city = "Bengaluru",
      distanceKm = 4.2,
      acceptsInrUpi = true
    )
    assertTrue(store.distanceKm < 5.0)
    assertTrue(store.acceptsInrUpi)
    assertEquals("Bengaluru Royal Lofts", store.storeName)
  }
}
