package com.kevinfreyap.product.presentation.screen.product_detail.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.ui.theme.InventoryTheme
import com.kevinfreyap.ui.theme.Theme
import com.kevinfreyap.ui.util.shimmerEffect

@Composable
fun SectionProductCore(
    name: String,
    sku: String? = null,
    category: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = Theme.custom.primaryText,
            textAlign = TextAlign.Center
        )

        Text(
            text = category,
            style = MaterialTheme.typography.labelLarge,
            color = Theme.custom.secondaryText
        )
    }
}

@Composable
fun SectionProductCorePlaceholder(
    shimmerColor: Color = Theme.custom.shimmer
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = "",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Transparent,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clearAndSetSemantics {
                    contentDescription = "Loading data"
                }
                .fillMaxWidth(0.8f)
                .clip(RoundedCornerShape(50))
                .shimmerEffect(shimmerColor)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Transparent,
                modifier = Modifier
                    .clearAndSetSemantics {
                        contentDescription = "Loading data"
                    }
                    .fillMaxWidth(0.3f)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(shimmerColor)
            )

            Text(
                text = "·",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Theme.custom.secondaryText,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
            )

            Text(
                text = "",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Transparent,
                modifier = Modifier
                    .clearAndSetSemantics {
                        contentDescription = "Loading data"
                    }
                    .fillMaxWidth(0.5f)
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
fun SectionProductCorePreview() {
    InventoryTheme {
        SectionProductCore(
            name = "Very Long Long Long Long Long Long Long Long Long Long Long",
            category = "Electronic",
            sku = "#SKU-1234-B",
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
fun SectionProductCorePlaceholderPreview() {
    InventoryTheme {
        SectionProductCorePlaceholder()
    }
}