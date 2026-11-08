package com.kevinfreyap.product.presentation.state

import com.kevinfreyap.product.domain.model.error.ProductFormError
import com.kevinfreyap.ui.state.UiState

data class ScreenEditProductState(
    // Initial Details
    val originalProductDetail: ProductFormDetailState = ProductFormDetailState(),
    val originalProductIdentification: ProductFormIdentificationState = ProductFormIdentificationState(),

    // Product Details
    val productDetail: ProductFormDetailState = ProductFormDetailState(),
    val productIdentification: ProductFormIdentificationState = ProductFormIdentificationState(),

    // UI Status
    val formErrors: ProductFormError? = null,
    val uiState: UiState<Unit> = UiState.Idle,
    val showSummaryConfirmationDialog: Boolean = false,

){
    val hasUnsavedChanges: Boolean
        get() = productDetail != originalProductDetail ||
                productIdentification != originalProductIdentification

    val isEditEnabled: Boolean
        get() = productDetail.productName.isNotBlank() &&
                formErrors?.hasAnyError != true &&
                hasUnsavedChanges
}
