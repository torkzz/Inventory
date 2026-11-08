package com.kevinfreyap.product.domain.usecase

import com.kevinfreyap.domain.Result
import com.kevinfreyap.domain.util.cleanInlineSpaces
import com.kevinfreyap.domain.util.toTitleCase
import com.kevinfreyap.product.domain.model.error.ProductCategoryError
import javax.inject.Inject

class ValidateProductCategoryUseCase @Inject constructor() {
    operator fun invoke(rawCategory: String): Result<String, ProductCategoryError> {
        val sanitizedCategory = rawCategory
            .cleanInlineSpaces()
            .toTitleCase()

        return when {
            sanitizedCategory.isBlank() -> Result.Success("")
            sanitizedCategory.length > 255 -> Result.Error(ProductCategoryError.TOO_LONG)
            sanitizedCategory.contains('\n') -> Result.Error(ProductCategoryError.CONTAINS_NEWLINE)
            else -> Result.Success(sanitizedCategory)
        }
    }
}
