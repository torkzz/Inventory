package com.kevinfreyap.product.presentation.screen.product_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kevinfreyap.domain.Result
import com.kevinfreyap.domain.model.InventoryBarcode
import com.kevinfreyap.product.R
import com.kevinfreyap.product.domain.model.ProductId
import com.kevinfreyap.product.domain.usecase.DeleteProductUseCase
import com.kevinfreyap.product.domain.usecase.GetProductByIdUseCase
import com.kevinfreyap.product.presentation.action.ProductDetailAction
import com.kevinfreyap.ui.event.UiEvent
import com.kevinfreyap.product.presentation.mapper.toUiModel
import com.kevinfreyap.product.presentation.navigation.ProductDetailNavigation
import com.kevinfreyap.product.presentation.navigation.ProductScreen
import com.kevinfreyap.product.presentation.state.ScreenProductDetailState
import com.kevinfreyap.product.presentation.util.DateFormatter.formatDatePickerDate
import com.kevinfreyap.product.presentation.util.resolveDisplayedImage
import com.kevinfreyap.product.presentation.util.toFormattedCurrency
import com.kevinfreyap.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getProductById: GetProductByIdUseCase,
    private val deleteProductById: DeleteProductUseCase
): ViewModel(){
    private val route = savedStateHandle.toRoute<ProductScreen.ProductDetail>()
    private val productId = ProductId(route.productId)

    private val _uiEvent = Channel<UiEvent<ProductDetailNavigation>>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val uiState: StateFlow<UiState<ScreenProductDetailState>> = getProductById(productId)
        .map { product ->
            if (product == null) {
                UiState.Error("Not Found")
            } else {
                val barcode = product.barcode?.let {
                    InventoryBarcode(
                        value = product.barcode,
                        format = product.barcodeFormat ?: ""
                    )
                }

                UiState.Success(
                    ScreenProductDetailState(
                        imageUri = product.resolveDisplayedImage(),
                        productName = product.name,
                        productCategory = product.category?.name ?: "General",
                        productSku = product.sku,
                        productTotalQty = product.totalQuantity,
                        productMinPrice = product.minBatchCost?.toFormattedCurrency(),
                        productMaxPrice = product.maxBatchCost?.toFormattedCurrency(),
                        productMinQty = product.minimumQuantity,
                        productTotalValue = product.productTotalValue?.toFormattedCurrency(),
                        productNearestExpDate = formatDatePickerDate(product.nearestExpiringBatch),
                        productDescription = product.description,
                        productBarcode = barcode,
                        batchesList = product.batches.toUiModel(),
                    )
                )
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Idle
        )

    fun onAction(action: ProductDetailAction) {
        when(action) {
            ProductDetailAction.OnAddBatchClick -> {
                viewModelScope.launch {
                    _uiEvent.send(
                        UiEvent.Navigate(
                            ProductDetailNavigation.AddBatch(
                                productId = productId.value
                            )
                        )
                    )
                }
            }
            ProductDetailAction.OnEditProductClick -> {
                viewModelScope.launch {
                    _uiEvent.send(
                        UiEvent.Navigate(
                            ProductDetailNavigation.EditProduct(
                                productId = productId.value
                            )
                        )
                    )
                }
            }
            is ProductDetailAction.OnBatchDetailClick -> {
                viewModelScope.launch { 
                    _uiEvent.send(
                        UiEvent.Navigate(
                            ProductDetailNavigation.BatchDetail(
                                productId = productId.value,
                                batchId = action.batchId
                            )
                        )
                    )
                }
            }
            ProductDetailAction.OnCancelDelete -> Unit
            ProductDetailAction.OnConfirmDelete -> deleteProduct()
            ProductDetailAction.OnDeleteButtonClick -> Unit
        }
    }

    fun deleteProduct() {
        viewModelScope.launch {
            try {
                when(
                    val result = deleteProductById(productId)
                ) {
                    is Result.Success -> {
                        _uiEvent.send(
                            UiEvent.ShowToast(R.string.success_product_deleted)
                        )

                        _uiEvent.send(
                            UiEvent.Navigate(ProductDetailNavigation.NavigateUp)
                        )
                    }
                    is Result.Error -> {
                        _uiEvent.send(UiEvent.ShowToast(R.string.error_session_missing))
                    }
                }


            } catch (_: Exception) {
                _uiEvent.send(
                    UiEvent.ShowToast(R.string.error_delete_product)
                )
            }
        }
    }
}