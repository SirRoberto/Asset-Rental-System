package com.rental.asset.infrastructure.adapter.`in`.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.rental.asset.IntegrationTestBase
import com.rental.asset.domain.model.ItemStatus
import com.rental.asset.infrastructure.adapter.`in`.web.dto.ChangeItemStatusRequest
import com.rental.asset.infrastructure.adapter.`in`.web.dto.CreateAssetRequest
import com.rental.asset.infrastructure.adapter.`in`.web.dto.CreateInventoryItemRequest
import com.rental.asset.infrastructure.adapter.out.persistence.CategoryEntity
import com.rental.asset.infrastructure.adapter.out.persistence.SpringDataCategoryRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@AutoConfigureMockMvc
class AssetControllerIntegrationTest : IntegrationTestBase() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var categoryRepository: SpringDataCategoryRepository

    private lateinit var testCategoryId: UUID

    @BeforeEach
    fun setUp() {
        testCategoryId = UUID.randomUUID()
        categoryRepository.save(
            CategoryEntity(
                id = testCategoryId,
                name = "Elektronarzędzia",
                description = "Narzędzia elektryczne do wypożyczenia"
            )
        )
    }

    @Test
    fun `should create new asset and then inventory item assigned to it`() {
        // 1. Create a logical Asset Product (using real category)
        val createAssetRequest = CreateAssetRequest(
            name = "Wiertarka Makita",
            description = "Solidna wiertarka udarowa",
            categoryId = testCategoryId,
            dailyRate = BigDecimal("50.00"),
            hourlyRate = BigDecimal("15.00"),
            depositAmount = BigDecimal("200.00")
        )

        val assetResult = mockMvc.perform(
            post("/api/v1/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAssetRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Wiertarka Makita"))
            .andReturn()

        val assetResponseString = assetResult.response.contentAsString
        val assetId = objectMapper.readTree(assetResponseString).get("id").asText()

        // 2. Add an Inventory Item mapping to the new Asset
        val createItemRequest = CreateInventoryItemRequest(
            assetId = UUID.fromString(assetId),
            serialNumber = "SN-MAKITA-001",
            qrCodeToken = null, // Should auto-generate
            lastMaintenanceDate = LocalDate.now().minusMonths(1),
            nextMaintenanceDate = LocalDate.now().plusMonths(5)
        )

        val itemResult = mockMvc.perform(
            post("/api/v1/inventory-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createItemRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.assetId").value(assetId))
            .andExpect(jsonPath("$.status").value(ItemStatus.AVAILABLE.name))
            .andReturn()

        val itemResponseString = itemResult.response.contentAsString
        val itemIdString = objectMapper.readTree(itemResponseString).get("id").asText()
        val generatedToken = objectMapper.readTree(itemResponseString).get("qrCodeToken").asText()

        assertTrue(generatedToken.isNotEmpty())

        // 3. Status transition: AVAILABLE -> MAINTENANCE
        val statusRequest = ChangeItemStatusRequest(status = ItemStatus.MAINTENANCE.name)
        mockMvc.perform(
            patch("/api/v1/inventory-items/$itemIdString/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest))
        )
            .andExpect(status().isNoContent)

        // 4. Verify updated GET
        mockMvc.perform(get("/api/v1/inventory-items/$itemIdString"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(ItemStatus.MAINTENANCE.name))
    }

    @Test
    fun `should fail to rent item if maintenance is overdue`() {
        // Arrange: create asset with real category
        val assetRequest = CreateAssetRequest(
            name = "Koparka",
            description = null,
            categoryId = testCategoryId,
            dailyRate = BigDecimal("500"),
            hourlyRate = BigDecimal("100"),
            depositAmount = BigDecimal("2000")
        )
        val assetId = objectMapper.readTree(
            mockMvc.perform(
                post("/api/v1/assets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(assetRequest))
            ).andReturn().response.contentAsString
        ).get("id").asText()

        val itemRequest = CreateInventoryItemRequest(
            assetId = UUID.fromString(assetId),
            serialNumber = "OVERDUE-01",
            qrCodeToken = null,
            lastMaintenanceDate = LocalDate.now().minusMonths(12),
            nextMaintenanceDate = LocalDate.now().minusDays(1) // OVERDUE!
        )
        val itemId = objectMapper.readTree(
            mockMvc.perform(
                post("/api/v1/inventory-items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(itemRequest))
            ).andReturn().response.contentAsString
        ).get("id").asText()

        // Act & Assert: Try RENTED - should fail (maintenance overdue)
        val statusRequest = ChangeItemStatusRequest(status = ItemStatus.RENTED.name)

        mockMvc.perform(
            patch("/api/v1/inventory-items/$itemId/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.title").exists())
    }
}
