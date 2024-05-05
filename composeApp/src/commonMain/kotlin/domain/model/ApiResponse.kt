package domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val meta: Metadata,
    val data: Map<String, Currency>
)

@Serializable
data class Metadata(
    @SerialName("last_updated_at")
    val lastUpdatedAt: String
)

@Serializable
data class Currency(
    val code: String,
    val value: Double
)