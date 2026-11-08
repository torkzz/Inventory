package com.kevinfreyap.product.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kevinfreyap.product.R
import com.kevinfreyap.product.presentation.mapper.color
import com.kevinfreyap.product.presentation.model.ProductListItemUi
import com.kevinfreyap.ui.components.AppBaseListItem
import com.kevinfreyap.ui.theme.Theme

@Composable
fun ProductListItem(
    product: ProductListItemUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppBaseListItem(
        itemName = product.name,
        imageUri = product.imageUri,
        titleContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Theme.custom.primaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Text(
                    text = "${product.quantity} ${product.unit.ifBlank { "pcs" }}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = product.stockLevel.color,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        subtitle = {
            Text(
                text = product.category,
                style = MaterialTheme.typography.bodySmall,
                color = Theme.custom.secondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        },
        trailingData = null,
        onClick = onClick,
        modifier = modifier
    )
}

