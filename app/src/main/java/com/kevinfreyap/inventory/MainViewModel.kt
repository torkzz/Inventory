package com.kevinfreyap.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.database.dao.GroupDao
import com.kevinfreyap.database.dao.UserDao
import com.kevinfreyap.database.entity.GroupEntity
import com.kevinfreyap.database.entity.UserEntity
import com.kevinfreyap.domain.manager.IAuthTokenManager
import com.kevinfreyap.domain.repository.IAuthRepository
import com.kevinfreyap.domain.session.IActiveGroupSession
import com.kevinfreyap.product.domain.usecase.ObserveRealTimeUpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    tokenManager: IAuthTokenManager,
    private val authRepository: IAuthRepository,
    private val activeGroupSession: IActiveGroupSession,
    private val groupDao: GroupDao,
    private val userDao: UserDao,
    private val observerRealTimeUpdate: ObserveRealTimeUpdateUseCase
): ViewModel() {

    val isLoading: StateFlow<Boolean> = tokenManager.tokenState
        .map { false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val isLoggedIn: StateFlow<Boolean> = tokenManager.tokenState
        .map { token -> !token.isNullOrBlank() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = tokenManager.fetchAccessToken() != null
        )

    init {
        viewModelScope.launch {
            val currentGroup = activeGroupSession.currentGroupId.firstOrNull()
            val defaultGroupId = "default-grocery-group"
            val timeStamp = System.currentTimeMillis()

            groupDao.insertOrUpdateGroups(
                listOf(
                    GroupEntity(
                        groupId = defaultGroupId,
                        name = "Local Grocery Store",
                        description = "Main grocery inventory",
                        address = "Local Store",
                        currentUserRole = "OWNER",
                        currentUserStatus = "ACTIVE",
                        totalMembers = 1,
                        totalProducts = 0,
                        createdAt = timeStamp,
                        lastUpdated = timeStamp
                    )
                )
            )

            userDao.insertOrUpdate(
                UserEntity(
                    userId = "user-local-pos",
                    name = "Grocery Manager",
                    email = "pos@local.inventory",
                    profilePictureUrl = null,
                    phoneNumber = null,
                    jobTitle = "Store Manager",
                    locale = null,
                    timeZone = null,
                    createdAt = timeStamp,
                    lastUpdated = timeStamp
                )
            )

            if (currentGroup.isNullOrBlank()) {
                activeGroupSession.setActiveGroup(defaultGroupId)
            }

            if (tokenManager.fetchAccessToken() == null) {
                try {
                    authRepository.localAccess()
                } catch (_: Exception) {}
            }
        }
    }

    suspend fun startListeningForSync() {
        observerRealTimeUpdate()
    }
}
