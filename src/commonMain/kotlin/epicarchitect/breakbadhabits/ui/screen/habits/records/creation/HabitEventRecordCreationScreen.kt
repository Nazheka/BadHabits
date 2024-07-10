package epicarchitect.breakbadhabits.ui.screen.habits.records.creation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import epicarchitect.breakbadhabits.data.AppData
import epicarchitect.breakbadhabits.data.Habit
import epicarchitect.breakbadhabits.operation.habits.totalHabitEventCountByDaily
import epicarchitect.breakbadhabits.operation.habits.validation.DailyHabitEventCountError
import epicarchitect.breakbadhabits.operation.habits.validation.HabitEventRecordTimeRangeError
import epicarchitect.breakbadhabits.operation.habits.validation.checkDailyHabitEventCount
import epicarchitect.breakbadhabits.operation.habits.validation.checkHabitEventRecordTimeRange
import epicarchitect.breakbadhabits.operation.math.ranges.ascended
import epicarchitect.breakbadhabits.ui.component.Dialog
import epicarchitect.breakbadhabits.ui.component.FlowStateContainer
import epicarchitect.breakbadhabits.ui.component.SimpleScrollableScreen
import epicarchitect.breakbadhabits.ui.component.button.Button
import epicarchitect.breakbadhabits.ui.component.button.ButtonStyles
import epicarchitect.breakbadhabits.ui.component.regex.Regexps
import epicarchitect.breakbadhabits.ui.component.stateOfOneOrNull
import epicarchitect.breakbadhabits.ui.component.text.InputCard
import epicarchitect.breakbadhabits.ui.component.text.Text
import epicarchitect.breakbadhabits.ui.component.text.TextInputCard
import epicarchitect.breakbadhabits.ui.format.formatted
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours

class HabitEventRecordCreationScreen(private val habitId: Int) : Screen {
    @Composable
    override fun Content() {
        HabitEventRecordCreation(habitId)
    }
}

@Composable
fun HabitEventRecordCreation(habitId: Int) {
    val strings = AppData.resources.strings.habitEventRecordCreationStrings
    val habitQueries = AppData.database.habitQueries
    val navigator = LocalNavigator.currentOrThrow

    FlowStateContainer(
        state = stateOfOneOrNull { habitQueries.habitById(habitId) }
    ) { habit ->
        SimpleScrollableScreen(
            title = strings.titleText(habit?.name ?: "..."),
            onBackClick = navigator::pop
        ) {
            if (habit != null) {
                Content(habit)
            }
        }
    }
}

private const val HIDE_PICKER = 0
private const val SHOW_PICKER_START = 1
private const val SHOW_PICKER_END = 2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.Content(habit: Habit) {
    val strings = AppData.resources.strings.habitEventRecordCreationStrings
    val habitEventRecordQueries = AppData.database.habitEventRecordQueries
    val navigator = LocalNavigator.currentOrThrow
    val timeZone by AppData.dateTime.currentTimeZoneState.collectAsState()
    val currentInstant by AppData.dateTime.currentInstantState.collectAsState()
    val currentDateTimeMinusHour = (currentInstant - 1.hours).toLocalDateTime(timeZone)
    val currentDateTime = currentInstant.toLocalDateTime(timeZone)

    var dateSelectionState by rememberSaveable { mutableStateOf(HIDE_PICKER) }
    var timeSelectionState by rememberSaveable { mutableStateOf(HIDE_PICKER) }

    var selectedStartDate by remember { mutableStateOf(currentDateTimeMinusHour.date) }
    var selectedStartTime by remember { mutableStateOf(currentDateTimeMinusHour.time) }

    var selectedEndDate by remember { mutableStateOf(currentDateTime.date) }
    var selectedEndTime by remember { mutableStateOf(currentDateTime.time) }

    var timeRangeError by remember(
        selectedStartTime,
        selectedStartDate,
        selectedEndDate,
        selectedEndTime
    ) { mutableStateOf<HabitEventRecordTimeRangeError?>(null) }

    var dailyEventCount by rememberSaveable { mutableIntStateOf(0) }
    var dailyEventCountError by remember { mutableStateOf<DailyHabitEventCountError?>(null) }

    var comment by rememberSaveable { mutableStateOf("") }

    if (dateSelectionState != HIDE_PICKER) {
        val date = if (dateSelectionState == SHOW_PICKER_START) selectedStartDate else selectedEndDate
        val state = rememberDatePickerState(
            initialSelectedDateMillis = date.toEpochMillis()
        )
        Dialog(
            onDismiss = {
                dateSelectionState = HIDE_PICKER
            }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                DatePicker(state)

                Button(
                    modifier = Modifier.align(Alignment.End),
                    text = strings.done(),
                    onClick = {
                        val newDate = Instant.fromEpochMilliseconds(state.selectedDateMillis!!)
                            .toLocalDateTime(timeZone).date

                        if (dateSelectionState == SHOW_PICKER_START) selectedStartDate = newDate
                        else selectedEndDate = newDate

                        dateSelectionState = HIDE_PICKER
                    }
                )
            }
        }
    }

    if (timeSelectionState != HIDE_PICKER) {
        val time = if (timeSelectionState == SHOW_PICKER_START) selectedStartTime else selectedEndTime
        val state = rememberTimePickerState(
            initialHour = time.hour,
            initialMinute = time.minute
        )
        Dialog(
            onDismiss = {
                timeSelectionState = HIDE_PICKER
            }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                TimePicker(state)

                Button(
                    modifier = Modifier.align(Alignment.End),
                    text = strings.done(),
                    onClick = {
                        val newTime = LocalTime(state.hour, state.minute, 0)

                        if (timeSelectionState == SHOW_PICKER_START) selectedStartTime = newTime
                        else selectedEndTime = newTime

                        timeSelectionState = HIDE_PICKER
                    }
                )
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    TextInputCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        title = strings.dailyEventCountTitle(),
        description = strings.dailyEventCountDescription(),
        value = dailyEventCount.toString(),
        onValueChange = {
            dailyEventCount = it.toIntOrNull() ?: 0
            dailyEventCountError = null
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        regex = Regexps.integersOrEmpty(maxCharCount = 4),
        error = dailyEventCountError?.let(strings::dailyEventCountError),
    )

    Spacer(Modifier.height(16.dp))

    InputCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        title = strings.timeRangeTitle(),
        description = strings.timeRangeDescription(),
        error = timeRangeError?.let(strings::timeRangeError)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = strings.startDateTimeLabel())
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                text = selectedStartDate.formatted(),
                onClick = {
                    dateSelectionState = SHOW_PICKER_START
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                text = selectedStartTime.formatted(),
                onClick = {
                    timeSelectionState = SHOW_PICKER_START
                }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = strings.endDateTimeLabel())
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                text = selectedEndDate.formatted(),
                onClick = {
                    dateSelectionState = SHOW_PICKER_END
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                text = selectedEndTime.formatted(),
                onClick = {
                    timeSelectionState = SHOW_PICKER_END
                }
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    TextInputCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        title = strings.commentTitle(),
        description = strings.commentDescription(),
        value = comment,
        onValueChange = {
            comment = it
        },
        multiline = true
    )

    Spacer(modifier = Modifier.weight(1.0f))

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .align(Alignment.End),
        text = strings.finishButton(),
        style = ButtonStyles.primary,
        onClick = {
            dailyEventCountError = checkDailyHabitEventCount(dailyEventCount)
            if (dailyEventCountError != null) return@Button

            val selectedTimeRange = (LocalDateTime(
                date = selectedStartDate,
                time = selectedStartTime
            ).toInstant(timeZone)..LocalDateTime(
                date = selectedEndDate,
                time = selectedEndTime
            ).toInstant(timeZone)).ascended()

            timeRangeError = checkHabitEventRecordTimeRange(
                timeRange = selectedTimeRange,
                currentTime = currentInstant
            )
            if (timeRangeError != null) return@Button

            habitEventRecordQueries.insert(
                habitId = habit.id,
                startTime = selectedTimeRange.start,
                endTime = selectedTimeRange.endInclusive,
                eventCount = totalHabitEventCountByDaily(
                    dailyEventCount = dailyEventCount,
                    timeRange = selectedTimeRange,
                    timeZone = timeZone
                ),
                comment = comment
            )
            navigator.pop()
        }
    )

    Spacer(modifier = Modifier.height(16.dp))
}

fun LocalDate.toEpochMillis() = LocalDateTime(
    date = this,
    time = LocalTime(
        hour = 0,
        minute = 0,
        second = 0
    )
).toInstant(offset = UtcOffset.ZERO).toEpochMilliseconds()