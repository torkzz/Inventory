package com.kevinfreyap.network.client

import com.kevinfreyap.domain.manager.IAuthTokenManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.takeFrom
import com.kevinfreyap.network.BuildConfig
import com.kevinfreyap.network.dto.request.RefreshTokenRequest
import com.kevinfreyap.network.dto.response.TokenResponse
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.post
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

fun provideKtorClient(
    tokenManager: IAuthTokenManager
): HttpClient {
    lateinit var client: HttpClient

    client = HttpClient(OkHttp) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
        }

        install(Auth) {
            bearer {
                sendWithoutRequest { true }

                loadTokens {
                    val accessToken = tokenManager.fetchAccessToken()
                    val refreshToken = tokenManager.fetchRefreshToken()

                    if (accessToken != null && refreshToken != null) {
                        BearerTokens(accessToken, refreshToken)
                    } else {
                        null
                    }
                }

                refreshTokens {
                    val refreshToken = tokenManager.fetchRefreshToken() ?: return@refreshTokens null

                    try {
                        val response: HttpResponse = client.post("/auth/refresh") {
                            markAsRefreshTokenRequest()
                            contentType(ContentType.Application.Json)
                            setBody(RefreshTokenRequest(refreshToken = refreshToken))
                        }

                        val tokenResponse = response.body<TokenResponse>()

                        tokenManager.saveToken(
                            accessToken = tokenResponse.accessToken,
                            refreshToken = tokenResponse.refreshToken
                        )

                        BearerTokens(tokenResponse.accessToken, tokenResponse.refreshToken)
                    } catch (e: ResponseException) {
                        val status = e.response.status

                        if (status == HttpStatusCode.Unauthorized || status == HttpStatusCode.BadRequest) {
                            tokenManager.clearToken()
                        }

                        return@refreshTokens null
                    } catch (_: Exception) {
                        null
                    }
                }
            }
        }

        install(Resources)

        install(WebSockets) {
            pingInterval = 20.seconds
        }

        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.INFO
        }

        defaultRequest {
            url {
                takeFrom(BuildConfig.BASE_URL)
            }
        }
    }

    CoroutineScope(Dispatchers.Main.immediate).launch {
        tokenManager.tokenState.collect { token ->
            if (token == null){
                client.authProvider<BearerAuthProvider>()?.clearToken()
            }
        }
    }

    return client
}