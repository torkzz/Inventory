package com.kevinfreyap.auth.presentation.screen.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.kevinfreyap.auth.R
import com.kevinfreyap.auth.domain.error.AuthConfirmPasswordError
import com.kevinfreyap.auth.domain.error.AuthNameError
import com.kevinfreyap.auth.presentation.action.AuthAction
import com.kevinfreyap.auth.presentation.components.AuthHeaderText
import com.kevinfreyap.auth.presentation.components.EmailPasswordInput
import com.kevinfreyap.auth.presentation.navigation.RegisterNavigation
import com.kevinfreyap.auth.presentation.state.ScreenAuthState
import com.kevinfreyap.ui.components.AppCenterTopBar
import com.kevinfreyap.ui.R as coreR
import com.kevinfreyap.ui.components.AppPrimaryButton
import com.kevinfreyap.ui.components.AppTextField
import com.kevinfreyap.ui.event.UiEvent
import com.kevinfreyap.ui.state.UiState
import com.kevinfreyap.ui.theme.InventoryTheme
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onNavigate: (RegisterNavigation) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val state by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.uiEvent, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvent.collect { event ->
                when(event) {
                    is UiEvent.Navigate -> {
                        when(val route = event.destination) {
                            is RegisterNavigation -> onNavigate(route)
                        }
                    }
                    is UiEvent.ShowToast -> {
                        Toast.makeText(
                            context,
                            event.asString(context),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    RegisterContent(
        state = state,
        onAction = viewModel::onAction,
        onNavigate = onNavigate,
        modifier = modifier
    )
}

@Composable
fun RegisterContent(
    state: ScreenAuthState,
    onAction: (AuthAction) -> Unit,
    onNavigate: (RegisterNavigation) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppCenterTopBar(
                title = "",
                onBackClick = {
                    onNavigate(RegisterNavigation.NavigateUp)
                },
                isLoading = state.uiState == UiState.Loading
            )
        },
        bottomBar = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.label_register_to_login))
                    append(" ")
                    withStyle(style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )) {
                        append(stringResource(R.string.text_btn_sign_in))
                    }
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onNavigate(RegisterNavigation.Login)
                    }
                    .padding(24.dp)
            )
        },
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    }
                )
            }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 16.dp
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AuthHeaderText(
                greeting = stringResource(R.string.label_register_greeting),
                headline = stringResource(R.string.label_register_headline),
                subtitle = stringResource(R.string.label_register_subtitle),
            )

            Spacer(Modifier.height(24.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(coreR.string.label_name),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                AppTextField(
                    value = state.name,
                    onValueChange = {
                        onAction(AuthAction.CredentialAction.OnNameChanged(it))
                    },
                    placeholder = stringResource(R.string.placeholder_name),
                    imeAction = ImeAction.Next,
                    isError = state.nameError != null,
                    errorMessage = if (state.nameError != null) {
                        when(state.nameError) {
                            AuthNameError.EMPTY -> stringResource(R.string.error_name_empty)
                            AuthNameError.TOO_LONG -> stringResource(R.string.error_name_too_long)
                            AuthNameError.CONTAINS_NEWLINE -> stringResource(R.string.error_name_contains_newline)
                        }
                    } else null
                )
            }

            EmailPasswordInput(
                state = state,
                onAction = onAction,
                lastFieldImeAction = ImeAction.Next,
                modifier = Modifier
                    .padding(vertical = 16.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_register_confirm_password),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                AppTextField(
                    value = state.confirmPassword,
                    onValueChange = {
                        onAction(
                            AuthAction.CredentialAction.OnConfirmPasswordChanged(it)
                        )
                    },
                    placeholder = stringResource(R.string.placeholder_confirm_password),
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        if (state.confirmPassword.isNotBlank()) {
                            val image = if (isPasswordVisible) {
                                painterResource(R.drawable.visibility_24)
                            } else {
                                painterResource(R.drawable.visibility_off_24)
                            }

                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    painter = image,
                                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        }
                    },
                    isError = state.confirmError != null,
                    errorMessage = if (state.confirmError != null) {
                        when(state.confirmError) {
                            AuthConfirmPasswordError.EMPTY -> stringResource(R.string.error_confirm_empty)
                            AuthConfirmPasswordError.DOES_NOT_MATCH -> stringResource(R.string.error_confirm_no_match)
                        }
                    } else null
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AppPrimaryButton(
                text = stringResource(R.string.btn_label_create_account),
                onClick = { 
                    onAction(AuthAction.Register)
                },
                cornerRadiusPercentage = 32,
                textAlign = TextAlign.Start,
                trailingIcon = {
                    Icon(
                        painter = painterResource(coreR.drawable.arrow_forward_24),
                        contentDescription = null,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1080px,height=2340px,dpi=416",
)
@Composable
fun RegisterScreenPreview() {
    InventoryTheme {
        RegisterContent(
            state = ScreenAuthState(),
            onAction = {},
            onNavigate = {},
        )
    }
}