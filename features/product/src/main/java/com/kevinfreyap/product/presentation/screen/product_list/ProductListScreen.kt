package com.kevinfreyap.product.presentation.screen.product_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.kevinfreyap.product.R
import com.kevinfreyap.product.presentation.action.FilterQueryAction
import com.kevinfreyap.product.presentation.components.ProductListItem
import com.kevinfreyap.product.presentation.navigation.ProductListNavigation
import com.kevinfreyap.ui.R as coreR
import com.kevinfreyap.product.presentation.model.ProductListItemUi
import com.kevinfreyap.product.presentation.model.StockLevel
import com.kevinfreyap.product.presentation.screen.bottom_sheet.filter.FilterBottomSheet
import com.kevinfreyap.product.presentation.state.FilterOptionList
import com.kevinfreyap.product.presentation.state.FilterState
import com.kevinfreyap.ui.components.AppBaseListItemPlaceholder
import com.kevinfreyap.ui.components.AppPrimaryButton
import com.kevinfreyap.ui.components.AppSearchBar
import com.kevinfreyap.ui.components.AppStateBanner
import com.kevinfreyap.ui.theme.InventoryTheme
import kotlinx.coroutines.flow.flowOf

@Composable
fun ProductListScreen(
    onNavigate: (ProductListNavigation) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val products = viewModel.products.collectAsLazyPagingItems()
    val totalCount by viewModel.totalCount.collectAsStateWithLifecycle()

    val maxAllowedDateMillis = viewModel.maxAllowedDateMillis
    val availableFilterOption by viewModel.availableFilters.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val appliedQuery by viewModel.appliedQuery.collectAsStateWithLifecycle()

    val draftFilter by viewModel.draftFilter.collectAsStateWithLifecycle()
    val appliedFilter by viewModel.filterState.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }

    ProductListContent(
        products = products,
        totalCount = totalCount,
        searchQuery = searchQuery,
        appliedQuery = appliedQuery,
        availableFilterOption = availableFilterOption,
        maxAllowedDateMillis = maxAllowedDateMillis,
        draftFilter = draftFilter,
        appliedFilter = appliedFilter,
        showFilterSheet = showFilterSheet,
        onToggleFilterSheet = { isVisible ->
            showFilterSheet = isVisible
        },
        onFilterAction = viewModel::onFilterAction,
        onNavigate = onNavigate,
        modifier = modifier
    )
}

@Composable
fun ProductListContent(
    products: LazyPagingItems<ProductListItemUi>,
    totalCount: Int,
    searchQuery: String,
    appliedQuery: String,
    availableFilterOption: FilterOptionList,
    maxAllowedDateMillis: Long,
    draftFilter: FilterState,
    appliedFilter: FilterState,
    showFilterSheet: Boolean,
    onToggleFilterSheet: (Boolean) -> Unit,
    onFilterAction: (FilterQueryAction) -> Unit,
    onNavigate: (ProductListNavigation) -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        contentWindowInsets = WindowInsets(top = 24.dp),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onNavigate(ProductListNavigation.AddProduct)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    painter = painterResource(coreR.drawable.add_24),
                    contentDescription = "Add Product"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .fillMaxSize()
        ) {
            ProductSearchAndFilterBar(
                searchQuery = searchQuery,
                appliedFilter = appliedFilter,
                onFilterAction = onFilterAction,
                onToggleFilterSheet = onToggleFilterSheet,
                onNavigate = onNavigate
            )

            Spacer(Modifier.height(16.dp))

            ProductContentList(
                products = products,
                totalCount = totalCount,
                searchQuery = appliedQuery,
                appliedFilter = appliedFilter,
                onNavigate = onNavigate,
                onFilterAction = onFilterAction,
            )
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            filterOptionList = availableFilterOption,
            filterState = draftFilter,
            maxAllowedDateMillis = maxAllowedDateMillis,
            onAction = { action ->
                onFilterAction(action)

                if (action is FilterQueryAction.ApplyFilter) {
                    onToggleFilterSheet(false)
                }
            },
            onDismiss = {
                onToggleFilterSheet(false)
            },
        )
    }
}

@Composable
private fun ProductSearchAndFilterBar(
    searchQuery: String,
    appliedFilter: FilterState,
    onFilterAction: (FilterQueryAction) -> Unit,
    onToggleFilterSheet: (Boolean) -> Unit,
    onNavigate: (ProductListNavigation) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AppSearchBar(
            searchQuery = searchQuery,
            placeholder = stringResource(R.string.placeholder_search_inventory),
            onQueryChange = { query ->
                onFilterAction(FilterQueryAction.UpdateSearchQuery(query))
            },
            modifier = Modifier
                .weight(1f)
        )

        IconButton(
            onClick = {
                onNavigate(ProductListNavigation.BarcodeScanner)
            },
            modifier = Modifier
                .padding(4.dp)
        ) {
            Icon(
                painter = painterResource(coreR.drawable.app_barcode_scanner),
                contentDescription = "Scan Barcode"
            )
        }

        IconButton(
            onClick = {
                onFilterAction(FilterQueryAction.ShowAppliedFilter)
                onToggleFilterSheet(true)
            },
            modifier = Modifier
                .padding(4.dp)
        ) {
            BadgedBox(
                badge = { if (appliedFilter.hasActiveFilter) Badge() },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.filter_list_24),
                    contentDescription = "Filter Icon",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProductContentList(
    products: LazyPagingItems<ProductListItemUi>,
    totalCount: Int,
    searchQuery: String,
    appliedFilter: FilterState,
    onNavigate: (ProductListNavigation) -> Unit,
    onFilterAction: (FilterQueryAction) -> Unit
) {
    val isLoading = products.loadState.refresh is LoadState.Loading
    val isError = products.loadState.refresh is LoadState.Error
    val isSearchOrFilterActive = searchQuery.isNotEmpty() || appliedFilter.hasActiveFilter

    var hasCompletedInitialLoad by rememberSaveable { mutableStateOf(false) }
    var displayAsNoResult by rememberSaveable { mutableStateOf(isSearchOrFilterActive) }

    LaunchedEffect(isLoading, hasCompletedInitialLoad) {
        if (!isLoading && !hasCompletedInitialLoad) {
            hasCompletedInitialLoad = true
        }
    }

    LaunchedEffect(isLoading, isSearchOrFilterActive) {
        if (!isLoading) {
            displayAsNoResult = isSearchOrFilterActive
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when {
            // initial loading
            isLoading && products.itemCount == 0 && !hasCompletedInitialLoad -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(count = 10) {
                        AppBaseListItemPlaceholder()
                    }
                }
            }

            // empty state, finished loading found nothing
            products.itemCount == 0 -> {
                ProductEmptyOrNoResultState(
                    isNoResultState = displayAsNoResult,
                    onFilterAction = onFilterAction,
                    onNavigate = onNavigate,
                )
            }

            // error state
            isError && products.itemCount == 0 -> {
                ProductErrorState(
                    onRetry = {
                        products.retry()
                    }
                )
            }

            // success
            else -> {
                ProductSuccessState(
                    products = products,
                    totalCount = totalCount,
                    isSearchFilterActive = isSearchOrFilterActive,
                    onNavigate = onNavigate
                )
            }
        }
    }
}

@Composable
private fun ProductEmptyOrNoResultState(
    isNoResultState: Boolean,
    onFilterAction: (FilterQueryAction) -> Unit,
    onNavigate: (ProductListNavigation) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = minHeight)
        ) {
            if (isNoResultState) {
                AppStateBanner(
                    bannerIcon = coreR.drawable.app_no_result_icon,
                    bannerTitle = stringResource(R.string.title_banner_no_result),
                    bannerSubtitle = stringResource(R.string.subtitle_banner_no_result),
                    actionButton = {
                        AppPrimaryButton(
                            text = stringResource(R.string.btn_label_clear_filters),
                            onClick = {
                                onFilterAction(FilterQueryAction.ClearAll)
                                onFilterAction(FilterQueryAction.UpdateSearchQuery(""))
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(coreR.drawable.delete_24),
                                    contentDescription = stringResource(R.string.btn_label_clear_filters),
                                )
                            },
                        )
                    },
                )
            } else {
                AppStateBanner(
                    bannerIcon = coreR.drawable.app_empty_storage_icon,
                    bannerTitle = stringResource(R.string.title_banner_empty_inventory),
                    bannerSubtitle = stringResource(R.string.subtitle_banner_empty_inventory),
                    actionButton = {
                        AppPrimaryButton(
                            text = stringResource(R.string.btn_label_add_first_item),
                            onClick = {
                                onNavigate(ProductListNavigation.AddProduct)
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(coreR.drawable.add_24),
                                    contentDescription = stringResource(R.string.btn_label_add_first_item),
                                )
                            },
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun ProductSuccessState(
    products: LazyPagingItems<ProductListItemUi>,
    totalCount: Int,
    isSearchFilterActive: Boolean,
    onNavigate: (ProductListNavigation) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
    ) {
        item{
            Text(
                text = if (isSearchFilterActive) {
                    stringResource(R.string.label_showing_search_filter_result, totalCount)
                } else {
                    stringResource(R.string.label_total_search_filter, totalCount)
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
        }

        items(
            count = products.itemCount,
            key = products.itemKey { item -> item.id }
        ) { index ->
            val product = products[index]
            if (product != null) {
                ProductListItem(
                    product = product,
                    onClick = {
                        onNavigate(ProductListNavigation.ProductDetail(product.id))
                    }
                )
            }
        }


        when (products.loadState.append) {
            LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }

            is LoadState.Error -> {
                item {
                    TextButton(
                        onClick = { products.retry() },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.txt_btn_label_tap_to_retry_load_more),
                        )
                    }
                }
            }

            is LoadState.NotLoading -> Unit
        }
    }
}

@Composable
private fun ProductErrorState(
    onRetry: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = minHeight)
        ) {
            AppStateBanner(
                bannerIcon = coreR.drawable.app_error_load_icon,
                bannerTitle = stringResource(R.string.title_banner_load_error),
                bannerSubtitle = stringResource(R.string.subtitle_banner_load_error),
                actionButton = {
                    AppPrimaryButton(
                        text = stringResource(R.string.btn_label_try_again),
                        onClick = onRetry,
                        icon = {
                            Icon(
                                painter = painterResource(coreR.drawable.refresh_24),
                                contentDescription = stringResource(R.string.btn_label_try_again),
                            )
                        },
                    )
                },
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1080px,height=2340px,dpi=416",
    showSystemUi = true
)
@Composable
fun ProductListPreview() {
    val sampleProducts = listOf(
        ProductListItemUi(
            id = "PROD-01",
            name = "Smartphone",
            quantity = 100,
            stockLevel = StockLevel.IN_STOCK,
            category = "Electronic",
            imageUri = null,
            sku = "#SKU-1234-Bdksadjfkasdjfkasljfkljfaljfkljskdjakjfkdjajsjdfkjsajdkjfajdfj"
        ),
        ProductListItemUi(
            id = "PROD-02",
            name = "Smartphone 2",
            quantity = 10,
            stockLevel = StockLevel.LOW_STOCK,
            category = "Electronic",
            imageUri = null,
            sku = null
        ),
        ProductListItemUi(
            id = "PROD-03",
            name = "Smartphone 3",
            quantity = 0,
            stockLevel = StockLevel.OUT_OF_STOCK,
            category = "Electronic",
            imageUri = null,
            sku = "#SKU-1234-B"
        ),
    )

    val loadStates = LoadStates(
//        refresh = LoadState.Error(error = Throwable()),
        refresh = LoadState.NotLoading(endOfPaginationReached = true),
        prepend = LoadState.NotLoading(endOfPaginationReached = true),
        append = LoadState.NotLoading(endOfPaginationReached = true)
    )

    val pagingData = PagingData.from(
//        data = emptyList<ProductListItemUi>(),
        data = sampleProducts,
        sourceLoadStates = loadStates
    )

    // Turn into LazyPagingItems for preview
    val lazyPagingItems = flowOf(pagingData).collectAsLazyPagingItems()

    InventoryTheme {
        ProductListContent(
            products = lazyPagingItems,
            totalCount = 3,
            searchQuery = "",
            appliedQuery = "",
            availableFilterOption = FilterOptionList(),
            maxAllowedDateMillis = 0L,
            draftFilter = FilterState(),
            appliedFilter = FilterState(),
            showFilterSheet = false,
            onToggleFilterSheet = { },
            onFilterAction = {},
            onNavigate = {}
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1080px,height=2340px,dpi=416",
    showSystemUi = true
)
@Composable
fun ProductListPreview_Loading() {
    val loadStates = LoadStates(
        refresh = LoadState.Loading,
        prepend = LoadState.NotLoading(endOfPaginationReached = true),
        append = LoadState.NotLoading(endOfPaginationReached = true)
    )

    val pagingData = PagingData.from(
        data = emptyList<ProductListItemUi>(),
        sourceLoadStates = loadStates
    )

    // Turn into LazyPagingItems for preview
    val lazyPagingItems = flowOf(pagingData).collectAsLazyPagingItems()

    InventoryTheme {
        ProductListContent(
            products = lazyPagingItems,
            totalCount = 0,
            searchQuery = "",
            appliedQuery = "",
            availableFilterOption = FilterOptionList(),
            maxAllowedDateMillis = 0L,
            draftFilter = FilterState(),
            appliedFilter = FilterState(),
            showFilterSheet = false,
            onToggleFilterSheet = { },
            onFilterAction = {},
            onNavigate = {}
        )
    }
}