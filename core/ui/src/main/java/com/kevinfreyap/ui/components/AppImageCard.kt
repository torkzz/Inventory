package com.kevinfreyap.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kevinfreyap.ui.R
import com.kevinfreyap.ui.theme.CyberBorder
import com.kevinfreyap.ui.theme.InventoryTheme
import com.kevinfreyap.ui.theme.NeonCyan
import com.kevinfreyap.ui.theme.Theme
import com.kevinfreyap.ui.util.shimmerEffect

@Composable
fun AppImageCard(
    imageUri: String?,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.35f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = Theme.custom.hint
        ),
        modifier = modifier
            .size(160.dp)
            .padding(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
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
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.image_24),
                    contentDescription = "No image available",
                    tint = Theme.custom.hint,
                    modifier = Modifier
                        .size(64.dp)
                )
            }
        }
    }
}

@Composable
fun AppImageCardPlaceholder(
    shimmerColor: Color = Theme.custom.shimmer
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CyberBorder),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = Theme.custom.hint
        ),
        modifier = Modifier
            .size(160.dp)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shimmerEffect(shimmerColor)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0x000000
)
@Composable
fun AppImageCardPreview() {
    InventoryTheme {
        AppImageCard(
            imageUri = null
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0x000000
)
@Composable
fun AppImageCardPlaceholderPreview() {
    InventoryTheme {
        AppImageCardPlaceholder()
    }
}