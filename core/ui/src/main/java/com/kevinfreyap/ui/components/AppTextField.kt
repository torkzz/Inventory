package com.kevinfreyap.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.ui.theme.CyberBorder
import com.kevinfreyap.ui.theme.CyberCard
import com.kevinfreyap.ui.theme.InventoryTheme
import com.kevinfreyap.ui.theme.Theme

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = 1,
    unfocusedColor: Color = Theme.custom.hint,
    disabledTextColor: Color = Theme.custom.primaryText,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        trailingIcon = trailingIcon,
        label = if (label != null) {
            {
                Text(
                    text = label,
                    fontWeight = FontWeight.Medium,
                )
            }
        } else null,
        placeholder = if (placeholder != null) {
            {
                Text(
                    text = placeholder,
                    fontWeight = FontWeight.Medium,
                )
            }
        } else null,
        isError = isError,
        supportingText = errorMessage?.let {
            {
                Text(
                    text = it
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
            capitalization = capitalization
        ),
        keyboardActions = KeyboardActions (
            onDone = {
                focusManager.clearFocus()
            }
        ),
        minLines = minLines,
        maxLines = maxLines,
        readOnly = readOnly,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedPlaceholderColor = unfocusedColor.copy(0.6f),
            focusedPlaceholderColor = unfocusedColor.copy(0.6f),

            unfocusedBorderColor = CyberBorder,
            focusedBorderColor = MaterialTheme.colorScheme.primary,

            unfocusedLabelColor = unfocusedColor,
            focusedLabelColor = MaterialTheme.colorScheme.primary,

            disabledLabelColor = unfocusedColor,
            disabledBorderColor = unfocusedColor,
            disabledTrailingIconColor = unfocusedColor,
            disabledTextColor = disabledTextColor,

            focusedTextColor = Theme.custom.primaryText,

            unfocusedTrailingIconColor = unfocusedColor,
            focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            errorTrailingIconColor = MaterialTheme.colorScheme.error
        ),
        visualTransformation = visualTransformation,
        modifier = modifier
            .fillMaxWidth()
    )
}

@Composable
fun AppTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = 1,
    unfocusedColor: Color = Theme.custom.hint,
    readOnly: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        trailingIcon = trailingIcon,
        label = if (label != null) {
            {
                Text(
                    text = label,
                    fontWeight = FontWeight.Medium,
                )
            }
        } else null,
        placeholder = if (placeholder != null) {
            {
                Text(
                    text = placeholder,
                    fontWeight = FontWeight.Medium,
                )
            }
        } else null,
        isError = isError,
        supportingText = errorMessage?.let {
            {
                Text(
                    text = it
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
            capitalization = capitalization
        ),
        keyboardActions = KeyboardActions (
            onDone = {
                focusManager.clearFocus()
            }
        ),
        minLines = minLines,
        maxLines = maxLines,
        readOnly = readOnly,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = CyberBorder,
            focusedBorderColor = MaterialTheme.colorScheme.primary,

            unfocusedLabelColor = unfocusedColor,
            focusedLabelColor = MaterialTheme.colorScheme.primary,

            unfocusedTrailingIconColor = unfocusedColor,
            focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            errorTrailingIconColor = MaterialTheme.colorScheme.error
        ),
        visualTransformation = visualTransformation,
        modifier = modifier
            .fillMaxWidth()
    )
}

@Preview(
    showBackground = true
)
@Composable
fun AppTextFieldPreview() {
    var previewValue by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        focusManager.clearFocus()
    }

    InventoryTheme {
        AppTextField(
            value = previewValue,
            onValueChange = { newValue -> previewValue = newValue },
            label = "Product Name",
            isError = false,
            modifier = Modifier.padding(8.dp),
            minLines = 1,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
            trailingIcon = null
        )
    }
}