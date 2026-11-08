package com.kevinfreyap.product.presentation.screen.product_detail.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.product.R
import com.kevinfreyap.product.presentation.components.BatchListItem
import com.kevinfreyap.product.presentation.components.BatchListItemPlaceholder
import com.kevinfreyap.product.presentation.model.StockBatchUi
import com.kevinfreyap.ui.theme.InventoryTheme
import com.kevinfreyap.ui.theme.Theme
import com.kevinfreyap.ui.util.shimmerEffect

@Composable
fun SectionBatches(
    batches: List<StockBatchUi>,
    onBatchItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxPreviewItems = 3

    var showAllBatches by rememberSaveable { mutableStateOf(false) }

    val displayedBatches = if (showAllBatches) {
        batches
    } else {
        batches.take(maxPreviewItems)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.label_product_batches),
                style = MaterialTheme.typography.labelLarge,
                color = Theme.custom.secondaryText,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            )


        }

        if (batches.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                Text(
                    text = stringResource(R.string.warning_no_active_batch),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Theme.custom.secondaryText
                )
            }
        } else {
            Column (
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                displayedBatches.forEach { batchUi ->
                    BatchListItem(
                        name = stringResource(R.string.placeholder_batch_name, batchUi.shortId),
                        location = batchUi.location,
                        quantity = batchUi.quantity,
                        cost = batchUi.price,
                        onBatchClick = {
                            onBatchItemClick(batchUi.id)
                        },
                    )
                }

                if (batches.size > maxPreviewItems) {
                    Text(
                        text = if (showAllBatches) {
                            stringResource(R.string.txt_btn_label_see_less)
                        } else {
                            stringResource(R.string.txt_btn_label_see_all_batch, batches.size)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                showAllBatches = !showAllBatches
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SectionBatchesPlaceholder(
    shimmerColor: Color = Theme.custom.shimmer
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Transparent,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(0.3f)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(shimmerColor)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) {
                BatchListItemPlaceholder()
            }

            Text(
                text = "",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Transparent,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .fillMaxWidth(0.3f)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(shimmerColor)
            )
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun SectionBatchesPreview() {
    InventoryTheme {
        SectionBatches(
            batches = listOf(
                StockBatchUi(
                    id = "batch-3f8d9b21-4c6e-4a12-9e83-7b5c1a9f0e2d",
                    shortId = "3F8D9B",
                    quantity = 2,
                    price = "₱1,000.00",
                    location = "Garage",
                    expDate = "31 September 2020",
                    supplier = null
                ),
                StockBatchUi(
                    id = "batch-4c6e4a12-9e83-7b5c1a9f0e2d-3f8d9b21",
                    shortId = "4C6E4A",
                    quantity = 3,
                    price = "",
                    location = "Garage",
                    expDate = "31 September 2020",
                    supplier = null
                ),
                StockBatchUi(
                    id = "batch-7b5c1a9f-9e83-7b5c1a9f0e2d-3f8d9b21",
                    shortId = "7B5C1A",
                    quantity = 2,
                    price = "₱1,000.00",
                    location = "Garage",
                    expDate = "31 September 2020",
                    supplier = null
                ),
                StockBatchUi(
                    id = "batch-1a9f0e2d-9e83-7b5c1a9f0e2d-3f8d9b21",
                    shortId = "1A9F0E",
                    quantity = 2,
                    price = "₱1,000.00",
                    location = "Garage",
                    expDate = "31 September 2020",
                    supplier = null
                ),
            ),
            onBatchItemClick = {  },
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
fun SectionBatchesPlaceholderPreview() {
    InventoryTheme {
        SectionBatchesPlaceholder()
    }
}