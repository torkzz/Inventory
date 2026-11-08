package com.kevinfreyap.domain.repository

import com.kevinfreyap.domain.Result
import com.kevinfreyap.domain.error.NetworkError

interface IAuthRepository {
    suspend fun localAccess(): Result<Unit, NetworkError>
    suspend fun register(name: String, email: String, pass: String, confirmPass: String): Result<Unit, NetworkError>
    suspend fun login(email: String, pass: String): Result<Unit, NetworkError>
    suspend fun logout(): Result<Unit, NetworkError>

    suspend fun isUserLoggedIn(): Boolean
}
