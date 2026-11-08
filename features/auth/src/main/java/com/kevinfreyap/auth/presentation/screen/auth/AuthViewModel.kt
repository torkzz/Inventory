package com.kevinfreyap.auth.presentation.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.auth.R

import com.kevinfreyap.auth.domain.usecase.LoginUseCase
import com.kevinfreyap.auth.domain.usecase.RegisterUseCase
import com.kevinfreyap.auth.presentation.action.AuthAction
import com.kevinfreyap.auth.presentation.navigation.AuthRoute
import com.kevinfreyap.auth.presentation.navigation.LoginNavigation
import com.kevinfreyap.auth.presentation.navigation.RegisterNavigation
import com.kevinfreyap.auth.presentation.state.ScreenAuthState
import com.kevinfreyap.domain.Result
import com.kevinfreyap.domain.error.NetworkError
import com.kevinfreyap.ui.event.UiEvent
import com.kevinfreyap.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val register: RegisterUseCase,
    private val login: LoginUseCase
): ViewModel() {
    private val _authState = MutableStateFlow(ScreenAuthState())
    val authState = _authState.asStateFlow()

    private val _uiDialogEvent = Channel<UiEvent<Unit>>()
    val uiDialogEvent = _uiDialogEvent.receiveAsFlow()

    private val _uiEvent = Channel<UiEvent<AuthRoute>>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onAction(action: AuthAction) {
        when(action) {
            is AuthAction.CredentialAction -> handleCredentialAction(action)
            is AuthAction.Register -> handleRegister()
            is AuthAction.SignIn -> handleLogin()
            is AuthAction.SendResetEmail -> handleSendResetEmail()
        }
    }

    private fun handleCredentialAction(action: AuthAction.CredentialAction) {
        _authState.update { currentState ->
            when(action) {
                is AuthAction.CredentialAction.OnNameChanged -> {
                    currentState.copy(
                        name = action.name,
                        nameError = null
                    )
                }
                is AuthAction.CredentialAction.OnEmailChanged -> {
                    currentState.copy(
                        email = action.email,
                        emailError = null
                    )
                }
                is AuthAction.CredentialAction.OnPasswordChanged -> {
                    currentState.copy(
                        password = action.password,
                        passwordError = null
                    )
                }
                is AuthAction.CredentialAction.OnConfirmPasswordChanged -> {
                    currentState.copy(
                        confirmPassword = action.confirmPass,
                        confirmError = null
                    )
                }
                is AuthAction.CredentialAction.OnResetEmailChanged -> {
                    currentState.copy(
                        resetEmail = action.resetEmail,
                        resetEmailError = null
                    )
                }
            }
        }
    }

    private fun handleSendResetEmail() {
        viewModelScope.launch {
            _uiDialogEvent.send(UiEvent.ShowToast(R.string.success_reset_email_sent))
        }
    }

    private fun handleRegister() {
        if (_authState.value.uiState is UiState.Loading) return

        _authState.update {
            it.copy(
                nameError = null,
                emailError = null,
                passwordError = null,
                confirmError = null,
                uiState = UiState.Loading
            )
        }

        viewModelScope.launch {
            try {
                val currentState = _authState.value

                val result = register(
                    name = currentState.name,
                    email = currentState.email,
                    pass = currentState.password,
                    confirmPass = currentState.confirmPassword
                )

                when(result) {
                    is Result.Success -> {
                        _authState.update {
                            it.copy(
                                uiState = UiState.Success(Unit)
                            )
                        }

                        _uiEvent.send(UiEvent.ShowToast(R.string.success_account_created))
                        _uiEvent.send(UiEvent.Navigate(RegisterNavigation.Dashboard))

                        _authState.update { ScreenAuthState() }
                    }
                    is Result.Error -> {
                        val errors = result.error

                        _authState.update {
                            it.copy(
                                nameError = errors.nameError,
                                emailError = errors.emailError,
                                passwordError = errors.passError,
                                confirmError = errors.confirmPassError,
                                uiState = UiState.Idle
                            )
                        }

                        if (errors.networkError != null) {
                            when(errors.networkError) {
                                NetworkError.Local.NO_INTERNET -> _uiEvent.send(UiEvent.ShowToast(R.string.error_network_no_connection))
                                NetworkError.Local.UNKNOWN -> _uiEvent.send(UiEvent.ShowToast(R.string.error_network_unknown))
                                NetworkError.Local.SERVER_DOWN -> _uiEvent.send(UiEvent.ShowToast(R.string.error_network_server_down))
                                is NetworkError.Server ->  _uiEvent.send(UiEvent.ShowToast(errors.networkError.message))
                                else -> {}
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _authState.update {
                    it.copy(
                        uiState = UiState.Error(
                            message = e.localizedMessage ?: "Something went wrong"
                        )
                    )
                }
            }
        }
    }

    private fun handleLogin() {
        if (_authState.value.uiState is UiState.Loading) return

        _authState.update {
            it.copy(
                emailError = null,
                passwordError = null,
                uiState = UiState.Loading
            )
        }

        viewModelScope.launch {
            try {
                val currentState = _authState.value

                val result = login(
                    email = currentState.email,
                    pass = currentState.password
                )

                when(result) {
                    is Result.Success -> {
                        _authState.update {
                            it.copy(
                                uiState = UiState.Success(Unit)
                            )
                        }

                        _uiEvent.send(UiEvent.ShowToast(R.string.success_login))
                        _uiEvent.send(UiEvent.Navigate(LoginNavigation.Dashboard))

                        _authState.update { ScreenAuthState() }
                    }
                    is Result.Error -> {
                        val errors = result.error

                        _authState.update {
                            it.copy(
                                emailError = errors.emailError,
                                passwordError = errors.passError,
                                uiState = UiState.Idle
                            )
                        }

                        if (errors.networkError != null) {
                            when(errors.networkError) {
                                NetworkError.Local.NO_INTERNET -> _uiEvent.send(UiEvent.ShowToast(R.string.error_network_no_connection))
                                NetworkError.Local.UNKNOWN -> _uiEvent.send(UiEvent.ShowToast(R.string.error_network_unknown))
                                NetworkError.Local.SERVER_DOWN -> _uiEvent.send(UiEvent.ShowToast(R.string.error_network_server_down))
                                is NetworkError.Server ->  _uiEvent.send(UiEvent.ShowToast(errors.networkError.message))
                                else -> {}
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _authState.update {
                    it.copy(
                        uiState = UiState.Error(
                            message = e.localizedMessage ?: "Something went wrong"
                        )
                    )
                }
            }
        }
    }


}