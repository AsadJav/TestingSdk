package com.github.shortiosdk

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import com.google.gson.Gson
import okhttp3.RequestBody.Companion.toRequestBody
import com.github.shortiosdk.ShortIOResult
import com.github.shortiosdk.ShortIOErrorModel


object ShortioSdk {
    fun shortenUrl(
        apiKey: String,
        parameters: ShortIOParametersModel
    ): ShortIOResult {
        val gson = Gson()
        val client = OkHttpClient()
        val mediaType = "application/json".toMediaType()
                val rawExpiresAt = when (val ea = parameters.expiresAt) {
            is StringOrInt.Str -> ea.value
            is StringOrInt.IntVal -> ea.value
            else -> null
        }
        val rawTtl = when (val t = parameters.ttl) {
            is StringOrInt.Str -> t.value
            is StringOrInt.IntVal -> t.value
            else -> null
        }

        val rawCreatedAt = when (val c = parameters.createdAt) {
            is StringOrInt.Str -> c.value
            is StringOrInt.IntVal -> c.value
            else -> null
        }
        val transformedParams = TransformedShortIOParametersModel(
            originalURL = parameters.originalURL, cloaking = parameters.cloaking, password = parameters.password, redirectType = parameters.redirectType,
            expiresAt = rawExpiresAt, expiredURL = parameters.expiredURL, title = parameters.title, tags = parameters.tags, utmSource = parameters.utmSource,
            utmMedium = parameters.utmMedium, utmCampaign = parameters.utmCampaign, utmTerm = parameters.utmTerm, utmContent = parameters.utmContent, ttl = rawTtl,
            path = parameters.path, androidURL = parameters.androidURL, iphoneURL = parameters.iphoneURL, createdAt = rawCreatedAt, clicksLimit = parameters.clicksLimit,
            passwordContact = parameters.passwordContact, skipQS = parameters.skipQS, archived = parameters.archived, splitURL = parameters.splitURL, splitPercent = parameters.splitPercent,
            integrationAdroll = parameters.integrationAdroll, integrationFB = parameters.integrationFB, integrationGA = parameters.integrationGA, integrationGTM = parameters.integrationGTM,
            domain = parameters.domain, folderId = parameters.folderId
        )
        val jsonBody = gson.toJson(transformedParams)
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
