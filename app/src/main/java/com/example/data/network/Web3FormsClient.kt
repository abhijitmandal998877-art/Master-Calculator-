package com.example.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object Web3FormsClient {
    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    suspend fun submitContactForm(
        name: String,
        email: String,
        message: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val jsonObject = JSONObject().apply {
                put("access_key", "1b84db46-8ff6-4b2a-84a1-65561f07545c")
                put("name", name)
                put("email", email)
                put("message", message)
                put("subject", "Master Calculator Feedback from $name")
            }

            val requestBody = jsonObject.toString().toRequestBody(JSON)
            val request = Request.Builder()
                .url("https://api.web3forms.com/submit")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    val jsonResponse = JSONObject(responseBody)
                    if (jsonResponse.optBoolean("success", false)) {
                        Result.success(jsonResponse.optString("message", "Message sent successfully!"))
                    } else {
                        Result.failure(Exception(jsonResponse.optString("message", "Failed to submit form.")))
                    }
                } else {
                    Result.failure(IOException("Server error: ${response.code}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
