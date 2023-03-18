package breakbadhabits.foundation.uikit.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import breakbadhabits.foundation.uikit.AppTheme

abstract class ComposeActivity : AppCompatActivity() {

    protected open val themeResourceId: Int? = null
    private lateinit var darkModeManager: DarkModeManager

    @Composable
    abstract fun Content()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
        darkModeManager = DarkModeManager(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeResourceId?.let(::setTheme)
        setContent {
            CompositionLocalProvider(
                LocalActivity provides this,
                LocalDarkModeManager provides darkModeManager
            ) {
                AppTheme(
                    isDarkTheme = when (darkModeManager.mode.value) {
                        DarkMode.ENABLED -> true
                        DarkMode.DISABLED -> false
                        DarkMode.BY_SYSTEM -> isSystemInDarkTheme()
                    }
                ) {
                    Content()
                }
            }
        }
    }
}