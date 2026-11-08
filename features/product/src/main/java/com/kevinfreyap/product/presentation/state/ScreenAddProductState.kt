package com.kevinfreyap.product.presentation.state

import com.kevinfreyap.product.domain.model.error.ProductFormError
import com.kevinfreyap.ui.state.UiState

data class ScreenAddProductState(
    // Product Details
    val productDetail: ProductFormDetailState = ProductFormDetailState(),
    val productIdentification: ProductFormIdentificationState = ProductFormIdentificationState(),

    // Stock Batch Details
    val batchDetail: BatchFormDetailState = BatchFormDetailState(),
    val batchInformation: BatchFormInformationState = BatchFormInformationState(),

    // Barcode Lookup Status
    val isSearchingBarcode: Boolean = false,
    val barcodeSearchStatusMessage: String? = null,
    val barcodeSearchSuccess: Boolean? = null,

    // UI Status
    val formErrors: ProductFormError? = null,
    val uiState: UiState<Unit> = UiState.Idle,
    val showSummaryConfirmationDialog: Boolean = false,
) {
    val hasUnsavedChanges: Boolean
        get() = productDetail.productName.isNotBlank() ||
                productDetail.productCategoryName.isNotBlank() ||
                productDetail.productDescription?.isNotBlank() == true ||
                productDetail.brand?.isNotBlank() == true ||
                productDetail.purchasePrice.isNotBlank() ||
                productDetail.sellingPrice.isNotBlank() ||
                (productDetail.productMinQuantity.isNotBlank() && productDetail.productMinQuantity != "0") ||
                productDetail.productImageUriString?.isNotBlank() == true ||
                productIdentification.productSku?.isNotBlank() == true ||
                productIdentification.productBarcode?.isNotBlank() == true ||
                (batchDetail.batchQuantity.isNotBlank() && batchDetail.batchQuantity != "0") ||
                batchDetail.batchLocation.isNotBlank() ||
                batchDetail.batchPrice.isNotBlank() ||
                batchInformation.batchExpirationTextFormatted?.isNotBlank() == true ||
                batchInformation.batchSupplier?.isNotBlank() == true

    val isSavedEnabled: Boolean
        get() {
            val isProductValid = productDetail.productName.isNotBlank() &&
                                 formErrors?.hasAnyError != true

            val isInitialStockValid = if (batchDetail.addInitialStock) {
                batchDetail.batchQuantity.isNotBlank() && batchDetail.batchQuantity != "0" &&
                batchDetail.batchLocation.isNotBlank()
            } else {
                true
            }

            return isProductValid && isInitialStockValid
        }
}
