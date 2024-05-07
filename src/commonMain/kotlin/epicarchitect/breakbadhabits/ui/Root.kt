package epicarchitect.breakbadhabits.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import epicarchitect.breakbadhabits.data.AppData
import epicarchitect.breakbadhabits.entity.util.flowOfOneOrNull
import epicarchitect.breakbadhabits.ui.dashboard.DashboardScreen
import epicarchitect.breakbadhabits.uikit.theme.AppColorsSchemes
import epicarchitect.breakbadhabits.uikit.theme.AppTheme

@Composable
fun Root() {
    val appSettingsState = remember {
        AppData.database.appSettingsQueries.settings().flowOfOneOrNull()
    }.collectAsState(initial = null)

    val appSettings = appSettingsState.value ?: return

    AppTheme(
        colorScheme = when (appSettings.theme) {
            1L -> AppColorsSchemes.light
            2L -> AppColorsSchemes.dark
            else -> AppColorsSchemes.system
        }
    ) {
        Navigator(DashboardScreen())
    }
}