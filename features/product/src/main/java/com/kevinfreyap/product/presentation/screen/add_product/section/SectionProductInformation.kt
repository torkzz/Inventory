package com.kevinfreyap.product.presentation.screen.add_product.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.InventoryBarcode
import com.kevinfreyap.product.R
import com.kevinfreyap.product.domain.model.error.ProductBarcodeError
import com.kevinfreyap.product.domain.model.error.ProductCategoryError
import com.kevinfreyap.product.domain.model.error.ProductDescriptionError
import com.kevinfreyap.product.domain.model.error.ProductFormError
import com.kevinfreyap.product.domain.model.error.ProductNameError
import com.kevinfreyap.product.presentation.action.ProductFormAction
import com.kevinfreyap.product.presentation.components.FieldListHeader
import com.kevinfreyap.product.presentation.state.ProductFormDetailState
import com.kevinfreyap.ui.R as coreR
import com.kevinfreyap.ui.components.AppDropdownField
import com.kevinfreyap.ui.components.AppTextField
import com.kevinfreyap.ui.theme.NeonCyan
import com.kevinfreyap.ui.theme.Theme

@Composable
fun SectionProductInformation(
    productDetailState: ProductFormDetailState,
    formErrors: ProductFormError?,
    onAction: (ProductFormAction) -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    barcode: String? = null,
    onBarcodeIconClicked: () -> Unit = {}
) {
    FieldListHeader(
        title = stringResource(R.string.label_product_detail),
        modifier = modifier,
        fieldsColumn = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. PRODUCT NAME
                AppTextField(
                    value = productDetailState.productName,
                    onValueChange = { name ->
                        onAction(ProductFormAction.ProductDetailAction.OnNameChanged(name))
                    },
                    label = stringResource(R.string.label_field_product_name),
                    placeholder = "Sterling Notebook",
                    minLines = 1,
                    maxLines = 1,
                    unfocusedColor = Theme.custom.hint,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Sentences,
                    isError = formErrors?.nameError != null,
                    errorMessage = if (formErrors?.nameError != null) {
                        when (formErrors.nameError) {
                            ProductNameError.EMPTY -> stringResource(R.string.error_product_name_empty)
                            ProductNameError.TOO_LONG -> stringResource(R.string.error_product_name_too_long)
                            ProductNameError.CONTAINS_NEWLINE -> stringResource(R.string.error_product_name_contains_newline)
                        }
                    } else null
                )

                // 2. INITIAL STOCK (Add) vs CURRENT STOCK (Edit read-only)
                if (isEditMode) {
                    val currentStockDisplay = "${productDetailState.standaloneQuantity} ${productDetailState.unit.ifBlank { "pcs" }}"
                    AppTextField(
                        value = currentStockDisplay,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = "Current Stock",
                        minLines = 1,
                        maxLines = 1,
                        unfocusedColor = Theme.custom.hint,
                        disabledTextColor = NeonCyan
                    )
                } else {
                    AppTextField(
                        value = productDetailState.standaloneQuantity,
                        onValueChange = { qty ->
                            val onlyDigit = qty.filter { it.isDigit() }
                            onAction(ProductFormAction.ProductDetailAction.OnStandaloneQuantityChanged(onlyDigit))
                        },
                        label = "Initial Stock",
                        placeholder = "0",
                        minLines = 1,
                        maxLines = 1,
                        unfocusedColor = Theme.custom.hint,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                }

                // 3. BRAND & UNIT
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppTextField(
                        value = productDetailState.brand ?: "",
                        onValueChange = { brand ->
                            onAction(ProductFormAction.ProductDetailAction.OnBrandChanged(brand))
                        },
                        label = "Brand (Optional)",
                        placeholder = "Optional",
                        minLines = 1,
                        maxLines = 1,
                        unfocusedColor = Theme.custom.hint,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.weight(0.6f)
                    )

                    AppTextField(
                        value = productDetailState.unit,
                        onValueChange = { unit ->
                            onAction(ProductFormAction.ProductDetailAction.OnUnitChanged(unit))
                        },
                        label = "Unit",
                        placeholder = "pcs",
                        minLines = 1,
                        maxLines = 1,
                        unfocusedColor = Theme.custom.hint,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.weight(0.4f)
                    )
                }

                // 4. SELLING PRICE & PURCHASE PRICE (PHP)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppTextField(
                        value = productDetailState.sellingPrice,
                        onValueChange = { price ->
                            onAction(ProductFormAction.ProductDetailAction.OnSellingPriceChanged(price))
                        },
                        label = "Selling Price (₱)",
                        placeholder = "₱0.00",
                        minLines = 1,
                        maxLines = 1,
                        unfocusedColor = Theme.custom.hint,
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.weight(0.5f)
                    )

                    AppTextField(
                        value = productDetailState.purchasePrice,
                        onValueChange = { price ->
                            onAction(ProductFormAction.ProductDetailAction.OnPurchasePriceChanged(price))
                        },
                        label = "Purchase Price (₱)",
                        placeholder = "₱0.00",
                        minLines = 1,
                        maxLines = 1,
                        unfocusedColor = Theme.custom.hint,
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.weight(0.5f)
                    )
                }

                // 5. CATEGORY
                AppDropdownField(
                    value = productDetailState.productCategoryName,
                    label = "Category",
                    floatingLabel = true,
                    options = productDetailState.filteredCategories,
                    optionText = { it },
                    onSearchQueryChange = { typedString ->
                        onAction(ProductFormAction.ProductDetailAction.OnCategoryChanged(typedString))
                    },
                    onOptionSelected = { selectedCategory ->
                        onAction(ProductFormAction.ProductDetailAction.OnCategoryChanged(selectedCategory))
                    },
                    unfocusedColor = Theme.custom.hint,
                    enableAddNew = true,
                    onAddNewOption = {
                        onAction(ProductFormAction.ProductDetailAction.OnCreateNewCategory(productDetailState.productCategoryName))
                    },
                    addNewText = stringResource(R.string.dropdown_new_category, productDetailState.productCategoryName),
                    isError = formErrors?.categoryError != null,
                    errorMessage = if (formErrors?.categoryError != null) {
                        when (formErrors.categoryError) {
                            ProductCategoryError.EMPTY -> null
                            ProductCategoryError.TOO_LONG -> stringResource(R.string.error_product_category_too_long)
                            ProductCategoryError.CONTAINS_NEWLINE -> stringResource(R.string.error_product_category_contains_newline)
                        }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                // 6. DESCRIPTION
                AppTextField(
                    value = productDetailState.productDescription ?: "",
                    onValueChange = { text ->
                        onAction(ProductFormAction.ProductDetailAction.OnDescriptionChanged(text))
                    },
                    label = "Description (Optional)",
                    placeholder = "Optional",
                    minLines = 2,
                    maxLines = 3,
                    unfocusedColor = Theme.custom.hint,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Sentences,
                    modifier = Modifier
                        .nestedScroll(rememberNestedScrollInteropConnection()),
                    isError = formErrors?.descriptionError != null,
                    errorMessage = if (formErrors?.descriptionError != null) {
                        when (formErrors.descriptionError) {
                            ProductDescriptionError.TOO_LONG -> stringResource(R.string.error_product_description_too_long)
                        }
                    } else null
                )

                // 7. BARCODE (Integrated into product form, without SKU)
                AppTextField(
                    value = barcode ?: "",
                    onValueChange = { newBarcode ->
                        val sanitized = newBarcode.replace(" ", "")
                        onAction(
                            ProductFormAction.ProductIdentificationAction.OnBarcodeChanged(
                                InventoryBarcode(
                                    value = sanitized,
                                    format = "CODE_128"
                                )
                            )
                        )
                    },
                    label = "Barcode",
                    placeholder = "Scan or enter barcode",
                    minLines = 1,
                    maxLines = 1,
                    unfocusedColor = Theme.custom.hint,
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Ascii,
                    isError = formErrors?.barcodeError != null,
                    errorMessage = if (formErrors?.barcodeError != null) {
                        when (formErrors.barcodeError) {
                            ProductBarcodeError.TOO_LONG -> stringResource(R.string.error_product_barcode_too_long)
                            ProductBarcodeError.INVALID_CHARACTER -> stringResource(R.string.error_product_barcode_invalid_characters)
                            ProductBarcodeError.CONTAINS_WHITESPACE -> stringResource(R.string.error_product_barcode_contains_spaces)
                            ProductBarcodeError.ALREADY_EXISTS -> stringResource(R.string.error_product_barcode_already_exists)
                        }
                    } else null,
                    trailingIcon = {
                        IconButton(
                            onClick = onBarcodeIconClicked,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(coreR.drawable.app_barcode_scanner),
                                contentDescription = "Barcode Scanner"
                            )
                        }
                    }
                )
            }
        }
    )
}
