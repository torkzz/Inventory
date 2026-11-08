package com.kevinfreyap.product.presentation.screen.product_detail.section

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.product.R
import com.kevinfreyap.ui.theme.CyberBorder
import com.kevinfreyap.ui.theme.InventoryTheme
import com.kevinfreyap.ui.theme.Theme
import com.kevinfreyap.ui.util.shimmerEffect

@Composable
fun SectionTotalQtyAndPriceRange (
    stockStatus: String,
    quantity: String?,
    minPrice: String?,
    maxPrice: String?,
    totalValue: String?,
    modifier: Modifier = Modifier
) {
    val isPriceRange = !minPrice.isNullOrBlank() && !maxPrice.isNullOrBlank()
    val combinedText = if (isPriceRange) "$minPrice - $maxPrice" else if (!minPrice.isNullOrBlank()) minPrice else ""

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Card(
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, CyberBorder),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight()
            )
            {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Text(
                        text = stockStatus,
                        style = MaterialTheme.typography.labelLarge,
                        color = Theme.custom.secondaryText,
                        textAlign = TextAlign.Center
                    )
                    if (!quantity.isNullOrBlank()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = quantity,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = Theme.custom.primaryText,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                )

                                Text(
                                    text = stringResource(R.string.label_quantity_items),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Theme.custom.hint
                                )
                            }
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, CyberBorder),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxHeight()
            )
            {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Text(
                        text = stringResource(
                            if (isPriceRange) R.string.label_price_range
                            else R.string.label_item_price
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = Theme.custom.secondaryText,
                    )

                    if (isPriceRange) {
                        AdaptiveTextLayout(
                            modifier = Modifier
                                .weight(1f),
                            singleLineContent = {
                                Text(
                                    text = combinedText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = Theme.custom.primaryText,
                                    textAlign = TextAlign.Center,
                                )
                            },
                            stackedContent = {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text = minPrice,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        color = Theme.custom.primaryText,
                                        textAlign = TextAlign.Center,
                                    )

                                    Text(
                                        text = stringResource(R.string.label_to),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Theme.custom.hint
                                    )

                                    Text(
                                        text = maxPrice,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        color = Theme.custom.primaryText,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        )
                    } else if (!minPrice.isNullOrBlank()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = minPrice,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = Theme.custom.primaryText,
                                textAlign = TextAlign.Center,
                            )
                        }
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.warning_no_price),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = Theme.custom.primaryText,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
            ) {
                Text(
                    text = stringResource(R.string.label_product_total_value),
                    style = MaterialTheme.typography.labelLarge,
                    color = Theme.custom.secondaryText,
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    Text(
                        text = totalValue ?: stringResource(R.string.warning_no_price_short),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Theme.custom.primaryText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(
                                horizontal = 4.dp,
                                vertical = 8.dp
                            )
                    )
                }
            }

        }
    }
}

@Composable
fun SectionTotalQtyAndPriceRangePlaceholder(
    shimmerColor: Color = Theme.custom.shimmer
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Transparent,
                        modifier = Modifier
                            .clearAndSetSemantics {}
                            .fillMaxWidth(0.5f)
                            .clip(RoundedCornerShape(50))
                            .shimmerEffect(shimmerColor)
                    )

                    Spacer(Modifier.height(4.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = Color.Transparent,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clearAndSetSemantics {}
                                    .fillMaxWidth(0.7f)
                                    .clip(RoundedCornerShape(50))
                                    .shimmerEffect(shimmerColor)
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = "",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Transparent,
                                modifier = Modifier
                                    .clearAndSetSemantics {}
                                    .fillMaxWidth(0.4f)
                                    .clip(RoundedCornerShape(50))
                                    .shimmerEffect(shimmerColor)
                            )
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxHeight()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Transparent,
                        modifier = Modifier
                            .clearAndSetSemantics {}
                            .fillMaxWidth(0.4f)
                            .clip(RoundedCornerShape(50))
                            .shimmerEffect(shimmerColor)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Theme.custom.primaryText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clearAndSetSemantics {}
                            .fillMaxWidth(0.8f)
                            .clip(RoundedCornerShape(50))
                            .shimmerEffect(shimmerColor)
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Transparent,
                    modifier = Modifier
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.3f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(shimmerColor)
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color.Transparent,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clearAndSetSemantics {}
                            .fillMaxWidth(0.7f)
                            .padding(
                                horizontal = 4.dp,
                                vertical = 8.dp
                            )
                            .clip(RoundedCornerShape(50))
                            .shimmerEffect(shimmerColor)
                    )
                }
            }

        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0x000000
)
@Composable
fun SectionTotalQtyAndPriceRangePreview() {
    InventoryTheme {
        SectionTotalQtyAndPriceRange(
            stockStatus = "In Stock",
            quantity = "50.000",
            minPrice = "",
            maxPrice = "",
            totalValue = null
        )
    }
}

@Preview (
    showBackground = true,
    backgroundColor = 0x000000
)
@Composable
fun SectionTotalQtyAndPriceRangePlaceholderPreview() {
    InventoryTheme {
        SectionTotalQtyAndPriceRangePlaceholder()
    }
}