package com.kevinfreyap.product.data.network.lookup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

data class ScannedProductInfo(
    val barcode: String,
    val name: String,
    val brand: String? = null,
    val category: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val packageSize: String? = null,
    val unit: String = "pcs"
)

sealed interface ProductLookupResult {
    data class Success(val product: ScannedProductInfo) : ProductLookupResult
    data object NotFound : ProductLookupResult
    data object Offline : ProductLookupResult
    data object Timeout : ProductLookupResult
    data object RateLimited : ProductLookupResult
    data class Error(val message: String) : ProductLookupResult
}

@Singleton
class ProductLookupService @Inject constructor() {
    suspend fun lookupBarcode(barcode: String): ProductLookupResult = withContext(Dispatchers.IO) {
        val cleanBarcode = barcode.trim()
        if (cleanBarcode.length < 3 || !cleanBarcode.all { it.isDigit() }) {
            return@withContext ProductLookupResult.NotFound
        }

        val urlString = "https://world.openfoodfacts.org/api/v2/product/$cleanBarcode.json"
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000
                setRequestProperty("User-Agent", "LocalGroceryInventory - Android/1.0")
                setRequestProperty("Accept", "application/json")
            }

            val responseCode = connection.responseCode
            if (responseCode == 429) {
                return@withContext ProductLookupResult.RateLimited
            }
            if (responseCode !in 200..299) {
                return@withContext ProductLookupResult.NotFound
            }

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val jsonString = reader.use { it.readText() }
            val json = JSONObject(jsonString)

            val status = json.optInt("status", 0)
            if (status != 1 || !json.has("product")) {
                return@withContext ProductLookupResult.NotFound
            }

            val productJson = json.getJSONObject("product")

            val name = productJson.optString("product_name").ifBlank {
                productJson.optString("product_name_en")
            }.ifBlank {
                null
            } ?: return@withContext ProductLookupResult.NotFound

            val brand = productJson.optString("brands").ifBlank { null }
            val categoriesRaw = productJson.optString("categories").ifBlank { null }
            val mainCategory = categoriesRaw?.split(",")?.firstOrNull()?.trim()?.ifEmpty { null }

            val description = productJson.optString("generic_name").ifBlank {
                productJson.optString("generic_name_en").ifBlank { null }
            }

            val imageUrl = productJson.optString("image_front_url").ifBlank {
                productJson.optString("image_url").ifBlank { null }
            }

            val packageSize = productJson.optString("quantity").ifBlank { null }

            val unit = when {
                packageSize != null && packageSize.contains("ml", ignoreCase = true) -> "ml"
                packageSize != null && packageSize.contains("kg", ignoreCase = true) -> "kg"
                packageSize != null && packageSize.contains("g", ignoreCase = true) -> "g"
                packageSize != null && packageSize.contains("L", ignoreCase = true) -> "L"
                else -> "pcs"
            }

            ProductLookupResult.Success(
                ScannedProductInfo(
                    barcode = cleanBarcode,
                    name = name.trim(),
                    brand = brand?.trim(),
                    category = mainCategory?.trim(),
                    description = description?.trim(),
                    imageUrl = imageUrl?.trim(),
                    packageSize = packageSize?.trim(),
                    unit = unit
                )
            )
        } catch (_: SocketTimeoutException) {
            ProductLookupResult.Timeout
        } catch (_: UnknownHostException) {
            ProductLookupResult.Offline
        } catch (e: Exception) {
            if (e.message?.contains("Unable to resolve host", ignoreCase = true) == true) {
                ProductLookupResult.Offline
            } else {
                ProductLookupResult.Error(e.message ?: "Lookup failed")
            }
        } finally {
            connection?.disconnect()
        }
    }
}
