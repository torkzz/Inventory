package com.kevinfreyap.auth.presentation.action

sealed interface AuthAction {
    sealed interface CredentialAction: AuthAction {
        data class OnNameChanged(val name: String): CredentialAction
        data class OnEmailChanged(val email: String): CredentialAction
        data class OnPasswordChanged(val password: String): CredentialAction
        data class OnConfirmPasswordChanged(val confirmPass: String): CredentialAction
        data class OnResetEmailChanged(val resetEmail: String): CredentialAction
    }


    object SendResetEmail: AuthAction
    object SignIn: AuthAction
    object Register: AuthAction
}