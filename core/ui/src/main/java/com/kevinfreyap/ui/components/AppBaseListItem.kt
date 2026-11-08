package com.kevinfreyap.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kevinfreyap.ui.R
import com.kevinfreyap.ui.theme.CyberBorder
import com.kevinfreyap.ui.theme.InventoryTheme
import com.kevinfreyap.ui.theme.Theme
import com.kevinfreyap.ui.util.shimmerEffect

@Composable
fun AppBaseListItem(
    itemName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconBackground: Color = Theme.custom.shimmer,
    imageUri: String? = null,
    icon: @Composable (() -> Unit)? = null,
    titleContent: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    trailingData: @Composable (() -> Unit)? = null
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CyberBorder),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(IntrinsicSize.Min)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBackground)
            ) {
                if (!imageUri.isNullOrBlank()){
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.image_24),
                                    contentDescription = "Error Profile Picture",
                                    tint =  Theme.custom.hint,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    )
                } else if (icon != null) {
                    icon()
                } else {
                    Icon(
                        painter = painterResource(R.drawable.image_24),
                        contentDescription = "No image available",
                        tint = Theme.custom.hint
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (titleContent != null) {
                    titleContent()
                } else {
                    Text(
                        text = itemName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Theme.custom.primaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                subtitle?.invoke()
            }

            trailingData?.invoke()
        }
    }
}

@Composable
fun AppBaseListItemPlaceholder(
    shimmerColor: Color = Theme.custom.shimmer,
    isSubtitleExist: Boolean = true,
    isTrailingDataExist: Boolean = true,
    isTrailingDataLabelExist: Boolean = true
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CyberBorder),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(IntrinsicSize.Min)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .shimmerEffect(shimmerColor = shimmerColor)
            ) {}

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.Transparent,
                    modifier = Modifier
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.7f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(shimmerColor)
                )

                if (isSubtitleExist) {
                    Spacer(
                        modifier = Modifier
                            .height(4.dp)
                    )

                    Text(
                        text = "",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Transparent,
                        modifier = Modifier
                            .clearAndSetSemantics {}
                            .fillMaxWidth(0.4f)
                            .clip(RoundedCornerShape(50))
                            .shimmerEffect(shimmerColor)
                    )
                }
            }

            if (isTrailingDataExist) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(4.dp)
                ) {
                    if (isTrailingDataLabelExist) {
                        Text(
                            text = "",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Transparent,
                            modifier = Modifier
                                .clearAndSetSemantics {}
                                .fillMaxWidth(0.17f)
                                .clip(RoundedCornerShape(50))
                                .shimmerEffect(shimmerColor)
                        )

                        Spacer(
                            modifier = Modifier
                                .height(4.dp)
                        )
                    }
                    Text(
                        text = "",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color.Transparent,
                        modifier = Modifier
                            .clearAndSetSemantics {}
                            .fillMaxWidth(0.12f)
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
    backgroundColor = 0x00000000
)
@Composable
fun AppBaseListItemPreview() {
    InventoryTheme {
        AppBaseListItem(
            itemName = "Product Name",
            onClick = {},
            subtitle = {
                Text(
                    text = "#SKU-1234-B",
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            },
            trailingData = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(4.dp)
                ) {
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.labelMedium,
                        color = Theme.custom.secondaryText,
                    )
                    Text(
                        text = "1000",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Theme.custom.success
                    )
                }
            }
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF121212,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AppBaseListItemPreviewDark() {
    InventoryTheme {
        AppBaseListItem(
            itemName = "Product Name",
            onClick = {},
            subtitle = {
                Text(
                    text = "#SKU-1234-B",
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            },
            trailingData = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(4.dp)
                ) {
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.labelMedium,
                        color = Theme.custom.secondaryText,
                    )
                    Text(
                        text = "1000",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Theme.custom.success
                    )
                }
            }
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0x000000,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AppBaseListItemPlaceholderPreview() {
    InventoryTheme {
        AppBaseListItemPlaceholder(
            isSubtitleExist = true,
            isTrailingDataExist = true,
            isTrailingDataLabelExist = true
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF121212,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AppBaseListItemPlaceholderPreviewDark() {
    InventoryTheme {
        AppBaseListItemPlaceholder(
            isSubtitleExist = true,
            isTrailingDataExist = true,
            isTrailingDataLabelExist = true
        )
    }
}