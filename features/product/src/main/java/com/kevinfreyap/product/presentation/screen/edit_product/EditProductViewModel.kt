package com.kevinfreyap.product.presentation.screen.edit_product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kevinfreyap.domain.Result
import com.kevinfreyap.domain.error.SessionError
import com.kevinfreyap.domain.model.GroupId
import com.kevinfreyap.domain.session.IActiveGroupSession
import com.kevinfreyap.product.R
import com.kevinfreyap.product.domain.model.ProductId
import com.kevinfreyap.product.domain.model.error.DatabaseError
import com.kevinfreyap.product.domain.model.error.ProductMinimumQuantityError
import com.kevinfreyap.product.domain.usecase.GetAllCategoryUseCase
import com.kevinfreyap.product.domain.usecase.GetProductByIdUseCase
import com.kevinfreyap.product.domain.usecase.UpdateProductUseCase
import com.kevinfreyap.product.domain.usecase.ValidateProductCategoryUseCase
import com.kevinfreyap.product.presentation.action.ProductFormAction
import com.kevinfreyap.product.presentation.action.SharedFormAction.sharedProductDetailAction
import com.kevinfreyap.product.presentation.action.SharedFormAction.sharedProductIdentificationAction
import com.kevinfreyap.product.presentation.navigation.EditProductNavigation
import com.kevinfreyap.product.presentation.navigation.ProductScreen
import com.kevinfreyap.product.presentation.state.ScreenEditProductState
import com.kevinfreyap.product.presentation.state.SharedProductFormState
import com.kevinfreyap.product.presentation.util.resolveDisplayedImage
import com.kevinfreyap.ui.event.UiEvent
import com.kevinfreyap.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.filter
import kotlin.collections.map

@HiltViewModel
class EditProductViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getProductById: GetProductByIdUseCase,
    private val getAllCategories: GetAllCategoryUseCase,
    private val updateProduct: UpdateProductUseCase,
    private val validateCategory: ValidateProductCategoryUseCase,
    private val activeGroupSession: IActiveGroupSession
): ViewModel(){
    private val route = savedStateHandle.toRoute<ProductScreen.EditProduct>()

    private val productId = ProductId(route.productId)

    private val _uiEvent = Channel<UiEvent<EditProductNavigation>>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _formState = MutableStateFlow(ScreenEditProductState())
    val formState = _formState.asStateFlow()

    init {
        loadInitialData()
        loadCategories()
    }

    fun onAction(action : ProductFormAction) {
        when(action) {
            is ProductFormAction.ProductDetailAction -> handleProductDetailActions(action)
            is ProductFormAction.ProductIdentificationAction -> handleProductIdentification(action)
            is ProductFormAction.SummaryDialogAction -> handleSummaryDialog(action)
            is ProductFormAction.StatusDialogAction -> handleStatusDialog(action)
            is ProductFormAction.Save -> updateProduct()
            is ProductFormAction.ImagePickerAction -> Unit
            is ProductFormAction.BatchDetailAction -> Unit
            is ProductFormAction.BatchInformationAction -> Unit
            is ProductFormAction.BatchToggleAction -> Unit
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val product = getProductById(productId).firstOrNull()

            if (product == null) {
                _formState.update { currentState ->
                    currentState.copy(
                        uiState = UiState.Error("Product not found")
                    )
                }
                return@launch
            }

            val resolvedImage = withContext(Dispatchers.IO) {
                product.resolveDisplayedImage()
            }

            _formState.update { currentState ->
                val initialDetailState = currentState.productDetail.copy(
                    productImageUriString = resolvedImage,
                    productName = product.name,
                    brand = product.brand,
                    unit = product.unit,
                    purchasePrice = if (product.purchasePrice > 0.0) product.purchasePrice.toString() else "",
                    sellingPrice = if (product.sellingPrice > 0.0) product.sellingPrice.toString() else "",
                    standaloneQuantity = product.quantity.toString(),
                    productCategoryName = product.category?.name ?: "",
                    productDescription = product.description,
                    productMinQuantity = product.minimumQuantity.toString()
                )

                val initialIdentificationState = currentState.productIdentification.copy(
                    productBarcode = product.barcode,
                    productSku = product.sku
                )

                currentState.copy(
                    originalProductDetail = initialDetailState,
                    originalProductIdentification = initialIdentificationState,

                    productDetail = initialDetailState,
                    productIdentification = initialIdentificationState,

                    uiState = UiState.Idle
                )
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getAllCategories().collect { categories ->
                val categoryNames = categories.map { it.name }

                _formState.update { currentState ->
                    val productDetails = currentState.productDetail

                    currentState.copy(
                        productDetail = productDetails.copy(
                            allCategories = categoryNames,
                            filteredCategories = categoryNames.filter {
                                it.contains(productDetails.productCategoryName, ignoreCase = true)
                            }
                        )
                    )
                }
            }
        }
    }

    private inline fun updateSharedState(
        crossinline updater: (SharedProductFormState) -> SharedProductFormState
    ) {
        _formState.update { currentState ->
            // Pack
            val currentSharedState = SharedProductFormState(
                productDetail = currentState.productDetail,
                productIdentification = currentState.productIdentification,
                formErrors = currentState.formErrors
            )

            val updatedSharedState = updater(currentSharedState)

            // Unpack
            currentState.copy(
                productDetail = updatedSharedState.productDetail,
                productIdentification = updatedSharedState.productIdentification,
                formErrors = updatedSharedState.formErrors
            )
        }
    }

    private fun handleProductDetailActions(action: ProductFormAction.ProductDetailAction) {
        updateSharedState { currentShared ->
            sharedProductDetailAction(
                action = action,
                currentState = currentShared,
                validateCategory = validateCategory::invoke
            )
        }
    }

    private fun handleProductIdentification(action: ProductFormAction.ProductIdentificationAction) {
        updateSharedState { currentShared ->
            sharedProductIdentificationAction(
                action = action,
                currentState = currentShared,
            )
        }
    }

    private fun handleSummaryDialog(action: ProductFormAction.SummaryDialogAction) {
        when (action) {
            ProductFormAction.SummaryDialogAction.OnConfirmAllWarnings -> {
                updateSharedState { currentShared ->
                    currentShared.copy(
                        productDetail = currentShared.productDetail.copy(
                            isMinQuantityConfirmed = true
                        )
                    )
                }

                _formState.update { currentState ->
                    currentState.copy(showSummaryConfirmationDialog = false)
                }

                updateProduct()
            }

            ProductFormAction.SummaryDialogAction.OnDismissWarningsDialog -> {
                _formState.update { currentState ->
                    currentState.copy(showSummaryConfirmationDialog = false)
                }
            }
        }
    }

    private fun handleStatusDialog(action: ProductFormAction.StatusDialogAction) {
        when(action) {
            ProductFormAction.StatusDialogAction.OnDismissError -> {
                _formState.update { currentState ->
                    currentState.copy(uiState = UiState.Idle)
                }
            }
        }
    }

    private fun updateProduct() {
        if (_formState.value.uiState == UiState.Loading) return

        _formState.update {
            it.copy(
                formErrors = null,
                uiState = UiState.Loading
            )
        }

        viewModelScope.launch {
            val groupId = activeGroupSession.currentGroupId.first()
                ?: return@launch

            try {
                val currentState = _formState.value

                val purchasePriceDbl = currentState.productDetail.purchasePrice.toDoubleOrNull() ?: 0.0
                val sellingPriceDbl = currentState.productDetail.sellingPrice.toDoubleOrNull() ?: 0.0
                val qtyInt = currentState.productDetail.standaloneQuantity.toIntOrNull() ?: 0

                val result = updateProduct(
                    groupId = GroupId(groupId),
                    productId = productId,
                    productName = currentState.productDetail.productName,
                    productCategoryName = currentState.productDetail.productCategoryName,
                    productDescription = currentState.productDetail.productDescription,
                    productImageUriString = currentState.productDetail.productImageUriString,
                    productMinQuantity = currentState.productDetail.productMinQuantity,
                    isMinQuantityConfirmed = currentState.productDetail.isMinQuantityConfirmed,
                    productBarcode = currentState.productIdentification.productBarcode,
                    productBarcodeFormat = currentState.productIdentification.productBarcodeFormat,
                    productSku = currentState.productIdentification.productSku,
                    brand = currentState.productDetail.brand,
                    unit = currentState.productDetail.unit,
                    purchasePrice = purchasePriceDbl,
                    sellingPrice = sellingPriceDbl,
                    quantity = qtyInt
                )

                when(result) {
                    is Result.Success -> {
                        _formState.update { currentState ->
                            currentState.copy(
                                uiState = UiState.Success(Unit)
                            )
                        }

                        _uiEvent.send(UiEvent.ShowToast(R.string.success_product_saved))
                        _uiEvent.send(UiEvent.Navigate(EditProductNavigation.NavigateUp))

                        _formState.update { ScreenEditProductState() }
                    }
                    is Result.Error -> {
                        val errors = result.error

                        if (errors.databaseError == DatabaseError.NOT_FOUND) {
                            _formState.update {
                                it.copy(
                                    uiState = UiState.Error("Cannot update product: Product has been deleted or not found."),
                                    formErrors = null
                                )
                            }
                            return@launch
                        }

                        if (errors.sessionError != null) {
                            when(errors.sessionError) {
                                SessionError.MISSING_GROUP -> {
                                    _uiEvent.send(UiEvent.ShowToast(R.string.error_session_missing))
                                }
                            }
                        }

                        val needsConfirmation =
                            errors.minQuantityError == ProductMinimumQuantityError.REQUIRES_CONFIRMATION

                        val sanitizedErrors = errors.copy(
                            minQuantityError = if (errors.minQuantityError == ProductMinimumQuantityError.REQUIRES_CONFIRMATION) {
                                null
                            } else errors.minQuantityError,
                        )

                        _formState.update {
                            it.copy(
                                formErrors = sanitizedErrors,
                                uiState = UiState.Idle,
                                showSummaryConfirmationDialog = needsConfirmation
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _formState.update {
                    it.copy(
                        uiState = UiState.Error(
                            message = e.localizedMessage ?: "A critical database error occurred"
                        )
                    )
                }
            }
        }
    }
}