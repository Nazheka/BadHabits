package epicarchitect.breakbadhabits.habits.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import epicarchitect.breakbadhabits.BaseActivity
import epicarchitect.breakbadhabits.ui.component.theme.AppColorsSchemes
import epicarchitect.breakbadhabits.ui.component.theme.AppTheme

class HabitsAppWidgetConfigCreationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val systemWidgetId = intent.extras!!.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)
        setContent {
            AppTheme(
                colorScheme = AppColorsSchemes.light
            ) {
                HabitWidgetCreation(
                    systemWidgetId = systemWidgetId,
                    onDone = {
                        setResult(
                            RESULT_OK,
                            Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, systemWidgetId)
                        )
                        finish()
                    }
                )
            }
        }
    }
}