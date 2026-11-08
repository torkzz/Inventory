package com.kevinfreyap.inventory.bottom_bar

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kevinfreyap.inventory.R
import com.kevinfreyap.ui.theme.CyberSurface
import com.kevinfreyap.ui.theme.NeonCyan
import com.kevinfreyap.ui.theme.TextMuted
import com.kevinfreyap.user.R as userR
import com.kevinfreyap.product.presentation.navigation.ProductScreen
import com.kevinfreyap.user.presentation.navigation.UserScreens

@Composable
fun BottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = CyberSurface,
        modifier = modifier
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val navigationItems = listOf(
            BottomTabItem(
                route = ProductScreen.Dashboard,
                title = stringResource(R.string.bottom_tab_dashboard),
                icon = R.drawable.dashboard_24
            ),
            BottomTabItem(
                route = ProductScreen.ProductList(filter = null),
                title = stringResource(R.string.bottom_tab_product_list),
                icon = R.drawable.format_list_bulleted_24
            ),
            BottomTabItem(
                route = UserScreens.ProfileScreen,
                title = stringResource(R.string.bottom_tab_profile),
                icon = userR.drawable.person_24
            )
        )

        navigationItems.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.hasRoute(item.route::class) == true,
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(item.title)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NeonCyan,
                    selectedTextColor = NeonCyan,
                    indicatorColor = NeonCyan.copy(alpha = 0.15f),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                ),
                onClick = {
                    navController.navigate(item.route){
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}