package com.kevinfreyap.auth.data.network.service

import com.kevinfreyap.auth.data.network.dto.request.LoginRequest
import com.kevinfreyap.auth.data.network.dto.request.LogoutRequest
import com.kevinfreyap.auth.data.network.dto.request.RegisterRequest
import com.kevinfreyap.auth.data.network.dto.response.AuthResponse
import com.kevinfreyap.auth.data.network.resources.AuthResource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import javax.inject.Inject

class AuthApiService @Inject constructor(
    private val client: HttpClient
) {
    suspend fun login(request: LoginRequest): AuthResponse {
        return client.post(AuthResource.Login(
            parent = AuthResource()
        )) {
            setBody(request)
        }.body()
    }

    suspend fun localAccess(): AuthResponse {
        return client.post(AuthResource.Local(
            parent = AuthResource()
        )).body()
    }

    suspend fun register(request: RegisterRequest): AuthResponse {
        return client.post(AuthResource.Register(
            parent = AuthResource()
        )) {
            setBody(request)
        }.body()
    }

    suspend fun logout(request: LogoutRequest): String {
        return client.post(AuthResource.Logout(
            parent = AuthResource()
        )) {
            setBody(request)
        }.bodyAsText()
    }
}
