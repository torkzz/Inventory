package com.kevinfreyap.inventory.screens

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kevinfreyap.domain.model.InventoryBarcode
import com.kevinfreyap.domain.util.SCANNED_BARCODE
import com.kevinfreyap.domain.util.SCANNED_FORMAT
import com.kevinfreyap.inventory.AppGraph
import com.kevinfreyap.product.presentation.navigation.AddProductNavigation
import com.kevinfreyap.product.presentation.navigation.BatchFormNavigation
import com.kevinfreyap.product.presentation.navigation.DashboardNavigation
import com.kevinfreyap.product.presentation.navigation.EditProductNavigation
import com.kevinfreyap.product.presentation.navigation.ProductDetailNavigation
import com.kevinfreyap.product.presentation.navigation.ProductListNavigation
import com.kevinfreyap.product.presentation.navigation.ProductScreen
import com.kevinfreyap.product.presentation.screen.add_product.AddProductScreen
import com.kevinfreyap.product.presentation.screen.batch_form.BatchFormScreen
import com.kevinfreyap.product.presentation.screen.dashboard.DashboardScreen
import com.kevinfreyap.product.presentation.screen.edit_product.EditProductScreen
import com.kevinfreyap.product.presentation.screen.product_detail.ProductDetailScreen
import com.kevinfreyap.product.presentation.screen.product_list.ProductListScreen
import com.kevinfreyap.scanner.navigation.ScannerNavigation
import com.kevinfreyap.scanner.navigation.ScannerScreens
import com.kevinfreyap.scanner.ui.ScannerScreen
import com.kevinfreyap.user.presentation.navigation.EditProfileNavigation
import com.kevinfreyap.user.presentation.navigation.GroupDetailNavigation
import com.kevinfreyap.user.presentation.navigation.GroupFormNavigation
import com.kevinfreyap.user.presentation.navigation.GroupListNavigation
import com.kevinfreyap.user.presentation.navigation.GroupMemberNavigation
import com.kevinfreyap.user.presentation.navigation.ProfileNavigation
import com.kevinfreyap.user.presentation.navigation.UserScreens
import com.kevinfreyap.user.presentation.screen.edit_profile.EditProfileScreen
import com.kevinfreyap.user.presentation.screen.group_detail.GroupDetailScreen
import com.kevinfreyap.user.presentation.screen.group_form.GroupFormScreen
import com.kevinfreyap.user.presentation.screen.group_list.GroupListScreen
import com.kevinfreyap.user.presentation.screen.group_members.GroupMembersScreen
import com.kevinfreyap.user.presentation.screen.profile.ProfileScreen

fun NavGraphBuilder.mainGraph(
    navController: NavHostController
) {
    navigation<AppGraph.Main>(
        startDestination = ProductScreen.Dashboard
    ) {
        composable<ProductScreen.Dashboard> { entry ->
            val scannedBarcode by entry.savedStateHandle
                .getStateFlow(SCANNED_BARCODE, "")
                .collectAsStateWithLifecycle()

            val scannedFormat by entry.savedStateHandle
                .getStateFlow(SCANNED_FORMAT, "")
                .collectAsStateWithLifecycle()

            val inventoryBarcode = InventoryBarcode(
                value = scannedBarcode,
                format = scannedFormat
            )

            DashboardScreen(
                scannedBarcode = inventoryBarcode,
                onClearBarcode = {
                    entry.savedStateHandle[SCANNED_BARCODE] = ""
                    entry.savedStateHandle[SCANNED_FORMAT] = ""
                },
                onNavigate = {destination ->
                    when(destination) {
                        is DashboardNavigation.AddProduct -> {
                            navController.navigate(
                                ProductScreen.AddProduct(
                                    barcodeValue = destination.barcodeValue,
                                    barcodeFormat = destination.barcodeFormat
                                )
                            )
                        }
                        is DashboardNavigation.BarcodeScanner -> {
                            navController.navigate(ScannerScreens.ScannerScreen)
                        }
                        is DashboardNavigation.ProductDetail -> {
                            navController.navigate(ProductScreen.ProductDetail(productId = destination.productId))
                        }
                        is DashboardNavigation.ProductList -> {
                            val filter = destination.filter
                            navController.navigate(ProductScreen.ProductList(filter = filter)) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }

        composable<ProductScreen.ProductList> {
            ProductListScreen(
                onNavigate = { destination ->
                    when(destination) {
                        is ProductListNavigation.AddProduct -> {
                            navController.navigate(ProductScreen.AddProduct())
                        }
                        is ProductListNavigation.BarcodeScanner -> {
                            navController.navigate(ScannerScreens.ScannerScreen)
                        }
                        is ProductListNavigation.ProductDetail -> {
                            navController.navigate(
                                ProductScreen.ProductDetail(destination.productId)
                            )
                        }
                    }
                },
            )
        }

        composable<ProductScreen.AddProduct> { entry ->
            val scannedBarcode by entry.savedStateHandle
                .getStateFlow(SCANNED_BARCODE, "")
                .collectAsStateWithLifecycle()

            val scannedFormat by entry.savedStateHandle
                .getStateFlow(SCANNED_FORMAT, "")
                .collectAsStateWithLifecycle()

            val inventoryBarcode = InventoryBarcode(
                value = scannedBarcode,
                format = scannedFormat
            )

            AddProductScreen(
                scannedBarcode = inventoryBarcode,
                onClearBarcode = {
                    entry.savedStateHandle[SCANNED_BARCODE] = ""
                    entry.savedStateHandle[SCANNED_FORMAT] = ""
                },
                onNavigate = { destination ->
                    when (destination) {
                        AddProductNavigation.NavigateUp -> {
                            navController.navigateUp()
                        }
                        AddProductNavigation.BarcodeScanner -> {
                            navController.navigate(
                                ScannerScreens.ScannerScreen
                            )
                        }
                    }
                }
            )
        }

        composable<ProductScreen.ProductDetail> {
            ProductDetailScreen(
                onNavigate = { destination ->
                    when (destination) {
                        is ProductDetailNavigation.NavigateUp -> {
                            navController.navigateUp()
                        }

                        is ProductDetailNavigation.EditProduct -> {
                            navController.navigate(
                                ProductScreen.EditProduct(destination.productId)
                            )
                        }

                        is ProductDetailNavigation.AddBatch -> {
                            val productId = destination.productId

                            navController.navigate(
                                ProductScreen.BatchForm(
                                    productId = productId,
                                    batchId = null
                                )
                            )
                        }

                        is ProductDetailNavigation.BatchDetail -> {
                            val productId = destination.productId
                            val batchId = destination.batchId

                            navController.navigate(
                                ProductScreen.BatchForm(
                                    productId = productId,
                                    batchId = batchId
                                )
                            )
                        }
                    }
                }
            )
        }

        composable<ProductScreen.EditProduct> { entry ->
            val scannedBarcode by entry.savedStateHandle
                .getStateFlow(SCANNED_BARCODE, "")
                .collectAsStateWithLifecycle()

            val scannedFormat by entry.savedStateHandle
                .getStateFlow(SCANNED_FORMAT, "")
                .collectAsStateWithLifecycle()

            val inventoryBarcode = InventoryBarcode(
                value = scannedBarcode,
                format = scannedFormat
            )

            EditProductScreen(
                scannedBarcode = inventoryBarcode,
                onClearBarcode = {
                    entry.savedStateHandle[SCANNED_BARCODE] = ""
                    entry.savedStateHandle[SCANNED_FORMAT] = ""
                },
                onNavigate = {destination ->
                    when (destination) {
                        EditProductNavigation.NavigateUp -> {
                            navController.navigateUp()
                        }
                        EditProductNavigation.BarcodeScanner -> {
                            navController.navigate(
                                ScannerScreens.ScannerScreen
                            )
                        }
                    }
                }
            )
        }

        composable<ProductScreen.BatchForm> {
            BatchFormScreen(
                onNavigate = { destination ->
                    when(destination) {
                        BatchFormNavigation.NavigateUp -> {
                            navController.navigateUp()
                        }
                    }
                }
            )
        }

        composable<ScannerScreens.ScannerScreen> {
            ScannerScreen(
                onScanSuccess = { barcode, format ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.apply {
                            set(SCANNED_BARCODE, barcode)
                            set(SCANNED_FORMAT, format)
                        }

                    navController.popBackStack()
                },
                onNavigate = { destination ->
                    when(destination) {
                        ScannerNavigation.NavigateUp -> {
                            navController.popBackStack()
                        }
                    }
                },
            )
        }

        composable<UserScreens.ProfileScreen> {
            ProfileScreen(
                onNavigate = {destination ->
                    when(destination) {
                        ProfileNavigation.Onboard -> {
                            navController.navigate(AppGraph.Auth) {
                                popUpTo<AppGraph.Main> {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                        ProfileNavigation.AddGroup -> {
                            navController.navigate(
                                UserScreens.GroupFormScreen()
                            )
                        }
                        ProfileNavigation.EditProfile -> {
                            navController.navigate(
                                UserScreens.EditProfileScreen
                            )
                        }
                        is ProfileNavigation.GroupDetail -> {
                            navController.navigate(
                                UserScreens.GroupDetailScreen(destination.groupId)
                            )
                        }
                        ProfileNavigation.ManageGroups -> {
                            navController.navigate(
                                UserScreens.GroupListScreen
                            )
                        }
                    }
                },
            )
        }

        composable<UserScreens.EditProfileScreen> {
            EditProfileScreen(
                onNavigate = {destination ->
                    when(destination) {
                        EditProfileNavigation.NavigateUp -> {
                            navController.navigateUp()
                        }
                    }
                }
            )
        }

        composable<UserScreens.GroupDetailScreen> {
            GroupDetailScreen(
                onNavigate = {destination ->
                    when(destination) {
                        GroupDetailNavigation.NavigateUp -> {
                            navController.navigateUp()
                        }

                        is GroupDetailNavigation.AllMember -> {
                            navController.navigate(
                                UserScreens.GroupMembersScreen(destination.groupId)
                            )
                        }
                        GroupDetailNavigation.BrowseInventory -> {
                            navController.navigate(
                                ProductScreen.Dashboard
                            ) {
                                popUpTo(0) {
                                    inclusive = true
                                }

                                launchSingleTop = true
                            }
                        }
                        is GroupDetailNavigation.EditGroup -> {
                            navController.navigate(
                                UserScreens.GroupFormScreen(destination.groupId)
                            )
                        }
                        is GroupDetailNavigation.MemberDetail -> {}
                    }
                }
            )
        }

        composable<UserScreens.GroupMembersScreen> {
            GroupMembersScreen(
                onNavigate = { destination ->
                    when(destination) {
                        GroupMemberNavigation.NavigateUp -> {
                            navController.navigateUp()
                        }
                    }
                }
            )
        }

        composable<UserScreens.GroupListScreen> {
            GroupListScreen(
                onNavigate = { destination ->
                    when(destination) {
                        is GroupListNavigation.GroupDetail -> {
                            navController.navigate(
                                UserScreens.GroupDetailScreen(destination.groupId)
                            )
                        }
                        GroupListNavigation.AddGroup -> {
                            navController.navigate(
                                UserScreens.GroupFormScreen()
                            )
                        }
                        GroupListNavigation.NavigateUp -> {
                            navController.navigateUp()
                        }
                    }
                }
            )
        }

        composable<UserScreens.GroupFormScreen> {
            GroupFormScreen(
                onNavigate = { destination ->
                    when (destination) {
                        GroupFormNavigation.NavigateUp -> {
                            navController.navigateUp()
                        }
                    }
                }
            )
        }
    }
}