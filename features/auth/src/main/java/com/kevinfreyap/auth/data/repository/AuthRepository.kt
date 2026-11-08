package com.kevinfreyap.auth.data.repository

import com.kevinfreyap.auth.data.mapper.toEntity

import com.kevinfreyap.auth.data.network.dto.request.LoginRequest
import com.kevinfreyap.auth.data.network.dto.request.LogoutRequest
import com.kevinfreyap.auth.data.network.dto.request.RegisterRequest
import com.kevinfreyap.auth.data.network.dto.response.AuthResponse
import com.kevinfreyap.auth.data.network.service.AuthApiService
import com.kevinfreyap.domain.repository.IAuthRepository
import com.kevinfreyap.database.dao.GroupDao
import com.kevinfreyap.database.dao.UserDao
import com.kevinfreyap.domain.Result
import com.kevinfreyap.domain.manager.IAuthTokenManager
import com.kevinfreyap.domain.error.NetworkError
import com.kevinfreyap.domain.preferences.IDevicePreferences
import com.kevinfreyap.domain.session.IActiveGroupSession
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.io.IOException
import model.MembershipStatus
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApiService,
    private val userDao: UserDao,
    private val groupDao: GroupDao,
    private val tokenManager: IAuthTokenManager,
    private val activeGroupSession: IActiveGroupSession,
    private val devicePreferences: IDevicePreferences
): IAuthRepository {
    override suspend fun localAccess(): Result<Unit, NetworkError> {
        return try {
            val response = api.localAccess()
            handleAuthSuccess(response)
            Result.Success(Unit)
        } catch (e: ResponseException) {
            val status = e.response.status.value
            val serverMessage = e.response.bodyAsText()

            if (status in 500..599 || serverMessage.contains("<html", ignoreCase = true)) {
                Result.Error(NetworkError.Local.SERVER_DOWN)
            } else {
                Result.Error(NetworkError.Server(serverMessage))
            }

        } catch (_: IOException) {
            Result.Error(NetworkError.Local.NO_INTERNET)

        } catch (_: Exception) {
            Result.Error(NetworkError.Local.UNKNOWN)
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        pass: String,
        confirmPass: String
    ): Result<Unit, NetworkError> {
        return try {
            val response = api.register(
                RegisterRequest(
                    name = name,
                    email = email,
                    password = pass,
                    confirmPassword = confirmPass
                )
            )

            handleAuthSuccess(response)

            Result.Success(Unit)
        } catch (e: ResponseException) {
            val status = e.response.status.value
            val serverMessage = e.response.bodyAsText()

            if (status in 500..599 || serverMessage.contains("<html", ignoreCase = true)) {
                Result.Error(NetworkError.Local.SERVER_DOWN)
            } else {
                Result.Error(NetworkError.Server(serverMessage))
            }

        } catch (_: IOException) {
            Result.Error(NetworkError.Local.NO_INTERNET)

        } catch (_: Exception) {
            Result.Error(NetworkError.Local.UNKNOWN)
        }
    }

    override suspend fun login(
        email: String,
        pass: String
    ): Result<Unit, NetworkError> {
        return try {
            val response = api.login(
                LoginRequest(
                    email = email,
                    password = pass
                )
            )

            handleAuthSuccess(response)

            Result.Success(Unit)
        } catch (e: ResponseException) {
            val status = e.response.status.value
            val serverMessage = e.response.bodyAsText()

            if (status in 501..599 || serverMessage.contains("<html", ignoreCase = true)) {
                Result.Error(NetworkError.Local.SERVER_DOWN)
            } else {
                Result.Error(NetworkError.Server(serverMessage))
            }

        } catch (_: IOException) {
            Result.Error(NetworkError.Local.NO_INTERNET)

        } catch (_: Exception) {
            Result.Error(NetworkError.Local.UNKNOWN)
        }
    }

    override suspend fun logout(): Result<Unit, NetworkError> {
        return try {
            val refreshToken = tokenManager.fetchRefreshToken()

            if (refreshToken != null) {
                try {
                    api.logout(LogoutRequest(refreshToken = refreshToken))
                } catch (_: Exception) { }
            }

            tokenManager.clearToken()

            Result.Success(Unit)
        } catch (e: ResponseException) {
            val serverMessage = e.response.bodyAsText()
            Result.Error(NetworkError.Server(serverMessage))

        } catch (_: IOException) {
            Result.Error(NetworkError.Local.NO_INTERNET)

        } catch (_: Exception) {
            Result.Error(NetworkError.Local.UNKNOWN)
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return tokenManager.awaitRefreshToken() != null
    }

    private suspend fun handleAuthSuccess(response: AuthResponse) {
        tokenManager.saveToken(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken
        )

        userDao.insertOrUpdate(response.user.toEntity())

        if (response.groups.isNotEmpty()) {
            groupDao.insertOrUpdateGroups(response.groups.map { it.toEntity() })

            val lastGroupId = devicePreferences.getLastActiveGroup(response.user.userId)
            val activeGroup = response.groups.find { it.group.groupId == lastGroupId }
            val isActiveGroupValid =
                lastGroupId != null &&
                        activeGroup != null &&
                        MembershipStatus.valueOf(activeGroup.status) == MembershipStatus.ACTIVE

            if (isActiveGroupValid) {
                activeGroupSession.setActiveGroup(lastGroupId)
            } else {
                val fallbackGroup = response.groups.find {
                    MembershipStatus.valueOf(it.status) == MembershipStatus.ACTIVE
                }

                if (fallbackGroup != null) {
                    val newGroupId = fallbackGroup.group.groupId
                    activeGroupSession.setActiveGroup(newGroupId)
                    devicePreferences.saveLastActiveGroup(
                        userId = response.user.userId,
                        groupId = newGroupId
                    )
                } else {
                    activeGroupSession.clear()
                }
            }
        } else {
            activeGroupSession.clear()
        }
    }
}