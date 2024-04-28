package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val meta: Metadata,
    val data: Map<String, Currency>
)

@Serializable
data class Metadata(
    val lastUpdatedAt: String
)

@Serializable
data class Currency(
    val code: String,
    val value: Double
)