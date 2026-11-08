package com.kevinfreyap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.ui.R
import com.kevinfreyap.ui.theme.CyberBorder
import com.kevinfreyap.ui.theme.InventoryTheme
import com.kevinfreyap.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AppDropdownField(
    value: String,
    label: String,
    options: List<T>,
    optionText: (T) -> String,
    onSearchQueryChange: (String) -> Unit,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    unfocusedColor: Color = Theme.custom.hint,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    floatingLabel: Boolean = true,
    enableAddNew: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    onAddNewOption: (() -> Unit)? = null,
    addNewText: String? = null,
    customTrailingIcon: @Composable (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    val isMenuVisible = if (readOnly) {
        expanded
    } else {
        expanded && value.isNotBlank()
    }

    ExposedDropdownMenuBox(
        expanded = if (enabled) isMenuVisible else false,
        onExpandedChange = { newExpand ->
            if (enabled) {
                expanded = newExpand
            }
        },
        modifier = modifier
    ) {
        AppTextField(
            value = value,
            onValueChange = { newValue ->
                onSearchQueryChange(newValue)
                if (!readOnly) {
                    expanded = true
                }
            },
            label = if (floatingLabel) {
                label
            } else null,
            placeholder = if (!floatingLabel){
                label
            } else null,
            modifier = Modifier.menuAnchor(
                type = if (readOnly) {
                    ExposedDropdownMenuAnchorType.PrimaryNotEditable
                } else {
                    ExposedDropdownMenuAnchorType.PrimaryEditable
                },
                enabled = enabled
            ),
            trailingIcon = {
                if (customTrailingIcon != null) {
                    customTrailingIcon()
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMenuVisible)
                }
            },
            unfocusedColor = unfocusedColor,
            readOnly = readOnly,
            enabled = enabled,
            isError = isError,
            errorMessage = errorMessage
        )

        ExposedDropdownMenu (
            expanded = if (enabled) isMenuVisible else false,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .heightIn(max = 200.dp)
                .background(Color.Transparent)
        ) {
            if (options.isNotEmpty()) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionText(option)) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            } else {
                if (enableAddNew) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = addNewText ?: stringResource(R.string.dropdown_item_add_new, value),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.add_24),
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        onClick = {
                            onAddNewOption?.invoke()
                            expanded = false
                        }
                    )
                } else {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.dropdown_item_no_result_found),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        onClick = {},
                        enabled = false
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun AppDropdownFieldPreview() {

    InventoryTheme {
        var previewValue by remember { mutableStateOf("") }

        val mockOptions = listOf("Electronics", "Clothing", "Food", "Furniture")

        val filteredOptions = if (previewValue.isBlank()) {
            mockOptions
        } else {
            mockOptions.filter { it.contains(previewValue, ignoreCase = true) }
        }

        AppDropdownField(
            value = previewValue,
            label = "Category",
            options = filteredOptions,
            optionText = { it },
            onSearchQueryChange = { newValue -> previewValue = newValue },
            onOptionSelected = { selectedOption -> previewValue = selectedOption },
            modifier = Modifier.padding(8.dp),
            unfocusedColor = Theme.custom.primaryText
        )
    }
}