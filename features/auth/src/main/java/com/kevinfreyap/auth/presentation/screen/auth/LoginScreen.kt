package com.kevinfreyap.auth.presentation.screen.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.kevinfreyap.auth.presentation.action.AuthAction
import com.kevinfreyap.auth.presentation.components.AuthHeaderText
import com.kevinfreyap.auth.presentation.components.EmailPasswordInput
import com.kevinfreyap.auth.presentation.navigation.LoginNavigation
import com.kevinfreyap.auth.presentation.state.ScreenAuthState
import com.kevinfreyap.ui.components.AppCenterTopBar
import com.kevinfreyap.ui.R as coreR
import com.kevinfreyap.ui.components.AppPrimaryButton
import com.kevinfreyap.ui.event.UiEvent
import com.kevinfreyap.ui.state.UiState
import com.kevinfreyap.ui.theme.InventoryTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigate: (LoginNavigation) -> Unit,
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
                            is LoginNavigation -> onNavigate(route)
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

    LoginContent(
        state = state,
        onAction = viewModel::onAction,
        onNavigate = onNavigate,
        modifier = modifier
    )
}

@Composable
fun LoginContent(
    state: ScreenAuthState,
    onAction: (AuthAction) -> Unit,
    onNavigate: (LoginNavigation) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            AppCenterTopBar(
                title = "",
                onBackClick = {
                    onNavigate(LoginNavigation.NavigateUp)
                },
                isLoading = state.uiState == UiState.Loading
            )
        },
        bottomBar = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.label_login_to_register))
                    append(" ")
                    withStyle(style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )) {
                        append(stringResource(R.string.text_btn_create_an_account))
                    }
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onNavigate(LoginNavigation.Register)
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
                greeting = stringResource(R.string.label_login_greeting),
                headline = stringResource(R.string.label_login_headline),
                subtitle = stringResource(R.string.label_login_subtitle),
            )

            Spacer(Modifier.height(24.dp))

            EmailPasswordInput(
                state = state,
                onAction = onAction,
                lastFieldImeAction = ImeAction.Done
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppPrimaryButton(
                text = stringResource(R.string.btn_label_sign_in),
                onClick = {
                    onAction(AuthAction.SignIn)
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
    showBackground = true
)
@Composable
fun LoginScreenPreview() {
    InventoryTheme {
        LoginContent(
            state = ScreenAuthState(),
            onAction = {},
            onNavigate = {}
        )
    }
}