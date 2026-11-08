package com.kevinfreyap.product.presentation.action

import com.kevinfreyap.domain.Result
import com.kevinfreyap.product.domain.model.error.BatchLocationError
import com.kevinfreyap.product.domain.model.error.ProductCategoryError
import com.kevinfreyap.product.presentation.state.SharedBatchFormState
import com.kevinfreyap.product.presentation.state.SharedProductFormState
import com.kevinfreyap.product.presentation.util.DateFormatter.formatDateLongToString
import com.kevinfreyap.product.presentation.util.DateFormatter.parseDateStringToLong

object SharedFormAction {
    fun sharedProductDetailAction(
        action: ProductFormAction.ProductDetailAction,
        currentState: SharedProductFormState,
        validateCategory: (String) -> Result<String, ProductCategoryError>
    ): SharedProductFormState {
        return when (action) {
            is ProductFormAction.ProductDetailAction.OnNameChanged -> {
                currentState.copy(
                    productDetail = currentState.productDetail.copy(productName = action.name),
                    formErrors = currentState.formErrors?.copy(nameError = null)
                )
            }
            is ProductFormAction.ProductDetailAction.OnCategoryChanged -> {
                val newName = action.category

                currentState.copy(
                    productDetail = currentState.productDetail.copy(
                        productCategoryName = newName,
                        filteredCategories = currentState.productDetail.allCategories.filter {
                            it.contains(newName, ignoreCase = true)
                        }
                    ),
                    formErrors = currentState.formErrors?.copy(categoryError = null)
                )
            }
            is ProductFormAction.ProductDetailAction.OnCreateNewCategory -> {
                when (
                    val validationResult = validateCategory(action.newCategory)
                ) {
                    is Result.Success -> {
                        currentState.copy(
                            productDetail = currentState.productDetail.copy(productCategoryName = validationResult.data),
                            formErrors = currentState.formErrors?.copy(categoryError = null)
                        )
                    }
                    is Result.Error -> {
                        currentState.copy(
                            formErrors = currentState.formErrors?.copy(
                                categoryError = validationResult.error
                            )
                        )
                    }
                }
            }
            is ProductFormAction.ProductDetailAction.OnDescriptionChanged -> {
                currentState.copy(
                    productDetail = currentState.productDetail.copy(productDescription = action.desc),
                    formErrors = currentState.formErrors?.copy(descriptionError = null)
                )
            }
            is ProductFormAction.ProductDetailAction.OnImageUriChanged -> {
                currentState.copy(
                    productDetail = currentState.productDetail.copy(productImageUriString = action.uri),
                    formErrors = currentState.formErrors?.copy(imageError = null)
                )
            }
            is ProductFormAction.ProductDetailAction.OnMinQuantityChanged -> {
                currentState.copy(
                    productDetail = currentState.productDetail.copy(
                        productMinQuantity = action.qty,
                        isMinQuantityConfirmed = false
                    ),
                    formErrors = currentState.formErrors?.copy(minQuantityError = null)
                )
            }
            is ProductFormAction.ProductDetailAction.OnBrandChanged -> {
                currentState.copy(
                    productDetail = currentState.productDetail.copy(brand = action.brand)
                )
            }
            is ProductFormAction.ProductDetailAction.OnUnitChanged -> {
                currentState.copy(
                    productDetail = currentState.productDetail.copy(unit = action.unit)
                )
            }
            is ProductFormAction.ProductDetailAction.OnPurchasePriceChanged -> {
                currentState.copy(
                    productDetail = currentState.productDetail.copy(purchasePrice = action.price)
                )
            }
            is ProductFormAction.ProductDetailAction.OnSellingPriceChanged -> {
                currentState.copy(
                    productDetail = currentState.productDetail.copy(sellingPrice = action.price)
                )
            }
            is ProductFormAction.ProductDetailAction.OnStandaloneQuantityChanged -> {
                currentState.copy(
                    productDetail = currentState.productDetail.copy(standaloneQuantity = action.qty)
                )
            }
        }
    }

    fun sharedProductIdentificationAction(
        action: ProductFormAction.ProductIdentificationAction,
        currentState: SharedProductFormState
    ): SharedProductFormState {
        return when(action) {
            is ProductFormAction.ProductIdentificationAction.OnBarcodeChanged -> {
                currentState.copy(
                    productIdentification = currentState.productIdentification.copy(
                        productBarcode = action.barcode?.value,
                        productBarcodeFormat = action.barcode?.format
                    ),
                    formErrors = currentState.formErrors?.copy(barcodeError = null)
                )
            }
            is ProductFormAction.ProductIdentificationAction.OnSkuChanged -> {
                currentState.copy(
                    productIdentification = currentState.productIdentification.copy(productSku = action.sku),
                    formErrors = currentState.formErrors?.copy(skuError = null)
                )
            }
        }
    }

    fun sharedBatchDetailAction(
        action: ProductFormAction.BatchDetailAction,
        currentState: SharedBatchFormState,
        validateLocation: (String) -> Result<String, BatchLocationError>
    ): SharedBatchFormState {
        return when (action) {
            is ProductFormAction.BatchDetailAction.OnAddInitialStockToggled -> {
                currentState.copy(
                    batchDetail = currentState.batchDetail.copy(addInitialStock = action.isChecked)
                )
            }
            is ProductFormAction.BatchDetailAction.OnBatchLocationChanged -> {
                val newLocation = action.loc

                currentState.copy(
                    batchDetail = currentState.batchDetail.copy(
                        batchLocation = newLocation,
                        filteredLocations = currentState.batchDetail.allLocations.filter {
                            it.contains(newLocation, ignoreCase = true)
                        }
                    ),
                    formErrors = currentState.formErrors?.copy(locationError = null)
                )
            }
            is ProductFormAction.BatchDetailAction.OnBatchPriceChanged -> {
                currentState.copy(
                    batchDetail = currentState.batchDetail.copy(batchPrice = action.price),
                    formErrors = currentState.formErrors?.copy(priceError = null)
                )
            }
            is ProductFormAction.BatchDetailAction.OnBatchQuantityChanged -> {
                currentState.copy(
                    batchDetail = currentState.batchDetail.copy(
                        batchQuantity = action.qty,
                        isQuantityConfirmed = false
                    ),
                    formErrors = currentState.formErrors?.copy(quantityError = null)
                )
            }
            is ProductFormAction.BatchDetailAction.OnCreateNewLocation -> {
                when (
                    val validationResult = validateLocation(action.newLocName)
                ) {
                    is Result.Success -> {
                        currentState.copy(
                            batchDetail = currentState.batchDetail.copy(batchLocation = validationResult.data),
                            formErrors = currentState.formErrors?.copy(locationError = null)
                        )
                    }
                    is Result.Error -> {
                        currentState.copy(
                            formErrors = currentState.formErrors?.copy(
                                locationError = validationResult.error
                            )
                        )
                    }
                }
            }
            ProductFormAction.BatchDetailAction.OnQuantityDecremented -> {
                val current = currentState.batchDetail.batchQuantity.toIntOrNull() ?: 0
                val newQty = if (current > 0) {
                    (current - 1).toString()
                } else {
                    current.toString()
                }

                currentState.copy(
                    batchDetail = currentState.batchDetail.copy(batchQuantity = newQty),
                    formErrors = currentState.formErrors?.copy(quantityError = null)
                )
            }
            ProductFormAction.BatchDetailAction.OnQuantityIncremented -> {
                val current = currentState.batchDetail.batchQuantity.toIntOrNull() ?: 0
                val newQty = (current + 1).toString()

                currentState.copy(
                    batchDetail = currentState.batchDetail.copy(batchQuantity = newQty),
                    formErrors = currentState.formErrors?.copy(quantityError = null)
                )
            }
        }
    }

    fun sharedBatchInformationAction(
        action: ProductFormAction.BatchInformationAction,
        currentState: SharedBatchFormState,
    ): SharedBatchFormState {
        return when(action) {
            is ProductFormAction.BatchInformationAction.OnBatchExpirationChanged -> {
                currentState.copy(
                    batchInformation = currentState.batchInformation.copy(batchExpirationFieldText = action.expString)
                )
            }
            ProductFormAction.BatchInformationAction.OnBatchExpirationConfirm -> {
                val rawString = currentState.batchInformation.batchExpirationFieldText

                val parsedMillis: Long? = if (rawString.length == 8) {
                    parseDateStringToLong(rawString)
                } else null

                currentState.copy(
                    batchInformation = currentState.batchInformation.copy(
                        batchExpirationMillis = parsedMillis,
                    ),
                    formErrors = currentState.formErrors?.copy(expirationError = null)
                )
            }
            is ProductFormAction.BatchInformationAction.OnBatchSupplierChanged -> {
                currentState.copy(
                    batchInformation = currentState.batchInformation.copy(batchSupplier = action.supplier),
                    formErrors = currentState.formErrors?.copy(supplierError = null)
                )
            }
            ProductFormAction.BatchInformationAction.OnOpenExpirationDialog -> {
                val savedMillis = currentState.batchInformation.batchExpirationMillis

                currentState.copy(
                    batchInformation = currentState.batchInformation.copy(batchExpirationFieldText = formatDateLongToString(savedMillis))
                )
            }
        }
    }

}