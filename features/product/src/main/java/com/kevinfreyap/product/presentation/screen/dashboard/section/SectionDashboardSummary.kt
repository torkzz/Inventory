package com.kevinfreyap.product.presentation.screen.dashboard.section

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.product.R
import com.kevinfreyap.ui.theme.CyberBorder
import com.kevinfreyap.ui.theme.InventoryTheme
import com.kevinfreyap.ui.theme.NeonCyan
import com.kevinfreyap.ui.theme.NeonPurple
import com.kevinfreyap.ui.theme.Theme
import com.kevinfreyap.ui.util.shimmerEffect

@Composable
fun SectionDashboardSummary(
    totalItem: String,
    totalProduct: String,
    estimatedValue: String?,
    modifier: Modifier = Modifier,
) {
    Card (
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CyberBorder),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 16.dp
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.label_total_item),
                        style = MaterialTheme.typography.labelMedium,
                        color = Theme.custom.secondaryText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = totalItem,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(R.string.label_products),
                        style = MaterialTheme.typography.labelMedium,
                        color = Theme.custom.secondaryText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = totalProduct,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeonPurple,
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.account_balance_wallet_24),
                    contentDescription = null,
                    tint = Theme.custom.hint,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = stringResource(R.string.label_estimated_value),
                    style = MaterialTheme.typography.labelMedium,
                    color = Theme.custom.hint
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = estimatedValue ?: stringResource(R.string.warning_no_price_short),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Theme.custom.secondaryText,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
            }
        }
    }
}

@Composable
fun SectionDashboardSummaryPlaceholder(
    shimmerColor: Color = Theme.custom.shimmer
) {
    Card (
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CyberBorder),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 16.dp
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Transparent,
                        modifier = Modifier
                            .clearAndSetSemantics {
                                contentDescription = "Loading data"
                            }
                            .fillMaxWidth(0.7f)
                            .clip(RoundedCornerShape(50))
                            .shimmerEffect(shimmerColor)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                ) {
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Transparent,
                        modifier = Modifier
                            .clearAndSetSemantics {
                                contentDescription = "Loading data"
                            }
                            .fillMaxWidth(0.7f)
                            .clip(RoundedCornerShape(50))
                            .shimmerEffect(shimmerColor)
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Transparent,
                maxLines = 1,
                modifier = Modifier
                    .clearAndSetSemantics {
                        contentDescription = "Loading data"
                    }
                    .fillMaxWidth(0.7f)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(shimmerColor)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun SectionDashboardSummaryPreview() {
    InventoryTheme {
        SectionDashboardSummary(
            totalItem = "1.700.000",
            totalProduct = "1.000.000",
            estimatedValue = "Rp 1.200.000.000"
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun SectionDashboardSummaryPlaceholderPreview() {
    InventoryTheme {
        SectionDashboardSummaryPlaceholder()
    }
}