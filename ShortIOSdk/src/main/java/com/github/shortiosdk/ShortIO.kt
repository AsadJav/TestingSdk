package com.github.shortio

import com.github.shortiosdk.ShortIOErrorModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import com.google.gson.Gson
import okhttp3.RequestBody.Companion.toRequestBody
import com.github.shortiosdk.ShortIOResult


object ShortioSdk {
    fun shortenUrl(
        apiKey: String,
        parameters: ShortIOParametersModel
    ): ShortIOResult {
        val gson = Gson()
        val client = OkHttpClient()
        val mediaType = "application/json".toMediaType()
        val jsonBody = gson.toJson(parameters)
        val body = jsonBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.short.io/links/public")
            .post(body)
            .addHeader("accept", "application/json")
            .addHeader("content-type", "application/json")
            .addHeader("authorization", apiKey)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        return if (response.isSuccessful) {
            val model = responseBody?.let { gson.fromJson(it, ShortIOResponseModel::class.java) }
            if (model != null) {
                ShortIOResult.Success(model)
            } else {
                val errorModel = ShortIOErrorModel(
                    message = "Empty or malformed success response",
                    statusCode = response.code,
                    code = "MALFORMED_SUCCESS",
                    success = false
                )
                ShortIOResult.Error(errorModel)
            }
        } else {
            val errorModel = try {
                responseBody?.let {
                    gson.fromJson(it, ShortIOErrorModel::class.java).copy(statusCode = response.code)
                } ?: ShortIOErrorModel(
                    message = "Unknown error",
                    statusCode = response.code,
                    code = "UNKNOWN",
                    success = false
                )
            } catch (e: Exception) {
                ShortIOErrorModel(
                    message = "Malformed error response",
                    statusCode = response.code,
                    code = "INVALID_JSON",
                    success = false
                )
            }
            ShortIOResult.Error(errorModel)
        }
    }
}
