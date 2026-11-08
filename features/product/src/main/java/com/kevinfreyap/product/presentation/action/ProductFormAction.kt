package com.kevinfreyap.product.presentation.action

import com.kevinfreyap.domain.model.InventoryBarcode

sealed interface ProductFormAction {
    // Product Detail Actions
    sealed interface ProductDetailAction: ProductFormAction {
        data class OnImageUriChanged(val uri: String?): ProductDetailAction
        data class OnNameChanged(val name: String): ProductDetailAction
        data class OnCategoryChanged(val category: String): ProductDetailAction
        data class OnCreateNewCategory(val newCategory: String): ProductDetailAction
        data class OnDescriptionChanged(val desc: String?): ProductDetailAction
        data class OnMinQuantityChanged(val qty: String): ProductDetailAction
        data class OnBrandChanged(val brand: String?): ProductDetailAction
        data class OnUnitChanged(val unit: String): ProductDetailAction
        data class OnPurchasePriceChanged(val price: String): ProductDetailAction
        data class OnSellingPriceChanged(val price: String): ProductDetailAction
        data class OnStandaloneQuantityChanged(val qty: String): ProductDetailAction
    }

    // Product Image Actions
    sealed interface ImagePickerAction : ProductFormAction {
        data object OnUploadClick : ImagePickerAction
        data object OnDismissSheet : ImagePickerAction
        data object OnGalleryClick : ImagePickerAction
        data object OnCameraClick : ImagePickerAction
        data object OnRemoveImage: ImagePickerAction
        data object OnDismissRemoveDialog: ImagePickerAction
        data object OnConfirmRemoveDialog: ImagePickerAction
    }

    // Product Identification Actions
    sealed interface ProductIdentificationAction: ProductFormAction {
        data class OnBarcodeChanged(val barcode: InventoryBarcode?): ProductIdentificationAction
        data class OnSkuChanged(val sku: String?): ProductIdentificationAction
    }

    // Batch Quantity & Price Actions
    sealed interface BatchDetailAction: ProductFormAction {
        data class OnAddInitialStockToggled(val isChecked: Boolean): BatchDetailAction
        data class OnBatchQuantityChanged(val qty: String): BatchDetailAction
        data object OnQuantityIncremented: BatchDetailAction
        data object OnQuantityDecremented: BatchDetailAction
        data class OnBatchPriceChanged(val price: String): BatchDetailAction
        data class OnBatchLocationChanged(val loc: String): BatchDetailAction
        data class OnCreateNewLocation(val newLocName: String): BatchDetailAction
    }

    // Batch Logistic Actions
    sealed interface BatchInformationAction: ProductFormAction {
        data class OnBatchSupplierChanged(val supplier: String?): BatchInformationAction
        data class OnBatchExpirationChanged(val expString: String): BatchInformationAction
        data object OnOpenExpirationDialog: BatchInformationAction
        data object OnBatchExpirationConfirm: BatchInformationAction
    }

    sealed interface SummaryDialogAction: ProductFormAction {
        data object OnConfirmAllWarnings: SummaryDialogAction
        data object OnDismissWarningsDialog: SummaryDialogAction
    }

    sealed interface StatusDialogAction: ProductFormAction {
        data object OnDismissError: StatusDialogAction
    }

    sealed interface BatchToggleAction: ProductFormAction {
        data object ToggleEditMode: BatchToggleAction
    }

    // Submit
    data object Save: ProductFormAction
}