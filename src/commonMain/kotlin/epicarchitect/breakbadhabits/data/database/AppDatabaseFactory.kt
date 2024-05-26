package epicarchitect.breakbadhabits.data.database

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import epicarchitect.breakbadhabits.data.AppDatabase
import epicarchitect.breakbadhabits.data.AppSettings
import epicarchitect.breakbadhabits.data.Habit
import epicarchitect.breakbadhabits.data.HabitTrack
import epicarchitect.breakbadhabits.data.HabitWidget
import epicarchitect.breakbadhabits.data.database.appSettings.AppSettingsThemeAdapter

operator fun AppDatabase.Companion.invoke(name: String) = AppDatabase(
    driver = SqlDriverFactory.create(
        schema = Schema,
        databaseName = name
    ),
    HabitAdapter = Habit.Adapter(
        idAdapter = IntColumnAdapter,
        iconIdAdapter = IntColumnAdapter
    ),
    HabitWidgetAdapter = HabitWidget.Adapter(
        idAdapter = IntColumnAdapter,
        systemIdAdapter = IntColumnAdapter,
        habitIdsAdapter = ListOfIntAdapter
    ),
    HabitTrackAdapter = HabitTrack.Adapter(
        idAdapter = IntColumnAdapter,
        habitIdAdapter = IntColumnAdapter,
        startTimeAdapter = InstantAdapter,
        endTimeAdapter = InstantAdapter,
        eventCountAdapter = IntColumnAdapter
    ),
    AppSettingsAdapter = AppSettings.Adapter(
        idAdapter = IntColumnAdapter,
        themeAdapter = AppSettingsThemeAdapter
    )
)