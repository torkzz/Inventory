package com.kevinfreyap.product.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.product.R
import com.kevinfreyap.ui.theme.CyberBorder
import com.kevinfreyap.ui.theme.InventoryTheme
import com.kevinfreyap.ui.theme.Theme
import com.kevinfreyap.ui.util.shimmerEffect

@Composable
fun BatchListItem(
    name: String,
    location: String,
    quantity: Int,
    cost: String?,
    onBatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onBatchClick,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CyberBorder),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(16.dp)
        ) {
            Column (
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f, fill = false)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Theme.custom.primaryText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.location_on_24),
                        contentDescription = null,
                        tint = Theme.custom.secondaryText,
                        modifier = Modifier
                            .size(16.dp)
                    )

                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Theme.custom.secondaryText,
                        modifier = Modifier
                            .padding(end = 8.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Theme.custom.primaryText,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append("$quantity ")
                        }

                        withStyle(
                            style = SpanStyle(
                                color = Theme.custom.hint,
                                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                fontWeight = MaterialTheme.typography.labelMedium.fontWeight
                            )
                        ) {
                            append(stringResource(R.string.label_quantity_items))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (!cost.isNullOrBlank()) cost else stringResource(R.string.warning_no_price_short),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Theme.custom.secondaryText
                )
            }
        }
    }
}

@Composable
fun BatchListItemPlaceholder(
    shimmerColor: Color = Theme.custom.shimmer
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CyberBorder),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(16.dp)
        ) {
            Column (
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f, fill = false)
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.Transparent,
                    modifier = Modifier
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.3f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(shimmerColor)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Theme.custom.secondaryText,
                    modifier = Modifier
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.8f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(shimmerColor)
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.Transparent,
                    modifier = Modifier
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.2f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(shimmerColor)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.Transparent,
                    modifier = Modifier
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.3f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(shimmerColor)
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun BatchListItemPreview() {
    InventoryTheme {
        BatchListItem(
            name = "Batch 1",
            location = "Garage Very Long Long Long Long One",
            quantity = 2,
            cost = "Rp 5.000.000.000",
            onBatchClick = {}
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
fun BatchListItemPlaceholderPreview() {
    InventoryTheme {
        BatchListItemPlaceholder()
    }
}