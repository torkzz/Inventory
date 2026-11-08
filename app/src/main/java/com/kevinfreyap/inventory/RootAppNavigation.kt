package com.kevinfreyap.inventory

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kevinfreyap.inventory.bottom_bar.BottomBar
import com.kevinfreyap.inventory.screens.authGraph
import com.kevinfreyap.inventory.screens.mainGraph
import com.kevinfreyap.product.presentation.navigation.ProductScreen
import com.kevinfreyap.user.presentation.navigation.UserScreens

@Composable
fun RootAppNavigation(
    isLoggedIn: Boolean
) {
    val rootNavController = rememberNavController()

    val navBackStackEntry by rootNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.let { destination ->
        destination.hasRoute<ProductScreen.Dashboard>() ||
        destination.hasRoute<ProductScreen.ProductList>() ||
        destination.hasRoute<UserScreens.ProfileScreen>()
    } ?: false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController = rootNavController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = rootNavController,
            startDestination = AppGraph.Main,
            modifier = Modifier
                .padding(innerPadding)
        ) {
            authGraph(navController = rootNavController)
            mainGraph(navController = rootNavController)
        }
    }
}