package com.kevinfreyap.product.presentation.screen.dashboard

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.kevinfreyap.domain.model.InventoryBarcode
import com.kevinfreyap.product.R
import com.kevinfreyap.product.domain.model.query.FilterStockOption
import com.kevinfreyap.product.domain.model.query.sort.SortOption
import com.kevinfreyap.product.presentation.components.ProductListItem
import com.kevinfreyap.product.presentation.navigation.DashboardNavigation
import com.kevinfreyap.product.presentation.model.AlertListUi
import com.kevinfreyap.product.presentation.model.ProductListItemUi
import com.kevinfreyap.product.presentation.model.StockLevel
import com.kevinfreyap.product.presentation.screen.dashboard.section.SectionDashboardSummary
import com.kevinfreyap.product.presentation.screen.dashboard.section.SectionDashboardSummaryPlaceholder
import com.kevinfreyap.product.presentation.screen.dashboard.section.SectionGroup
import com.kevinfreyap.product.presentation.screen.dashboard.section.SectionGroupPlaceholder
import com.kevinfreyap.product.presentation.screen.dashboard.section.SectionListWithHeader
import com.kevinfreyap.product.presentation.screen.dashboard.section.SectionListWithHeaderPlaceholder
import com.kevinfreyap.product.presentation.state.ScreenDashboardState
import com.kevinfreyap.ui.R as coreR
import com.kevinfreyap.ui.components.AppIconName
import com.kevinfreyap.ui.components.AppPrimaryButton
import com.kevinfreyap.ui.components.AppStateBanner
import com.kevinfreyap.ui.event.UiEvent
import com.kevinfreyap.ui.state.UiState
import com.kevinfreyap.ui.theme.InventoryTheme

@Composable
fun DashboardScreen(
    scannedBarcode: InventoryBarcode,
    onClearBarcode: () -> Unit,
    onNavigate: (DashboardNavigation) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(scannedBarcode) {
        if (scannedBarcode.value.isNotBlank() && scannedBarcode.format.isNotBlank()) {
            viewModel.onBarcodeScanned(scannedBarcode)
            onClearBarcode()
        }
    }

    LaunchedEffect(viewModel.uiEvent, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvent.collect { event ->
                when(event) {
                    is UiEvent.Navigate -> {
                        onNavigate(event.destination)
                    }
                    is UiEvent.ShowToast -> {
                        Toast.makeText(
                            context,
                            event.messageRes,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    DashboardContent(
        state = state,
        onNavigate = onNavigate,
        onRetryClicked = {
            viewModel.onRetryClicked()
        },
        modifier = modifier
    )
}

@Composable
fun DashboardContent(
    state: UiState<ScreenDashboardState>,
    onNavigate: (DashboardNavigation) -> Unit,
    onRetryClicked: () ->  Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                AppIconName()
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onNavigate(DashboardNavigation.BarcodeScanner)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    painter = painterResource(coreR.drawable.app_barcode_scanner),
                    contentDescription = "Barcode Scanner",
                    modifier = Modifier
                        .size(32.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    top = 12.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            when (state) {
                UiState.Idle -> {}
                UiState.Empty -> {
                    DashboardEmpty(
                        state = ScreenDashboardState(
                            recentProduct = AlertListUi(
                                R.string.label_recently_updated,
                                R.string.btn_label_view_all_product,
                                emptyList()
                            )
                        ),
                        onNavigate = onNavigate
                    )
                }
                UiState.Loading -> {
                    DashboardShimmer()
                }
                is UiState.Error -> {
                    DashboardError(
                        onRetryClicked = onRetryClicked
                    )
                }
                is UiState.Success -> {
                    if (state.data.totalProductRaw == 0) {
                        DashboardEmpty(
                            state = state.data,
                            onNavigate = onNavigate
                        )
                    } else {
                        DashboardSuccess(
                            state = state.data,
                            onNavigate = onNavigate
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardShimmer() {
    SectionGroupPlaceholder()
    SectionDashboardSummaryPlaceholder()
    SectionListWithHeaderPlaceholder()
    SectionListWithHeaderPlaceholder()
}

@Composable
private fun DashboardEmpty(
    state: ScreenDashboardState,
    onNavigate: (DashboardNavigation) -> Unit
) {
    val title = state.groupName.ifBlank {
        stringResource(R.string.label_header_no_group)
    }

    val subtitle = buildString {
        if (state.userName.isNotBlank()) append(state.userName)
        if (state.userRole.isNotBlank()) {
            if (isNotEmpty()) append(" | ")
            append(state.userRole)
        }
    }.ifBlank {
        stringResource(R.string.label_subtitle_no_group)
    }

    SectionGroup(
        title = title,
        subtitle = subtitle
    )

    SectionDashboardSummary(
        totalItem = "0",
        totalProduct = "0",
        estimatedValue = null
    )

    AppStateBanner(
        bannerIcon = coreR.drawable.app_empty_storage_icon,
        bannerTitle = stringResource(R.string.title_banner_empty_inventory),
        bannerSubtitle = stringResource(R.string.subtitle_banner_empty_inventory),
        actionButton = {
            AppPrimaryButton(
                text = stringResource(R.string.btn_label_add_first_item),
                onClick = {
                    onNavigate(DashboardNavigation.AddProduct())
                },
                icon = {
                    Icon(
                        painter = painterResource(coreR.drawable.add_24),
                        contentDescription = stringResource(R.string.btn_label_add_first_item),
                    )
                },
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    )
}

@Composable
private fun DashboardSuccess(
    state: ScreenDashboardState,
    onNavigate: (DashboardNavigation) -> Unit,
) {
    val title = state.groupName.ifBlank {
        stringResource(R.string.label_header_no_group)
    }

    val subtitle = buildString {
        if (state.userName.isNotBlank()) append(state.userName)
        if (state.userRole.isNotBlank()) {
            if (isNotEmpty()) append(" | ")
            append(state.userRole)
        }
    }

    SectionGroup(
        title = title,
        subtitle = subtitle
    )

    SectionDashboardSummary(
        totalItem = state.totalItemsCount,
        totalProduct = state.totalProductCount,
        estimatedValue = state.estimatedValue
    )

    if (state.lowStockAlert != null) {
        SectionListWithHeader(
            title = stringResource(state.lowStockAlert.title),
            list = state.lowStockAlert.products,
            textButton = {
                if (state.lowStockAlert.textButtonArg > 3) {
                    Text(
                        text = stringResource(state.lowStockAlert.textButton, state.lowStockAlert.textButtonArg),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50))
                            .clickable  {
                                onNavigate(
                                    DashboardNavigation.ProductList(FilterStockOption.STOCK_WARNING.name)
                                )
                            }
                            .padding(
                                top = 4.dp,
                            )
                    )
                }
            }
        ) { product ->
            ProductListItem(
                product = product,
                onClick = {
                    onNavigate(DashboardNavigation.ProductDetail(product.id))
                }
            )
        }

        Spacer(Modifier.height(8.dp))
    }


    SectionListWithHeader(
        title = stringResource(state.recentProduct.title),
        list = state.recentProduct.products,
        textButton = {
            Text(
                text = stringResource(state.recentProduct.textButton),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .clickable  {
                        onNavigate(DashboardNavigation.ProductList(SortOption.LAST_UPDATED.name))
                    }
                    .padding(
                        top = 4.dp,
                        bottom = 8.dp
                    )
            )
        }
    ) { product ->
        ProductListItem(
            product = product,
            onClick = {
                onNavigate(DashboardNavigation.ProductDetail(product.id))
            }
        )
    }
}

@Composable
private fun DashboardError(
    onRetryClicked: () -> Unit
) {
    SectionGroup(
        title = stringResource(R.string.label_header_no_group),
        subtitle = stringResource(R.string.label_subtitle_no_group)
    )

    AppStateBanner(
        bannerIcon = coreR.drawable.app_error_load_icon,
        bannerTitle = stringResource(R.string.title_banner_load_error),
        bannerSubtitle = stringResource(R.string.subtitle_banner_load_error),
        actionButton = {
            AppPrimaryButton(
                text = stringResource(R.string.btn_label_try_again),
                onClick = onRetryClicked,
                icon = {
                    Icon(
                        painter = painterResource(coreR.drawable.refresh_24),
                        contentDescription = stringResource(R.string.btn_label_try_again),
                    )
                },
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    )
}

@Preview(
    showBackground = true,
    device = "spec:width=1080px,height=2340px,dpi=416",
)
@Composable
fun DashboardScreenPreview() {
    InventoryTheme {
        DashboardContent(
            state = UiState.Success(
                ScreenDashboardState(
                    groupName = "Test 1's Inventory",
                    userName = "Test 1",
                    userRole = "Admin",
                    totalProductCount = "1.000",
                    totalItemsCount = "1.700",
                    estimatedValue = "₱1,200,000.00",
                    recentProduct = AlertListUi(
                        title = R.string.label_recently_updated,
                        textButton = R.string.btn_label_view_all_product,
                        products = listOf(
                            ProductListItemUi(
                                id = "PROD-01",
                                name = "Smartphone",
                                quantity = 100,
                                stockLevel = StockLevel.IN_STOCK,
                                category = "Electronic",
                                imageUri = null,
                                sku = "#SKU-1234-B"
                            ),
                            ProductListItemUi(
                                id = "PROD-02",
                                name = "Smartphone 2",
                                quantity = 100,
                                stockLevel = StockLevel.IN_STOCK,
                                category = "Electronic",
                                imageUri = null,
                                sku = "#SKU-1234-B"
                            ),
                            ProductListItemUi(
                                id = "PROD-03",
                                name = "Smartphone 3",
                                quantity = 100,
                                stockLevel = StockLevel.IN_STOCK,
                                category = "Electronic",
                                imageUri = null,
                                sku = "#SKU-1234-B"
                            ),
                        ),
                    )
                )
            ),
            onNavigate = {},
            onRetryClicked = {}
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1080px,height=2340px,dpi=416",
    showSystemUi = true
)
@Composable
fun DashboardScreenPreview_StateLoading() {
    InventoryTheme {
        DashboardContent(
            state = UiState.Loading,
            onNavigate = {},
            onRetryClicked = {}
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1080px,height=2340px,dpi=416",
    showSystemUi = true
)
@Composable
fun DashboardScreenPreview_StateEmpty() {
    InventoryTheme {
        DashboardContent(
            state = UiState.Empty,
            onNavigate = {},
            onRetryClicked = {}
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1080px,height=2340px,dpi=416",
    showSystemUi = true
)
@Composable
fun DashboardScreenPreview_StateError() {
    InventoryTheme {
        DashboardContent(
            state = UiState.Error(""),
            onNavigate = {},
            onRetryClicked = {}
        )
    }
}