package com.kevinfreyap.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kevinfreyap.ui.R
import com.kevinfreyap.ui.theme.CyberBorder
import com.kevinfreyap.ui.theme.InventoryTheme
import com.kevinfreyap.ui.theme.Theme

@Composable
fun AppTextIconDialog(
    icon: Painter,
    title: String,
    subtitle: String,
    iconColor: Color,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 120.dp,
    positiveBtn: @Composable (() -> Unit)? = null,
    negativeBtn: @Composable (() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        AppTextIconDialogContent(
            icon = icon,
            title = title,
            subtitle = subtitle,
            iconColor = iconColor,
            iconSize = iconSize,
            positiveBtn = positiveBtn,
            negativeBtn = negativeBtn,
            modifier = modifier
        )
    }
}

@Composable
fun AppTextIconDialogContent(
    icon: Painter,
    title: String,
    subtitle: String,
    iconColor: Color,
    modifier: Modifier = Modifier,
    iconSize: Dp = 120.dp,
    positiveBtn: @Composable (() -> Unit)? = null,
    negativeBtn: @Composable (() -> Unit)? = null
) {
    val hasBothButton = positiveBtn != null && negativeBtn != null

    Card(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CyberBorder),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 312.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier
                    .size(iconSize)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Theme.custom.primaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Theme.custom.secondaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = if (hasBothButton) Arrangement.spacedBy(8.dp) else Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (negativeBtn != null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = if (hasBothButton) Modifier.weight(1f) else Modifier
                    ) {
                        negativeBtn()
                    }
                }

                if (positiveBtn != null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = if (hasBothButton) Modifier.weight(1f) else Modifier
                    ) {
                        positiveBtn()
                    }
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
fun AppTextIconDialogPreview() {
    InventoryTheme {
        AppTextIconDialogContent(
            icon = painterResource(R.drawable.close_24),
            title = "Discard unsaved changes?",
            subtitle = "Any information you entered will be lost",
            iconColor = MaterialTheme.colorScheme.error,
            positiveBtn = {
                AppPrimaryButton(
                    text = "Discard",
                    onClick = {  },
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
            }
        )
    }
}