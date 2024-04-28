package domain.model

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
enum class CurrencyCode(
    val country: String,
    val flag: DrawableResource
) {

}