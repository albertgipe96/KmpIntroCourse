package data.remote.api

import domain.CurrencyApiService
import domain.PreferencesRepository
import domain.model.ApiResponse
import domain.model.Currency
import domain.model.CurrencyCode
import domain.model.RequestState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CurrencyApiServiceImpl(
    private val preferencesRepository: PreferencesRepository
) : CurrencyApiService {
    companion object {
        const val CURRENCY_EXCHANGE_ENDPOINT = "https://api.currencyapi.com/v3/latest"
        const val API_KEY = "cur_live_ELmeQAhWNI3OXxAQJj6Jut7jLgTcv7mZmdbMPMm0"
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000L
        }
        install(DefaultRequest) {
            headers {
                append("apikey", API_KEY)
            }
        }
    }

    override suspend fun getLatestExchangeRates(): RequestState<List<Currency>> {
        return try {
            val response = httpClient.get(CURRENCY_EXCHANGE_ENDPOINT)
            when (val status = response.status) {
                HttpStatusCode.OK -> {
                    val apiResponse = Json.decodeFromString<ApiResponse>(response.body())

                    // Persist the timestamp locally
                    val lastUpdated = apiResponse.meta.lastUpdatedAt
                    preferencesRepository.saveLastUpdated(lastUpdated)

                    val availableCurrencyCodes = apiResponse.data.keys.filter { key ->
                        CurrencyCode.entries
                            .map { code -> code.name }
                            .toSet()
                            .contains(key)
                    }

                    val availableCurrencies = apiResponse.data.values.filter { currency ->
                        availableCurrencyCodes.contains(currency.code)
                    }

                    RequestState.Success(availableCurrencies)
                }
                else -> RequestState.Error("Network Error: ${status.value} - ${status.description}")
            }
        } catch (e: Exception) {
            RequestState.Error(e.message.toString())
        }
    }
}